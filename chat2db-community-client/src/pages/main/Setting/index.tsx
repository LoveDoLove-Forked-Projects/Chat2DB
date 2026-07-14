import BaseSetting from '@/blocks/Setting/BaseSetting';
import AiSetting from '@/blocks/Setting/AiSetting';
import EditorSetting from '@/blocks/Setting/EditorSetting';
import ShortcutSetting from '@/blocks/Setting/ShortcutSetting';

const settingComponents = {
  base: BaseSetting,
  ai: AiSetting,
  editor: EditorSetting,
  shortcut: ShortcutSetting,
};

// Add shortcut settings to the menu configuration.
const menuItems = [
  {
    key: 'base',
    label: 'Basic Settings',
  },
  {
    key: 'ai',
    label: 'AI Settings',
  },
  {
    key: 'editor',
    label: 'Editor Settings',
  },
  {
    key: 'shortcut',
    label: 'Keyboard Shortcuts',
  },
];
