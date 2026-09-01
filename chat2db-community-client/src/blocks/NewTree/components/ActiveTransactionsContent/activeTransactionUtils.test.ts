import assert from 'node:assert/strict';

import {
  buildSessionInspectionSql,
  canOpenTransactionSession,
  getActiveTransactionRowKey,
  getLiveTransactionAge,
  getTransactionSessionThreadId,
} from './activeTransactionUtils';
import type { IActiveTransactionItem } from '@/service/sql';

const waitingTransaction: IActiveTransactionItem = {
  trxId: '421337',
  state: 'LOCK WAIT',
  startedAt: '2026-08-31 12:00:00',
  ageSeconds: 12,
  isolationLevel: 'REPEATABLE READ',
  rowsLocked: 1,
  rowsModified: 0,
  lockStructs: 2,
  threadId: 45,
  user: 'ops002_user',
  host: '127.0.0.1:50000',
  db: 'ops002_test',
  query: null,
  queryState: 'UNAVAILABLE',
  sessionAvailable: true,
  sessionState: 'LIVE',
  canOpenSession: true,
  waitingLockId: '421337:7:3:2',
  blockingLockId: '421336:7:3:2',
  blockingTrxId: '421336',
  blockingThreadId: 44,
  blockingSessionAvailable: true,
  canOpenBlockingSession: true,
  blockingUser: 'ops002_admin',
  blockingHost: '127.0.0.1:49999',
  blockingDb: 'ops002_test',
  lockMetadataState: 'AVAILABLE',
  lockMetadataSource: 'MYSQL_80_PERFORMANCE_SCHEMA',
};

assert.equal(getActiveTransactionRowKey(waitingTransaction), '421337:421337:7:3:2:45');
assert.equal(canOpenTransactionSession(waitingTransaction, 'owner'), true);
assert.equal(canOpenTransactionSession(waitingTransaction, 'blocker'), true);
assert.equal(getTransactionSessionThreadId(waitingTransaction, 'owner'), 45);
assert.equal(getTransactionSessionThreadId(waitingTransaction, 'blocker'), 44);
assert.equal(getLiveTransactionAge(12, 1_000, 4_900), 15);
assert.equal(getLiveTransactionAge(12, 5_000, 4_000), 12);
assert.equal(getLiveTransactionAge(null, 1_000, 4_900), null);
assert.equal(
  buildSessionInspectionSql(45),
  [
    'SELECT ID, USER, HOST, DB, COMMAND, TIME, STATE, INFO',
    'FROM information_schema.PROCESSLIST',
    'WHERE ID = 45;',
  ].join('\n'),
);

assert.throws(() => buildSessionInspectionSql(-1), /valid MySQL processlist/);
