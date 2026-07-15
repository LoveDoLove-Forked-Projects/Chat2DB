import { memo, useMemo } from 'react';
import { ConfigProvider as AntdConfigProvider, ConfigProviderProps } from 'antd';
import { useGlobalStore } from '@/store/global';
import { settingSelectors } from '@/store/global/selectors';
import { LangType } from '@/constants/settings';
import enUS from 'antd/locale/en_US';
import jaJP from 'antd/locale/ja_JP';
import zhCN from 'antd/locale/zh_CN';
import dayjs from 'dayjs';

import 'dayjs/locale/zh-cn';
import 'dayjs/locale/ja';
import 'dayjs/locale/en';

dayjs.locale('en');
dayjs.locale('ja');

const ConfigProvider = memo<ConfigProviderProps>(({ children }) => {
  const { language } = useGlobalStore((state) => {
    return {
      ...settingSelectors.currentBaseSetting(state),
    };
  });

  const locale = useMemo(() => {
    switch (language) {
      case LangType.ZH_CN:
        return zhCN;
      case LangType.JA_JP:
        return jaJP;
      default:
        return enUS;
    }
  }, [language]);

  return <AntdConfigProvider locale={locale}>{children}</AntdConfigProvider>;
});

export default ConfigProvider;
