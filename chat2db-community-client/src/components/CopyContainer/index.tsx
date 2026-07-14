import React, { memo } from 'react';
import { useStyles } from './style';
import { copyToClipboard } from '@/utils';

interface IProps {
  className?: string;
  children: string;
  copyText?: string;
}

export default memo<IProps>((props) => {
  const { className, children,copyText } = props;
  const { styles, cx } = useStyles();

  const handleCopy = () => {
    copyToClipboard(copyText || children);
  }
  
  return <span className={styles.copyContainer} onClick={handleCopy}>{children}</span>;
});
