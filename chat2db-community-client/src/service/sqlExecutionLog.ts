import type { IExecutionContext, IExecutionMetrics, IManageResultData } from '@/typings';
import type { SqlExecutionEvent, SqlExecutionMessage } from './sqlExecutionStream';

export type SqlExecutionLogStatus = 'running' | 'success' | 'failed' | 'cancelled';

export interface SqlExecutionLogContext {
  dataSourceId?: number;
  dataSourceName?: string;
  databaseType?: string;
  databaseName?: string;
  schemaName?: string;
}

export interface SqlExecutionLogMessageOutput {
  kind: 'message';
  id: string;
  occurredAtEpochMs: number;
  level: 'INFO' | 'WARN' | 'ERROR';
  message: string;
  errorCode?: number;
  sqlState?: string | null;
  source?: string;
}

export interface SqlExecutionLogResultOutput {
  kind: 'result';
  id: string;
  occurredAtEpochMs: number;
  resultKey?: string;
  resultSequence?: number;
  success: boolean;
  rowCount?: number;
  updateCount?: number;
  durationMs?: number;
  executionMetrics?: IExecutionMetrics;
  message?: string;
}

export type SqlExecutionLogOutput = SqlExecutionLogMessageOutput | SqlExecutionLogResultOutput;

export interface SqlExecutionLogRecord {
  id: string;
  executionId: string;
  statementSequence: number;
  startedAtEpochMs: number;
  finishedAtEpochMs?: number;
  status: SqlExecutionLogStatus;
  sql: string;
  comment?: string;
  context: SqlExecutionLogContext;
  outputs: SqlExecutionLogOutput[];
  pendingRowCounts: Record<string, number>;
  durationMs?: number;
  temporary?: boolean;
}

export interface SqlExecutionLogState {
  records: SqlExecutionLogRecord[];
}

export interface BeginWebSqlExecutionParams {
  executionId: string;
  sql: string;
  context: SqlExecutionLogContext;
  occurredAtEpochMs?: number;
}

export interface CompleteWebSqlExecutionParams extends BeginWebSqlExecutionParams {
  results: IManageResultData[];
}

export interface FailWebSqlExecutionParams extends BeginWebSqlExecutionParams {
  error: unknown;
}

const MAX_RECORDS = 200;

export function createSqlExecutionLogState(): SqlExecutionLogState {
  return { records: [] };
}

export function clearSqlExecutionLog(state: SqlExecutionLogState): SqlExecutionLogState {
  return {
    records: state.records
      .filter((record) => record.status === 'running')
      .map((record) => ({ ...record, outputs: [] })),
  };
}

export function beginWebSqlExecution(
  state: SqlExecutionLogState,
  params: BeginWebSqlExecutionParams,
): SqlExecutionLogState {
  if (state.records.some((record) => record.executionId === params.executionId)) return state;
  const startedAtEpochMs = params.occurredAtEpochMs ?? Date.now();
  return setRecords(state, [
    ...state.records,
    createRecord({ ...params, statementSequence: 1, startedAtEpochMs, temporary: true }),
  ]);
}

export function completeWebSqlExecution(
  state: SqlExecutionLogState,
  params: CompleteWebSqlExecutionParams,
): SqlExecutionLogState {
  const completedAt = params.occurredAtEpochMs ?? Date.now();
  const temporary = state.records.find((record) => record.executionId === params.executionId && record.temporary);
  if (!params.results.length) {
    return updateRecord(state, temporary?.id, (record) => ({
      ...record,
      temporary: false,
      status: 'success',
      finishedAtEpochMs: completedAt,
      durationMs: Math.max(0, completedAt - record.startedAtEpochMs),
    }));
  }

  const groups = new Map<number, IManageResultData[]>();
  params.results.forEach((result, index) => {
    const sequence = result.statementSequence ?? numeric(result.extra?.statementSequence) ?? index + 1;
    groups.set(sequence, [...(groups.get(sequence) || []), result]);
  });

  const replacement = [...groups.entries()].map(([statementSequence, results]) => {
    const first = results[0];
    const startedAtEpochMs =
      first.executionMetrics?.startedAtEpochMs ?? temporary?.startedAtEpochMs ?? completedAt;
    let record = createRecord({
      executionId: params.executionId,
      statementSequence,
      sql: first.originalSql || params.sql,
      comment: first.comment,
      context: mergeExecutionContext(params.context, first.executionContext),
      startedAtEpochMs,
    });
    results.forEach((result, resultIndex) => {
      (result.extra?.messages || []).forEach((message, messageIndex) => {
        record = addMessage(record, message, completedAt, resultIndex * 1000 + messageIndex);
      });
      record = addResult(record, result, completedAt, undefined, resultIndex);
    });
    const finishedAtEpochMs = results.reduce(
      (latest, result) => Math.max(latest, result.executionMetrics?.finishedAtEpochMs || 0),
      completedAt,
    );
    return {
      ...record,
      status: results.some((result) => result.success === false) ? ('failed' as const) : ('success' as const),
      finishedAtEpochMs,
      durationMs: maximumDuration(results) ?? Math.max(0, finishedAtEpochMs - startedAtEpochMs),
    };
  });

  return setRecords(state, [
    ...state.records.filter((record) => record.id !== temporary?.id),
    ...replacement,
  ]);
}

export function failWebSqlExecution(
  state: SqlExecutionLogState,
  params: FailWebSqlExecutionParams,
): SqlExecutionLogState {
  const finishedAtEpochMs = params.occurredAtEpochMs ?? Date.now();
  const cancelled = isCancellationError(params.error);
  const existing = latestRecord(state.records, params.executionId);
  if (!existing) {
    const record = createRecord({
      ...params,
      statementSequence: 1,
      startedAtEpochMs: finishedAtEpochMs,
    });
    const finishedRecord = {
      ...record,
      status: cancelled ? ('cancelled' as const) : ('failed' as const),
      finishedAtEpochMs,
      durationMs: 0,
    };
    return setRecords(state, [
      ...state.records,
      cancelled ? finishedRecord : addError(finishedRecord, params.error, finishedAtEpochMs),
    ]);
  }
  return updateRecord(state, existing.id, (record) => {
    const finishedRecord = {
      ...record,
      temporary: false,
      status: cancelled ? ('cancelled' as const) : ('failed' as const),
      finishedAtEpochMs,
      durationMs: Math.max(0, finishedAtEpochMs - record.startedAtEpochMs),
    };
    return cancelled ? finishedRecord : addError(finishedRecord, params.error, finishedAtEpochMs);
  });
}

export function reduceDesktopSqlExecutionEvent(
  state: SqlExecutionLogState,
  event: SqlExecutionEvent,
  context: SqlExecutionLogContext,
): SqlExecutionLogState {
  const occurredAt = event.occurredAtEpochMs ?? Date.now();
  const statementSequence = event.statementSequence ?? 1;
  const recordId = idFor(event.executionId, statementSequence);

  if (event.eventType === 'started') return state;
  if (event.eventType === 'statementStarted') {
    if (state.records.some((record) => record.id === recordId)) return state;
    return setRecords(state, [
      ...state.records,
      createRecord({
        executionId: event.executionId,
        statementSequence,
        sql: text(event.message?.originalSql) || text(event.message?.sql),
        comment: text(event.message?.comment) || undefined,
        context,
        startedAtEpochMs: occurredAt,
      }),
    ]);
  }

  const target =
    (event.statementSequence === undefined
      ? latestRecord(state.records, event.executionId)
      : state.records.find((record) => record.id === recordId)) || latestRecord(state.records, event.executionId);
  if (!target && (event.eventType === 'failed' || event.eventType === 'cancelled')) {
    const record = createRecord({
      executionId: event.executionId,
      statementSequence,
      sql: '',
      context,
      startedAtEpochMs: occurredAt,
    });
    return setRecords(state, [...state.records, finishTerminal(record, event, occurredAt)]);
  }
  if (!target) return state;

  return updateRecord(state, target.id, (record) => {
    switch (event.eventType) {
      case 'resultStarted':
        return {
          ...record,
          context: mergeExecutionContext(record.context, event.message?.executionContext),
        };
      case 'rows': {
        const sequence = eventResultSequence(event);
        const key = String(sequence);
        return {
          ...record,
          pendingRowCounts: {
            ...record.pendingRowCounts,
            [key]: (record.pendingRowCounts[key] || 0) + (event.message?.dataList?.length || 0),
          },
        };
      }
      case 'message':
        return addMessage(record, event.message, occurredAt, event.eventSequence);
      case 'resultFinished':
      case 'updateCount': {
        const contextualRecord = {
          ...record,
          context: mergeExecutionContext(record.context, event.message?.executionContext),
        };
        const messageSequenceBase =
          typeof event.eventSequence === 'number' ? event.eventSequence * 1000 : contextualRecord.outputs.length;
        const withMessages = (event.message?.extra?.messages || []).reduce(
          (current, message, index) =>
            addMessage(current, message, occurredAt, messageSequenceBase + index),
          contextualRecord,
        );
        const next = addResult(withMessages, event.message, occurredAt, event);
        return event.message?.success === false ? { ...next, status: 'failed' as const } : next;
      }
      case 'statementFinished':
        return {
          ...record,
          status: record.status === 'failed' ? record.status : ('success' as const),
          finishedAtEpochMs: occurredAt,
          durationMs: numeric(event.message?.duration) ?? Math.max(0, occurredAt - record.startedAtEpochMs),
        };
      case 'failed':
      case 'cancelled':
        return finishTerminal(record, event, occurredAt);
      case 'finished':
        return record.status === 'running'
          ? {
              ...record,
              status: 'success',
              finishedAtEpochMs: occurredAt,
              durationMs: Math.max(0, occurredAt - record.startedAtEpochMs),
            }
          : record;
      default:
        return record;
    }
  });
}

function mergeExecutionContext(
  context: SqlExecutionLogContext,
  executionContext?: IExecutionContext,
): SqlExecutionLogContext {
  if (!executionContext) return context;
  return {
    ...context,
    databaseName: executionContext.databaseName ?? context.databaseName,
    schemaName: executionContext.schemaName ?? context.schemaName,
  };
}

function createRecord(params: {
  executionId: string;
  statementSequence: number;
  sql: string;
  context: SqlExecutionLogContext;
  startedAtEpochMs: number;
  comment?: string;
  temporary?: boolean;
}): SqlExecutionLogRecord {
  return {
    id: idFor(params.executionId, params.statementSequence),
    executionId: params.executionId,
    statementSequence: params.statementSequence,
    startedAtEpochMs: params.startedAtEpochMs,
    status: 'running',
    sql: params.sql,
    comment: params.comment,
    context: params.context,
    outputs: [],
    pendingRowCounts: {},
    temporary: params.temporary,
  };
}

function addMessage(
  record: SqlExecutionLogRecord,
  message: SqlExecutionMessage,
  occurredAtEpochMs: number,
  sequence = record.outputs.length,
): SqlExecutionLogRecord {
  const messageText = text(message?.message);
  if (!messageText) return record;
  const level = levelOf(message?.level);
  const outputId = `${record.id}:message:${sequence}`;
  if (record.outputs.some((output) => output.id === outputId)) return record;
  return {
    ...record,
    outputs: [
      ...record.outputs,
      {
        kind: 'message',
        id: outputId,
        occurredAtEpochMs,
        level,
        message: messageText,
        errorCode: message?.errorCode,
        sqlState: message?.sqlState,
        source: message?.source,
      },
    ],
  };
}

function addResult(
  record: SqlExecutionLogRecord,
  result: IManageResultData,
  occurredAtEpochMs: number,
  event?: SqlExecutionEvent,
  fallbackSequence = record.outputs.length,
): SqlExecutionLogRecord {
  const resultSequence =
    event?.resultSequence ??
    numeric(result.extra?.resultSequence) ??
    numeric(result.extra?.streamResultId) ??
    result.resultSetId ??
    fallbackSequence + 1;
  const rawResultKey = event?.resultKey || text(result.extra?.resultKey) || undefined;
  const resultKey = (result.headerList?.length || 0) > 1 ? rawResultKey : undefined;
  const outputId = `${record.id}:result:${rawResultKey || resultSequence}`;
  if (record.outputs.some((output) => output.kind === 'result' && output.id === outputId)) return record;
  const rowCount =
    result.executionMetrics?.fetchedRowCount ??
    record.pendingRowCounts[String(resultSequence)] ??
    (result.dataList ? result.dataList.length : undefined);
  return {
    ...record,
    outputs: [
      ...record.outputs,
      {
        kind: 'result',
        id: outputId,
        occurredAtEpochMs: result.executionMetrics?.finishedAtEpochMs ?? occurredAtEpochMs,
        resultKey,
        resultSequence,
        success: result.success !== false,
        rowCount,
        updateCount: result.updateCount,
        durationMs: result.duration,
        executionMetrics: result.executionMetrics,
        message: result.message,
      },
    ],
  };
}

function finishTerminal(record: SqlExecutionLogRecord, event: SqlExecutionEvent, occurredAtEpochMs: number) {
  const status = event.eventType === 'cancelled' ? ('cancelled' as const) : ('failed' as const);
  if (status === 'cancelled') {
    return {
      ...record,
      temporary: false,
      status,
      outputs: record.outputs.filter(
        (output) => !(output.kind === 'result' && !output.success && isCancellationMessage(output.message)),
      ),
      finishedAtEpochMs: occurredAtEpochMs,
      durationMs: Math.max(0, occurredAtEpochMs - record.startedAtEpochMs),
    };
  }
  return addError(
    {
      ...record,
      temporary: false,
      status,
      finishedAtEpochMs: occurredAtEpochMs,
      durationMs: Math.max(0, occurredAtEpochMs - record.startedAtEpochMs),
    },
    event.message,
    occurredAtEpochMs,
  );
}

function addError(record: SqlExecutionLogRecord, error: unknown, occurredAtEpochMs: number) {
  const message = errorText(error);
  return message ? addMessage(record, { level: 'ERROR', message }, occurredAtEpochMs) : record;
}

function updateRecord(
  state: SqlExecutionLogState,
  id: string | undefined,
  updater: (record: SqlExecutionLogRecord) => SqlExecutionLogRecord,
) {
  if (!id) return state;
  let changed = false;
  const records = state.records.map((record) => {
    if (record.id !== id) return record;
    const next = updater(record);
    changed = changed || next !== record;
    return next;
  });
  return changed ? setRecords(state, records) : state;
}

function setRecords(state: SqlExecutionLogState, records: SqlExecutionLogRecord[]): SqlExecutionLogState {
  if (records.length <= MAX_RECORDS) return { records };
  let overflow = records.length - MAX_RECORDS;
  const next = records.filter((record) => {
    if (overflow > 0 && record.status !== 'running') {
      overflow -= 1;
      return false;
    }
    return true;
  });
  return { records: next };
}

function latestRecord(records: SqlExecutionLogRecord[], executionId: string) {
  return [...records].reverse().find((record) => record.executionId === executionId);
}

function eventResultSequence(event: SqlExecutionEvent) {
  return (
    event.resultSequence ?? numeric(event.message?.resultSetId) ?? numeric(event.message?.extra?.streamResultId) ?? 1
  );
}

function maximumDuration(results: IManageResultData[]) {
  const durations = results.map((result) => result.duration).filter((value): value is number => typeof value === 'number');
  return durations.length ? Math.max(...durations) : undefined;
}

function idFor(executionId: string, statementSequence: number) {
  return `${executionId}:${statementSequence}`;
}

function levelOf(level?: string): 'INFO' | 'WARN' | 'ERROR' {
  const value = (level || 'INFO').toUpperCase();
  if (value === 'ERROR') return 'ERROR';
  if (value === 'WARN' || value === 'WARNING') return 'WARN';
  return 'INFO';
}

function errorText(error: unknown): string {
  if (typeof error === 'string') return error;
  if (!error || typeof error !== 'object') return '';
  const value = error as { message?: unknown; errorMessage?: unknown };
  if (typeof value.message === 'object' && value.message) {
    const nested = value.message as { message?: unknown; errorMessage?: unknown };
    return text(nested.message) || text(nested.errorMessage);
  }
  return text(value.message) || text(value.errorMessage);
}

function isCancellationError(error: unknown) {
  if (!error || typeof error !== 'object') {
    return typeof error === 'string' && isCancellationMessage(error);
  }
  const value = error as { name?: unknown; code?: unknown; message?: unknown };
  if (value.name === 'AbortError' || value.code === 'ERR_CANCELED') return true;
  return typeof value.message === 'string' && isCancellationMessage(value.message);
}

function isCancellationMessage(message?: string) {
  return !!message && /\b(?:aborted|cancell?ed)\b/i.test(message);
}

function text(value: unknown) {
  return typeof value === 'string' ? value : '';
}

function numeric(value: unknown) {
  return typeof value === 'number' && Number.isFinite(value) ? value : undefined;
}
