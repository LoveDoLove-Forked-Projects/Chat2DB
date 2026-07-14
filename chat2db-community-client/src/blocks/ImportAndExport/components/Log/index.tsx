import React, { memo, useEffect, useRef, useState, forwardRef, ForwardedRef, useImperativeHandle } from 'react';
import { useStyles } from './style';
import { Progress } from 'antd';
import importExportServices from '@/service/importExport';
import { ImportExportTaskDetails } from '@/typings/importExport';
import { ImportExportTaskStatus } from '@/constants/importExport';
import dayjs from 'dayjs';
import i18n from '@/i18n';
import { useImportExportStore } from '@/store/importExport';

interface IProps {
  className?: string;
  taskId: number;
  finish?: (taskDetails: ImportExportTaskDetails) => void;
}

export interface LogRef {}

const Log = forwardRef((props: IProps, ref: ForwardedRef<LogRef>) => {
  const { taskId } = props;
  const { styles } = useStyles();
  const [taskDetails, setTaskDetails] = useState<ImportExportTaskDetails>();
  const timer = useRef<NodeJS.Timeout>();
  const logEndRef = useRef<HTMLDivElement>(null);
  const timerNumber = useRef<number>(500);
  const { getTaskList } = useImportExportStore((state) => {
    return {
      getTaskList: state.getTaskList,
    };
  });

  useEffect(() => {
    getTaskDetails();
    timerNumber.current = 500;
    return () => {
      // clear timer
      if (timer.current) {
        clearTimeout(timer.current);
      }
    };
  }, [taskId]);

  const getTaskDetails = () => {
    // clear timer
    if (timer.current) {
      clearTimeout(timer.current);
    }
    // Get task details
    importExportServices.getTaskDetails({ id: taskId }).then((res) => {
      // Setup task details
      setTaskDetails(res);
      // If the task status is INIT, PROCESSING, RUNNING, continue to poll for task details
      if (
        [ImportExportTaskStatus.INIT, ImportExportTaskStatus.PROCESSING, ImportExportTaskStatus.RUNNING].includes(
          res.taskStatus,
        )
      ) {
        //
        timer.current = setTimeout(() => {
          getTaskDetails();
        }, timerNumber.current);
        // timer time increment
        timerNumber.current = timerNumber.current + 1000;
        return;
      }
      // If the task status is FINISHED, execute the finish callback
      if (res.taskStatus === ImportExportTaskStatus.FINISHED) {
        props.finish?.(res);
      }
      // Get task list
      getTaskList({ visible: true });
    });
  };

  useImperativeHandle(ref, () => ({}));

  useEffect(() => {
    // Scroll to the bottom of the log element when taskDetails?.infoLog changes
    logEndRef.current?.scrollTo({
      top: logEndRef.current.scrollHeight,
      behavior: 'smooth',
    } as ScrollToOptions);
  }, [taskDetails?.infoLog]);

  return (
    <div className={styles.log}>
      <div className={styles.logList}>
        <div className={styles.logListItem}>
          <div className={styles.logListItemLabel}>{i18n('workspace.text.taskName')}：</div>
          <div className={styles.logListItemValue}>{taskDetails?.taskName}</div>
        </div>
        <div className={styles.logListItem}>
          <div className={styles.logListItemLabel}>{i18n('workspace.text.startTime')}：</div>
          <div className={styles.logListItemValue}>{dayjs(taskDetails?.gmtCreate).format('YYYY-MM-DD HH:mm:ss')}</div>
        </div>
      </div>
      <div className={styles.logBody} ref={logEndRef}>
        {taskDetails?.infoLog}
        {taskDetails?.errorLog}
      </div>
      <Progress size="small" percent={Number(taskDetails?.taskProgress || 0)} showInfo={false} />
    </div>
  );
});

export default memo(Log);
