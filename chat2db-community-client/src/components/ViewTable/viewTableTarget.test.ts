import assert from 'node:assert/strict';
import type { IViewTableParams } from '@/typings';
import { getViewTableTargetKey } from './viewTableTarget';

const params: IViewTableParams = {
  dataSourceId: 1,
  databaseName: 'chat2db',
  schemaName: 'public',
  databaseType: 'POSTGRESQL',
  tableName: 'orders',
};

// The workspace tab layer rebuilds the params object on unrelated renders, so a
// new object with the same table identity must keep the same key. Otherwise the
// browse request is re-issued on every tab switch.
assert.equal(
  getViewTableTargetKey({ ...params }),
  getViewTableTargetKey(params),
  'a rebuilt params object with the same table identity must keep the same target key',
);

// Pagination is not part of the table identity: paging must not look like a new table.
assert.equal(
  getViewTableTargetKey({ ...params, pageNo: 3, pageSize: 100 }),
  getViewTableTargetKey(params),
  'pagination fields must not change the target key',
);

assert.notEqual(
  getViewTableTargetKey({ ...params, dataSourceId: 2 }),
  getViewTableTargetKey(params),
  'a different datasource must produce a different target key',
);
assert.notEqual(
  getViewTableTargetKey({ ...params, databaseName: 'other' }),
  getViewTableTargetKey(params),
  'a different database must produce a different target key',
);
assert.notEqual(
  getViewTableTargetKey({ ...params, schemaName: 'other' }),
  getViewTableTargetKey(params),
  'a different schema must produce a different target key',
);
assert.notEqual(
  getViewTableTargetKey({ ...params, tableName: 'geo_test' }),
  getViewTableTargetKey(params),
  'a different table must produce a different target key',
);

// Adjacent identity fields must not be able to shift into each other.
assert.notEqual(
  getViewTableTargetKey({ dataSourceId: 1, databaseName: 'a', schemaName: 'b', tableName: 'c' }),
  getViewTableTargetKey({ dataSourceId: 1, databaseName: 'a\u0000b', tableName: 'c' }),
  'identity fields must not collide when a name contains the separator',
);

assert.equal(getViewTableTargetKey(undefined), '', 'a missing params object has no target key');
assert.equal(getViewTableTargetKey(null), '', 'a null params object has no target key');

console.log('view table target tests passed');
