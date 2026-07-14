import React, { memo } from 'react';
import RunSqlModal from './components/RunSqlModal';
import ImportFileModal from './components/RunSql';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  return (
    <>
      <RunSqlModal />
      <ImportFileModal />
    </>
  );
});
