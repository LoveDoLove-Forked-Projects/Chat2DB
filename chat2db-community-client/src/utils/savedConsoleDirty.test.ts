import assert from 'node:assert/strict';
import { hasUnsavedSavedConsoleChanges } from './savedConsoleDirty';

assert.equal(hasUnsavedSavedConsoleChanges('', false, undefined), false);
assert.equal(hasUnsavedSavedConsoleChanges('select 1', false, undefined), true);
assert.equal(hasUnsavedSavedConsoleChanges('select 1', true, 'select 1'), false);
assert.equal(hasUnsavedSavedConsoleChanges('', true, 'select 1'), true);
assert.equal(hasUnsavedSavedConsoleChanges('', true, ''), false);
console.log('saved console dirty tests passed');
