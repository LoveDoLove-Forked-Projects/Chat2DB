import { ConsoleStatus } from '@/constants/common';
import { WorkspaceTabType } from '@/constants/workspace';
import {
  ContentDiffDenyReason,
  ContentDiffSourceKind,
  getContentDiffDecorationCount,
  getContentDiffEligibility,
  guardContentDiffTexts,
  isContentDiffHunkBudgetExceeded,
} from './contentDiffGuard';

function assertEqual(actual: any, expected: any, message: string) {
  const actualJson = JSON.stringify(actual);
  const expectedJson = JSON.stringify(expected);
  if (actualJson !== expectedJson) {
    throw new Error(`${message}: expected ${expectedJson}, got ${actualJson}`);
  }
}

function assert(condition: boolean, message: string) {
  if (!condition) {
    throw new Error(message);
  }
}

assertEqual(
  getContentDiffEligibility({
    editorType: WorkspaceTabType.VIEW,
    readOnly: false,
    dbInfo: {
      dataSourceId: 1,
      databaseName: 'app',
      schemaName: 'public',
      viewName: 'v_user',
    },
  }).sourceKind,
  ContentDiffSourceKind.EditableDDL,
  'allow editable ddl view',
);

assertEqual(
  getContentDiffEligibility({
    editorType: WorkspaceTabType.CONSOLE,
    readOnly: false,
    dbInfo: {
      consoleId: 12,
      status: ConsoleStatus.RELEASE,
    },
  }),
  {
    enabled: true,
    sourceKind: ContentDiffSourceKind.SavedSQL,
    sourceId: 'console:12',
  },
  'allow saved sql record',
);

assertEqual(
  getContentDiffEligibility({
    editorType: WorkspaceTabType.LocalSQLFile,
    readOnly: false,
    dbInfo: {
      filePath: '/tmp/a.sql',
    },
  }),
  {
    enabled: true,
    sourceKind: ContentDiffSourceKind.LocalSQLFile,
    sourceId: 'file:/tmp/a.sql',
  },
  'allow local sql file',
);

assertEqual(
  getContentDiffEligibility({
    editorType: WorkspaceTabType.CONSOLE,
    readOnly: false,
    dbInfo: {
      status: ConsoleStatus.DRAFT,
    },
  }).reason,
  ContentDiffDenyReason.UnsavedSQL,
  'deny unsaved sql record before status check',
);

assertEqual(
  getContentDiffEligibility({
    editorType: WorkspaceTabType.CONSOLE,
    readOnly: false,
    dbInfo: {
      consoleId: 'chat2db_temporary_1',
      status: ConsoleStatus.RELEASE,
    },
  }).reason,
  ContentDiffDenyReason.UnsavedSQL,
  'deny temporary sql record',
);

assertEqual(
  getContentDiffEligibility({
    editorType: WorkspaceTabType.CONSOLE,
    readOnly: false,
    dbInfo: {
      consoleId: 12,
      status: ConsoleStatus.DRAFT,
    },
  }).reason,
  ContentDiffDenyReason.UnsupportedStatus,
  'deny draft saved sql record',
);

assertEqual(
  getContentDiffEligibility({
    editorType: WorkspaceTabType.FUNCTION,
    readOnly: false,
    dbInfo: {
      dataSourceId: 1,
      databaseName: 'app',
    },
  }).reason,
  ContentDiffDenyReason.MissingSource,
  'deny ddl without object name',
);

assertEqual(
  getContentDiffEligibility({
    editorType: WorkspaceTabType.VIEW,
    readOnly: true,
    dbInfo: {
      viewName: 'v_user',
    },
  }).reason,
  ContentDiffDenyReason.ReadOnly,
  'deny read only editor',
);

assertEqual(
  guardContentDiffTexts('select 1', 'select 1').reason,
  ContentDiffDenyReason.Unchanged,
  'skip unchanged content',
);

assert(guardContentDiffTexts('select 1', 'select 2').enabled, 'allow changed content');

assertEqual(
  guardContentDiffTexts('a'.repeat(200001), 'b').reason,
  ContentDiffDenyReason.TextTooLarge,
  'deny large baseline',
);

assertEqual(
  getContentDiffDecorationCount([
    {
      currentRange: {
        startLineNumber: 3,
        endLineNumber: 5,
      },
    },
    {
      currentRange: {
        startLineNumber: 8,
        endLineNumber: 8,
      },
    },
  ]),
  4,
  'count current-line decorations',
);

assert(
  isContentDiffHunkBudgetExceeded({ hunkCount: 501, decorationCount: 1 }),
  'deny too many hunks',
);

assert(
  isContentDiffHunkBudgetExceeded({ hunkCount: 1, decorationCount: 1001 }),
  'deny too many decorations',
);

assert(
  !isContentDiffHunkBudgetExceeded({ hunkCount: 500, decorationCount: 1000 }),
  'allow budget boundary',
);
