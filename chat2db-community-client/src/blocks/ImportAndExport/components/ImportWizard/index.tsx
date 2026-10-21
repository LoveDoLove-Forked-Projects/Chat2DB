import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Alert, Button, Descriptions, Modal, Select, Spin } from 'antd';
import { TriangleAlert } from 'lucide-react';
import { staticMessage } from '@chat2db/ui';
import i18n from '@/i18n';
import sqlService, { type IImportPreview } from '@/service/sql';
import importExportServices, { type ImportTaskParams } from '@/service/importExport';
import {
  ImportExportFileType,
  ImportPreviewErrorCode,
  ImportUnmappedTarget,
  SKIP_IMPORT_SOURCE_FIELD,
} from '@/constants/importExport';
import type {
  ICsvOptions,
  IExcelOptions,
  IJsonOptions,
  ImportDuplicatePolicy,
  ImportExecutionMode,
  ImportFailurePolicy,
  ISqlImportOptions,
} from '@/typings/importExport';
import { useImportExportStore } from '@/store/importExport';
import type { RestoredTaskSpec } from '@/blocks/ImportAndExport/utils/restoreTaskSpec';
import {
  DEFAULT_EXCEL_OPTIONS,
  DEFAULT_JSON_OPTIONS,
  DEFAULT_SQL_IMPORT_OPTIONS,
} from '@/blocks/ImportAndExport/utils/importOptions';
import {
  buildCsvOptionsForTaskSubmit,
  DEFAULT_CSV_OPTIONS,
  inferImportFileFormat,
} from '@/blocks/ImportAndExport/utils/csvOptions';
import {
  deleteImportTemplate,
  filterImportTemplatesByFileType,
  loadImportTemplates,
  saveImportTemplate,
  templateOptionKey,
  type ImportTemplate,
  type ImportTemplateFormatOptions,
} from '@/blocks/ImportAndExport/utils/importTemplates';
import {
  canPressImportWizardNext,
  IMPORT_PREVIEW_AUTO_LOAD_DELAY_MS,
  IMPORT_WIZARD_FORMAT_STEP,
  IMPORT_WIZARD_LAST_STEP,
  isImportWizardLastStep,
  shouldAutoLoadImportPreview,
  shouldLoadImportPreviewOnAdvance,
  type ImportWizardPreviewStatus,
} from '@/blocks/ImportAndExport/utils/importWizardSteps';
import { planImportTargetTableSwitch } from '@/blocks/ImportAndExport/utils/importTargetTables';
import {
  resolveImportPoliciesForTarget,
  supportsDuplicateIgnore,
} from '@/blocks/ImportAndExport/utils/importPolicies';
import {
  buildInitialImportMapping,
  getDuplicateImportMappings,
  getImportPreviewErrorMessage,
} from '../ImportMappingContent/mapping';
import { stageSelectedImportFile } from '../ImportMappingContent/fileStaging';
import useImportDataSections from '../ImportMappingContent/ImportDataSections';
import CsvOptionsSections from '../ImportMappingContent/CsvOptionsSections';
import ExcelOptionsSections from '../ImportMappingContent/ExcelOptionsSections';
import JsonOptionsSections from '../ImportMappingContent/JsonOptionsSections';
import SqlImportOptionsFields from '../SqlImportOptionsFields';
import ImportModeControl from '../ImportModeControl';
import ImportExportFile, { type ImportExportFileRef } from '../ImportExportFile';
import { prepareWebImportParams } from '../ImportFileModal/submission';
import type { FileUrl } from '@/components/UploadLocalFile';
import { isDesktop } from '@/utils/env';
import ImportTemplateControls from './ImportTemplateControls';
import ImportTemplateSelect from './ImportTemplateSelect';
import TargetTableSelect from './TargetTableSelect';
import { useStyles } from './style';

interface IProps {
  /** Prefill produced by "new from this configuration"; null for a regular import. */
  restoredSpec?: RestoredTaskSpec | null;
  onSubmitted: (taskId: number) => void;
  /** Reports the visible step so the owner can name it in the dialog title. */
  onStepChange?: (step: number) => void;
}

/** The one option group the current file format sends to the preview and execute endpoints. */
interface ResolvedFileOptions {
  csvOptions?: ICsvOptions;
  jsonOptions?: IJsonOptions;
  excelOptions?: IExcelOptions;
}

/** Formats the preview endpoint can parse into rows and columns. */
const isPreviewableFileName = (fileName: string) => /\.(csv|xls|xlsx|json)$/i.test(fileName);

const CSV_CHARACTER_I18N_KEYS: Record<string, Parameters<typeof i18n>[0]> = {
  ',': 'workspace.importExport.delimiterComma',
  ';': 'workspace.importExport.delimiterSemicolon',
  '\t': 'workspace.importExport.delimiterTab',
  '|': 'workspace.importExport.delimiterPipe',
  '"': 'workspace.importExport.quoteDouble',
  "'": 'workspace.importExport.quoteSingle',
  '`': 'workspace.importExport.quoteBacktick',
  '~': 'workspace.importExport.quoteTilde',
  '\\': 'workspace.importExport.escapeBackslash',
};

const describeCharacter = (value: string) => {
  const key = CSV_CHARACTER_I18N_KEYS[value];
  return key ? i18n(key) : value;
};

/**
 * Four-step import wizard: file, format options, mapping + preview, confirmation.
 *
 * The format step owns the preview request: it is sent when the step is entered or an option
 * changes, and the mapping step reuses it while the configuration stays the same. The file step can
 * switch the target table, which drops the mapping of the previous table and asks for a new preview.
 * Mapping templates live in the backend user data directory through `utils/importTemplates`.
 */
export default function ImportWizard({ restoredSpec, onSubmitted, onStepChange }: IProps) {
  const { styles } = useStyles();
  const [modal, modalContextHolder] = Modal.useModal();
  const importExportFileRef = useRef<ImportExportFileRef>(null);
  const stagingRef = useRef<{ file: FileUrl; promise: Promise<string> } | null>(null);
  /** Answers of preview requests of a target table the user already left are ignored. */
  const previewRequestGenerationRef = useRef(0);
  /** Configuration key of the preview the wizard requested by itself, so it is not retried. */
  const autoPreviewKeyRef = useRef<string>();
  const autoPreviewTimerRef = useRef<ReturnType<typeof setTimeout>>();
  const { importExportDataBoundInfo, setImportExportDataBoundInfo } = useImportExportStore((state) => ({
    importExportDataBoundInfo: state.importExportDataBoundInfo,
    setImportExportDataBoundInfo: state.setImportExportDataBoundInfo,
  }));

  const [step, setStep] = useState(0);
  const [importFile, setImportFile] = useState<FileUrl>();
  // The bound table is only the starting point: the file step can switch the import target.
  const [tableName, setTableName] = useState(() => importExportDataBoundInfo?.tableName || '');
  const [targetTableNotice, setTargetTableNotice] = useState<{ tableName: string } | null>(null);
  /** Template the user applied on the format step, reported by the confirmation summary. */
  const [appliedTemplateName, setAppliedTemplateName] = useState<string | null>(null);
  const [fileType, setFileType] = useState<ImportExportFileType>(
    () => restoredSpec?.formValue.exportType ?? ImportExportFileType.CSV,
  );
  const [sqlImportOptions, setSqlImportOptions] = useState<ISqlImportOptions>(
    () => restoredSpec?.sqlImportOptions ?? DEFAULT_SQL_IMPORT_OPTIONS,
  );
  const [csvOptions, setCsvOptions] = useState<ICsvOptions>(DEFAULT_CSV_OPTIONS);
  const [excelOptions, setExcelOptions] = useState(DEFAULT_EXCEL_OPTIONS);
  const [jsonOptions, setJsonOptions] = useState(DEFAULT_JSON_OPTIONS);
  const [fileId, setFileId] = useState<string>();
  const [sheets, setSheets] = useState<string[]>([]);
  const [stagingError, setStagingError] = useState<string | null>(null);
  const [preview, setPreview] = useState<IImportPreview | null>(null);
  const [previewError, setPreviewError] = useState<string | null>(null);
  const [loadingPreview, setLoadingPreview] = useState(false);
  const [loadedPreviewKey, setLoadedPreviewKey] = useState<string>();
  const [mapping, setMapping] = useState<Record<string, string>>({});
  const [unmappedTarget, setUnmappedTarget] = useState(ImportUnmappedTarget.DEFAULT);
  const [mode, setMode] = useState<ImportExecutionMode>('STANDARD');
  const [duplicatePolicy, setDuplicatePolicy] = useState<ImportDuplicatePolicy>('INSERT');
  const [failurePolicy, setFailurePolicy] = useState<ImportFailurePolicy>('ABORT');
  const [executing, setExecuting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [templates, setTemplates] = useState<ImportTemplate[]>([]);

  const dataSourceId = importExportDataBoundInfo?.dataSourceId;
  const databaseType = importExportDataBoundInfo?.databaseType;
  const databaseName = importExportDataBoundInfo?.databaseName;
  const schemaName = importExportDataBoundInfo?.schemaName;
  const dataSourceName = importExportDataBoundInfo?.dataSourceName;
  const selectedFileName = importFile?.fileName || importFile?.file?.name || importFile?.filePath || '';
  const fileFormat = selectedFileName ? inferImportFileFormat(selectedFileName) : fileType;
  const isCsv = fileFormat === ImportExportFileType.CSV;
  const isJson = fileFormat === ImportExportFileType.JSON;
  const isExcel = fileFormat === ImportExportFileType.XLS || fileFormat === ImportExportFileType.XLSX;
  const isSql = fileFormat === ImportExportFileType.SQL;
  const previewRequired = importFile != null && isPreviewableFileName(selectedFileName);
  const emptyAsNull = isCsv ? csvOptions.emptyAsNull : isJson ? jsonOptions.emptyAsNull : excelOptions.emptyAsNull;
  const setEmptyAsNull = (value: boolean) => {
    if (isCsv) setCsvOptions((current) => ({ ...current, emptyAsNull: value }));
    else if (isJson) setJsonOptions((current) => ({ ...current, emptyAsNull: value }));
    else setExcelOptions((current) => ({ ...current, emptyAsNull: value }));
  };
  const resolveErrorMessage = useCallback(
    (requestError: unknown) =>
      getImportPreviewErrorMessage(requestError, i18n('workspace.importExport.previewFailed'), {
        [ImportPreviewErrorCode.DUPLICATE_SOURCE_COLUMNS]: i18n('workspace.importExport.duplicateSourceColumns'),
        [ImportPreviewErrorCode.INVALID_CSV_OPTIONS]: i18n('workspace.importExport.invalidCsvOptions'),
      }),
    [],
  );
  const resolveFileOptions = useCallback(
    (overrides?: ImportTemplateFormatOptions): ResolvedFileOptions => {
      if (isCsv) {
        return { csvOptions: overrides?.csvOptions ?? csvOptions };
      }
      if (isJson) {
        return { jsonOptions: overrides?.jsonOptions ?? jsonOptions };
      }
      return { excelOptions: overrides?.excelOptions ?? excelOptions };
    },
    [isCsv, isJson, csvOptions, jsonOptions, excelOptions],
  );
  const fileOptions = useMemo(() => resolveFileOptions(), [resolveFileOptions]);
  const mappingParams = useMemo(
    () =>
      Object.entries(mapping)
        .filter(([, target]) => target && target !== SKIP_IMPORT_SOURCE_FIELD)
        .map(([sourceColumn, targetColumn]) => ({ sourceColumn, targetColumn })),
    [mapping],
  );
  const currentPreviewKey = useMemo(
    () => JSON.stringify({ dataSourceId, databaseName, schemaName, tableName, fileId, ...fileOptions }),
    [dataSourceId, databaseName, schemaName, tableName, fileId, fileOptions],
  );
  const previewReady = preview !== null && loadedPreviewKey === currentPreviewKey;
  const previewStatus: ImportWizardPreviewStatus = !previewRequired
    ? 'loaded'
    : previewReady
    ? 'loaded'
    : loadingPreview
    ? 'loading'
    : previewError || stagingError
    ? 'failed'
    : 'idle';
  const gateState = { fileSelected: importFile != null, previewRequired, previewStatus };
  // Stage the file as soon as it is picked: the preview and the sheet list both need a file id,
  // and staging earlier is what lets the format step list worksheets.
  useEffect(() => {
    if (!importFile || !previewRequired) {
      stagingRef.current = null;
      setFileId(undefined);
      setSheets([]);
      setStagingError(null);
      setPreview(null);
      setPreviewError(null);
      setLoadedPreviewKey(undefined);
      setMapping({});
      setAppliedTemplateName(null);
      return;
    }
    let active = true;
    const promise = stageSelectedImportFile(importFile, sqlService.uploadImportFile, sqlService.stageDesktopImportFile);
    stagingRef.current = { file: importFile, promise };
    setFileId(undefined);
    setSheets([]);
    setStagingError(null);
    setPreview(null);
    setPreviewError(null);
    setLoadedPreviewKey(undefined);
    setMapping({});
    setAppliedTemplateName(null);
    setMode('STANDARD');
    promise
      .then((stagedFileId) => {
        if (active) {
          setFileId(stagedFileId);
        }
      })
      .catch((requestError) => {
        if (active) {
          setStagingError(resolveErrorMessage(requestError));
        }
      });
    return () => {
      active = false;
    };
  }, [importFile, previewRequired, resolveErrorMessage]);

  useEffect(() => {
    const resolved = resolveImportPoliciesForTarget(restoredSpec?.importPolicies, databaseType);
    setDuplicatePolicy(resolved.duplicatePolicy);
    setFailurePolicy(resolved.failurePolicy);
  }, [restoredSpec, databaseType]);

  useEffect(() => {
    if (!fileId || !isExcel || dataSourceId == null || !databaseName) {
      return;
    }
    let active = true;
    sqlService
      .getImportSheets({ dataSourceId, databaseName, schemaName, fileId })
      .then((names) => {
        if (active) setSheets(names);
      })
      .catch(() => {
        // Worksheets only refine the selection; the preview below already reports file problems.
        if (active) setSheets([]);
      });
    return () => {
      active = false;
    };
  }, [fileId, isExcel, dataSourceId, databaseName, schemaName]);

  // Presets belong to one file type, so the wizard asks for the type of the file it is importing;
  // switching the file asks again instead of offering presets that cannot apply.
  useEffect(() => {
    let active = true;
    void loadImportTemplates({ fileType: fileFormat }).then((stored) => {
      if (active) {
        setTemplates(stored);
      }
    });
    return () => {
      active = false;
    };
  }, [fileFormat]);

  /** Drops the answer of a preview request that belongs to a target table the user has left. */
  const invalidatePendingPreview = () => {
    previewRequestGenerationRef.current += 1;
    setLoadingPreview(false);
  };

  /**
   * Sends the preview request once per file plus options. Returns the loaded preview, or null when
   * the request failed, so the caller can keep the user on the format step instead of letting them
   * pass a broken configuration to the mapping step.
   */
  const loadPreview = useCallback(
    async (
      overrides?: ImportTemplateFormatOptions,
      // A target table switch has to name the new table before its state update is visible here.
      tableNameOverride?: string,
    ): Promise<IImportPreview | null> => {
      if (!previewRequired || dataSourceId == null || !databaseName) {
        return null;
      }
      const requestTableName = tableNameOverride ?? tableName;
      let stagedFileId = fileId;
      const pendingStaging = stagingRef.current;
      if (pendingStaging && pendingStaging.file === importFile) {
        try {
          stagedFileId = await pendingStaging.promise;
        } catch {
          stagedFileId = undefined;
        }
      }
      if (!stagedFileId) {
        setPreviewError(stagingError ?? i18n('workspace.importExport.wizard.previewUnavailable'));
        return null;
      }
      const requestOptions = resolveFileOptions(overrides);
      const requestKey = JSON.stringify({
        dataSourceId,
        databaseName,
        schemaName,
        tableName: requestTableName,
        fileId: stagedFileId,
        ...requestOptions,
      });
      if (preview && loadedPreviewKey === requestKey) {
        return preview;
      }
      let validatedCsvOptions: ICsvOptions | undefined;
      try {
        validatedCsvOptions = buildCsvOptionsForTaskSubmit(isCsv, requestOptions.csvOptions ?? csvOptions);
      } catch (requestError) {
        setPreview(null);
        setPreviewError(resolveErrorMessage(requestError));
        return null;
      }
      const generation = ++previewRequestGenerationRef.current;
      setLoadingPreview(true);
      setPreviewError(null);
      try {
        const data = await sqlService.getImportPreview({
          dataSourceId,
          databaseName,
          schemaName,
          tableName: requestTableName,
          fileId: stagedFileId,
          ...requestOptions,
          csvOptions: validatedCsvOptions,
        });
        if (generation !== previewRequestGenerationRef.current) {
          return null;
        }
        setPreview(data);
        setLoadedPreviewKey(requestKey);
        setMapping(buildInitialImportMapping(data.sourceColumns, data.suggestedMapping));
        return data;
      } catch (requestError) {
        if (generation !== previewRequestGenerationRef.current) {
          return null;
        }
        setPreview(null);
        setLoadedPreviewKey(undefined);
        setMapping({});
        setPreviewError(resolveErrorMessage(requestError));
        return null;
      } finally {
        if (generation === previewRequestGenerationRef.current) {
          setLoadingPreview(false);
        }
      }
    },
    [
      previewRequired,
      dataSourceId,
      databaseName,
      schemaName,
      tableName,
      fileId,
      importFile,
      stagingError,
      preview,
      loadedPreviewKey,
      isCsv,
      resolveFileOptions,
      resolveErrorMessage,
    ],
  );

  /**
   * The format step can only be left with a loaded preview, so it requests one on entry and after
   * every option change (debounced). A configuration that already failed is not requested again
   * here: the error stays visible and the user retries it with the "next" button.
   */
  useEffect(() => {
    const attempt = { attemptedKey: autoPreviewKeyRef.current, currentPreviewKey };
    const state = { fileSelected: importFile != null, previewRequired, previewStatus };
    if (!shouldAutoLoadImportPreview(step, state, attempt)) {
      return;
    }
    autoPreviewKeyRef.current = currentPreviewKey;
    if (autoPreviewTimerRef.current) {
      clearTimeout(autoPreviewTimerRef.current);
    }
    autoPreviewTimerRef.current = setTimeout(() => {
      autoPreviewTimerRef.current = undefined;
      void loadPreview();
    }, IMPORT_PREVIEW_AUTO_LOAD_DELAY_MS);
  }, [step, importFile, previewRequired, previewStatus, currentPreviewKey, loadPreview]);

  useEffect(
    () => () => {
      if (autoPreviewTimerRef.current) {
        clearTimeout(autoPreviewTimerRef.current);
      }
    },
    [],
  );

  const blockedColumns = useMemo(() => {
    if (!preview) return [];
    return preview.targetColumns.filter(
      (column) =>
        !column.nullable &&
        !column.autoIncrement &&
        !Object.values(mapping).includes(column.name) &&
        (unmappedTarget === ImportUnmappedTarget.NULL ||
          (column.defaultValue === null && unmappedTarget === ImportUnmappedTarget.DEFAULT)),
    );
  }, [preview, mapping, unmappedTarget]);
  const duplicateMappings = getDuplicateImportMappings(mapping);
  const mappedTargetCount = Object.values(mapping).filter(
    (target) => target && target !== SKIP_IMPORT_SOURCE_FIELD,
  ).length;
  const mappedPairs = Object.entries(mapping)
    .filter(([, target]) => target && target !== SKIP_IMPORT_SOURCE_FIELD)
    .map(([source, target]) => `${source} → ${target}`);
  const dataSectionItems = useImportDataSections({
    preview,
    mapping,
    duplicateMappings,
    blockedColumnNames: new Set(blockedColumns.map(({ name }) => name)),
    unmappedTarget,
    emptyAsNull,
    emptyAsNullLabel: i18n(isJson ? 'workspace.importExport.emptyStringAsNull' : 'workspace.importExport.emptyAsNull'),
    loading: loadingPreview,
    disabled: executing,
    onMappingChange: (sourceColumn, targetColumn) =>
      setMapping((current) => ({ ...current, [sourceColumn]: targetColumn })),
    onUnmappedTargetChange: setUnmappedTarget,
    onEmptyAsNullChange: setEmptyAsNull,
  });
  // A failed preview keeps the button clickable so the user can retry it by hand.
  const canPressNext = canPressImportWizardNext(step, gateState);
  // The format step reports parsing problems inline next to its own retry button.
  const stepError =
    error ??
    (isImportWizardLastStep(step) || step === IMPORT_WIZARD_FORMAT_STEP ? null : previewError ?? stagingError);

  const handleImportFileChange = (file?: FileUrl) => {
    setImportFile(file);
    setError(null);
  };

  /**
   * The file step can switch the import target. Different target columns mean the mapping the user
   * picked for the previous table is dropped, the loaded preview is invalidated, and the preview of
   * the new table is requested again through the same path as the first one.
   */
  const handleTargetTableChange = (nextTableName: string) => {
    const plan = planImportTargetTableSwitch({
      previousTableName: tableName,
      nextTableName,
      fileSelected: importFile != null,
    });
    if (!plan.mappingCleared) {
      return;
    }
    invalidatePendingPreview();
    setTableName(plan.tableName);
    if (plan.previewReloadRequired) {
      // Clearing the attempt lets the format step ask for the new table; the old answer is dropped.
      autoPreviewKeyRef.current = undefined;
      setPreview(null);
      setLoadedPreviewKey(undefined);
      setPreviewError(null);
    }
    setMapping({});
    setAppliedTemplateName(null);
    setTargetTableNotice(plan.notifyMappingCleared ? { tableName: plan.tableName } : null);
  };

  const handleNext = async () => {
    if (executing || loadingPreview) {
      return;
    }
    if (shouldLoadImportPreviewOnAdvance(step, gateState)) {
      const loadedPreview = await loadPreview();
      if (!loadedPreview) {
        return;
      }
    }
    setTargetTableNotice(null);
    setStep((current) => Math.min(current + 1, IMPORT_WIZARD_LAST_STEP));
  };

  const currentTemplateFormatOptions = (): ImportTemplateFormatOptions | undefined => {
    if (isCsv) return { csvOptions };
    if (isJson) return { jsonOptions };
    if (isExcel) return { excelOptions };
    return undefined;
  };

  /**
   * The presets the format step may offer. The store already answers with the current file type;
   * filtering again drops the presets of other types a save or delete response still carries, and
   * keeps an unknown-type legacy preset applicable instead of losing it.
   */
  const visibleTemplates = useMemo(
    () => filterImportTemplatesByFileType(templates, fileFormat),
    [templates, fileFormat],
  );

  const applyTemplateFormatOptions = (formatOptions: ImportTemplateFormatOptions) => {
    if (formatOptions.csvOptions) {
      setCsvOptions((current) => ({ ...current, ...formatOptions.csvOptions }));
    }
    if (formatOptions.excelOptions) {
      setExcelOptions((current) => ({ ...current, ...formatOptions.excelOptions }));
    }
    if (formatOptions.jsonOptions) {
      setJsonOptions((current) => ({ ...current, ...formatOptions.jsonOptions }));
    }
  };

  /** A template is the option group of one file format plus the format it belongs to. */
  const handleSaveTemplate = (name: string) => {
    const template: ImportTemplate = {
      name,
      createdAt: Date.now(),
      fileType: fileFormat,
      formatOptions: currentTemplateFormatOptions(),
    };
    const persist = async (overwrite: boolean) => {
      const result = await saveImportTemplate(template, { overwrite });
      if (result.status === 'nameExists') {
        modal.confirm({
          title: i18n('workspace.importExport.template.overwriteTitle'),
          content: i18n('workspace.importExport.template.overwriteContent', name),
          okText: i18n('workspace.importExport.template.overwriteConfirm'),
          cancelText: i18n('common.button.cancel'),
          onOk: () => persist(true),
        });
        return;
      }
      if (result.status === 'invalidName' || result.status === 'storageUnavailable') {
        staticMessage.error(i18n('workspace.importExport.template.saveFailed'));
        return;
      }
      setTemplates(result.templates);
      staticMessage.success(
        i18n(
          result.status === 'overwritten'
            ? 'workspace.importExport.template.overwriteSuccessTyped'
            : 'workspace.importExport.template.saveSuccessTyped',
          fileFormat,
          name,
        ),
      );
    };
    void persist(false);
  };

  /**
   * Applies a stored format preset: the option controls of the current step take the saved values
   * and the file is parsed again through the ordinary preview path, so the mapping step previews
   * what the preset produces.
   */
  const handleApplyTemplate = async (template: ImportTemplate) => {
    if (executing) {
      return;
    }
    const formatOptions = template.formatOptions;
    // The type of a preset owns its option group; an unknown-type preset falls back to the group of
    // the file being imported.
    const formatKey = templateOptionKey(template.fileType) ?? templateOptionKey(fileFormat);
    if (!formatKey || !formatOptions?.[formatKey]) {
      // A preset belongs to one file format; applying it to another file would change nothing.
      staticMessage.warning(i18n('workspace.importExport.template.appliedNone'));
      return;
    }
    applyTemplateFormatOptions(formatOptions);
    const reloaded = await loadPreview(formatOptions);
    if (!reloaded) {
      return;
    }
    setAppliedTemplateName(template.name);
    staticMessage.success(i18n('workspace.importExport.template.appliedWithName', template.name));
  };

  const handleDeleteTemplate = (template: ImportTemplate) => {
    void deleteImportTemplate(template.name, { fileType: template.fileType }).then((result) => {
      if (result.status === 'storageUnavailable') {
        // The store rejected the delete: the template is still there, and the user has to know.
        staticMessage.error(i18n('workspace.importExport.template.deleteFailed'));
        return;
      }
      setTemplates(result.templates);
      staticMessage.success(i18n('workspace.importExport.template.deleteSuccess'));
    });
  };

  const submitScriptImport = async () => {
    const params = importExportFileRef.current?.getValues();
    if (!params || !('sourceFile' in params)) {
      return;
    }
    const selectedFile = importFile?.file;
    if (!isDesktop && !selectedFile) {
      setError(i18n('workspace.importExport.wizard.selectFileFirst'));
      return;
    }
    setExecuting(true);
    setError(null);
    try {
      const importParams: ImportTaskParams = selectedFile
        ? await prepareWebImportParams(params, selectedFile, sqlService.uploadImportFile)
        : params;
      const response = await importExportServices.submitImport(importParams);
      onSubmitted(response.taskId);
    } catch (requestError) {
      setError(resolveErrorMessage(requestError));
    } finally {
      setExecuting(false);
    }
  };

  const execute = async () => {
    if (executing) {
      return;
    }
    if (!previewRequired) {
      await submitScriptImport();
      return;
    }
    const duplicateMapping = Object.values(duplicateMappings)[0];
    if (duplicateMapping) {
      modal.error({
        title: i18n('workspace.importExport.duplicateMappingTitle'),
        content: i18n(
          'workspace.importExport.duplicateMappingContent',
          duplicateMapping.targetColumn,
          duplicateMapping.mappedSource,
        ),
      });
      return;
    }
    if (blockedColumns.length > 0) {
      modal.error({
        title: i18n('workspace.importExport.requiredUnmapped'),
        content: blockedColumns.map((column) => `${column.name} (${column.dataType})`).join(', '),
      });
      return;
    }
    if (dataSourceId == null || !databaseName || !fileId) {
      return;
    }
    let taskCsvOptions: ICsvOptions | undefined;
    try {
      taskCsvOptions = buildCsvOptionsForTaskSubmit(isCsv, csvOptions);
    } catch (requestError) {
      setError(resolveErrorMessage(requestError));
      return;
    }
    setExecuting(true);
    setError(null);
    try {
      const result = await sqlService.executeImportWithMapping({
        dataSourceId,
        databaseName,
        schemaName,
        tableName,
        fileId,
        mappings: mappingParams,
        unmappedTarget,
        ...fileOptions,
        csvOptions: taskCsvOptions,
        mode,
        duplicatePolicy,
        failurePolicy,
      });
      onSubmitted(result.taskId);
    } catch (requestError) {
      setError(resolveErrorMessage(requestError));
    } finally {
      setExecuting(false);
    }
  };

  const targetDisplay = [dataSourceName, databaseName, schemaName, isSql ? undefined : tableName]
    .filter(Boolean)
    .join('/');
  const formatHighlights = useMemo(() => {
    if (isCsv) {
      return [
        { label: i18n('workspace.importExport.encoding'), value: csvOptions.encoding },
        { label: i18n('workspace.importExport.delimiter'), value: describeCharacter(csvOptions.delimiter) },
        { label: i18n('workspace.importExport.textQualifier'), value: describeCharacter(csvOptions.quote) },
        {
          label: i18n('workspace.importExport.sourceRows'),
          value: csvOptions.hasHeader
            ? `${i18n('workspace.importExport.headerRow')}: ${csvOptions.headerRow}`
            : i18n('workspace.importExport.noHeader'),
        },
        { label: i18n('workspace.importExport.dataStartRow'), value: String(csvOptions.dataStartRow) },
        {
          label: i18n('workspace.importExport.dataEndRow'),
          value:
            csvOptions.dataEndRow == null ? i18n('workspace.importExport.endOfFile') : String(csvOptions.dataEndRow),
        },
      ];
    }
    if (isExcel) {
      return [
        {
          label: i18n('workspace.importExport.sheet'),
          value: sheets[excelOptions.sheetIndex] ?? String(excelOptions.sheetIndex),
        },
        { label: i18n('workspace.importExport.columnRange'), value: excelOptions.columnRange || '-' },
        {
          label: i18n('workspace.importExport.sourceRows'),
          value: excelOptions.hasHeader
            ? `${i18n('workspace.importExport.headerRow')}: ${excelOptions.headerRow}`
            : i18n('workspace.importExport.noHeader'),
        },
        { label: i18n('workspace.importExport.dataStartRow'), value: String(excelOptions.dataStartRow) },
      ];
    }
    if (isJson) {
      return [
        { label: i18n('workspace.importExport.encoding'), value: jsonOptions.encoding },
        {
          label: i18n('workspace.importExport.jsonStructure'),
          value: i18n(`workspace.importExport.json${jsonOptions.structure}`),
        },
        { label: i18n('workspace.importExport.jsonDataPath'), value: jsonOptions.dataPath },
      ];
    }
    return [{ label: i18n('workspace.importExport.encoding'), value: sqlImportOptions.encoding }];
  }, [isCsv, isExcel, isJson, csvOptions, excelOptions, jsonOptions, sheets, sqlImportOptions]);

  // The step bar is gone, so the dialog title names the step the user is looking at.
  useEffect(() => {
    onStepChange?.(step);
  }, [onStepChange, step]);

  if (!importExportDataBoundInfo) {
    return null;
  }

  return (
    <div className={styles.container}>
      {modalContextHolder}
      <div className={styles.scrollContent}>
        {stepError && <Alert className={styles.errorAlert} type="error" showIcon message={stepError} />}
        <div className={styles.stepPanel} hidden={step !== 0}>
          {targetTableNotice && (
            <Alert
              className={styles.errorAlert}
              type="warning"
              showIcon
              message={i18n('workspace.importExport.wizard.targetTableChanged', targetTableNotice.tableName)}
            />
          )}
          <ImportExportFile
            ref={importExportFileRef}
            restoredSpec={restoredSpec}
            onImportFileChange={handleImportFileChange}
            onFileTypeChange={setFileType}
            sqlImportOptions={sqlImportOptions}
            onSqlImportOptionsChange={setSqlImportOptions}
            hideSqlImportOptions
            targetTableName={tableName}
            targetTableField={
              dataSourceId == null || !databaseName ? undefined : (
                <TargetTableSelect
                  dataSourceId={dataSourceId}
                  databaseName={databaseName}
                  schemaName={schemaName}
                  value={tableName}
                  disabled={executing}
                  onChange={handleTargetTableChange}
                />
              )
            }
          />
        </div>
        {step === 1 && (
          <div className={styles.stepPanel}>
            {!isSql && (
              <ImportTemplateSelect
                templates={visibleTemplates}
                fileType={fileFormat}
                disabled={executing}
                onApply={handleApplyTemplate}
                onDelete={handleDeleteTemplate}
              />
            )}
            {/* Parsing reports itself through the options and the button state; only a failure
                earns a line, with the way out next to it. */}
            {previewRequired && (previewError || stagingError) && (
              <div className={styles.parseStatus}>
                <span className={styles.parseStatusError}>{previewError ?? stagingError}</span>
                <Button size="small" disabled={executing} onClick={() => void loadPreview()}>
                  {i18n('workspace.importExport.wizard.retry')}
                </Button>
              </div>
            )}
            {isCsv && (
              <CsvOptionsSections value={csvOptions} disabled={executing} dataItems={[]} onChange={setCsvOptions} />
            )}
            {isJson && (
              <JsonOptionsSections value={jsonOptions} disabled={executing} dataItems={[]} onChange={setJsonOptions} />
            )}
            {isExcel && (
              <ExcelOptionsSections
                value={excelOptions}
                sheets={sheets}
                disabled={executing}
                dataItems={[]}
                onChange={setExcelOptions}
              />
            )}
            {isSql && <SqlImportOptionsFields value={sqlImportOptions} onChange={setSqlImportOptions} />}
          </div>
        )}
        {step === 2 && (
          <div className={styles.stepPanel}>
            {isSql ? (
              <Alert type="info" showIcon message={i18n('workspace.importExport.wizard.sqlNoMapping')} />
            ) : preview ? (
              <>
                {(dataSectionItems || []).map((item) =>
                  item ? (
                    <section key={String(item.key)} className={styles.sectionBlock}>
                      <div className={styles.sectionTitle}>{item.label}</div>
                      {item.children}
                    </section>
                  ) : null,
                )}
              </>
            ) : (
              <div className={styles.previewState}>
                {previewError || stagingError ? (
                  <div className={styles.previewError} role="alert">
                    <TriangleAlert size={24} />
                    <span>{previewError ?? stagingError}</span>
                  </div>
                ) : (
                  <Spin />
                )}
              </div>
            )}
          </div>
        )}
        {step === IMPORT_WIZARD_LAST_STEP && (
          <div className={styles.stepPanel}>
            {previewRequired && (
              <div className={styles.policySection}>
                <div className={styles.sectionTitle}>{i18n('workspace.importExport.policy.title')}</div>
                <div className={styles.policyRow}>
                  <div className={styles.policyField}>
                    <span className={styles.policyLabel}>
                      {i18n('workspace.importExport.policy.duplicateLabel')}
                    </span>
                    <Select<ImportDuplicatePolicy>
                      className={styles.policySelect}
                      size="small"
                      value={duplicatePolicy}
                      disabled={executing}
                      onChange={setDuplicatePolicy}
                      options={[
                        {
                          value: 'INSERT',
                          label: i18n('workspace.importExport.policy.duplicateInsert'),
                        },
                        {
                          value: 'IGNORE',
                          label: i18n('workspace.importExport.policy.duplicateIgnore'),
                          disabled: !supportsDuplicateIgnore(databaseType),
                        },
                      ]}
                    />
                  </div>
                  <div className={styles.policyField}>
                    <span className={styles.policyLabel}>
                      {i18n('workspace.importExport.policy.failureLabel')}
                    </span>
                    <Select<ImportFailurePolicy>
                      className={styles.policySelect}
                      size="small"
                      value={failurePolicy}
                      disabled={executing}
                      onChange={setFailurePolicy}
                      options={[
                        { value: 'ABORT', label: i18n('workspace.importExport.policy.failureAbort') },
                        {
                          value: 'SKIP_ERROR_ROWS',
                          label: i18n('workspace.importExport.policy.failureSkip'),
                        },
                      ]}
                    />
                  </div>
                </div>
                {duplicatePolicy === 'IGNORE' && (
                  <div className={styles.policyHint}>
                    {i18n('workspace.importExport.policy.duplicateIgnoreHint')}
                  </div>
                )}
                {failurePolicy === 'SKIP_ERROR_ROWS' && (
                  <div className={styles.policyHint}>
                    {i18n('workspace.importExport.policy.failureSkipHint')}
                  </div>
                )}
              </div>
            )}
            <Descriptions
              className={styles.summary}
              size="small"
              bordered
              column={1}
              items={[
                { key: 'target', label: i18n('workspace.importExport.wizard.summaryTarget'), children: targetDisplay },
                {
                  key: 'file',
                  label: i18n('workspace.importExport.wizard.summaryFile'),
                  children: selectedFileName || '-',
                },
                {
                  key: 'format',
                  label: i18n('workspace.importExport.wizard.summaryFormat'),
                  children: (
                    <div className={styles.summaryValue}>
                      {formatHighlights.map(({ label, value }) => (
                        <div key={label}>
                          {label}: {value}
                        </div>
                      ))}
                    </div>
                  ),
                },
                {
                  key: 'mapping',
                  label: i18n('workspace.importExport.wizard.summaryMapping'),
                  children: previewRequired ? (
                    <div className={styles.summaryValue}>
                      <div>{i18n('workspace.importExport.wizard.mappingCountValue', mappedTargetCount)}</div>
                      {mappedPairs.length > 0 && <div>{mappedPairs.join(' · ')}</div>}
                    </div>
                  ) : (
                    i18n('workspace.importExport.wizard.notApplicable')
                  ),
                },
                ...(appliedTemplateName
                  ? [
                      {
                        key: 'template',
                        label: i18n('workspace.importExport.wizard.summaryTemplate'),
                        children: i18n('workspace.importExport.wizard.templateFrom', appliedTemplateName),
                      },
                    ]
                  : []),
                {
                  key: 'unmapped',
                  label: i18n('workspace.importExport.wizard.summaryUnmapped'),
                  children: previewRequired
                    ? `${i18n(
                        unmappedTarget === ImportUnmappedTarget.NULL
                          ? 'workspace.importExport.unmappedNull'
                          : 'workspace.importExport.unmappedDefault',
                      )} · ${
                        emptyAsNull
                          ? i18n('workspace.importExport.wizard.emptyAsNullOn')
                          : i18n('workspace.importExport.wizard.emptyAsNullOff')
                      }`
                    : i18n('workspace.importExport.wizard.notApplicable'),
                },
                ...(previewRequired
                  ? [
                      {
                        key: 'duplicatePolicy',
                        label: i18n('workspace.importExport.wizard.summaryDuplicatePolicy'),
                        children:
                          duplicatePolicy === 'IGNORE'
                            ? i18n('workspace.importExport.policy.duplicateIgnore')
                            : i18n('workspace.importExport.policy.duplicateInsert'),
                      },
                      {
                        key: 'failurePolicy',
                        label: i18n('workspace.importExport.wizard.summaryFailurePolicy'),
                        children:
                          failurePolicy === 'SKIP_ERROR_ROWS'
                            ? i18n('workspace.importExport.policy.failureSkip')
                            : i18n('workspace.importExport.policy.failureAbort'),
                      },
                    ]
                  : []),
                ...(previewRequired
                  ? [
                      {
                        key: 'mode',
                        label: i18n('workspace.importExport.wizard.summaryMode'),
                        children: (
                          <div className={styles.summaryModeRow}>
                            <span>
                              {mode === 'FAST'
                                ? i18n('workspace.importExport.ultraMode')
                                : i18n('workspace.importExport.standardMode')}
                            </span>
                            {isCsv && (
                              <>
                                <ImportModeControl value={mode} onChange={setMode} disabled={executing} />
                                <span className={styles.modeRisk}>
                                  {i18n('workspace.importExport.ultraModeHint')}
                                </span>
                              </>
                            )}
                          </div>
                        ),
                      },
                    ]
                  : []),
              ]}
            />
          </div>
        )}
      </div>
      <div className={styles.footer}>
        <div className={styles.footerLeft}>
          <Button disabled={executing} onClick={() => setImportExportDataBoundInfo(null)}>
            {i18n('common.button.cancel')}
          </Button>
          {step === 1 && !isSql && preview ? (
            <ImportTemplateControls disabled={executing} fileType={fileFormat} onSave={handleSaveTemplate} />
          ) : null}
        </div>
        <div className={styles.footerRight}>
          {step > 0 && (
            <Button
              disabled={executing || loadingPreview}
              onClick={() => setStep((current) => Math.max(0, current - 1))}
            >
              {i18n('workspace.importExport.wizard.previous')}
            </Button>
          )}
          {isImportWizardLastStep(step) ? (
            <Button
              type="primary"
              loading={executing}
              disabled={previewRequired && (!previewReady || !fileId || mappedTargetCount === 0)}
              onClick={execute}
            >
              {i18n('common.button.execute')}
            </Button>
          ) : (
            <Button type="primary" disabled={!canPressNext} loading={loadingPreview} onClick={handleNext}>
              {i18n('workspace.importExport.wizard.next')}
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}
