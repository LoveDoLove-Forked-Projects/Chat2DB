import {
  ABSTRACT_TAB_ID,
  CONSOLE_TAB_ID,
  MESSAGES_TAB_ID,
  getPreferredActiveTabId,
  resolveAvailableActiveTabId,
} from './tabSelection';
import type { IManageResultData } from '@/typings';

function assertEqual(actual: unknown, expected: unknown, message: string) {
  if (actual !== expected) {
    throw new Error(`${message}: expected ${String(expected)}, got ${String(actual)}`);
  }
}

function result(overrides: Partial<IManageResultData> = {}): IManageResultData {
  return {
    dataList: [],
    headerList: [],
    description: '',
    sql: '',
    originalSql: '',
    success: true,
    duration: 0,
    sqlType: 'SELECT' as any,
    refreshTargets: [],
    pageNo: 1,
    pageSize: 200,
    fuzzyTotal: '0',
    hasNextPage: false,
    ...overrides,
  };
}

const tableResult = result({
  uuid: 'table-result',
  headerList: [{ name: '#' }, { name: 'id' }] as any,
});

let activeTabId = getPreferredActiveTabId(tableResult, false);
activeTabId = resolveAvailableActiveTabId(activeTabId, [ABSTRACT_TAB_ID, 'table-result']);

assertEqual(
  activeTabId,
  'table-result',
  'view-table result selection survives fallback validation in the same React update',
);
assertEqual(getPreferredActiveTabId(tableResult, true), 'table-result', 'console queries open a new tabular result');
assertEqual(
  getPreferredActiveTabId(result({ uuid: 'message-only', extra: { messageOnly: true } }), false),
  MESSAGES_TAB_ID,
  'message-only legacy results open the messages tab',
);
assertEqual(
  getPreferredActiveTabId(result({ uuid: 'command-result' }), false),
  ABSTRACT_TAB_ID,
  'successful non-tabular legacy results open the summary',
);
assertEqual(
  getPreferredActiveTabId(result({ uuid: 'failed-result', success: false }), true),
  CONSOLE_TAB_ID,
  'console failures open the execution console',
);
assertEqual(
  resolveAvailableActiveTabId('table-result', [ABSTRACT_TAB_ID, 'table-result']),
  'table-result',
  'fallback validation preserves a result selected earlier in the same React update',
);
assertEqual(
  resolveAvailableActiveTabId('closed-result', [ABSTRACT_TAB_ID, 'next-result']),
  ABSTRACT_TAB_ID,
  'closing the active result selects the first available tab',
);
assertEqual(resolveAvailableActiveTabId('closed-result', []), '', 'removing all tabs clears the active tab');

console.log('SearchResult tab selection tests passed');
