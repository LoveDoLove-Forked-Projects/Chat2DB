import assert from 'node:assert/strict';
import { WorkspaceTabType } from '@/constants/workspace';
import { createQuickTerminalTab } from './quickTerminal';

const tab = createQuickTerminalTab(
  {
    sessionId: 'session-1',
    cwd: '/Users/chat2db',
    shell: 'Zsh',
    shellId: 'zsh',
  },
  'Terminal',
);

assert.deepEqual(tab, {
  id: `${WorkspaceTabType.Terminal}:session-1`,
  type: WorkspaceTabType.Terminal,
  title: 'Terminal',
  uniqueData: {
    terminalSessionId: 'session-1',
    terminalCwd: '/Users/chat2db',
    terminalShell: 'Zsh',
    terminalShellId: 'zsh',
  },
});

console.log('Quick terminal tab tests passed');
