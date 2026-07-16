'use strict';

const fs = require('node:fs');
const path = require('node:path');

const BOT_LOGIN = 'github-actions[bot]';
const MARKER_PREFIX = '<!-- chat2db-claim-state:';
const MARKER_PATTERN = /<!-- chat2db-claim-state:(.*?) -->/s;
const ACTIVE_STATUS = 'active';
const VALID_STATUSES = new Set(['unclaimed', ACTIVE_STATUS, 'released', 'expired']);

function parseCommand(body) {
  const command = String(body || '').toLowerCase();
  if (command === '/claim') return 'claim';
  if (command === '/unclaim') return 'unclaim';
  if (command === '/renew') return 'renew';
  if (command === '/claim status') return 'status';
  return null;
}

function loadPolicy(policyPath) {
  const policy = JSON.parse(fs.readFileSync(policyPath, 'utf8'));
  if (policy.version !== 1) throw new Error('Unsupported claim policy version.');
  if (!Array.isArray(policy.eligibleLabels) || policy.eligibleLabels.length === 0) {
    throw new Error('eligibleLabels must contain at least one label.');
  }
  for (const key of ['leaseDays', 'maxRenewals', 'maxActiveClaimsPerUser']) {
    if (!Number.isInteger(policy[key]) || policy[key] < 0) {
      throw new Error(`${key} must be a non-negative integer.`);
    }
  }
  if (policy.leaseDays === 0 || policy.maxActiveClaimsPerUser !== 1) {
    throw new Error('leaseDays must be positive and maxActiveClaimsPerUser must be 1.');
  }
  return policy;
}

function normalizeLogin(login) {
  return String(login || '').toLowerCase();
}

function issueLabels(issue) {
  return (issue.labels || []).map((label) => (
    typeof label === 'string' ? label : label.name
  ));
}

function isEligibleIssue(issue, policy) {
  if (!issue || issue.state !== 'open' || issue.pull_request) return false;
  const labels = new Set(issueLabels(issue));
  return policy.eligibleLabels.some((label) => labels.has(label));
}

function addDays(isoDate, days) {
  const date = new Date(isoDate);
  if (Number.isNaN(date.getTime())) throw new Error(`Invalid date: ${isoDate}`);
  date.setUTCDate(date.getUTCDate() + days);
  return date.toISOString();
}

function emptyState(issueNumber, now) {
  return {
    version: 1,
    issue: issueNumber,
    status: 'unclaimed',
    claimant: null,
    claimedAt: null,
    expiresAt: null,
    renewals: 0,
    updatedAt: now,
    reason: null,
  };
}

function parseStateMarker(body) {
  const match = String(body || '').match(MARKER_PATTERN);
  if (!match) return null;
  try {
    const state = JSON.parse(match[1]);
    if (state.version !== 1
      || !Number.isInteger(state.issue)
      || state.issue <= 0
      || !VALID_STATUSES.has(state.status)
      || !Number.isInteger(state.renewals)
      || state.renewals < 0) {
      return null;
    }
    if (state.status === ACTIVE_STATUS) {
      if (typeof state.claimant !== 'string' || state.claimant.length === 0) return null;
      if (Number.isNaN(new Date(state.claimedAt).getTime())) return null;
      if (Number.isNaN(new Date(state.expiresAt).getTime())) return null;
    }
    return state;
  } catch {
    return null;
  }
}

function serializeStateMarker(state) {
  return `${MARKER_PREFIX}${JSON.stringify(state)} -->`;
}

function isLeaseExpired(state, now) {
  return state.status === ACTIVE_STATUS
    && Boolean(state.expiresAt)
    && new Date(state.expiresAt).getTime() <= new Date(now).getTime();
}

function reconcileExpiredClaim(state, now, hasValidPullRequest) {
  if (!state || !isLeaseExpired(state, now) || hasValidPullRequest) {
    return { state, effects: [] };
  }

  return {
    state: {
      ...state,
      status: 'expired',
      updatedAt: now,
      reason: 'lease_expired',
    },
    effects: [{ type: 'remove-assignee', login: state.claimant }],
  };
}

function transitionClaim({
  state,
  command,
  actor,
  now,
  policy,
  issue,
  actorHasOtherActiveClaim = false,
  hasValidPullRequest = false,
  canManage = false,
}) {
  const current = state || emptyState(issue.number, now);
  const actorLogin = normalizeLogin(actor);
  const claimantLogin = normalizeLogin(current.claimant);

  const eligible = isEligibleIssue(issue, policy);
  const canInspectExistingClaim = current.status === ACTIVE_STATUS
    && (command === 'unclaim' || command === 'status');
  if (!eligible && !canInspectExistingClaim) {
    return { ok: false, code: 'issue_not_eligible', state: current, effects: [] };
  }

  if (command === 'status') {
    const statusCode = current.status === ACTIVE_STATUS
      ? 'status_active'
      : ((issue.assignees || []).length > 0 ? 'status_assigned' : 'status_available');
    return {
      ok: true,
      code: statusCode,
      state: current,
      effects: [],
    };
  }

  if (command === 'claim') {
    if (current.status === ACTIVE_STATUS) {
      const code = claimantLogin === actorLogin ? 'already_claimed' : 'claimed_by_other';
      return { ok: false, code, state: current, effects: [] };
    }
    if ((issue.assignees || []).length > 0) {
      return { ok: false, code: 'issue_already_assigned', state: current, effects: [] };
    }
    if (actorHasOtherActiveClaim) {
      return { ok: false, code: 'actor_has_other_claim', state: current, effects: [] };
    }

    return {
      ok: true,
      code: 'claimed',
      state: {
        version: 1,
        issue: issue.number,
        status: ACTIVE_STATUS,
        claimant: actor,
        claimedAt: now,
        expiresAt: addDays(now, policy.leaseDays),
        renewals: 0,
        updatedAt: now,
        reason: null,
      },
      effects: [{ type: 'add-assignee', login: actor }],
    };
  }

  if (current.status !== ACTIVE_STATUS) {
    return { ok: false, code: 'no_active_claim', state: current, effects: [] };
  }

  if (command === 'unclaim') {
    if (claimantLogin !== actorLogin && !canManage) {
      return { ok: false, code: 'not_claimant', state: current, effects: [] };
    }
    return {
      ok: true,
      code: 'released',
      state: {
        ...current,
        status: 'released',
        updatedAt: now,
        reason: claimantLogin === actorLogin ? 'released_by_claimant' : 'released_by_maintainer',
      },
      effects: [{ type: 'remove-assignee', login: current.claimant }],
    };
  }

  if (command === 'renew') {
    if (claimantLogin !== actorLogin) {
      return { ok: false, code: 'not_claimant', state: current, effects: [] };
    }
    if (hasValidPullRequest) {
      return { ok: false, code: 'lease_paused_by_pr', state: current, effects: [] };
    }
    if (current.renewals >= policy.maxRenewals) {
      return { ok: false, code: 'renewal_limit_reached', state: current, effects: [] };
    }
    return {
      ok: true,
      code: 'renewed',
      state: {
        ...current,
        expiresAt: addDays(now, policy.leaseDays),
        renewals: current.renewals + 1,
        updatedAt: now,
        reason: null,
      },
      effects: [],
    };
  }

  return { ok: false, code: 'unknown_command', state: current, effects: [] };
}

function resultMessage(code, state, actor, validPullRequest) {
  const messages = {
    claimed: `@${actor} claimed this task.`,
    released: `@${state.claimant}'s claim was released.`,
    renewed: `@${state.claimant}'s claim was renewed.`,
    already_claimed: `@${actor}, you already hold this claim.`,
    claimed_by_other: `This task is already claimed by @${state.claimant}.`,
    issue_already_assigned: 'This task already has an assignee and cannot be claimed automatically.',
    actor_has_other_claim: `@${actor}, release or finish your current claimed task before claiming another one.`,
    no_active_claim: 'This task does not have an active claim.',
    not_claimant: `Only @${state.claimant} or a maintainer can change this claim.`,
    renewal_limit_reached: `@${state.claimant} has already used the available renewal.`,
    lease_paused_by_pr: validPullRequest
      ? `The lease is protected by linked pull request #${validPullRequest.number}.`
      : 'The lease is protected by a linked pull request.',
    issue_not_eligible: 'This command is only available on open community contribution tasks.',
    status_active: `This task is claimed by @${state.claimant}.`,
    status_assigned: 'This task is assigned outside the claim automation and is not available.',
    status_available: 'This task is available to claim.',
    lease_expired: `@${state.claimant}'s claim expired because no linked pull request was found.`,
    assignment_removed: `@${state.claimant}'s claim was released after the assignment was removed.`,
  };
  return messages[code] || 'The claim state did not change.';
}

function renderStateComment(state, message, policy, validPullRequest = null) {
  const lines = ['### Task claim', '', message, ''];

  if (state.status === ACTIVE_STATUS) {
    lines.push(`- Assignee: @${state.claimant}`);
    if (validPullRequest) {
      lines.push(`- Linked PR: [#${validPullRequest.number}](${validPullRequest.html_url})`);
      lines.push('- Lease: paused while the linked PR is open');
    } else {
      lines.push(`- Lease expires: ${state.expiresAt}`);
    }
    lines.push(`- Renewals: ${state.renewals}/${policy.maxRenewals}`);
  } else {
    lines.push('- Automation claim: inactive');
  }

  lines.push('', 'Commands: `/claim`, `/unclaim`, `/renew`, `/claim status`', '');
  lines.push(serializeStateMarker(state));
  return lines.join('\n');
}

class GitHubClient {
  constructor({
    token,
    repository,
    apiUrl = 'https://api.github.com',
    graphqlUrl,
  }) {
    if (!token) throw new Error('GITHUB_TOKEN is required.');
    const [owner, repo] = String(repository || '').split('/');
    if (!owner || !repo) throw new Error('GITHUB_REPOSITORY must be owner/repository.');
    this.token = token;
    this.owner = owner;
    this.repo = repo;
    this.apiUrl = apiUrl.replace(/\/$/, '');
    this.graphqlUrl = graphqlUrl || `${this.apiUrl}/graphql`;
    this.repoPath = `/repos/${encodeURIComponent(owner)}/${encodeURIComponent(repo)}`;
  }

  async request(endpoint, { method = 'GET', body, headers = {} } = {}) {
    const requestUrl = endpoint.startsWith('https://') ? endpoint : `${this.apiUrl}${endpoint}`;
    const response = await fetch(requestUrl, {
      method,
      headers: {
        Accept: 'application/vnd.github+json',
        Authorization: `Bearer ${this.token}`,
        'Content-Type': 'application/json',
        'X-GitHub-Api-Version': '2022-11-28',
        ...headers,
      },
      body: body === undefined ? undefined : JSON.stringify(body),
    });
    if (!response.ok) {
      const detail = (await response.text()).slice(0, 1000);
      throw new Error(`GitHub API ${method} ${endpoint} failed (${response.status}): ${detail}`);
    }
    if (response.status === 204) return null;
    return response.json();
  }

  async paginate(endpoint, options = {}) {
    const separator = endpoint.includes('?') ? '&' : '?';
    const output = [];
    for (let page = 1; ; page += 1) {
      const batch = await this.request(`${endpoint}${separator}per_page=100&page=${page}`, options);
      output.push(...batch);
      if (batch.length < 100) return output;
    }
  }

  async graphql(query, variables) {
    const payload = await this.request(this.graphqlUrl, {
      method: 'POST',
      body: { query, variables },
    });
    if (payload.errors && payload.errors.length > 0) {
      throw new Error(`GitHub GraphQL failed: ${JSON.stringify(payload.errors).slice(0, 1000)}`);
    }
    return payload.data;
  }

  getIssue(issueNumber) {
    return this.request(`${this.repoPath}/issues/${issueNumber}`);
  }

  listComments(issueNumber) {
    return this.paginate(`${this.repoPath}/issues/${issueNumber}/comments`);
  }

  async getStateRecord(issueNumber) {
    const comments = await this.listComments(issueNumber);
    const records = comments
      .filter((comment) => comment.user && comment.user.login === BOT_LOGIN)
      .map((comment) => ({ comment, state: parseStateMarker(comment.body) }))
      .filter((record) => record.state && record.state.issue === issueNumber)
      .sort((left, right) => new Date(right.comment.updated_at) - new Date(left.comment.updated_at));
    return records[0] || null;
  }

  async upsertStateComment(issueNumber, record, body) {
    if (record) {
      return this.request(`${this.repoPath}/issues/comments/${record.comment.id}`, {
        method: 'PATCH',
        body: { body },
      });
    }
    return this.request(`${this.repoPath}/issues/${issueNumber}/comments`, {
      method: 'POST',
      body: { body },
    });
  }

  async addAssignee(issueNumber, login) {
    const issue = await this.request(`${this.repoPath}/issues/${issueNumber}/assignees`, {
      method: 'POST',
      body: { assignees: [login] },
    });
    const assigned = (issue.assignees || []).some(
      (assignee) => normalizeLogin(assignee.login) === normalizeLogin(login),
    );
    if (!assigned) throw new Error(`GitHub did not assign @${login} to issue #${issueNumber}.`);
  }

  removeAssignee(issueNumber, login) {
    return this.request(`${this.repoPath}/issues/${issueNumber}/assignees`, {
      method: 'DELETE',
      body: { assignees: [login] },
    });
  }

  async listEligibleIssues(policy, assignee = null) {
    const issues = new Map();
    for (const label of policy.eligibleLabels) {
      const query = new URLSearchParams({ state: 'open', labels: label });
      if (assignee) query.set('assignee', assignee);
      const endpoint = `${this.repoPath}/issues?${query.toString()}`;
      for (const issue of await this.paginate(endpoint)) {
        if (!issue.pull_request) issues.set(issue.number, issue);
      }
    }
    return [...issues.values()];
  }

  async findValidPullRequest(issueNumber, claimant, claimedAt) {
    const data = await this.graphql(`
      query ClaimClosingPullRequests($owner: String!, $repo: String!, $issue: Int!) {
        repository(owner: $owner, name: $repo) {
          issue(number: $issue) {
            closedByPullRequestsReferences(first: 100, includeClosedPrs: true) {
              nodes {
                number
                url
                state
                createdAt
                isDraft
                author { login }
              }
            }
          }
        }
      }
    `, {
      owner: this.owner,
      repo: this.repo,
      issue: issueNumber,
    });
    const nodes = data.repository
      && data.repository.issue
      && data.repository.issue.closedByPullRequestsReferences.nodes;
    if (!nodes) return null;

    const claimTime = new Date(claimedAt).getTime();
    return nodes
      .filter((pullRequest) => pullRequest.state === 'OPEN')
      .filter((pullRequest) => pullRequest.author
        && normalizeLogin(pullRequest.author.login) === normalizeLogin(claimant))
      .filter((pullRequest) => new Date(pullRequest.createdAt).getTime() >= claimTime)
      .map((pullRequest) => ({
        number: pullRequest.number,
        html_url: pullRequest.url,
        isDraft: pullRequest.isDraft,
      }))[0] || null;
  }

  async actorHasOtherActiveClaim(actor, currentIssueNumber, policy, now) {
    for (const issue of await this.listEligibleIssues(policy, actor)) {
      if (issue.number === currentIssueNumber) continue;
      const record = await this.getStateRecord(issue.number);
      if (!record || record.state.status !== ACTIVE_STATUS) continue;
      if (normalizeLogin(record.state.claimant) !== normalizeLogin(actor)) continue;
      const claimantAssigned = (issue.assignees || []).some(
        (assignee) => normalizeLogin(assignee.login) === normalizeLogin(actor),
      );
      if (!claimantAssigned) continue;
      if (!isLeaseExpired(record.state, now)) return true;
      if (await this.findValidPullRequest(issue.number, actor, record.state.claimedAt)) return true;
    }
    return false;
  }
}

function canManageClaims(authorAssociation) {
  return ['OWNER', 'MEMBER', 'COLLABORATOR'].includes(authorAssociation);
}

async function applyEffects(client, issueNumber, effects, appliedEffects = []) {
  for (const effect of effects) {
    if (!effect.login) continue;
    if (effect.type === 'add-assignee') await client.addAssignee(issueNumber, effect.login);
    if (effect.type === 'remove-assignee') await client.removeAssignee(issueNumber, effect.login);
    appliedEffects.push(effect);
  }
  return appliedEffects;
}

async function rollbackEffects(client, issueNumber, effects) {
  const failures = [];
  for (const effect of [...effects].reverse()) {
    try {
      if (effect.type === 'add-assignee') await client.removeAssignee(issueNumber, effect.login);
      if (effect.type === 'remove-assignee') await client.addAssignee(issueNumber, effect.login);
    } catch (error) {
      failures.push(error);
    }
  }
  if (failures.length > 0) {
    throw new AggregateError(failures, 'Failed to roll back assignment changes.');
  }
}

async function processEvent({ client, policy, event, now = new Date().toISOString() }) {
  if (!event.issue || event.issue.pull_request || !event.comment) return 'ignored';
  if (event.comment.user && event.comment.user.type === 'Bot') return 'ignored';

  const command = parseCommand(event.comment.body);
  if (!command) return 'ignored';

  const issueNumber = event.issue.number;
  const actor = event.comment.user.login;
  let issue = await client.getIssue(issueNumber);
  const record = await client.getStateRecord(issueNumber);
  const existingActiveClaim = record && record.state.status === ACTIVE_STATUS;
  if (!isEligibleIssue(issue, policy)
    && !(existingActiveClaim && (command === 'unclaim' || command === 'status'))) {
    return 'ignored';
  }
  let state = record ? record.state : emptyState(issueNumber, now);
  let validPullRequest = state.status === ACTIVE_STATUS
    ? await client.findValidPullRequest(issueNumber, state.claimant, state.claimedAt)
    : null;

  const appliedEffects = [];
  try {
    const reconciliation = reconcileExpiredClaim(state, now, Boolean(validPullRequest));
    if (reconciliation.effects.length > 0) {
      await applyEffects(client, issueNumber, reconciliation.effects, appliedEffects);
      state = reconciliation.state;
      validPullRequest = null;
      issue = await client.getIssue(issueNumber);
    }

    const actorHasOtherActiveClaim = command === 'claim'
      ? await client.actorHasOtherActiveClaim(actor, issueNumber, policy, now)
      : false;
    const result = transitionClaim({
      state,
      command,
      actor,
      now,
      policy,
      issue,
      actorHasOtherActiveClaim,
      hasValidPullRequest: Boolean(validPullRequest),
      canManage: canManageClaims(event.comment.author_association),
    });

    const addEffect = result.effects.find((effect) => effect.type === 'add-assignee');
    try {
      await applyEffects(client, issueNumber, result.effects, appliedEffects);
    } catch (error) {
      if (!addEffect) throw error;
      const failureState = {
        ...state,
        updatedAt: now,
        reason: 'assignment_failed',
      };
      const body = renderStateComment(
        failureState,
        'GitHub could not assign this task automatically. A maintainer needs to check repository assignment permissions.',
        policy,
      );
      try {
        await client.upsertStateComment(issueNumber, record, body);
        appliedEffects.length = 0;
      } catch (commentError) {
        throw new AggregateError([error, commentError], 'Failed to assign the task and report the failure.');
      }
      throw error;
    }

    const displayedPullRequest = result.state.status === ACTIVE_STATUS ? validPullRequest : null;
    const message = resultMessage(result.code, result.state, actor, displayedPullRequest);
    const body = renderStateComment(result.state, message, policy, displayedPullRequest);
    await client.upsertStateComment(issueNumber, record, body);
    return result.code;
  } catch (error) {
    try {
      await rollbackEffects(client, issueNumber, appliedEffects);
    } catch (rollbackError) {
      throw new AggregateError(
        [error, rollbackError],
        'Failed to persist claim state and roll back assignment changes.',
      );
    }
    throw error;
  }
}

async function sweepExpiredClaims({ client, policy, now = new Date().toISOString() }) {
  const failures = [];
  let released = 0;

  for (const issue of await client.listEligibleIssues(policy)) {
    try {
      const record = await client.getStateRecord(issue.number);
      if (!record || record.state.status !== ACTIVE_STATUS) continue;

      const claimantAssigned = (issue.assignees || []).some(
        (assignee) => normalizeLogin(assignee.login) === normalizeLogin(record.state.claimant),
      );
      if (!claimantAssigned) {
        const state = {
          ...record.state,
          status: 'released',
          updatedAt: now,
          reason: 'assignment_removed',
        };
        const body = renderStateComment(
          state,
          resultMessage('assignment_removed', state),
          policy,
        );
        await client.upsertStateComment(issue.number, record, body);
        released += 1;
        continue;
      }

      const validPullRequest = await client.findValidPullRequest(
        issue.number,
        record.state.claimant,
        record.state.claimedAt,
      );
      const reconciliation = reconcileExpiredClaim(record.state, now, Boolean(validPullRequest));
      if (reconciliation.effects.length === 0) continue;

      const appliedEffects = [];
      await applyEffects(client, issue.number, reconciliation.effects, appliedEffects);
      const body = renderStateComment(
        reconciliation.state,
        resultMessage('lease_expired', reconciliation.state),
        policy,
      );
      try {
        await client.upsertStateComment(issue.number, record, body);
      } catch (error) {
        try {
          await rollbackEffects(client, issue.number, appliedEffects);
        } catch (rollbackError) {
          throw new AggregateError(
            [error, rollbackError],
            'Failed to persist expiry and roll back assignment changes.',
          );
        }
        throw error;
      }
      released += 1;
    } catch (error) {
      failures.push(`#${issue.number}: ${error.message}`);
    }
  }

  if (failures.length > 0) {
    throw new Error(`Claim sweep failed for ${failures.join('; ')}`);
  }
  return released;
}

async function cleanupClaim({ client, policy, event, now = new Date().toISOString() }) {
  if (!event.issue || event.issue.pull_request) return 'ignored';
  const issue = await client.getIssue(event.issue.number);
  const record = await client.getStateRecord(issue.number);
  if (!record || record.state.status !== ACTIVE_STATUS) return 'ignored';

  const claimantAssigned = (issue.assignees || []).some(
    (assignee) => normalizeLogin(assignee.login) === normalizeLogin(record.state.claimant),
  );
  if (isEligibleIssue(issue, policy) && claimantAssigned) return 'ignored';

  const appliedEffects = [];
  if (claimantAssigned) {
    await applyEffects(client, issue.number, [{
      type: 'remove-assignee',
      login: record.state.claimant,
    }], appliedEffects);
  }
  const reason = issue.state !== 'open'
    ? 'issue_closed'
    : (isEligibleIssue(issue, policy) ? 'assignment_removed' : 'task_unpublished');
  const state = {
    ...record.state,
    status: 'released',
    updatedAt: now,
    reason,
  };
  const messages = {
    issue_closed: `@${state.claimant}'s claim was released because the issue was closed.`,
    task_unpublished: `@${state.claimant}'s claim was released because this is no longer a published contribution task.`,
    assignment_removed: `@${state.claimant}'s claim was released after the assignment was removed.`,
  };
  try {
    await client.upsertStateComment(
      issue.number,
      record,
      renderStateComment(state, messages[reason], policy),
    );
  } catch (error) {
    try {
      await rollbackEffects(client, issue.number, appliedEffects);
    } catch (rollbackError) {
      throw new AggregateError(
        [error, rollbackError],
        'Failed to persist cleanup and roll back assignment changes.',
      );
    }
    throw error;
  }
  return reason;
}

async function main() {
  const mode = process.argv[2];
  const policyPath = process.env.CLAIM_POLICY_PATH
    || path.resolve(process.cwd(), '.github/claim-policy.json');
  const policy = loadPolicy(policyPath);
  const client = new GitHubClient({
    token: process.env.GITHUB_TOKEN,
    repository: process.env.GITHUB_REPOSITORY,
    apiUrl: process.env.GITHUB_API_URL,
    graphqlUrl: process.env.GITHUB_GRAPHQL_URL,
  });

  if (mode === 'event') {
    if (!process.env.GITHUB_EVENT_PATH) throw new Error('GITHUB_EVENT_PATH is required.');
    const event = JSON.parse(fs.readFileSync(process.env.GITHUB_EVENT_PATH, 'utf8'));
    const result = await processEvent({ client, policy, event });
    console.log(`Claim command result: ${result}`);
    return;
  }
  if (mode === 'sweep') {
    const released = await sweepExpiredClaims({ client, policy });
    console.log(`Released ${released} expired claim(s).`);
    return;
  }
  if (mode === 'cleanup') {
    if (!process.env.GITHUB_EVENT_PATH) throw new Error('GITHUB_EVENT_PATH is required.');
    const event = JSON.parse(fs.readFileSync(process.env.GITHUB_EVENT_PATH, 'utf8'));
    const result = await cleanupClaim({ client, policy, event });
    console.log(`Claim cleanup result: ${result}`);
    return;
  }
  throw new Error('Usage: node script/github/issue-claim.js <event|cleanup|sweep>');
}

if (require.main === module) {
  main().catch((error) => {
    console.error(error.stack || error.message);
    process.exitCode = 1;
  });
}

module.exports = {
  ACTIVE_STATUS,
  GitHubClient,
  addDays,
  canManageClaims,
  cleanupClaim,
  emptyState,
  isEligibleIssue,
  isLeaseExpired,
  loadPolicy,
  normalizeLogin,
  parseCommand,
  parseStateMarker,
  processEvent,
  reconcileExpiredClaim,
  renderStateComment,
  serializeStateMarker,
  sweepExpiredClaims,
  transitionClaim,
};
