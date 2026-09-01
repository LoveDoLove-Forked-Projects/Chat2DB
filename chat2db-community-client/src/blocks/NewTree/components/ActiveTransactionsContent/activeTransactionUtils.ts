import type { IActiveTransactionItem } from '@/service/sql';

export function getActiveTransactionRowKey(record: IActiveTransactionItem): string {
  return [
    record.trxId || 'no-trx',
    record.waitingLockId || 'no-wait',
    record.threadId == null ? 'no-thread' : String(record.threadId),
  ].join(':');
}

export function getLiveTransactionAge(
  ageSeconds: number | null,
  snapshotAtMs: number,
  nowMs: number,
): number | null {
  if (ageSeconds == null) {
    return null;
  }
  return ageSeconds + Math.max(0, Math.floor((nowMs - snapshotAtMs) / 1000));
}

export function canOpenTransactionSession(record: IActiveTransactionItem, target: 'owner' | 'blocker'): boolean {
  if (target === 'blocker') {
    return Boolean(record.canOpenBlockingSession && record.blockingThreadId != null);
  }
  return Boolean(record.canOpenSession && record.threadId != null);
}

export function getTransactionSessionThreadId(
  record: IActiveTransactionItem,
  target: 'owner' | 'blocker',
): number | null {
  if (!canOpenTransactionSession(record, target)) {
    return null;
  }
  return target === 'blocker' ? record.blockingThreadId! : record.threadId!;
}

export function buildSessionInspectionSql(threadId: number): string {
  if (!Number.isSafeInteger(threadId) || threadId < 0) {
    throw new Error('A valid MySQL processlist thread ID is required.');
  }
  return [
    'SELECT ID, USER, HOST, DB, COMMAND, TIME, STATE, INFO',
    'FROM information_schema.PROCESSLIST',
    `WHERE ID = ${threadId};`,
  ].join('\n');
}
