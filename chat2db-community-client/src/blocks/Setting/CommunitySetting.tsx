import i18n from '@/i18n';
import { useEffect, useMemo } from 'react';
import { history } from 'umi';
import About from './About';
import BaseSetting from './BaseSetting';
import EditorSetting from './EditorSetting';
import McpSetting from './McpSetting';
import NetworkProxySetting from './NetworkProxySetting';
import ShortcutSetting from './ShortcutSetting';

import { IconButton, ListItem } from '@chat2db/ui';
import { useStyles } from './style';

import { runtimeEditionConfig } from '@/constants/runtimeEdition';
import { useGlobalStore } from '@/store/global';

function CommunitySetting() {
  const {
    settingPageActiveTab = 'basic',
    setSettingPageActiveTab,
    language,
  } = useGlobalStore((state) => ({
    settingPageActiveTab: state.settingPageActiveTab,
    setSettingPageActiveTab: state.setSettingPageActiveTab,
    language: state.baseSetting.language,
  }));

  const { styles } = useStyles();

  function changeMenu(tab: string) {
    setSettingPageActiveTab(tab);
  }

  const menusList = useMemo(
    () => [
      {
        title: i18n('setting.nav.basic'),
        describe: i18n('setting.nav.basicDescribe'),
        iconCode: 'icon-setting',
        body: <BaseSetting />,
        code: 'basic',
      },
      {
        title: i18n('setting.nav.editSetting'),
        describe: i18n('setting.nav.editSettingDescribe'),
        iconCode: 'icon-clipboard1',
        body: <EditorSetting />,
        code: 'editSetting',
      },
      ...(runtimeEditionConfig.mcpSetting
        ? [
            {
              title: i18n('setting.nav.mcp'),
              describe: i18n('setting.nav.mcpDescribe'),
              iconCode: 'icon-mcp',
              body: <McpSetting />,
              code: 'mcp',
            },
          ]
        : []),
      ...(runtimeEditionConfig.networkProxySetting
        ? [
            {
              title: i18n('setting.nav.networkProxy'),
              describe: i18n('setting.nav.networkProxyDescribe'),
              iconCode: 'icon-wangluo',
              body: <NetworkProxySetting />,
              code: 'networkProxy',
            },
          ]
        : []),
      {
        title: i18n('setting.nav.shortcut'),
        describe: '',
        iconCode: 'icon-a-kuaijiejian1',
        body: <ShortcutSetting />,
        code: 'shortcut',
      },
      {
        title: i18n('setting.nav.aboutUs'),
        describe: i18n('setting.nav.aboutUsDescribe'),
        iconCode: 'icon-exclamation-circle',
        body: <About />,
        code: 'about',
      },
    ],
    [language],
  );

  useEffect(() => {
    if (settingPageActiveTab && !menusList.some((item) => item.code === settingPageActiveTab)) {
      setSettingPageActiveTab('basic');
    }
  }, [menusList, settingPageActiveTab, setSettingPageActiveTab]);

  return (
    <div className={styles.settingBox}>
      <div className={styles.header}>
        <div className={styles.headerTitle}>{i18n('setting.title.setting')}</div>
        <IconButton
          className={styles.headerClose}
          onClick={() => {
            const pathName = window.location.pathname.split('/')[1];
            setSettingPageActiveTab(false);
            if (pathName === 'settings') {
              history.push('/');
            }
          }}
          size="lg"
          code="icon-close"
        />
      </div>
      <div className={styles.content}>
        <div className={styles.left}>
          {menusList.map((item) => (
            <ListItem
              key={item.code}
              isActive={settingPageActiveTab === item.code}
              code={item.iconCode}
              title={item.title}
              onClick={changeMenu.bind(null, item.code)}
            />
          ))}
        </div>
        <div className={styles.menuContent}>
          {menusList.map((item) => (settingPageActiveTab === item.code ? item.body : null))}
        </div>
      </div>
    </div>
  );
}

export default CommunitySetting;
