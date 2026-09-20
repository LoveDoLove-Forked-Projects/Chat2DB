import assert from 'node:assert/strict';
import {
  UPDATE_CHECK_INTERVAL_MINUTES,
  nextNotifiedVersion,
  shouldNotifyVersion,
  updateCheckDelayMs,
} from './updateCheckSchedule';

const minute = 60 * 1000;

assert.deepEqual(UPDATE_CHECK_INTERVAL_MINUTES, [30, 60, 120, 240, 360]);
assert.equal(updateCheckDelayMs(0), 30 * minute);
assert.equal(updateCheckDelayMs(1), 60 * minute);
assert.equal(updateCheckDelayMs(4), 360 * minute);
assert.equal(updateCheckDelayMs(5), 30 * minute, 'schedule repeats after the last interval');
assert.equal(updateCheckDelayMs(9), 360 * minute, 'second cycle keeps the same order');
assert.equal(updateCheckDelayMs(11), 60 * minute);

assert.equal(shouldNotifyVersion('5.3.8', ''), true);
assert.equal(shouldNotifyVersion('5.3.8', '5.3.8'), false, 'same version is not announced twice');
assert.equal(shouldNotifyVersion('5.3.9', '5.3.8'), true, 'a newer version is announced again');
assert.equal(shouldNotifyVersion('', ''), false, 'missing version is not announced');
assert.equal(shouldNotifyVersion(undefined, ''), false);

assert.equal(nextNotifiedVersion(true, '5.3.8', ''), '5.3.8', 'first sighting is announced');
assert.equal(nextNotifiedVersion(true, '5.3.8', '5.3.8'), '5.3.8', 'repeat check keeps the marker');
assert.equal(nextNotifiedVersion(true, '5.3.9', '5.3.8'), '5.3.9', 'a newer version replaces the marker');
assert.equal(nextNotifiedVersion(false, '5.3.8', ''), '', 'reminders disabled: nothing announced');
assert.equal(nextNotifiedVersion(false, '5.3.8', '5.3.7'), '5.3.7', 'reminders disabled: marker untouched');
assert.equal(nextNotifiedVersion(true, undefined, '5.3.7'), '5.3.7', 'missing version is not announced');

console.log('Update check schedule tests passed');
