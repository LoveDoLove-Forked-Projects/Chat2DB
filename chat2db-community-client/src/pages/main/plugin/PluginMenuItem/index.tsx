import React from 'react';
import { IPluginItem, PluginStatus } from '@/typings/plugin';
import { useStyles } from './style';
import { Flex, Spin } from 'antd';
import i18n from '@/i18n';

interface PluginMenuItemProps {
  plugin: IPluginItem;
  isActive: boolean;
  onClick?: () => void;
  onButtonClick?: () => void;
}

const PluginMenuItem = ({ plugin, isActive, onClick, onButtonClick }: PluginMenuItemProps) => {
  const { styles } = useStyles({ isActive });

  const renderButton = () => {
    const statusToAction = {
      [PluginStatus.INSTALLED]: 'launch',
      [PluginStatus.UNINSTALLED]: 'install',
      [PluginStatus.INSTALLING]: 'installing',
      [PluginStatus.UPDATE]: 'update',
    };

    const action = statusToAction[plugin.pluginStatus];

    if (!action) return null;

    if (action === statusToAction[PluginStatus.INSTALLING]) {
      return <Spin spinning={true} />;
    }

    return (
      <div
        className={styles[`${action}Button`]}
        onClick={(e) => {
          e.stopPropagation();
          onButtonClick && onButtonClick();
        }}
      >
        {i18n(`plugin.${action}`)}
      </div>
    );
  };

  return (
    <div className={styles.container} onClick={onClick}>
      <div className={styles.icon}>
        <img src={plugin.icon} alt={plugin.name} />
      </div>
      <Flex justify="space-between" align="center" flex={1}>
        <Flex vertical>
          <div className={styles.title}>{plugin.name}</div>
          <div className={styles.description}>{plugin.description}</div>
        </Flex>

        {renderButton()}
      </Flex>
    </div>
  );
};

export default PluginMenuItem;
