import { WorkspaceTabType } from '@/constants/workspace';
import type { IWorkspaceTab } from '@/typings/workspace';

export type WorkspaceLeftPanel = 'explorer' | 'database';

export type DirectActiveTabLocateTarget =
  | {
      surface: 'localFile';
      filePath: string;
    }
  | {
      surface: 'databaseTree';
    };

export function resolveWorkspaceLeftPanel(panel?: WorkspaceLeftPanel): WorkspaceLeftPanel {
  return panel || 'database';
}

export function getDirectActiveTabLocateTarget(
  activeTab?: IWorkspaceTab | null,
): DirectActiveTabLocateTarget | null | undefined {
  if (!activeTab) {
    return null;
  }

  if (activeTab.type === WorkspaceTabType.CONSOLE) {
    return activeTab.uniqueData?.dataSourceId ? { surface: 'databaseTree' } : null;
  }

  if (activeTab.type === WorkspaceTabType.LocalSQLFile) {
    const filePath = activeTab.uniqueData?.filePath;
    return filePath ? { surface: 'localFile', filePath } : null;
  }

  return undefined;
}
