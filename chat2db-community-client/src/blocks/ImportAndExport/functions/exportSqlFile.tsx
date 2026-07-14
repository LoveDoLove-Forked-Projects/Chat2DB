import { IDatabaseBaseInfo } from '@/typings/database';
import importExportServices from '@/service/importExport';
import jcefApi from '@/jcef';

export interface ExportSqlFileProps extends IDatabaseBaseInfo {
  scope: 'ALL' | 'SCHEMA' | 'TABLE';
  tableNames?: string[];
  getTaskList?: (p: any) => void;
  openLogModal?: (taskId: number) => void;
  setShowExportToolbar?: (showExportToolbar: boolean) => void;
}

export const handleExportSqlFile = async (props: ExportSqlFileProps) => {
  const exportPath = await jcefApi?.selectDirectory();

  if (!exportPath) return;

  const getTaskList = props.getTaskList;
  const openLogModal = props.openLogModal;

  const params = {
    ...props,
    exportPath,
    containsHeader: true,
  };

  delete params.getTaskList;
  delete params.openLogModal;
  delete params.setShowExportToolbar;

  importExportServices.exportSqlFile(params).then((res) => {
    getTaskList && getTaskList({ visible: true });
    openLogModal && openLogModal(res);
  });
  return;
};
