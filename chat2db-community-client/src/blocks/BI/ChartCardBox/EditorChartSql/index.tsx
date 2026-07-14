import React, {
  memo,
  useState,
  useEffect,
  useRef,
  useMemo,
  forwardRef,
  useImperativeHandle,
  ForwardedRef,
} from 'react';
import { useStyles } from './style';
import { IDatabaseBaseInfo } from '@/typings/database';
import { IChartItem } from '@/typings/dashboard';
import SQLExecute, { SQLExecuteRef } from '@/pages/main/workspace/components/SQLExecute';
import { WorkspaceTabType } from '@/constants';
import { randomLargeLong } from '@/utils';

export interface IProps {
  className?: string;
  chartDetail: IChartItem;
}

type DatabaseInfoAndMetaData = Pick<IChartItem, 'databaseInfo' | 'metaData'>;
export interface EditorChartSqlRef {
  getDatabaseInfoAndMetaData: () => DatabaseInfoAndMetaData;
}

const EditorChartSql = forwardRef((props: IProps, ref: ForwardedRef<EditorChartSqlRef>) => {
  const { className, chartDetail } = props;
  const { styles, cx } = useStyles();
  const sqlExecuteRef = useRef<SQLExecuteRef>(null);
  const chartDetailRef = useRef<IChartItem>(chartDetail);

  const [databaseInfoAndMetaData, setDatabaseInfoAndMetaData] = useState<DatabaseInfoAndMetaData | null>();

  useImperativeHandle(ref, () => ({
    getDatabaseInfoAndMetaData: () => {
      // If the user does not run sql, then there is no databaseInfoAndMetaData. If the user clicks save, then manually obtain sqlExecuteRef.current?.getDatabaseInfo()
      if (!databaseInfoAndMetaData) {
        return {
          databaseInfo: sqlExecuteRef.current?.getDatabaseInfo(),
          metaData: undefined,
        };
      }
      return databaseInfoAndMetaData;
    },
  }));

  useEffect(() => {
    chartDetailRef.current = chartDetail;
  }, [chartDetail]);

  const databaseBaseInfo: IDatabaseBaseInfo = useMemo(() => {
    return chartDetail.databaseInfo || {};
  }, []);

  useEffect(() => {
    if (databaseBaseInfo?.sql) {
      sqlExecuteRef.current?.executeSQL({ sql: databaseBaseInfo.sql });
    } else {
      // setChartDetail({ ...chartDetail });
    }
  }, []);

  const consoleId: any = useMemo(() => {
    return randomLargeLong();
  }, []);

  const onExecuteSQLCallback = (params) => {
    const {
      databaseInfo: { dataSourceId, dataSourceName, databaseType, databaseName, schemaName, sql },
      data,
    } = params;

    const { dataList, headerList } = data[0];

    setDatabaseInfoAndMetaData({
      databaseInfo: { dataSourceId, dataSourceName, databaseType, databaseName, schemaName, sql },
      metaData: {
        dataList,
        headerList,
      } as any,
    });
  };

  return (
    <div className={cx(styles.editorChartSqlBox, className)}>
      <SQLExecute
        boundInfo={{ ...databaseBaseInfo, consoleId }}
        type={WorkspaceTabType.CONSOLE}
        initDDL={databaseBaseInfo?.sql || ''}
        isActive={true}
        onExecuteSQLCallback={onExecuteSQLCallback}
        ref={sqlExecuteRef}
        isConsole={false}
      />
    </div>
  );
});

export default memo(EditorChartSql);
