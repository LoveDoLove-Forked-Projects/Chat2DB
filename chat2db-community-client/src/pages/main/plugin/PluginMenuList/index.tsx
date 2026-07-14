import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import PageTitle from '@/components/PageTitle';
import pluginService from '@/service/plugin';
import { IPluginItem, PluginStatus } from '@/typings/plugin';
import { useStyles } from './style';
import PluginMenuItem from '../PluginMenuItem';
import i18n from '@/i18n';
import { useUserStore } from '@/store/user';
import { staticMessage } from '@chat2db/ui';
interface PluginMenuListProps {
  curPlugin?: IPluginItem;
  onClick: (plugin: IPluginItem) => void;
}
const PluginMenuList = ({ curPlugin, onClick }: PluginMenuListProps) => {
  const [pluginList, setPluginList] = useState<IPluginItem[]>([]);
  const scanPluginObj = useRef({});
  const { styles } = useStyles();

  const { curUser } = useUserStore((s) => ({
    curUser: s.curUser,
  }));

  const isVip = useMemo(() => curUser?.vip || curUser?.activated, [curUser?.vip, curUser?.activated]);

  useEffect(() => {
    queryPluginList();
  }, []);

  const scanPluginCallback = useCallback(
    (event, message) => {
      scanPluginObj.current = message;
      changePluginStatus(pluginList);
    },
    [pluginList],
  );

  useEffect(() => {
    window.electronApi?.ipcRenderer?.on('plugins-scanned', scanPluginCallback);
    return () => {
      window.electronApi?.ipcRenderer?.removeAllListeners('plugins-scanned');
    };
  }, [pluginList]);

  useEffect(() => {
    triggerScanPlugin();
    window.electronApi?.ipcRenderer?.on('plugins-scanned', scanPluginCallback);

    window?.electronApi?.ipcRenderer?.on('message-from-main', (event, message) => {
      console.log('message-from-main', event, message);
      triggerScanPlugin();
    });
  }, []);

  const triggerScanPlugin = () => {
    // Trigger a plugin scan.
    window?.electronApi?.scanPlugin?.();
  };

  const queryPluginList = async () => {
    const res = await pluginService.queryPluginList();
    const newPluginList = res.map((item) => ({ ...item, pluginStatus: PluginStatus.UNINSTALLED }));
    setPluginList(newPluginList);

    if (!curPlugin && newPluginList[0]) {
      if (!isVip) {
        return staticMessage.error(i18n('plugin.item.usage.status.needBuy'));
      } else {
        newPluginList[0].token = await pluginService.queryToken();
      }
      handleClickItem(newPluginList[0]);
    }
    changePluginStatus(newPluginList);
  };

  const changePluginStatus = (pluginList) => {
    const newPluginList = pluginList.map((item) => {
      let pluginStatus = scanPluginObj.current[item.name] ? PluginStatus.INSTALLED : PluginStatus.UNINSTALLED;
      // Mark an installed plugin as pending update when its version differs.
      if (pluginStatus === PluginStatus.INSTALLED && scanPluginObj.current[item.name].version !== item.version) {
        pluginStatus = PluginStatus.UPDATE;
      }

      return {
        ...item,
        pluginStatus,
      };
    });
    setPluginList(newPluginList);
  };

  const handleClickItem = async (plugin) => {
    if (!plugin.token) {
      if (!isVip) {
        return staticMessage.error(i18n('plugin.item.usage.status.needBuy'));
      }
      plugin.token = await pluginService.queryToken();
    }
    onClick(plugin);
  };

  const handleButtonClick = async (plugin) => {
    if (plugin.pluginStatus === PluginStatus.INSTALLED) {
      if (!isVip) {
        return staticMessage.error(i18n('plugin.item.usage.status.needBuy'));
      }
      if (!plugin.token) {
        plugin.token = await pluginService.queryToken();
      }
      window.electronApi?.openPlugin({ pluginName: plugin.name, token: plugin.token });
    }
    if (plugin.pluginStatus === PluginStatus.UNINSTALLED || plugin.pluginStatus === PluginStatus.UPDATE) {
      plugin.pluginStatus = PluginStatus.INSTALLING;
      setPluginList([...pluginList]);
      window.electronApi?.installPlugin(plugin.name);

      // Call the backend to record the installation count.
      pluginService.addPluginDownloadCount({ id: plugin.id });
    }
  };
  return (
    <div className={styles.container}>
      <PageTitle title={i18n('plugin.title')} style={{ padding: '16px' }} />

      <div className={styles.listWrapper}>
        {pluginList.map((plugin) => (
          <PluginMenuItem
            isActive={plugin.name === curPlugin?.name}
            key={plugin.id}
            plugin={plugin}
            onClick={() => onClick(plugin)}
            onButtonClick={() => handleButtonClick(plugin)}
          />
        ))}
      </div>
    </div>
  );
};

export default PluginMenuList;
