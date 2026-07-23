import assert from 'node:assert/strict';
import { WorkspaceTabType, initUserConfigTree } from '@/constants/workspace';
import type { IWorkspaceTab } from '@/typings';
import {
  getAutoFollowWorkspaceLeftPanel,
  getDirectActiveTabLocateTarget,
  resolveWorkspaceLeftPanel,
} from './activeTabTarget';

function consoleTab(uniqueData: IWorkspaceTab['uniqueData']): IWorkspaceTab {
  return {
    id: 101,
    type: WorkspaceTabType.CONSOLE,
    title: 'Query',
    uniqueData,
  };
}

for (const uniqueData of [
  { dataSourceId: 7 },
  { dataSourceId: 7, databaseName: 'app' },
  { dataSourceId: 7, databaseName: 'app', schemaName: 'public' },
]) {
  assert.deepEqual(getDirectActiveTabLocateTarget(consoleTab(uniqueData)), { surface: 'databaseTree' });
}

assert.deepEqual(
  getDirectActiveTabLocateTarget({
    id: 'local-file',
    type: WorkspaceTabType.LocalSQLFile,
    title: 'local.sql',
    uniqueData: { filePath: '/tmp/local.sql' },
  }),
  {
    surface: 'localFile',
    filePath: '/tmp/local.sql',
  },
);

assert.equal(getDirectActiveTabLocateTarget(consoleTab(undefined)), null);
assert.equal(initUserConfigTree.workspaceLeftPanel, 'database');
assert.equal(resolveWorkspaceLeftPanel(undefined), 'database');
assert.equal(resolveWorkspaceLeftPanel('explorer'), 'explorer');
assert.equal(getAutoFollowWorkspaceLeftPanel(true, { surface: 'databaseTree' }), 'database');
assert.equal(getAutoFollowWorkspaceLeftPanel(true, { surface: 'localFile' }), 'explorer');
assert.equal(getAutoFollowWorkspaceLeftPanel(false, { surface: 'databaseTree' }), undefined);

console.log('Active workspace tab locator tests passed');
