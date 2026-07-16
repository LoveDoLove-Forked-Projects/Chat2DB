import assert from 'node:assert/strict';
import type { RedisDataItem } from '@/typings/redis';
import { buildRedisKeyTree, collectRedisGroupKeys, redisKeyNodeKey } from './redisKeyTree';

const keys = ['user:10', 'plain', 'cache:user:2', 'user:2', 'user', 'broken::key'];
const tree = buildRedisKeyTree(keys.map((name) => ({ name } as RedisDataItem)));

assert.deepEqual(
  tree.map((node) => [node.kind, node.title]),
  [
    ['group', 'cache'],
    ['group', 'user'],
    ['key', 'broken::key'],
    ['key', 'plain'],
    ['key', 'user'],
  ],
);

const userGroup = tree[1];
assert.equal(userGroup.count, 2);
assert.deepEqual(
  userGroup.children?.map((node) => node.title),
  ['user:2', 'user:10'],
);
assert.equal(userGroup.children?.[0].rowIndex, 3);
assert.equal(userGroup.children?.[0].key, redisKeyNodeKey('user:2'));
assert.deepEqual(collectRedisGroupKeys(tree), ['redis-group:cache', 'redis-group:cache/user', 'redis-group:user']);

console.log('Redis key hierarchy tests passed');
