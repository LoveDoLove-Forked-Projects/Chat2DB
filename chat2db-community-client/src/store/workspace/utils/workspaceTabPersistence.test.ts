import assert from 'node:assert/strict';
import { WorkspaceTabType } from '@/constants/workspace';
import type { IWorkspaceTab } from '@/typings/workspace';
import { getPersistableWorkspaceTabList } from './workspaceTabPersistence';

const tabs: IWorkspaceTab[] = [
  {
    id: 'markdown',
    type: WorkspaceTabType.LocalSQLFile,
    title: 'README.md',
    uniqueData: { filePath: '/tmp/README.md', fileExtension: 'md', ddl: '# Hello' },
  },
  {
    id: 'image',
    type: WorkspaceTabType.LocalSQLFile,
    title: 'diagram.png',
    uniqueData: {
      filePath: '/tmp/diagram.png',
      fileExtension: 'png',
      filePreviewMimeType: 'image/png',
      filePreviewUrl: 'chat2db-resource://preview/root/image',
    },
  },
  {
    id: 'terminal',
    type: WorkspaceTabType.Terminal,
    title: 'Terminal',
    uniqueData: { terminalSessionId: 'session-1', terminalCwd: '/tmp' },
  },
];

assert.deepEqual(
  getPersistableWorkspaceTabList(tabs)?.map((tab) => tab.id),
  ['markdown'],
  'ephemeral binary previews and PTY sessions must not be written to workspace storage',
);

console.log('workspace tab persistence tests passed');
