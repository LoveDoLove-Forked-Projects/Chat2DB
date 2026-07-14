import React, { memo } from 'react';
import { useStyles } from './style';
import { IconButton } from '@chat2db/ui';
import { refreshPage } from '@/utils';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const { styles, cx } = useStyles();
  return <IconButton code="icon-refresh" onClick={refreshPage} />;
});
