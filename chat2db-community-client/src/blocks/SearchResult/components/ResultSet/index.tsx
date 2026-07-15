import { memo, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useStyles } from './style';
import ResultSetToolbar, { ResultSetToolbarRef, ToolbarOperationType } from '../ResultSetToolbar';
import ScreeningResult, { IScreeningResultRef } from '../ScreeningResult';
import FESearch, { FESearchRef } from '../FESearch';
import ResultSetTable, { ResultSetTableRef } from '../ResultSetTable';
import useSqlExecutor from '@/hooks/useSqlExecutor';
import executeSql from '@/service/executeSql';
import SQLPreviewExecute, { SQLPreviewExecuteRef } from '../SQLPreviewExecute';
import ViewData, { ViewDataRef } from '../ViewData';
import RowDetail, { RowDetailRef } from '../RowDetail';
import { IManageResultData } from '@/typings';
import { Spin } from 'antd';
import i18n from '@/i18n';
import { copyToClipboard } from '@/utils';
import StatusBar from '../StatusBar';
import { getBlankCreateCellValue, transformOperations } from '@/blocks/SearchResult/utils';
import MonacoEditorErrorTips from '@/components/SQLEditor/components/MonacoEditorErrorTips';
import { v4 as uuidv4 } from 'uuid';
import { ITableInstance } from '@/blocks/CanvasTable/typings';
import {
  ShortcutAction,
  ShortcutOverrides,
  getEffectiveShortcutConfigMap,
  isShortcutEventMatch,
} from '@/constants/shortcut';
import { useGlobalStore } from '@/store/global';
import { staticMessage } from '@chat2db/ui';

interface IProps {
  resultData: IManageResultData;
  viewTable?: boolean;
}

export default memo<IProps>(
  (props) => {
    const { viewTable } = props;
    const { styles, cx } = useStyles();
    const { executeSQL, stopExecuteSQL, executing } = useSqlExecutor();
    const [resultData, setResultData] = useState<IManageResultData>(props.resultData);
    const resultSetToolbarRef = useRef<ResultSetToolbarRef>(null);
    const screenResultRef = useRef<IScreeningResultRef>(null);
    const resultSetTableRef = useRef<ResultSetTableRef>(null);
    const [hasOperationRecord, setHasOperationRecord] = useState(false);
    const sqlPreviewExecuteRef = useRef<SQLPreviewExecuteRef>(null);
    const viewDataRef = useRef<ViewDataRef>(null);
    const rowDetailRef = useRef<RowDetailRef>(null);
    const [executeErrorMessage, setExecuteErrorMessage] = useState<string | null>(null);
    const [tableInstance, setTableInstance] = useState<ITableInstance | null>(null);
    const [showFESearch, setShowFESearch] = useState(true);
    const [activeFilterCount, setActiveFilterCount] = useState(0);
    const resultSetRef = useRef<HTMLDivElement>(null);
    const searchAreaId = useMemo(() => uuidv4(), []);
    const feSearchRef = useRef<FESearchRef>(null);
    const [orderByText, setOrderByText] = useState<string>('');
    const [submitLoading, setSubmitLoading] = useState(false);
    const shortcutOverrides = useGlobalStore((s) => s.shortcutOverrides);
    const shortcutConfig = useMemo(
      () => getEffectiveShortcutConfigMap(shortcutOverrides as ShortcutOverrides),
      [shortcutOverrides],
    );

    useEffect(() => {
      setResultData(props.resultData);
    }, [props.resultData]);

    // Only resultData changes here. Database metadata is stable, and the toolbar controls pagination.
    const handleExecuteSQL = useCallback(
      ({ pageNo: _pageNo }: { pageNo?: number } = {}) => {
        // Clear operation records
        resultSetTableRef.current?.operationRecordUtils?.clearOperationRecord?.();
        // Do not execute before the result toolbar is mounted.
        if (!resultSetToolbarRef.current) return;
        // If there is no executeSqlParams, the execution information is not known, and no execution is performed.
        if (!resultData.executeSqlParams) return;
        // Get the current paging
        const { pageNo, pageSize } = resultSetToolbarRef.current.getPagingParams();
        const executeSqlParams = {
          ...resultData.executeSqlParams,
          pageSize,
          pageNo: _pageNo || pageNo,
        };
        // Filter conditions when viewing tables
        if (viewTable) {
          executeSqlParams.sql = screenResultRef.current?.getJointSQL() || '';
        }
        executeSQL(executeSqlParams).then((data) => {
          setExecuteErrorMessage(null);
          if (data.length) {
            const curResult = data.filter((item) => item.resultSetId === executeSqlParams.resultSetId)?.[0];
            if (curResult) {
              setResultData({
                ...curResult,
                executeSqlParams: {
                  ...resultData.executeSqlParams,
                  sql: curResult.originalSql,
                },
              });
            } else {
              setExecuteErrorMessage(data[0].message || '');
            }
          }
        });
      },
      [resultData],
    );

    const handleSearch = useCallback(() => {
      handleExecuteSQL({ pageNo: 1 });
    }, [handleExecuteSQL]);

    const completeActiveEditor = useCallback(async () => {
      await Promise.resolve(resultSetTableRef.current?.tableInstance?.completeEditCell?.());
      await new Promise((resolve) => setTimeout(resolve, 0));
    }, []);

    const handleUpdateSubmit = useCallback(() => {
      completeActiveEditor().then(() => {
        const operations = resultSetTableRef.current?.operationRecordUtils?.getOperationChangeDetail();
        sqlPreviewExecuteRef.current?.handleExecuteSql({
          operations: transformOperations(operations, resultData.headerList),
          resultData,
          callback: setSubmitLoading,
        });
      });
    }, [completeActiveEditor, resultData]);

    useEffect(() => {
      const handleKeyDown = (e: KeyboardEvent) => {
        if (e.key === 'Escape') {
          feSearchRef.current?.close();
          return;
        }
        if (isShortcutEventMatch(e, shortcutConfig[ShortcutAction.ResultSearch].binding)) {
          setShowFESearch(true);
          e.preventDefault();
          setTimeout(() => {
            feSearchRef.current?.focus();
          });
        }
        if (isShortcutEventMatch(e, shortcutConfig[ShortcutAction.ResultSubmit].binding)) {
          e.preventDefault();
          if (hasOperationRecord) {
            handleUpdateSubmit();
          }
        }
        if (isShortcutEventMatch(e, shortcutConfig[ShortcutAction.ResultRefresh].binding)) {
          e.preventDefault();
          handleSearch();
        }
      };
      const resultSetContent = resultSetRef.current;
      resultSetContent?.addEventListener('keydown', handleKeyDown);
      return () => {
        resultSetContent?.removeEventListener('keydown', handleKeyDown);
      };
    }, [hasOperationRecord, handleSearch, handleUpdateSubmit, shortcutConfig]);

    // SQL execution successful
    const handleExecuteSuccess = useCallback(() => {
      setExecuteErrorMessage(null);
      handleExecuteSQL();
    }, [handleExecuteSQL]);

    // SQL execution failed
    const handleExecuteError = useCallback((errorMessage) => {
      setExecuteErrorMessage(errorMessage);
    }, []);

    // Close SQL execution failure prompt
    const handleCloseExecuteErrorMessage = useCallback(() => {
      setExecuteErrorMessage(null);
    }, []);

    const handleAddBlankRow = useCallback(() => {
      // creates blank rows of data
      const blankRow: any = {};
      const uuid = uuidv4();
      resultData.headerList.forEach((item, index) => {
        if (index === 0) {
          blankRow.CHAT2DB_ROW_NUMBER = uuid;
          return;
        }
        blankRow[index] = getBlankCreateCellValue(item);
      });
      resultSetTableRef.current?.operationRecordUtils?.handleAddBlankRow(blankRow, uuid);
    }, [resultData.headerList]);

    const handleDeleteRow = useCallback(() => {
      resultSetTableRef.current?.operationRecordUtils?.handleDeleteRow();
    }, []);

    const handleRevocation = useCallback(() => {
      resultSetTableRef.current?.operationRecordUtils?.handleRevocation();
    }, []);

    const handleOperationChange = useCallback((_hasOperationRecord) => {
      setHasOperationRecord(_hasOperationRecord);
    }, []);

    const handleViewSQl = () => {
      completeActiveEditor().then(() => {
        const operations = resultSetTableRef.current?.operationRecordUtils?.getOperationChangeDetail();
        sqlPreviewExecuteRef.current?.handleViewSQL({
          operations: transformOperations(operations, resultData.headerList),
          resultData,
        });
      });
    };

    const handleToolbarOperation = (type: ToolbarOperationType) => {
      switch (type) {
        // execute SQL
        case ToolbarOperationType.EXECUTE_SQL:
          handleExecuteSQL();
          break;
        // Add blank line
        case ToolbarOperationType.ADD_BLANK_ROW:
          handleAddBlankRow();
          break;
        // Delete row
        case ToolbarOperationType.DELETE_ROW:
          handleDeleteRow();
          break;
        // Cancel
        case ToolbarOperationType.REVOKE:
          handleRevocation();
          break;
        // View SQL
        case ToolbarOperationType.VIEW_SQL:
          handleViewSQl();
          break;
        // update submission
        case ToolbarOperationType.UPDATE_SUBMIT:
          handleUpdateSubmit();
          break;
        default:
          break;
      }
    };

    const onTableOperationUtils = useMemo(() => {
      return {
        // Copy as insert or update or where statement
        copyGenerateSQL: (operations: any) => {
          executeSql
            .getCopyUpdateDataSql({
              ...(resultData.executeSqlParams || {}),
              tableName: resultData.tableName,
              headerList: resultData.headerList,
              operations: transformOperations(operations, resultData.headerList),
            })
            .then((sql) => {
              copyToClipboard(sql);
            });
        },
        copyGenerateInValues: (operations: any) => {
          executeSql
            .getCopyInValuesSql({
              ...(resultData.executeSqlParams || {}),
              headerList: resultData.headerList,
              sourceType: 'RESULT_SET',
              operations: transformOperations(operations, resultData.headerList),
            })
            .then((sql) => {
              if (copyToClipboard(sql)) {
                staticMessage.success(i18n('common.button.copySuccessfully'));
              } else {
                staticMessage.warning(i18n('common.sqlInValues.copyFailed'));
              }
            });
        },
        handleViewUpdateData: (params) => {
          viewDataRef.current?.openModal({
            ...params,
            canEdit: !!resultData?.canEdit,
            operationRecordUtils: resultSetTableRef.current?.operationRecordUtils,
          });
        },
        handleViewRowDetail: (params) => {
          rowDetailRef.current?.openModal(params);
        },
      };
    }, [resultData]);

    const handleCloseFESearch = useCallback(() => {
      setShowFESearch(false);
    }, []);

    const handleClearAllFilters = useCallback(() => {
      resultSetTableRef.current?.clearAllFilters?.();
    }, []);

    return (
      <>
        <div tabIndex={0} className={cx(styles.container)} ref={resultSetRef} id={searchAreaId}>
          {(executing || submitLoading) && (
            <div className={styles.tableLoading}>
              <Spin />
              {executing && (
                <div className={styles.stopExecuteSql} onClick={stopExecuteSQL}>
                  {i18n('common.button.cancelRequest')}
                </div>
              )}
            </div>
          )}
          <>
            <ResultSetToolbar
              ref={resultSetToolbarRef}
              handleToolbarOperation={handleToolbarOperation}
              hasOperationRecord={hasOperationRecord}
              resultData={resultData}
              activeFilterCount={activeFilterCount}
              onClearAllFilters={handleClearAllFilters}
            />
            {viewTable && (
              <ScreeningResult
                ref={screenResultRef}
                onSearch={handleSearch}
                originalSql={props.resultData.originalSql}
                promptWord={resultData.headerList}
                orderByText={orderByText}
              />
            )}
            {showFESearch && (
              <FESearch
                ref={feSearchRef}
                searchAreaId={searchAreaId}
                onClose={handleCloseFESearch}
                tableInstance={tableInstance}
              />
            )}
            <div className={styles.resultSetTableContainer}>
              <ResultSetTable
                tableInstance={tableInstance}
                setTableInstance={setTableInstance}
                ref={resultSetTableRef}
                resultData={resultData}
                setOrderByText={setOrderByText}
                onOperationChange={handleOperationChange}
                onTableOperationUtils={onTableOperationUtils}
                onFilterCountChange={setActiveFilterCount}
              />
            </div>
            <StatusBar resultData={resultData} />
          </>
          <MonacoEditorErrorTips errorMessage={executeErrorMessage} handleClose={handleCloseExecuteErrorMessage} />
        </div>
        <SQLPreviewExecute
          onExecuteError={handleExecuteError}
          onExecuteSuccess={handleExecuteSuccess}
          ref={sqlPreviewExecuteRef}
        />
        <ViewData ref={viewDataRef} />
        <RowDetail
          ref={rowDetailRef}
          resultData={resultData}
          onViewFullValue={(params) => {
            viewDataRef.current?.openModal({
              ...params,
              canEdit: !!resultData?.canEdit,
              operationRecordUtils: resultSetTableRef.current?.operationRecordUtils,
            });
          }}
        />
      </>
    );
  },
  (prevProps, nextProps) =>
    prevProps.resultData === nextProps.resultData && prevProps.viewTable === nextProps.viewTable,
);
