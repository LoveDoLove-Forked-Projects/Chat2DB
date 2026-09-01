import assert from 'node:assert/strict';
import { LocalFileSaveCoordinator, normalizeLocalFileSaveKey } from './localFileSaveCoordinator';

function deferred() {
  let resolve!: () => void;
  let reject!: (error: unknown) => void;
  const promise = new Promise<void>((done, fail) => {
    resolve = done;
    reject = fail;
  });
  return { promise, resolve, reject };
}

async function testSavesAreSerializedAndCoalesced() {
  const coordinator = new LocalFileSaveCoordinator();
  const firstGate = deferred();
  const calls: string[] = [];
  const mutation = async ({ fileContent }: { fileContent: string }) => {
    calls.push(`start:${fileContent}`);
    if (fileContent === 'first') {
      await firstGate.promise;
    }
    calls.push(`end:${fileContent}`);
  };

  const first = coordinator.save({ filePath: '/tmp/query.sql', fileContent: 'first' }, mutation);
  const second = coordinator.save({ filePath: '/tmp/query.sql', fileContent: 'second' }, mutation);
  await Promise.resolve();
  assert.deepEqual(calls, ['start:first']);

  firstGate.resolve();
  assert.deepEqual(await first, { filePath: '/tmp/query.sql', fileContent: 'second' });
  assert.deepEqual(await second, { filePath: '/tmp/query.sql', fileContent: 'second' });
  assert.deepEqual(calls, ['start:first', 'end:first', 'start:second', 'end:second']);
}

async function testDifferentPathsCanRunIndependently() {
  const coordinator = new LocalFileSaveCoordinator();
  const firstGate = deferred();
  const calls: string[] = [];
  const mutation = async ({ filePath }: { filePath: string }) => {
    calls.push(`start:${filePath}`);
    if (filePath.endsWith('first.sql')) {
      await firstGate.promise;
    }
    calls.push(`end:${filePath}`);
  };

  const first = coordinator.save({ filePath: '/tmp/first.sql', fileContent: '1' }, mutation);
  const second = coordinator.save({ filePath: '/tmp/second.sql', fileContent: '2' }, mutation);
  await second;
  assert.deepEqual(calls, ['start:/tmp/first.sql', 'start:/tmp/second.sql', 'end:/tmp/second.sql']);
  firstGate.resolve();
  await first;
}

async function testFailureRejectsCurrentBatchAndAllowsRetry() {
  const coordinator = new LocalFileSaveCoordinator();
  let attempts = 0;
  const mutation = async () => {
    attempts += 1;
    if (attempts === 1) {
      throw new Error('expected failure');
    }
  };

  await assert.rejects(
    coordinator.save({ filePath: '/tmp/failure.sql', fileContent: 'bad' }, mutation),
    /expected failure/,
  );
  const result = await coordinator.save({ filePath: '/tmp/failure.sql', fileContent: 'retry' }, mutation);
  assert.equal(result.fileContent, 'retry');
  assert.equal(attempts, 2);
}

async function run() {
  assert.equal(normalizeLocalFileSaveKey('C:\\SQL\\query.sql'), 'C:/SQL/query.sql');
  assert.equal(normalizeLocalFileSaveKey('\\\\SERVER\\Share\\query.sql'), '//SERVER/Share/query.sql');
  assert.equal(normalizeLocalFileSaveKey('/tmp//query.sql'), '/tmp/query.sql');
  assert.notEqual(
    normalizeLocalFileSaveKey('/tmp/query.sql'),
    normalizeLocalFileSaveKey('/tmp/query.sql '),
    'valid trailing spaces must not merge distinct files',
  );
  assert.notEqual(
    normalizeLocalFileSaveKey('/tmp/query.sql'),
    normalizeLocalFileSaveKey('/tmp/query.sql/'),
    'the coordinator must not repair invalid paths into another file key',
  );
  await testSavesAreSerializedAndCoalesced();
  await testDifferentPathsCanRunIndependently();
  await testFailureRejectsCurrentBatchAndAllowsRetry();
  console.log('local file save coordinator tests passed');
}

void run();
