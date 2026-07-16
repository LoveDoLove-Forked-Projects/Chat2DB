import {
  memo,
  forwardRef,
  useImperativeHandle,
  ForwardedRef,
  useState,
  useMemo,
  useRef,
  useEffect,
} from 'react';
import { useStyles } from './style';
import { Alert, Button, Segmented, Space } from 'antd';
import { ToolbarBtn } from '@chat2db/ui';
import i18n from '@/i18n';
import MonacoEditor from '@/components/MonacoEditor';
import { v4 as uuidv4 } from 'uuid';
import type { IHandleViewUpdateDataParams } from '@/blocks/SearchResult/components/ResultSetTable/typings';
import type { OperationRecordUtils } from '@/blocks/SearchResult/components/ResultSetTable/hooks/useOperationRecord';
import sqlService from '@/service/sql';
import { ILargeCellChunk, IResultCell } from '@/typings/database';
import { downloadLargeCellValue } from '@/utils/file';
import { isDesktop } from '@/utils/env';
import {
  LargeCellBinaryFormat,
  LARGE_CELL_BINARY_FORMAT,
  canSubmitLargeCellEdit,
  getEditorLanguage,
  getInitialBinaryFormat,
  getLargeCellDownloadFormat,
  getLargeCellErrorMessage,
  getLargeCellDisplayMessage,
  getLargeCellDisplayValue,
  getLargeCellEditorLimit,
  getLargeCellImagePreviewSrc,
  getLargeCellLoadedBytes,
  getLargeCellRequestFormat,
  getNextLargeCellChunkLimit,
  LARGE_CELL_ERROR_MESSAGE,
  isJsonDisplayMode,
  isBinaryDisplayMode,
  isLargeCellTokenExpiredError,
} from './largeCellValue';
import { isJsonObjectOrArray } from './jsonValue';

interface IProps {
  className?: string;
  onExecuteSuccess?: () => void;
  onClose?: () => void;
}

interface IViewData extends IHandleViewUpdateDataParams {
  canEdit: boolean;
  field?: string;
  operationRecordUtils?: OperationRecordUtils;
}

type LargeValueLoadingAction = 'initial' | 'more' | 'all' | 'format' | null;

const cloneCellMeta = (cellMeta?: IResultCell) => (cellMeta ? { ...cellMeta } : undefined);

export interface ViewDataRef {
  openPanel: (params: IViewData) => any;
}

const ViewData = forwardRef((props: IProps, ref: ForwardedRef<ViewDataRef>) => {
  const [viewData, setViewData] = useState<IViewData | null>(null);
  const monacoEditorRef = useRef<any>(null);
  const [isJsonContent, setIsJsonContent] = useState(false);
  const [largeValueChunks, setLargeValueChunks] = useState<ILargeCellChunk[]>([]);
  const [largeValueLoading, setLargeValueLoading] = useState(false);
  const [largeValueLoadingAction, setLargeValueLoadingAction] = useState<LargeValueLoadingAction>(null);
  const [largeValueDownloading, setLargeValueDownloading] = useState(false);
  const [largeValueError, setLargeValueError] = useState<string>('');
  const [largeValueExpired, setLargeValueExpired] = useState(false);
  const [binaryFormat, setBinaryFormat] = useState<LargeCellBinaryFormat>(LARGE_CELL_BINARY_FORMAT.HEX);
  const editorInstanceRef = useRef<any>(null);
  const editorContentChangeDisposerRef = useRef<{ dispose: () => void } | null>(null);
  const pendingEditorValueRef = useRef<string>('');
  const largeValueRequestVersionRef = useRef(0);
  const activeLargeValueIdRef = useRef<string>('');
  const originalLargeValueRef = useRef<{
    value: string;
    restoreValue: string | null;
    cellMeta?: IResultCell;
  } | null>(null);
  const { styles } = useStyles();

  const uuid = useMemo(() => {
    return uuidv4();
  }, []);

  useEffect(() => {
    if (viewData) {
      const { row, col, tableInstance, cellMeta } = viewData;
      setLargeValueChunks([]);
      setLargeValueError('');
      setLargeValueDownloading(false);
      setLargeValueExpired(false);
      largeValueRequestVersionRef.current += 1;
      activeLargeValueIdRef.current = cellMeta?.largeValueId || '';
      originalLargeValueRef.current = null;
      if (cellMeta?.largeValue) {
        const initialBinaryFormat = getInitialBinaryFormat(cellMeta.valueType);
        setBinaryFormat(initialBinaryFormat);
        originalLargeValueRef.current = {
          value: cellMeta.value || '',
          restoreValue: tableInstance.getCellOriginValue(col, row),
          cellMeta: cloneCellMeta(cellMeta),
        };
        setEditorValue(cellMeta.value || '');
        if (!isDesktop) {
          return;
        }
        if (cellMeta.largeValueId) {
          loadLargeValueChunk(cellMeta.largeValueId, 0, false, undefined, cellMeta.valueType, initialBinaryFormat, 'initial');
        } else if (cellMeta.unsupportedReason) {
          setLargeValueError(getLargeCellDisplayMessage(cellMeta.unsupportedReason));
        }
        return;
      }
      const data = tableInstance.getCellOriginValue(col, row);
      setEditorValue(data);
    } else {
      editorInstanceRef.current = null;
      activeLargeValueIdRef.current = '';
      originalLargeValueRef.current = null;
      setLargeValueDownloading(false);
      setLargeValueExpired(false);
      largeValueRequestVersionRef.current += 1;
    }
  }, [viewData]);

  const setEditorValue = (value?: string | null) => {
    const nextValue = value === null || value === undefined ? '' : String(value);
    setIsJsonContent(isJsonObjectOrArray(nextValue));
    pendingEditorValueRef.current = nextValue;
    if (editorInstanceRef.current) {
      editorInstanceRef.current.setValue(nextValue);
      return;
    }
    monacoEditorRef.current?.setValue?.(nextValue, 'cover');
  };

  const handleEditorDidMount = (editor) => {
    editorContentChangeDisposerRef.current?.dispose();
    editorInstanceRef.current = editor;
    editor.setValue(pendingEditorValueRef.current || '');
    editorContentChangeDisposerRef.current = editor.onDidChangeModelContent(() => {
      setIsJsonContent(isJsonObjectOrArray(editor.getValue()));
    });
  };

  useEffect(() => {
    return () => editorContentChangeDisposerRef.current?.dispose();
  }, []);

  const latestChunk = largeValueChunks[largeValueChunks.length - 1];
  const largeValueMeta = viewData?.cellMeta;
  const isLargeValue = !!largeValueMeta?.largeValue;
  const canUseLargeValueActions = isLargeValue && isDesktop;
  const displayMode = latestChunk?.displayMode || largeValueMeta?.valueType;
  const editorLimit = getLargeCellEditorLimit(displayMode);
  const loadedSize = getLargeCellLoadedBytes(largeValueChunks);
  const canLoadAll =
    canUseLargeValueActions &&
    !latestChunk?.eof &&
    getNextLargeCellChunkLimit({ loadedSize, editorLimit, binaryFormat }) > 0;
  const canShowJsonTools = isJsonContent && (!isLargeValue || isJsonDisplayMode(displayMode));
  const showToolbar = canShowJsonTools || canUseLargeValueActions;
  const largeValueText = useMemo(() => {
    return getLargeCellDisplayValue(largeValueChunks, largeValueMeta?.value);
  }, [largeValueChunks, largeValueMeta?.value]);
  const imagePreviewSrc = useMemo(() => {
    return getLargeCellImagePreviewSrc({
      displayMode,
      chunks: largeValueChunks,
      eof: latestChunk?.eof,
      contentType: latestChunk?.contentType,
    });
  }, [displayMode, largeValueChunks, latestChunk]);
  const canSubmitEdit = canSubmitLargeCellEdit({
    isLargeValue,
    displayMode,
    eof: latestChunk?.eof,
    loadedSize,
    editorLimit,
  });
  const hasReachedEditorLimit = canUseLargeValueActions && loadedSize >= editorLimit && !latestChunk?.eof;
  const largeValueStatus = useMemo(() => {
    const items = [
      displayMode ? String(displayMode) : '',
      isBinaryDisplayMode(displayMode) ? binaryFormat.toUpperCase() : '',
      i18n('common.largeCellValue.status.loadedBytes', loadedSize),
      latestChunk?.eof
        ? i18n('common.largeCellValue.status.complete')
        : i18n('common.largeCellValue.status.partial'),
    ];
    if (hasReachedEditorLimit) {
      items.push(i18n('common.largeCellValue.status.editorLimitReached'));
    }
    return items.filter(Boolean).join(' · ');
  }, [displayMode, binaryFormat, loadedSize, latestChunk?.eof, hasReachedEditorLimit]);

  useEffect(() => {
    if (!isLargeValue) {
      return;
    }
    setTimeout(() => {
      setEditorValue(largeValueText);
    }, 0);
  }, [isLargeValue, largeValueText]);

  useEffect(() => {
    if (!isLargeValue || !latestChunk?.eof || !viewData) {
      return;
    }
    originalLargeValueRef.current = {
      value: largeValueText,
      restoreValue: viewData.tableInstance.getCellOriginValue(viewData.col, viewData.row),
      cellMeta: cloneCellMeta(largeValueMeta),
    };
  }, [isLargeValue, latestChunk?.eof, largeValueText, largeValueMeta, viewData]);

  const loadLargeValueChunk = async (
    largeValueId: string,
    offset: number,
    append = true,
    limit?: number,
    mode: string = displayMode || largeValueMeta?.valueType || 'TEXT',
    nextBinaryFormat: LargeCellBinaryFormat = binaryFormat,
    loadingAction: LargeValueLoadingAction = 'initial',
  ) => {
    const requestVersion = largeValueRequestVersionRef.current;
    setLargeValueLoading(true);
    setLargeValueLoadingAction(loadingAction);
    setLargeValueError('');
    try {
      const chunk = await sqlService.getLargeCellValue({
        largeValueId,
        offset,
        limit,
        format: getLargeCellRequestFormat(mode, nextBinaryFormat),
      });
      if (requestVersion !== largeValueRequestVersionRef.current || activeLargeValueIdRef.current !== largeValueId) {
        return;
      }
      setLargeValueChunks((prev) => (append ? [...prev, chunk] : [chunk]));
    } catch (error: any) {
      if (requestVersion !== largeValueRequestVersionRef.current || activeLargeValueIdRef.current !== largeValueId) {
        return;
      }
      setLargeValueExpired(isLargeCellTokenExpiredError(error));
      setLargeValueError(getLargeCellErrorMessage(error, LARGE_CELL_ERROR_MESSAGE.LOAD_FAILED));
    } finally {
      if (requestVersion === largeValueRequestVersionRef.current && activeLargeValueIdRef.current === largeValueId) {
        setLargeValueLoading(false);
        setLargeValueLoadingAction(null);
      }
    }
  };

  const loadMore = () => {
    if (!canUseLargeValueActions || largeValueExpired || !largeValueMeta?.largeValueId) {
      return;
    }
    loadLargeValueChunk(largeValueMeta.largeValueId, latestChunk?.nextOffset || 0, true, undefined, undefined, binaryFormat, 'more');
  };

  const loadAllUpToLimit = async () => {
    if (!canUseLargeValueActions || largeValueExpired || !largeValueMeta?.largeValueId) {
      return;
    }
    const largeValueId = largeValueMeta.largeValueId;
    const requestVersion = largeValueRequestVersionRef.current;
    let offset = latestChunk?.nextOffset || 0;
    let eof = !!latestChunk?.eof;
    let nextLoadedSize = loadedSize;
    let nextLimit = getNextLargeCellChunkLimit({ loadedSize: nextLoadedSize, editorLimit, binaryFormat });
    setLargeValueLoading(true);
    setLargeValueLoadingAction('all');
    setLargeValueError('');
    try {
      while (!eof && nextLimit > 0) {
        const chunk = await sqlService.getLargeCellValue({
          largeValueId,
          offset,
          limit: nextLimit,
          format: getLargeCellRequestFormat(displayMode, binaryFormat),
        });
        if (requestVersion !== largeValueRequestVersionRef.current || activeLargeValueIdRef.current !== largeValueId) {
          break;
        }
        setLargeValueChunks((prev) => [...prev, chunk]);
        eof = chunk.eof;
        nextLoadedSize += chunk.nextOffset - chunk.offset;
        offset = chunk.nextOffset;
        nextLimit = getNextLargeCellChunkLimit({ loadedSize: nextLoadedSize, editorLimit, binaryFormat });
      }
    } catch (error: any) {
      if (requestVersion === largeValueRequestVersionRef.current && activeLargeValueIdRef.current === largeValueId) {
        setLargeValueExpired(isLargeCellTokenExpiredError(error));
        setLargeValueError(getLargeCellErrorMessage(error, LARGE_CELL_ERROR_MESSAGE.LOAD_FAILED));
      }
    } finally {
      if (requestVersion === largeValueRequestVersionRef.current && activeLargeValueIdRef.current === largeValueId) {
        setLargeValueLoading(false);
        setLargeValueLoadingAction(null);
      }
    }
  };

  const download = async () => {
    if (!canUseLargeValueActions || largeValueExpired || !largeValueMeta?.largeValueId || largeValueDownloading) {
      return;
    }
    setLargeValueDownloading(true);
    try {
      await downloadLargeCellValue(largeValueMeta.largeValueId, getLargeCellDownloadFormat(displayMode));
    } catch (error: any) {
      setLargeValueExpired(isLargeCellTokenExpiredError(error));
      setLargeValueError(getLargeCellErrorMessage(error, LARGE_CELL_ERROR_MESSAGE.DOWNLOAD_FAILED));
    } finally {
      setLargeValueDownloading(false);
    }
  };

  const changeBinaryFormat = (value: LargeCellBinaryFormat) => {
    if (!canUseLargeValueActions || largeValueExpired) {
      return;
    }
    setBinaryFormat(value);
    setLargeValueChunks([]);
    if (largeValueMeta?.largeValueId) {
      loadLargeValueChunk(largeValueMeta.largeValueId, 0, false, undefined, displayMode, value, 'format');
    }
  };

  const formatJson = () => {
    try {
      const parsed = JSON.parse(monacoEditorRef.current?.getAllContent() || '{}');
      const formatted = JSON.stringify(parsed, null, 2);
      monacoEditorRef.current?.setValue(formatted, 'cover');
    } catch (err) {
      console.error('无效的 JSON 格式，请检查语法', err);
    }
  };

  const compressJson = () => {
    try {
      const parsed = JSON.parse(monacoEditorRef.current?.getAllContent() || '{}');
      const compressed = JSON.stringify(parsed);
      monacoEditorRef.current?.setValue(compressed, 'cover');
    } catch (err) {
      console.error('无效的 JSON 格式，请检查语法', err);
    }
  };

  const renderMonacoEditor = useMemo(() => {
    return (
      <div className={styles.monacoEditor}>
        {showToolbar && (
          <div className={styles.toolbar}>
            <Space size={8}>
              {canShowJsonTools && (
                <>
                  <ToolbarBtn text={i18n('workspace.format.json')} onClick={formatJson} />
                  <ToolbarBtn text={i18n('workspace.format.json.compress')} onClick={compressJson} />
                </>
              )}
              {canUseLargeValueActions && isBinaryDisplayMode(displayMode) && (
                <Segmented
                  size="small"
                  disabled={largeValueExpired || largeValueLoading}
                  value={binaryFormat}
                  options={[
                    { label: 'Hex', value: LARGE_CELL_BINARY_FORMAT.HEX },
                    { label: 'Base64', value: LARGE_CELL_BINARY_FORMAT.BASE64 },
                  ]}
                  onChange={(value) => changeBinaryFormat(value as LargeCellBinaryFormat)}
                />
              )}
            </Space>
            {canUseLargeValueActions && (
              <Space size={8}>
                <Button
                  size="small"
                  loading={
                    largeValueLoadingAction === 'initial' ||
                    largeValueLoadingAction === 'more' ||
                    largeValueLoadingAction === 'format'
                  }
                  disabled={
                    largeValueExpired || largeValueLoading || !!latestChunk?.eof || !largeValueMeta?.largeValueId
                  }
                  onClick={loadMore}
                >
                  {i18n('common.largeCellValue.button.loadMore')}
                </Button>
                <Button
                  size="small"
                  loading={largeValueLoadingAction === 'all'}
                  disabled={largeValueExpired || largeValueLoading || !canLoadAll || !largeValueMeta?.largeValueId}
                  onClick={loadAllUpToLimit}
                >
                  {i18n('common.largeCellValue.button.loadAllUpToLimit')}
                </Button>
                <Button
                  size="small"
                  loading={largeValueDownloading}
                  disabled={
                    largeValueExpired || largeValueLoading || largeValueDownloading || !largeValueMeta?.largeValueId
                  }
                  onClick={download}
                >
                  {i18n('common.largeCellValue.button.download')}
                </Button>
              </Space>
            )}
          </div>
        )}
        {largeValueError && <Alert className={styles.alert} type="warning" showIcon message={largeValueError} />}
        {canUseLargeValueActions && (
          <div className={styles.meta}>
            {largeValueStatus}
          </div>
        )}
        {hasReachedEditorLimit && (
          <Alert
            className={styles.alert}
            type="info"
            showIcon
            message={i18n('common.largeCellValue.tip.editorLimit')}
          />
        )}
        {imagePreviewSrc && (
          <div className={styles.imagePreview}>
            <img src={imagePreviewSrc} alt={viewData?.field || i18n('common.largeCellValue.imageAlt')} />
          </div>
        )}
        <div className={styles.editorContainer}>
          <MonacoEditor
            ref={monacoEditorRef}
            id={uuid}
            language={getEditorLanguage(displayMode)}
            didMount={handleEditorDidMount}
            options={{
              lineNumbers: 'off',
              readOnly: !canSubmitEdit,
            }}
          />
        </div>
      </div>
    );
  }, [
    isJsonContent,
    isLargeValue,
    canUseLargeValueActions,
    canShowJsonTools,
    latestChunk,
    largeValueMeta,
    largeValueLoading,
    largeValueLoadingAction,
    largeValueDownloading,
    largeValueExpired,
    largeValueError,
    loadedSize,
    largeValueStatus,
    displayMode,
    binaryFormat,
    canLoadAll,
    imagePreviewSrc,
    canSubmitEdit,
    hasReachedEditorLimit,
    showToolbar,
  ]);

  const monacoEditorEditData = () => {
    if (!viewData) return;
    const { row, col, tableInstance, cellMeta, operationRecordUtils } = viewData;
    if (cellMeta?.largeValue && !canSubmitEdit) {
      return;
    }
    const data = monacoEditorRef.current?.getAllContent() ?? '';
    const originData = tableInstance.getRecordByCell(col, row);
    const originalLargeValue = cellMeta?.largeValue ? originalLargeValueRef.current : null;
    const currentValue = originalLargeValue?.value ?? tableInstance.getCellOriginValue(col, row);
    const restoreValue = originalLargeValue?.restoreValue;
    const restoreCellMeta = cloneCellMeta(originalLargeValue?.cellMeta);
    const hasChanged = currentValue !== data;
    if (originData) {
      originData[col] = hasChanged ? data : (restoreValue ?? data);
      const nextCellMeta = originData.__CHAT2DB_CELL_META__?.[col];
      if (hasChanged && nextCellMeta) {
        nextCellMeta.value = data;
        if (nextCellMeta.largeValue) {
          nextCellMeta.largeValue = false;
          nextCellMeta.largeValueId = undefined;
          nextCellMeta.loadedBytes = undefined;
          nextCellMeta.loadedChars = undefined;
          nextCellMeta.truncated = false;
          nextCellMeta.unsupportedReason = undefined;
        }
      }
    }
    tableInstance.changeCellValue(col, row, hasChanged ? data : (restoreValue ?? data));
    if (!hasChanged && originData && restoreCellMeta) {
      originData.__CHAT2DB_CELL_META__[col] = restoreCellMeta;
    }
    if (operationRecordUtils && originData && hasChanged) {
      operationRecordUtils.handleCellValueChange({
        field: String(tableInstance.getHeaderField(col, row)),
        rowId: originData.CHAT2DB_ROW_NUMBER,
        rawValue: currentValue,
        currentValue,
        changedValue: data,
        restoreValue,
        restoreCellMeta,
      });
    }
    setViewData(null);
    props.onClose?.();
  };

  const openPanel = (params) => {
    if (!params) return;
    const { col, tableInstance } = params;
    const field = tableInstance?.columns?.[col - 1]?.title || col;
    setViewData({ ...params, field });
  };

  useImperativeHandle(
    ref,
    () => ({
      openPanel,
    }),
    [],
  );

  if (!viewData) {
    return null;
  }

  return (
    <div className={styles.container}>
      {renderMonacoEditor}
      {viewData.canEdit && (
        <div className={styles.footer}>
          <Button type="primary" disabled={!canSubmitEdit} onClick={monacoEditorEditData}>
            {i18n('common.button.modify')}
          </Button>
        </div>
      )}
    </div>
  );
});

export default memo(ViewData);
