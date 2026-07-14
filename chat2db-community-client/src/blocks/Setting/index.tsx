import PurchaseDetails from '@/components/PurchaseDetails';
import i18n from '@/i18n';
import { useEffect, useMemo } from 'react';
import { history } from 'umi';
import About from './About';
import BaseSetting from './BaseSetting';
import EditorSetting from './EditorSetting';
import Invite from './Invite';
import License from './License';
import McpSetting from './McpSetting';
import NetworkProxySetting from './NetworkProxySetting';
import Personal from './Personal';

import { IconButton, ListItem } from '@chat2db/ui';
import { useStyles } from './style';

// ---- store -----
import { runtimeEditionConfig } from '@/constants/runtimeEdition';
import { useGlobalStore } from '@/store/global';
import DeviceCer from './DeviceCer';
import ShortcutSetting from './ShortcutSetting';

function Setting() {
  const {
    settingPageActiveTab = 'basic',
    setSettingPageActiveTab,
    language,
    isCN,
  } = useGlobalStore((state) => {
    return {
      settingPageActiveTab: state.settingPageActiveTab,
      setSettingPageActiveTab: state.setSettingPageActiveTab,
      language: state.baseSetting.language,
      isCN: state.appConfig.isCN,
    };
  });

  const { styles } = useStyles();

  function changeMenu(t: any) {
    setSettingPageActiveTab(t);
  }

  const menusList = useMemo(() => {
    if (runtimeEditionConfig.settingMenuProfile === 'community') {
      return [
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
      ];
    }

    if (runtimeEditionConfig.settingMenuProfile === 'local') {
      return [
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
        ...(runtimeEditionConfig.licenseSetting
          ? [
              {
                title: i18n('setting.license.title'),
                describe: i18n('setting.license.titleDes'),
                iconCode: 'icon-plus-1',
                body: <License />,
                code: 'license',
              },
            ]
          : []),
        {
          title: i18n('setting.nav.aboutUs'),
          describe: i18n('setting.nav.aboutUsDescribe'),
          iconCode: 'icon-exclamation-circle',
          body: <About />,
          code: 'about',
        },
      ];
    }

    const list = [
      {
        title: i18n('setting.nav.basic'),
        describe: i18n('setting.nav.basicDescribe'),
        iconCode: 'icon-setting',
        body: <BaseSetting />,
        code: 'basic',
      },
      {
        title: i18n('setting.nav.personal'),
        describe: i18n('setting.nav.personalDescribe'),
        iconCode: 'icon-user-circle',
        body: <Personal />,
        code: 'personal',
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
      // {
      //   title: i18n('setting.nav.apiKeys'),
      //   describe: '',
      //   iconCode: 'icon-apikeys',
      //   body: <ApiKeys />,
      //   code: 'apiKeys',
      // },
      {
        title: i18n('invite.setting.nav.title'),
        describe: '',
        iconCode: 'icon-plus-1',
        body: <Invite />,
        code: 'invite',
      },
      {
        title: i18n('setting.purchaseDetails.title'),
        describe: '',
        iconCode: 'icon-purchase-details',
        body: <PurchaseDetails />,
        code: 'purchase',
      },
      {
        title: i18n('license.deviceCertificateTitle'),
        describe: '',
        iconCode: 'icon-xingzhuang',
        body: <DeviceCer />,
        code: 'deviceCer',
      },
      {
        title: i18n('setting.nav.aboutUs'),
        describe: i18n('setting.nav.aboutUsDescribe'),
        iconCode: 'icon-exclamation-circle',
        body: <About />,
        code: 'about',
      },
    ];

    return list;
  }, [language, isCN]);

  useEffect(() => {
    if (settingPageActiveTab && !menusList.some((t) => t.code === settingPageActiveTab)) {
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
          {menusList.map((t) => {
            return (
              <ListItem
                key={t.code}
                isActive={settingPageActiveTab === t.code}
                code={t.iconCode}
                title={t.title}
                onClick={changeMenu.bind(null, t.code)}
              />
            );
          })}
        </div>
        <div className={styles.menuContent}>
          {menusList.map((t) => {
            return settingPageActiveTab === t.code ? t.body : null;
          })}
        </div>
      </div>
    </div>
  );
}

export default Setting;
