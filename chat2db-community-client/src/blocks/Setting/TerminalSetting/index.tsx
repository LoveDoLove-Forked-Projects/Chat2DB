import { DEFAULT_TERMINAL_SETTINGS, TERMINAL_THEMES } from '@/constants/terminal';
import i18n from '@/i18n';
import jcefApi from '@/jcef';
import { useGlobalStore } from '@/store/global';
import type { TerminalShellId, TerminalThemeId } from '@/typings/settings';
import { Alert, Select, Spin } from 'antd';
import { ChevronDown } from 'lucide-react';
import { useEffect, useState } from 'react';
import SettingSubsection from '../SettingSubsection';
import { useStyles } from './style';

interface ShellOption {
  id: TerminalShellId;
  label: string;
  available: boolean;
}

const themeOptions = Object.values(TERMINAL_THEMES);

export default function TerminalSetting() {
  const { styles, cx } = useStyles();
  const { terminalSettings, updateTerminalSettings } = useGlobalStore((state) => ({
    terminalSettings: {
      ...DEFAULT_TERMINAL_SETTINGS,
      ...state.terminalSettings,
    },
    updateTerminalSettings: state.updateTerminalSettings,
  }));
  const [shells, setShells] = useState<ShellOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadFailed, setLoadFailed] = useState(false);

  useEffect(() => {
    let disposed = false;
    jcefApi
      .getTerminalCapabilities()
      .then((capabilities) => {
        if (disposed) {
          return;
        }
        const availableShells = capabilities.shells.filter((shell) => shell.available) as ShellOption[];
        setShells(availableShells);
        if (!availableShells.some((shell) => shell.id === terminalSettings.shellId)) {
          updateTerminalSettings({ shellId: 'system' });
        }
      })
      .catch((error) => {
        console.error('get terminal capabilities error', error);
        if (!disposed) {
          setLoadFailed(true);
        }
      })
      .finally(() => {
        if (!disposed) {
          setLoading(false);
        }
      });
    return () => {
      disposed = true;
    };
  }, [terminalSettings.shellId, updateTerminalSettings]);

  return (
    <div className={styles.container}>
      <section>
        <SettingSubsection
          title={i18n('setting.terminal.shell')}
          describe={i18n('setting.terminal.shellDescribe')}
        />
        {loading ? (
          <Spin size="small" />
        ) : loadFailed ? (
          <Alert type="warning" showIcon message={i18n('setting.terminal.capabilitiesFailed')} />
        ) : (
          <Select
            value={terminalSettings.shellId}
            className={styles.shellSelect}
            suffixIcon={<ChevronDown size={14} />}
            options={shells.map((shell) => ({ value: shell.id, label: shell.label }))}
            onChange={(shellId: TerminalShellId) => updateTerminalSettings({ shellId })}
          />
        )}
        <div className={styles.hint}>{i18n('setting.terminal.shellApplyHint')}</div>
      </section>

      <section>
        <SettingSubsection
          title={i18n('setting.terminal.theme')}
          describe={i18n('setting.terminal.themeDescribe')}
        />
        <div className={styles.themeGrid}>
          {themeOptions.map((config) => {
            const colors = [
              config.theme.red,
              config.theme.yellow,
              config.theme.green,
              config.theme.cyan,
              config.theme.blue,
              config.theme.magenta,
            ];
            return (
              <button
                key={config.id}
                type="button"
                className={cx(styles.themeCard, {
                  [styles.activeThemeCard]: terminalSettings.themeId === config.id,
                })}
                style={{
                  backgroundColor: config.theme.background,
                  color: config.theme.foreground,
                }}
                onClick={() => updateTerminalSettings({ themeId: config.id as TerminalThemeId })}
              >
                <span className={styles.themeTitle}>{config.name}</span>
                <span className={styles.colorRow}>
                  {colors.map((color, index) => (
                    <span key={`${config.id}-${index}`} style={{ backgroundColor: color }} />
                  ))}
                </span>
                <span className={styles.commandPreview}>
                  <span style={{ color: config.theme.green }}>$</span> npm run dev
                </span>
              </button>
            );
          })}
        </div>
        <div className={styles.hint}>{i18n('setting.terminal.themeApplyHint')}</div>
      </section>
    </div>
  );
}
