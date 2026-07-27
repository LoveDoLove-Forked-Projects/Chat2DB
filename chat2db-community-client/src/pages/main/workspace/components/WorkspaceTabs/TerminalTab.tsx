import { memo, useCallback, useEffect, useRef, useState } from 'react';
import Xterm, { IXtermRef } from '@/components/Xterm';
import i18n from '@/i18n';
import jcefApi from '@/jcef';
import { JavaPushActionType, JcefEventBus } from '@/jcef/eventBus';
import styles from './TerminalTab.less';
import { useGlobalStore } from '@/store/global';
import { DEFAULT_TERMINAL_SETTINGS, getTerminalTheme } from '@/constants/terminal';

interface TerminalTabProps {
  sessionId: string;
  cwd?: string;
  shell?: string;
}

const terminalMounts = new Map<string, { count: number; killTimer?: ReturnType<typeof setTimeout> }>();

const TerminalTab = memo(({ sessionId, cwd, shell }: TerminalTabProps) => {
  const terminalRef = useRef<IXtermRef>(null);
  const [exited, setExited] = useState(false);
  const themeId = useGlobalStore(
    (state) => state.terminalSettings?.themeId || DEFAULT_TERMINAL_SETTINGS.themeId,
  );
  const terminalTheme = getTerminalTheme(themeId).theme;

  useEffect(() => {
    const outputEvent = `${JavaPushActionType.TERMINAL_OUTPUT}_${sessionId}`;
    const exitEvent = `${JavaPushActionType.TERMINAL_EXIT}_${sessionId}`;
    const handleOutput = (message: { data?: string }) => {
      if (message?.data) {
        terminalRef.current?.xtermWrite(message.data);
      }
    };
    const handleExit = (message: { exitCode?: number }) => {
      terminalRef.current?.xtermWrite(
        `\r\n[${i18n('workspace.terminal.exited')}: ${message?.exitCode ?? '-'}]\r\n`,
      );
      setExited(true);
    };
    JcefEventBus.on(outputEvent, handleOutput);
    JcefEventBus.on(exitEvent, handleExit);
    const mountState = terminalMounts.get(sessionId) || { count: 0 };
    if (mountState.killTimer) {
      clearTimeout(mountState.killTimer);
    }
    terminalMounts.set(sessionId, { count: mountState.count + 1 });
    jcefApi.attachTerminal({ sessionId }).catch((error) => {
      console.error('attach terminal error', error);
      setExited(true);
    });
    return () => {
      JcefEventBus.off(outputEvent, handleOutput);
      JcefEventBus.off(exitEvent, handleExit);
      const currentMountState = terminalMounts.get(sessionId);
      const count = Math.max((currentMountState?.count || 1) - 1, 0);
      if (count > 0) {
        terminalMounts.set(sessionId, { count });
        return;
      }
      const killTimer = setTimeout(() => {
        terminalMounts.delete(sessionId);
        jcefApi.killTerminal({ sessionId }).catch(() => undefined);
      }, 500);
      terminalMounts.set(sessionId, { count: 0, killTimer });
    };
  }, [sessionId]);

  const handleData = useCallback(
    (data: string) => {
      if (!exited) {
        jcefApi.writeTerminal({ sessionId, data }).catch((error) => {
          console.error('write terminal error', error);
        });
      }
    },
    [exited, sessionId],
  );

  const handleResize = useCallback(
    (columns: number, rows: number) => {
      if (!exited) {
        jcefApi.resizeTerminal({ sessionId, columns, rows }).catch(() => undefined);
      }
    },
    [exited, sessionId],
  );

  return (
    <Xterm
      ref={terminalRef}
      className={styles.terminal}
      readOnly={exited}
      theme={terminalTheme}
      onData={handleData}
      onResize={handleResize}
      xtermHeaderSlot={
        <div
          className={styles.header}
          style={{
            color: terminalTheme.foreground,
            backgroundColor: terminalTheme.background,
            borderBottomColor: terminalTheme.brightBlack,
          }}
        >
          <span className={styles.location}>
            {shell ? (
              <>
                <span style={{ color: terminalTheme.blue }}>{shell}</span>
                <span style={{ color: terminalTheme.brightBlack }}> · </span>
              </>
            ) : null}
            <span style={{ color: terminalTheme.green }}>{cwd}</span>
          </span>
        </div>
      }
    />
  );
});

export default TerminalTab;
