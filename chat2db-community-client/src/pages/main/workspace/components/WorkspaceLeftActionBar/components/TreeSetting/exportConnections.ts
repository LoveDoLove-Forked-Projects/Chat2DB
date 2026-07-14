import ConnectionServer from '@/service/connection';
import {saveFileToDesktop} from '@/utils/file';

export const exportConnections = (params: { datasourceIds: number[] | null }) => {
  ConnectionServer.exportDataSource(params).then((res) => {
    saveFileToDesktop({
      fileName: `export_chat2db_connections`,
      fileContent: res.message,
      fileType: 'json'
    });
  });
};
