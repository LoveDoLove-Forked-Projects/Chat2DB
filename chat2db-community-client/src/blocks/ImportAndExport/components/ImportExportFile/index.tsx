import SqlImportOptionsFields from '../SqlImportOptionsFields';
import { DEFAULT_SQL_IMPORT_OPTIONS } from '../../utils/importOptions';
import {
  memo,
  useMemo,
  useState,
  forwardRef,
  ForwardedRef,
  ReactNode,
  useImperativeHandle,
  useEffect,
} from 'react';
import { useStyles } from './style';
import UploadLocalFile, { type FileUrl } from '@/components/UploadLocalFile';
import SingleCharacterSelect from '@/components/SingleCharacterSelect';
import { Alert, Form, Input, Select, Switch } from 'antd';
import i18n from '@/i18n';
import { useImportExportStore } from '@/store/importExport';
import { IconButton } from '@chat2db/ui';
import { ImportExportType, ImportExportFileType, ImportExportTaskType } from '@/constants/importExport';
import { ExportTaskParams, ImportTaskParams } from '@/service/importExport';
import { isDesktop, isDevelopment } from '@/utils/env';
import jcefApi from '@/jcef';
import { hasSelectedImportFile } from './selection';
import { buildExportTaskParams, DEFAULT_EXPORT_FORM_VALUE, ExportFormValue } from './exportParams';
import type { RestoredTaskSpec } from '@/blocks/ImportAndExport/utils/restoreTaskSpec';
import type { ISqlImportOptions } from '@/typings/importExport';

interface IProps {
  className?: string;
  /** Present when the wizard was opened from a finished task ("new from this configuration"). */
  restoredSpec?: RestoredTaskSpec | null;
  setIsReady?: (p: boolean) => void;
  onImportFileChange?: (file?: FileUrl) => void;
  /** Reports the file type the user picked; the import wizard uses it to choose the format step. */
  onFileTypeChange?: (fileType: ImportExportFileType) => void;
  /** Controlled SQL import encoding, so the import wizard can render it in its own format step. */
  sqlImportOptions?: ISqlImportOptions;
  onSqlImportOptionsChange?: (value: ISqlImportOptions) => void;
  /** Hides the SQL encoding field when the owner renders it elsewhere. */
  hideSqlImportOptions?: boolean;
  /**
   * Import target chosen by the owner (the import wizard can switch it); it overrides the bound
   * table in the submit parameters and in the read-only row.
   */
  targetTableName?: string;
  /**
   * Searchable target table picker of the import wizard. It replaces the read-only row on the file
   * step; the export path keeps the bound table row.
   */
  targetTableField?: ReactNode;
}

export interface ImportExportFileRef {
  getValues: () => ExportTaskParams | ImportTaskParams | null;
}

/** Character sets the export writer supports; the backend rejects anything outside this list. */
const EXPORT_ENCODING_OPTIONS = [
  'UTF-8',
  'GBK',
  'GB18030',
  'UTF-16LE',
  'UTF-16BE',
  'ISO-8859-1',
  'BIG5',
].map((encoding) => ({ value: encoding, label: encoding }));

const exportTypeOptions = [
  { label: 'CSV', value: ImportExportFileType.CSV, accept: '.csv' },
  { label: 'XLSX', value: ImportExportFileType.XLSX, accept: '.xlsx' },
  { label: 'XLS', value: ImportExportFileType.XLS, accept: '.xls' },
  { label: 'JSON', value: ImportExportFileType.JSON, accept: '.json' },
  { label: 'SQL', value: ImportExportFileType.SQL, accept: '.sql' },
];

const ImportExportFile = forwardRef((props: IProps, ref: ForwardedRef<ImportExportFileRef>) => {
  const {
    restoredSpec,
    setIsReady,
    onImportFileChange,
    onFileTypeChange,
    sqlImportOptions: controlledSqlImportOptions,
    onSqlImportOptionsChange,
    hideSqlImportOptions,
    targetTableName,
    targetTableField,
  } = props;
  const { styles } = useStyles();
  const [form] = Form.useForm();
  const [internalSqlImportOptions, setInternalSqlImportOptions] = useState(
    () => restoredSpec?.sqlImportOptions ?? DEFAULT_SQL_IMPORT_OPTIONS,
  );
  // The import wizard owns the value so it can render the encoding field in its format step.
  const sqlImportOptions = controlledSqlImportOptions ?? internalSqlImportOptions;
  const updateSqlImportOptions = (value: ISqlImportOptions) => {
    setInternalSqlImportOptions(value);
    onSqlImportOptionsChange?.(value);
  };
  const [selectedFiles, setSelectedFiles] = useState<FileUrl[]>([]);
  // The export directory is a desktop concept; a restored spec never seeds it on web.
  const [exportLocation, setExportLocation] = useState<string>(() =>
    isDesktop ? restoredSpec?.exportPath ?? '' : '',
  );
  const [formValue, setFormValue] = useState<ExportFormValue>(() => ({
    ...DEFAULT_EXPORT_FORM_VALUE,
    ...restoredSpec?.formValue,
  }));

  const { importExportDataBoundInfo } = useImportExportStore((state) => {
    return {
      importExportDataBoundInfo: state.importExportDataBoundInfo,
    };
  });

  const isImport = importExportDataBoundInfo?.type === ImportExportType.IMPORT;
  const isExport = importExportDataBoundInfo?.type === ImportExportType.EXPORT;
  const isSqlImport = isImport && formValue.exportType === ImportExportFileType.SQL;
  // A SQL script runs in the bound environment, so only a data file has a switchable target table.
  const showTargetTableField = isImport && !isSqlImport && targetTableField != null;

  useEffect(() => {
    if (importExportDataBoundInfo) {
      const { dataSourceName, databaseName, schemaName, tableName } = importExportDataBoundInfo;
      const tableNameDisplay = [
        dataSourceName,
        databaseName,
        schemaName,
        isImport && formValue.exportType === ImportExportFileType.SQL ? undefined : tableName,
      ]
        .filter(Boolean)
        .join('/');
      form.setFieldsValue({
        tableNameDisplay: tableNameDisplay,
      });
    }
  }, [importExportDataBoundInfo, formValue.exportType, isImport]);

  // Gets the corresponding file type based on the export type
  const uploadLocalFileAccept = useMemo(() => {
    return formValue.exportType
      ? exportTypeOptions.find((item) => item.value === formValue.exportType)?.accept
      : '';
  }, [formValue.exportType]);

  useEffect(() => {
    if (isExport) {
      setIsReady?.(!isDesktop || !!exportLocation || !!formValue.fileUrl);
    }
  }, [exportLocation, formValue]);

  useEffect(() => {
    if (isImport) {
      setIsReady?.(hasSelectedImportFile(selectedFiles));
    }
  }, [isImport, selectedFiles, setIsReady]);

  const handleSelectedFilesChange = (files: FileUrl[]) => {
    setSelectedFiles(files);
    if (isImport) {
      onImportFileChange?.(files[0]);
    }
  };

  useImperativeHandle(ref, () => ({
    getValues: () => {
      if (!importExportDataBoundInfo) return null;
      const { dataSourceId, databaseName, schemaName, tableName } = importExportDataBoundInfo;
      const commonValues = {
        dataSourceId,
        databaseName,
        schemaName,
        format: formValue.exportType,
      };
      if (isExport) {
        return buildExportTaskParams(importExportDataBoundInfo, formValue, exportLocation);
      }
      return {
        ...commonValues,
        taskType:
          formValue.exportType === ImportExportFileType.SQL
            ? ImportExportTaskType.SQL_FILE_IMPORT
            : ImportExportTaskType.DATA_FILE_IMPORT,
        // The wizard can change the import target, so its controlled value wins over the bound one.
        tableName: targetTableName || tableName,
        sourceFile: selectedFiles[0]?.filePath || '',
        ...(formValue.exportType === ImportExportFileType.SQL ? { sqlImportOptions } : {}),
        // SQL-script imports have no mapping step, so a restored task keeps the policies it ran with;
        // mapped imports take them from the wizard's confirm step instead.
        ...(restoredSpec?.importPolicies ?? {}),
      };
    },
  }));

  const handleFormChange = (changedValues, allValues) => {
    setFormValue({
      ...formValue,
      ...allValues,
    });
    if (changedValues.exportType) {
      onFileTypeChange?.(allValues.exportType);
    }
  };

  const handleSelectExportLocation = async () => {
    const fileName = await jcefApi?.selectDirectory();
    if (!fileName) return;
    setExportLocation(fileName);
  };

  return (
    <Form
      className={styles.form}
      layout="vertical"
      form={form}
      autoComplete="off"
      onValuesChange={handleFormChange}
      initialValues={formValue}
    >
      {restoredSpec?.notices.length ? (
        <Alert
          data-testid="restored-spec-notices"
          message={restoredSpec.notices.map((notice) => (
            <div key={notice.code}>{i18n(notice.i18nKey, ...(notice.params ?? []))}</div>
          ))}
          showIcon
          style={{ marginBottom: 12 }}
          type="info"
        />
      ) : null}
      <Form.Item
        label={`${i18n(
          isExport
            ? 'workspace.importExport.exportScope'
            : isSqlImport
            ? 'workspace.importExport.executionEnvironment'
            : 'workspace.importExport.targetTable',
        )}:`}
        name={showTargetTableField ? undefined : 'tableNameDisplay'}
      >
        {showTargetTableField ? targetTableField : <Input autoComplete="off" disabled />}
      </Form.Item>
      <Form.Item label={`${i18n('workspace.importExport.fileType')}:`} name="exportType">
        <Select options={exportTypeOptions} />
      </Form.Item>
      {isExport && (
        // The header switch precedes the destination: it changes the file, the destination does not.
        <Form.Item label={`${i18n('workspace.importExport.containsHeader')}:`} name="containsHeader" valuePropName="checked">
          <Switch aria-label={i18n('workspace.importExport.containsHeader')} />
        </Form.Item>
      )}
      {isExport && formValue.exportType === ImportExportFileType.CSV && (
        // CSV output options: the character set and the field delimiter of the produced file.
        <>
          <Form.Item label={`${i18n('workspace.importExport.encoding')}:`} name="encoding">
            <Select options={EXPORT_ENCODING_OPTIONS} />
          </Form.Item>
          <Form.Item label={`${i18n('workspace.importExport.delimiter')}:`} name="delimiter">
            <SingleCharacterSelect
              className={styles.exportCsvField}
              customInputClassName={styles.customCharacterInput}
              customOptionLabel={(character) => i18n('workspace.importExport.customCharacterValue', character)}
              customInputLabel={i18n('workspace.importExport.customCharacter')}
              options={[
                { value: ',', label: i18n('workspace.importExport.delimiterComma') },
                { value: '\t', label: i18n('workspace.importExport.delimiterTab') },
                { value: ';', label: i18n('workspace.importExport.delimiterSemicolon') },
                { value: '|', label: i18n('workspace.importExport.delimiterPipe') },
              ]}
            />
          </Form.Item>
        </>
      )}
      {isExport && isDesktop && (
        <Form.Item label={`${i18n('workspace.importExport.exportLocation')}:`} name="exportLocation">
          <div className={styles.exportLocationBox}>
            <Input autoComplete="off" disabled value={exportLocation} />
            <IconButton
              className={styles.iconButton}
              size={{ boxSize: 30, iconSize: 22, borderRadius: 6 }}
              code="icon-folder"
              onClick={handleSelectExportLocation}
            />
          </div>
        </Form.Item>
      )}
      {isImport && (
        <Form.Item>
          <UploadLocalFile fileUrlListChange={handleSelectedFilesChange} accept={uploadLocalFileAccept} />
        </Form.Item>
      )}
      {isImport && formValue.exportType === ImportExportFileType.SQL && !hideSqlImportOptions && (
        <SqlImportOptionsFields value={sqlImportOptions} onChange={updateSqlImportOptions} />
      )}
      {isDevelopment && isExport && (
        <Form.Item label="File URL" name="fileUrl">
          <Input autoComplete="off" />
        </Form.Item>
      )}
    </Form>
  );
});

export default memo(ImportExportFile);
