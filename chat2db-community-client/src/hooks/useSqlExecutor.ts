import { useCallback, useState } from 'react';
import { IManageResultData, IExecuteSqlParams } from '@/typings';
import executeSqlServer from '@/service/executeSql';
import useAbortRequest from './useAbortRequest';
import { isDesktop } from '@/utils/env';
import {
  SqlExecutionEvent,
  cancelSqlExecution,
  onSqlExecutionEvent,
  startSqlExecution,
} from '@/service/sqlExecutionStream';
import { v4 as uuidv4 } from 'uuid';

interface IUseSqlExecutorProps {
  // Whether to return only one piece of data
  onlyOne?: boolean;
  onExecutionEvent?: (event: SqlExecutionEvent) => void;
}

const useSqlExecutor = (props?: IUseSqlExecutorProps) => {
  const { onlyOne, onExecutionEvent } = props || {};
  const [executing, setExecuting] = useState(false);
  const [executionId, setExecutionId] = useState<string>();
  // interrupt request
  const [initSignal, abortRequest] = useAbortRequest();

  // Process data
  const handleData = (params: { data: any[] }) => {
    const { data } = params;
    let res = data;
    if (onlyOne) {
      res = data[0];
    }
    return res;
  };

  // execute sql
  const executeSQL = useCallback((params: IExecuteSqlParams): Promise<IManageResultData[]> => {
    if (isDesktop && onExecutionEvent) {
      const requestUuid = uuidv4();
      setExecuting(true);
      return new Promise((resolve, reject) => {
        let offEvent: (() => void) | undefined;
        offEvent = onSqlExecutionEvent(requestUuid, (event) => {
          onExecutionEvent(event);
          if (event.eventType === 'finished') {
            offEvent?.();
            setExecuting(false);
            setExecutionId(undefined);
            resolve([]);
          }
          if (event.eventType === 'failed' || event.eventType === 'cancelled') {
            offEvent?.();
            setExecuting(false);
            setExecutionId(undefined);
            if (event.eventType === 'cancelled') {
              resolve([]);
            } else {
              reject(event.message);
            }
          }
        });
        startSqlExecution(params, requestUuid)
          .then((res) => {
            if (!res?.executionId) {
              offEvent?.();
              setExecuting(false);
              reject(getStartExecutionError(res));
              return;
            }
            setExecutionId(res.executionId);
          })
          .catch((err) => {
            offEvent?.();
            setExecuting(false);
            reject(err);
          });
      });
    }
    return new Promise((resolve, reject) => {
      // Parameters for executing sql
      const executeSqlParams = params;

      setExecuting(true);

      // execute sql
      return executeSqlServer
        .executeSql(executeSqlParams, {
          signal: initSignal(),
        })
        .then((res) => {
          const data = handleData({ data: res });
          resolve(data);
        })
        .catch((err) => {
          reject(err);
        })
        .finally(() => {
          setExecuting(false);
        });
    });
  }, [onExecutionEvent]);

  // Stop executing sql
  const stopExecuteSQL = useCallback(() => {
    if (isDesktop && executionId) {
      cancelSqlExecution(executionId);
      return;
    }
    abortRequest();
    setExecuting(false);
  }, [abortRequest, executionId]);

  return {
    executing,
    executeSQL,
    stopExecuteSQL,
  };
};

function getStartExecutionError(response: any) {
  const message = response?.message;
  if (typeof message === 'string') {
    return message;
  }
  if (message?.message) {
    return message.message;
  }
  if (message?.errorMessage) {
    return message.errorMessage;
  }
  if (response?.errorMessage) {
    return response.errorMessage;
  }
  return 'SQL execution failed to start';
}

export default useSqlExecutor;
