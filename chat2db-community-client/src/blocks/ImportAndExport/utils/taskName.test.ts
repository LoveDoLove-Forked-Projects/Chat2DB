import assert from 'node:assert/strict';
import { formatTaskName, type TaskNameMessageKey } from './taskName';

const zh: Record<TaskNameMessageKey, string> = {
  'workspace.task.name.importSql': '导入 SQL 文件',
  'workspace.task.name.importTable': '导入表数据',
  'workspace.task.name.exportTable': '导出表数据',
  'workspace.task.name.importTarget': '导入 {1}',
};

const translate = (key: TaskNameMessageKey, ...args: Array<string | number>) =>
  args.reduce(
    (message, value) => message.replace('{1}', String(value)),
    zh[key],
  );

assert.equal(formatTaskName('Import SQL file', translate), '导入 SQL 文件');
assert.equal(formatTaskName('Import table data - app.orders', translate), '导入表数据 - app.orders');
assert.equal(formatTaskName('Export table data - app.orders', translate), '导出表数据 - app.orders');
assert.equal(formatTaskName('Import orders', translate), '导入 orders');
assert.equal(formatTaskName('用户自定义任务', translate), '用户自定义任务');
