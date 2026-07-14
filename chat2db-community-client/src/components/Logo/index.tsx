import React, { memo } from 'react';
import { useStyles } from './style';
import { Logo, type LogoProps } from '@chat2db/ui';
import { APP_CONFIG } from '@/constants/appConfig';

export default memo<LogoProps>((props) => {
  return <Logo appName={APP_CONFIG.name} {...props} />;
});
