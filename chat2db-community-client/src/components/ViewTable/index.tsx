import { memo, useCallback, useState, useEffect, useRef } from 'react';
import SearchResult from '@/blocks/SearchResult';
import { processResultDataList } from '@/utils/database';
import { IExecuteSqlParams, IManageResultData, IViewTableParams } from '@/typings';
import useViewTable from '@/hooks/useViewTable';
import useViewTablePaging from '@/hooks/useViewTablePaging';
import { replaceViewTableResult } from '@/hooks/viewTablePagingModel';
import SqlExecutionLoading from '@/components/SqlExecutionLoading';
import { useStyles } from './style';
import { beginLatestRequest, invalidateLatestRequest, isLatestRequest } from '@/utils/latestRequest';
import { IMPORT_TARGET_TABLE_REFRESH_EVENT } from '@/store/importExport/taskCenterUtils';
import { getViewTableTargetKey } from './viewTableTarget';

interface IProps {
  className?: string;
  viewTableParams: IViewTableParams;
}

const ViewTable = memo<IProps>((props) => {
  const { viewTableParams } = props;
  const { styles } = useStyles();
  const [resultDataList, setResultDataList] = useState<IManageResultData[]>();
  const requestGenerationRef = useRef(0);
  const {
    executing: initialExecuting,
    executeSQL: executeInitialTable,
    stopExecuteSQL: stopInitialTable,
  } = useViewTable();
  const { resultData: pagedResultData, executing: pagingExecuting, executePage, stopExecuteSQL: stopPaging } =
    useViewTablePaging();
  // The workspace tab layer keeps every open tab mounted and rebuilds the tab
  // bodies whenever unrelated workspace state changes, so `viewTableParams` is a
  // new object on most parent renders. Loading is keyed on the table identity
  // and reads the latest params from a ref, otherwise every tab switch re-issues
  // the table browse request.
  const viewTableParamsRef = useRef(viewTableParams);
  const viewTableTargetKey = getViewTableTargetKey(viewTableParams);

  useEffect(() => {
    viewTableParamsRef.current = viewTableParams;
  }, [viewTableParams]);

  const refreshCurrentTable = useCallback(() => {
    const params = viewTableParamsRef.current;
    if (params) {
      const requestGeneration = beginLatestRequest(requestGenerationRef);
      executeInitialTable(params).then((data) => {
        if (!isLatestRequest(requestGenerationRef, requestGeneration)) return;
        const _resultDataList = processResultDataList(data, params);
        setResultDataList(_resultDataList);
      });
    }
  }, [executeInitialTable]);

  useEffect(() => {
    refreshCurrentTable();
    return () => {
      invalidateLatestRequest(requestGenerationRef);
    };
  }, [refreshCurrentTable, viewTableTargetKey]);

  useEffect(() => {
    const handleImportRefresh = (event: Event) => {
      const target = (event as CustomEvent<IViewTableParams>).detail;
      if (getViewTableTargetKey(target) === viewTableTargetKey) {
        refreshCurrentTable();
      }
    };
    window.addEventListener(IMPORT_TARGET_TABLE_REFRESH_EVENT, handleImportRefresh);
    return () => window.removeEventListener(IMPORT_TARGET_TABLE_REFRESH_EVENT, handleImportRefresh);
  }, [refreshCurrentTable, viewTableTargetKey]);

  useEffect(() => {
    if (pagedResultData) {
      setResultDataList((current) => replaceViewTableResult(current, pagedResultData));
    }
  }, [pagedResultData]);

  const handleResultPagingChange = useCallback(
    (_resultData: IManageResultData, executeSqlParams: IExecuteSqlParams) => {
      if (executeSqlParams.dataSourceId == null || !executeSqlParams.sql) {
        return;
      }
      return executePage(executeSqlParams);
    },
    [executePage],
  );

  return (
    <div className={styles.container}>
      {(initialExecuting || pagingExecuting) && (
        <SqlExecutionLoading onCancel={pagingExecuting ? stopPaging : stopInitialTable} />
      )}
      {resultDataList && (
        <SearchResult
          viewTable
          resultDataList={resultDataList}
          onResultPagingChange={handleResultPagingChange}
        />
      )}
    </div>
  );
});

export default ViewTable;
