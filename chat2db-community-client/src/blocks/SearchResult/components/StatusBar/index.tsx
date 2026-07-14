import React, { memo, useEffect } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { useStyles } from './style';
import { IManageResultData } from '@/typings';

interface IProps {
  className?: string;
  resultData: IManageResultData;
}

export default memo<IProps>((props) => {
  const { className, resultData } = props;
  const { styles } = useStyles();
  if (!resultData) return null;

  const { description, duration } = resultData;
  const dataLength = resultData.dataList?.length;

  return (
    <div className={classnames(styles.statusBar, className)}>
      <span>{`【${i18n('common.text.result')}】${description}.`}</span>
      <span>{`【${i18n('common.text.timeConsuming')}】${duration}ms.`}</span>
      {!!dataLength && <span>{`【${i18n('common.text.searchRow')}】${dataLength} ${i18n('common.text.row')}.`}</span>}
    </div>
  );
});
