import React, { memo, useEffect, useRef, useState } from 'react';
import { useStyles } from './style';
import PurchaseDetails from '@/components/PurchaseDetails';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const { styles, cx } = useStyles();

  return <PurchaseDetails />;
});
