import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';

import editionUiExtension from '@edition-ui';
import {
  filterVisibleOrganizationPanels,
  mergeMainNavigationItems,
  mergeOrganizationPanelContributions,
} from './merge';
import type { EditionMainNavigationContribution, EditionOrganizationPanelContribution } from './types';

assert.deepEqual(editionUiExtension.mainNavigationItems, [], 'Community should not contribute edition navigation');
assert.deepEqual(editionUiExtension.organizationPanels, [], 'Community should not contribute organization panels');

const coreNavigation = [
  {
    key: 'workspace',
    icon: 'core-workspace-icon',
    name: 'Workspace',
    component: 'core-workspace-panel',
  },
];
const navigationContributions: EditionMainNavigationContribution[] = [
  {
    id: 'workspace',
    icon: 'extension-workspace-icon',
    name: 'Duplicate workspace',
    component: 'duplicate-workspace-panel',
  },
  {
    id: 'edition-knowledge',
    icon: 'knowledge-icon',
    name: 'Knowledge',
    component: 'knowledge-panel',
  },
  {
    id: 'edition-knowledge',
    icon: 'duplicate-knowledge-icon',
    name: 'Duplicate knowledge',
    component: 'duplicate-knowledge-panel',
  },
];

const mergedNavigation = mergeMainNavigationItems(coreNavigation, navigationContributions);
assert.deepEqual(
  mergedNavigation.map((item) => item.key),
  ['workspace', 'edition-knowledge'],
  'core navigation should win and duplicate extension ids should keep their first registration',
);
assert.equal(mergedNavigation[1].name, 'Knowledge');
assert.notStrictEqual(mergedNavigation, coreNavigation, 'navigation merge should return a new array');

const organizationContributions: EditionOrganizationPanelContribution[] = [
  { id: 'TeamSettings', label: 'Duplicate settings', panel: 'duplicate-settings-panel' },
  { id: 'edition-audit', label: 'Audit', panel: 'audit-panel' },
  { id: 'edition-audit', label: 'Duplicate audit', panel: 'duplicate-audit-panel' },
];
const mergedOrganizationPanels = mergeOrganizationPanelContributions(organizationContributions, ['TeamSettings']);
assert.deepEqual(
  mergedOrganizationPanels.map((panel) => panel.id),
  ['edition-audit'],
  'core organization ids should win and duplicate extension ids should keep their first registration',
);
assert.equal(mergedOrganizationPanels[0].panel, 'audit-panel');

const visibilityContext = { organizationId: 7, roleCodes: ['MEMBER'], isAdmin: false, isOwner: false };
const visibleOrganizationPanels = filterVisibleOrganizationPanels(
  [
    { id: 'public-panel', label: 'Public', panel: 'public-panel' },
    {
      id: 'admin-panel',
      label: 'Admin',
      panel: 'admin-panel',
      isVisible: ({ isAdmin }) => isAdmin,
    },
  ],
  visibilityContext,
);
assert.deepEqual(
  visibleOrganizationPanels.map((panel) => panel.id),
  ['public-panel'],
  'organization panels should be filtered by the current role context',
);

for (const host of [
  'src/pages/main/index.tsx',
  'src/pages/main/CommunityMainPage.tsx',
  'src/pages/main/organization/index.tsx',
]) {
  assert.match(readFileSync(host, 'utf8'), /editionUiExtension/, `${host} should consume the edition UI extension`);
}
assert.match(readFileSync('.umirc.ts', 'utf8'), /'@edition-ui':/, 'Umi should resolve the edition UI alias');

console.log('Edition UI extension tests passed.');
