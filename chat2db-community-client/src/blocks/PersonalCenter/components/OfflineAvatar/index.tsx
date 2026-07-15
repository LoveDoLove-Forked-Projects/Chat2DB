import { i18n } from '@/i18n';
import { useGlobalStore } from '@/store/global';
import Logo from '@/components/Logo';
import { Tooltip } from 'antd';

const OfflineAvatar = () => {
  const { setSettingPageActiveTab } = useGlobalStore((state) => {
    return {
      setSettingPageActiveTab: state.setSettingPageActiveTab,
    };
  });
  const handleClick = () => {
    setSettingPageActiveTab('basic');
  };
  return (
    <Tooltip title={i18n('setting.title.setting')} placement="right">
      <div onClick={handleClick} style={{ cursor: 'pointer' }}>
        <Logo size={36} />
      </div>
    </Tooltip>
  );
};

export default OfflineAvatar;
