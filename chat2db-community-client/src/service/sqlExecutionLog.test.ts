import assert from 'node:assert/strict';
import { TableDataType } from '@/constants/table';
import type { IManageResultData } from '@/typings';
import { SqlTypeEnum } from '@/typings/sqlParser';
import {
  beginWebSqlExecution,
  clearSqlExecutionLog,
  completeWebSqlExecution,
  createSqlExecutionLogState,
  failWebSqlExecution,
  reduceDesktopSqlExecutionEvent,
} from './sqlExecutionLog';

const context = {
  dataSourceId: 1,
  dataSourceName: '@localhost',
  databaseName: 'app',
};

function result(overrides: Partial<IManageResultData> = {}): IManageResultData {
  return {
    dataList: [],
    headerList: [],
    description: '',
    sql: 'select 1',
    originalSql: 'select 1',
    success: true,
    duration: 10,
    sqlType: SqlTypeEnum.SELECT,
    refreshTargets: [],
    pageNo: 1,
    pageSize: 1000,
    fuzzyTotal: '0',
    hasNextPage: false,
    ...overrides,
  };
}

function webExecution() {
  return beginWebSqlExecution(createSqlExecutionLogState(), {
    executionId: 'web-1',
    sql: 'select 1',
    context,
    occurredAtEpochMs: 100,
  });
}

{
  const running = webExecution();
  assert.equal(clearSqlExecutionLog(running).records.length, 1);
  const completed = completeWebSqlExecution(running, {
    executionId: 'web-1',
    sql: 'select 1',
    context,
    occurredAtEpochMs: 110,
    results: [],
  });
  assert.equal(clearSqlExecutionLog(completed).records.length, 0);
}

{
  const state = completeWebSqlExecution(webExecution(), {
    executionId: 'web-1',
    sql: 'select 1',
    context,
    occurredAtEpochMs: 140,
    results: [
      result({
        statementSequence: 1,
        dataList: [[], []],
        headerList: [
          { name: '#', dataType: TableDataType.CHAT2DB_ROW_NUMBER },
          { name: 'value', dataType: TableDataType.NUMERIC },
        ],
        extra: {
          resultKey: 'web-1:1:1',
          messages: [{ level: 'INFO', message: 'notice' }],
        },
        executionMetrics: {
          startedAtEpochMs: 100,
          finishedAtEpochMs: 140,
          executeDurationMs: 12,
          fetchDurationMs: 8,
          fetchedRowCount: 2,
        },
      }),
    ],
  });
  assert.equal(state.records.length, 1);
  assert.equal(state.records[0].status, 'success');
  assert.deepEqual(
    state.records[0].outputs.map((output) => output.kind),
    ['message', 'result'],
  );
  const output = state.records[0].outputs[1];
  assert.equal(output.kind === 'result' ? output.rowCount : undefined, 2);
  assert.equal(output.kind === 'result' ? output.resultKey : undefined, 'web-1:1:1');
  assert.equal('dataList' in output, false);
}

{
  const state = completeWebSqlExecution(webExecution(), {
    executionId: 'web-1',
    sql: 'update t set c = 1',
    context,
    occurredAtEpochMs: 130,
    results: [result({ originalSql: 'update t set c = 1', sqlType: SqlTypeEnum.UPDATE, updateCount: 3 })],
  });
  const output = state.records[0].outputs[0];
  assert.equal(output.kind === 'result' ? output.updateCount : undefined, 3);
  assert.equal(output.kind === 'result' ? output.resultKey : undefined, undefined);
}

{
  const state = failWebSqlExecution(webExecution(), {
    executionId: 'web-1',
    sql: 'select 1',
    context,
    occurredAtEpochMs: 120,
    error: { message: 'connection failed' },
  });
  assert.equal(state.records[0].status, 'failed');
  assert.equal(state.records[0].outputs[0].kind, 'message');
}

{
  const state = failWebSqlExecution(webExecution(), {
    executionId: 'web-1',
    sql: 'select 1',
    context,
    occurredAtEpochMs: 120,
    error: { name: 'AbortError', message: 'The request was aborted' },
  });
  assert.equal(state.records[0].status, 'cancelled');
  assert.equal(state.records[0].outputs.length, 0);
}

{
  let state = createSqlExecutionLogState();
  state = reduceDesktopSqlExecutionEvent(
    state,
    {
      executionId: 'desktop-1',
      eventSequence: 1,
      occurredAtEpochMs: 200,
      eventType: 'statementStarted',
      statementSequence: 1,
      message: { originalSql: 'select * from t' },
    },
    context,
  );
  for (const eventSequence of [2, 3]) {
    state = reduceDesktopSqlExecutionEvent(
      state,
      {
        executionId: 'desktop-1',
        eventSequence,
        occurredAtEpochMs: 205 + eventSequence,
        eventType: 'rows',
        statementSequence: 1,
        resultSequence: 1,
        message: result({ dataList: eventSequence === 2 ? [[], []] : [[]] }),
      },
      context,
    );
  }
  const messageEvent = {
    executionId: 'desktop-1',
    eventSequence: 4,
    occurredAtEpochMs: 210,
    eventType: 'message' as const,
    statementSequence: 1,
    message: { level: 'WARN' as const, message: 'slow' },
  };
  state = reduceDesktopSqlExecutionEvent(state, messageEvent, context);
  state = reduceDesktopSqlExecutionEvent(state, { ...messageEvent, eventSequence: 5 }, context);
  state = reduceDesktopSqlExecutionEvent(
    state,
    {
      executionId: 'desktop-1',
      eventSequence: 6,
      occurredAtEpochMs: 220,
      eventType: 'resultFinished',
      statementSequence: 1,
      resultSequence: 1,
      resultKey: 'desktop-1:1:1',
      message: result({
        dataList: [],
        extra: {
          messages: [
            { level: 'WARN', message: 'slow' },
            { level: 'INFO', message: 'finished notice' },
          ],
        },
      }),
    },
    context,
  );
  state = reduceDesktopSqlExecutionEvent(
    state,
    {
      executionId: 'desktop-1',
      eventSequence: 7,
      occurredAtEpochMs: 230,
      eventType: 'statementFinished',
      statementSequence: 1,
      message: { duration: 30 },
    },
    context,
  );
  assert.equal(state.records[0].status, 'success');
  assert.equal(state.records[0].outputs.filter((output) => output.kind === 'message').length, 2);
  assert.ok(state.records[0].outputs.some((output) => output.kind === 'message' && output.message === 'finished notice'));
  const output = state.records[0].outputs.find((item) => item.kind === 'result');
  assert.equal(output?.kind === 'result' ? output.rowCount : undefined, 3);
}

{
  let state = createSqlExecutionLogState();
  for (const statementSequence of [1, 2]) {
    state = reduceDesktopSqlExecutionEvent(
      state,
      {
        executionId: 'desktop-cancel',
        occurredAtEpochMs: 300 + statementSequence,
        eventType: 'statementStarted',
        statementSequence,
        message: { originalSql: `select ${statementSequence}` },
      },
      context,
    );
  }
  state = reduceDesktopSqlExecutionEvent(
    state,
    {
      executionId: 'desktop-cancel',
      occurredAtEpochMs: 310,
      eventType: 'resultFinished',
      statementSequence: 2,
      resultSequence: 2,
      message: result({ success: false, message: 'SQL execution canceled' }),
    },
    context,
  );
  state = reduceDesktopSqlExecutionEvent(
    state,
    {
      executionId: 'desktop-cancel',
      occurredAtEpochMs: 311,
      eventType: 'cancelled',
      message: { message: 'SQL execution canceled' },
    },
    context,
  );
  assert.equal(state.records[0].status, 'running');
  assert.equal(state.records[1].status, 'cancelled');
  assert.equal(state.records[1].outputs.length, 0);
}

{
  let state = beginWebSqlExecution(createSqlExecutionLogState(), {
    executionId: 'still-running',
    sql: 'select sleep(10)',
    context,
  });
  for (let index = 0; index < 205; index += 1) {
    const executionId = `history-${index}`;
    state = beginWebSqlExecution(state, { executionId, sql: 'select 1', context, occurredAtEpochMs: index });
    state = completeWebSqlExecution(state, {
      executionId,
      sql: 'select 1',
      context,
      occurredAtEpochMs: index + 1,
      results: [result()],
    });
  }
  assert.ok(state.records.length <= 200);
  assert.equal(state.records[0].executionId, 'still-running');
  assert.equal(state.records.at(-1)?.executionId, 'history-204');
}

console.log('SQL execution log tests passed');
