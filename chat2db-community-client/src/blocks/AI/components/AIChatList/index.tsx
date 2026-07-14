import React from 'react';
import { useStyles } from './style';

export interface IProps {
  // currently streaming output?
  isStreaming?: boolean;
}

const AIChatList = (props: IProps) => {
  const { isStreaming = false } = props;
  const { styles } = useStyles();

  return <div className={styles.container}>AIChatList</div>;
};

export default AIChatList;
