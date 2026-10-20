import { ImportExportTaskType } from '@/constants/importExport';
import zhCN from '@/i18n/zh-CN/workspace';
import enUS from '@/i18n/en-US/workspace';
import jaJP from '@/i18n/ja-JP/workspace';
import koKR from '@/i18n/ko-KR/workspace';
import esES from '@/i18n/es-ES/workspace';
import assert from 'node:assert/strict';
import { formatTaskEventMessage, type TaskEventMessageKey } from './eventMessage';
import type { ImportExportTaskEvent } from '@/typings/importExport';

const event = (code: string, message: string): ImportExportTaskEvent => ({
  eventId: 1,
  taskId: 1,
  sequence: 1,
  level: 'INFO',
  code,
  message,
  createdAt: 1,
});

const translations: Record<'zh' | 'en', Partial<Record<TaskEventMessageKey, string>>> = {
  zh: {
    'workspace.task.event.taskCreated': '任务已创建',
    'workspace.task.event.importPreparing': '正在准备导入数据',
    'workspace.task.event.batchExecuted': '已执行批量导入：{1} 条',
    'workspace.task.event.importRowsSkipped': '导入完成，{1} 行被跳过并记录到错误文件',
    'workspace.task.event.importBatchRowsSkipped': '本批次跳过 {2} 行，成功导入 {1} 行',
    'workspace.task.event.errorArtifactPublished': '错误行文件已保存',
    'workspace.task.event.taskCancelled': '任务已取消',
    'workspace.task.event.importResumeStarted': '从第 {1} 行继续导入；续跑按行号跳过，源文件需保持不变',
    'workspace.task.event.importBatchRetry': '批次被拒绝，正在逐行重试（{1} 行）',
    'workspace.task.event.rowsExportedFromTable': '已从表 {2} 导出 {1} 行',
    'workspace.task.event.finalizingExport': '正在完成 {1} 导出文件',
    'workspace.task.event.tableExportStarted': '正在导出表 {3}（{1}/{2}）',
    'workspace.task.event.tableExportCompleted': '表 {3} 导出完成（{1}/{2}）',
    'workspace.task.event.artifactPublished': '导出产物已发布',
  },
  en: {
    'workspace.task.event.taskCreated': 'Task created',
    'workspace.task.event.batchExecuted': 'Imported batch: {1} rows',
    'workspace.task.event.importRowsSkipped': 'Import finished: {1} rejected rows were written to the error file',
    'workspace.task.event.importBatchRowsSkipped': 'Batch completed: {1} rows imported, {2} rejected',
    'workspace.task.event.errorArtifactPublished': 'Error file saved',
    'workspace.task.event.taskCancelled': 'Task cancelled',
    'workspace.task.event.importResumeStarted': 'Continuing import after row {1}',
    'workspace.task.event.importBatchRetry': 'Batch rejected; retrying row by row ({1} rows)',
  },
};

const translate = (language: keyof typeof translations) =>
  (key: TaskEventMessageKey, ...args: Array<string | number>) =>
    args.reduce(
      (message, value, index) => message.replace(`{${index + 1}}`, String(value)),
      translations[language][key] || key,
    );

const created = event('TASK_CREATED', 'Task created');
assert.equal(formatTaskEventMessage(created, translate('zh')), '任务已创建');
assert.equal(formatTaskEventMessage(created, translate('en')), 'Task created');
assert.equal(
  formatTaskEventMessage(event('IMPORT_PREPARING', 'Preparing data import'), translate('zh')),
  '正在准备导入数据',
);
assert.equal(
  formatTaskEventMessage(event('BATCH_EXECUTED', 'Executing batch insert: 120'), translate('zh')),
  '已执行批量导入：120 条',
);
assert.equal(
  formatTaskEventMessage(event('TASK_FAILED', 'CSV 存在从第 123 行开始未闭合的引号'), translate('en')),
  'CSV 存在从第 123 行开始未闭合的引号',
);
assert.equal(
  formatTaskEventMessage({ ...event('IMPORT_ROWS_SKIPPED', 'Import finished with rejected rows'),
    details: { rejectedRows: 1 } }, translate('zh')),
  '导入完成，1 行被跳过并记录到错误文件',
);
assert.equal(
  formatTaskEventMessage({ ...event('IMPORT_ROWS_SKIPPED', 'Batch completed with rejected rows'),
    details: { statementCount: 1000, importedRows: 998, rejectedRows: 2 } }, translate('en')),
  'Batch completed: 998 rows imported, 2 rejected',
);
assert.equal(
  formatTaskEventMessage(event('TASK_CANCELLED', 'Task cancelled'), translate('zh')),
  '任务已取消',
);
assert.equal(
  formatTaskEventMessage({ ...event('IMPORT_RESUME_STARTED',
    'Continuing import after row 4200; rows are skipped by position'), details: { resumeFromRow: 4200 } },
  translate('zh')),
  '从第 4200 行继续导入；续跑按行号跳过，源文件需保持不变',
);
assert.equal(
  formatTaskEventMessage({ ...event('IMPORT_BATCH_RETRY', 'Retrying a rejected batch row by row'),
    details: { statementCount: 1000 } }, translate('en')),
  'Batch rejected; retrying row by row (1000 rows)',
);
assert.equal(
  formatTaskEventMessage({ ...event('ROWS_EXPORTED', 'Exported 1000 rows from orders'),
    details: { exportedRows: 1000, tableName: 'orders' } }, translate('zh')),
  '已从表 orders 导出 1000 行',
);
assert.equal(
  formatTaskEventMessage(event('FILE_FINALIZING', 'Finalizing CSV export file'), translate('zh')),
  '正在完成 CSV 导出文件',
);
assert.equal(
  formatTaskEventMessage(event('FILE_FINALIZING', 'Finalizing CSV export file from orders'), translate('zh')),
  '正在完成 CSV 导出文件',
);
assert.equal(
  formatTaskEventMessage({ ...event('TABLE_EXPORT_STARTED', 'Exporting table 1/2: orders'),
    details: { tableName: 'orders', exportedTables: 1, totalTables: 2 } }, translate('zh')),
  '正在导出表 orders（1/2）',
);
assert.equal(
  formatTaskEventMessage({ ...event('TABLE_EXPORT_COMPLETED', 'Table export completed 2/2: users'),
    details: { tableName: 'users', exportedTables: 2, totalTables: 2 } }, translate('zh')),
  '表 users 导出完成（2/2）',
);
assert.equal(
  formatTaskEventMessage(event('ARTIFACT_PUBLISHED', 'Artifact published'), translate('zh')),
  '导出产物已发布',
);
assert.equal(
  formatTaskEventMessage(event('ERROR_ARTIFACT_PUBLISHED', 'Error artifact published'), translate('zh')),
  '错误行文件已保存',
);

// Format the same stored events using each locale, including switching languages.
const csvSummary = event('IMPORT_SUMMARY', 'CSV import finished');
const completedBatch = {
  ...event('BATCH_EXECUTED', 'SQL batch executed'),
  details: { batch: 1, statementCount: 20_000 },
};
for (const messages of [zhCN, enUS, jaJP, koKR, esES, zhCN]) {
  const localize = (key: TaskEventMessageKey, ...args: Array<string | number>) =>
    args.reduce((message, value, index) => message.replace(`{${index + 1}}`, String(value)), messages[key]);
  const format = (entry: ImportExportTaskEvent) =>
    formatTaskEventMessage(entry, localize, ImportExportTaskType.DATA_FILE_IMPORT);
  assert.equal(format(csvSummary), messages['workspace.task.event.csvImportFinished']);
  assert.equal(format(completedBatch), messages['workspace.task.event.batchCompletedRows'].replace('{1}', '20000'));
  assert.equal(format({ ...completedBatch, details: { statementCount: 20000, importedRows: 40000 } }),
    messages['workspace.task.event.batchCompletedTotal'].replace('{1}', '20000').replace('{2}', '40000'));
  assert.equal(format({ ...csvSummary, details: { importedRows: 100000, elapsedMillis: 14321 } }),
    messages['workspace.task.event.csvImportSummary'].replace('{1}', '100000').replace('{2}', '14.32'));
  assert.equal(format({ ...csvSummary, details: { importedRows: 0, elapsedMillis: 0 } }),
    messages['workspace.task.event.csvImportSummary'].replace('{1}', '0').replace('{2}', '0.00'));
  for (const count of [undefined, '20000', -1, NaN]) {
    assert.equal(format({ ...completedBatch, details: { statementCount: count } }),
      messages['workspace.task.event.sqlBatchExecuted']);
    assert.equal(format({ ...csvSummary, details: { importedRows: 100, elapsedMillis: count } }),
      messages['workspace.task.event.csvImportFinished']);
  }
  assert.equal(formatTaskEventMessage(completedBatch, localize, ImportExportTaskType.SQL_FILE_IMPORT),
    messages['workspace.task.event.sqlBatchExecuted']);
  assert.equal(format(event('CUSTOM_EVENT', 'driver message')), 'driver message');
}
