import React, { memo, useEffect } from 'react';
import { useStyles } from './style';
import { Modal } from 'antd';
import { useGlobalStore } from '@/store/global';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const { styles, cx } = useStyles();
  const [modal, contextHolder] = Modal.useModal();
  const setDeleteModal = useGlobalStore((s) => s.setDeleteModal);

  useEffect(() => {
    setDeleteModal(modal);
  }, []);

  return <>{contextHolder}</>;
});
