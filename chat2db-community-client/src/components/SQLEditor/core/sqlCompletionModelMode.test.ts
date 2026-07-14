import assert from 'node:assert/strict';
import { DatabaseTypeCode } from '../../../constants/common';
import {
  isBackendCompletionDatabaseType,
  isBackendCompletionModel,
  setBackendCompletionModel,
} from './sqlCompletionModelMode';

const model = {} as any;

assert.equal(isBackendCompletionDatabaseType(DatabaseTypeCode.MYSQL), true, 'MySQL uses backend completion mode');
assert.equal(
  isBackendCompletionDatabaseType(DatabaseTypeCode.POSTGRESQL),
  false,
  'non-configured databases keep legacy completion mode',
);
assert.equal(isBackendCompletionDatabaseType(undefined), false, 'missing database type keeps legacy completion mode');

assert.equal(isBackendCompletionModel(model), false, 'model is not marked by default');

setBackendCompletionModel(model, true);
assert.equal(isBackendCompletionModel(model), true, 'model can be marked as backend completion');

setBackendCompletionModel(model, false);
assert.equal(isBackendCompletionModel(model), false, 'model can be unmarked');

setBackendCompletionModel(null, true);
setBackendCompletionModel(undefined, false);
assert.equal(isBackendCompletionModel(null), false, 'null model is ignored');
assert.equal(isBackendCompletionModel(undefined), false, 'undefined model is ignored');

console.log('sqlCompletionModelMode tests passed');
