const assert = require('node:assert/strict');
const test = require('node:test');

const {
  closingIssueNumbers,
  fallbackIssueStatus,
  pullRequestStatus,
} = require('./sync-community-project');

test('extracts unique same-repository closing references', () => {
  assert.deepEqual(closingIssueNumbers('Closes #12\nfixes #12\nResolves #34'), [12, 34]);
  assert.deepEqual(closingIssueNumbers('Related to #99'), []);
});

test('maps pull request lifecycle to implementation states', () => {
  assert.equal(pullRequestStatus('opened', { state: 'open', draft: true }), 'In Progress');
  assert.equal(pullRequestStatus('opened', { state: 'open', draft: false }), 'In Review');
  assert.equal(pullRequestStatus('closed', { state: 'closed', draft: false }), 'Done');
  assert.equal(pullRequestStatus('opened', { state: 'open', draft: false, merged: true }), 'Done');
});

test('returns an unmerged issue to the correct executable state', () => {
  assert.equal(fallbackIssueStatus({ state: 'closed', labels: [] }), 'Done');
  assert.equal(fallbackIssueStatus({ state: 'open', labels: ['contribution/help-wanted'] }), 'Ready');
  assert.equal(fallbackIssueStatus({ state: 'open', labels: [{ name: 'area/docs' }] }), 'Backlog');
});
