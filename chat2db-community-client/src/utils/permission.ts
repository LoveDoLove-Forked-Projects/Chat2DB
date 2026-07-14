import { formatParams } from './url';

const handleExecuteSQLPermission = (props: {
  type: 'data' | 'script';
  dataSourceId: number;
  databaseName: string;
  schemaName: string;
  sqlScript: string;
}) => {
  const { type, dataSourceId, databaseName, schemaName, sqlScript } = props;
  const params = formatParams({
    form: type,
    script: sqlScript,
    dataSourceId: dataSourceId,
    databaseName: databaseName,
    schemaName: schemaName,
  });
  // return permission;
};

export default handleExecuteSQLPermission;
