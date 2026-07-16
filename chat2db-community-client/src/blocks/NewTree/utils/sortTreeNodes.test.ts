import assert from 'node:assert/strict';
import { TreeNodeType } from '@/constants/tree';
import type { TreeNodeData } from '@/typings';
import { applyDatabaseObjectTreeSorting } from './sortTreeNodes';

function treeNode(
  originalTitle: string,
  treeNodeType: TreeNodeType,
  options?: { pinned?: boolean; children?: TreeNodeData[] },
): TreeNodeData {
  return {
    key: originalTitle,
    originalTitle,
    treeNodeType,
    extraParams: {},
    decorativeParams: { pinned: options?.pinned },
    children: options?.children,
  };
}

const originalNodes = [
  treeNode('Zulu source', TreeNodeType.DATA_SOURCE, {
    children: [
      treeNode('Views', TreeNodeType.VIEWS, {
        children: [treeNode('view10', TreeNodeType.VIEW), treeNode('view2', TreeNodeType.VIEW)],
      }),
      treeNode('Tables', TreeNodeType.TABLES, {
        children: [
          treeNode('table10', TreeNodeType.TABLE),
          treeNode('beta', TreeNodeType.TABLE),
          treeNode('table2', TreeNodeType.TABLE),
          treeNode('Zulu', TreeNodeType.TABLE, { pinned: true }),
          treeNode('alpha', TreeNodeType.TABLE, { pinned: true }),
        ],
      }),
    ],
  }),
  treeNode('Alpha source', TreeNodeType.DATA_SOURCE),
];

const unchangedNodes = applyDatabaseObjectTreeSorting(originalNodes, false);
assert.strictEqual(unchangedNodes, originalNodes);

const sortedNodes = applyDatabaseObjectTreeSorting(originalNodes, true);
assert.deepEqual(
  sortedNodes.map((node) => node.originalTitle),
  ['Zulu source', 'Alpha source'],
  'data source order must remain unchanged',
);
assert.deepEqual(
  sortedNodes[0].children?.map((node) => node.originalTitle),
  ['Views', 'Tables'],
  'directory order must remain unchanged',
);
assert.deepEqual(
  sortedNodes[0].children?.[0].children?.map((node) => node.originalTitle),
  ['view2', 'view10'],
  'view objects should use natural name order',
);
assert.deepEqual(
  sortedNodes[0].children?.[1].children?.map((node) => node.originalTitle),
  ['alpha', 'Zulu', 'beta', 'table2', 'table10'],
  'expandable table objects should use natural name order with pinned nodes first',
);
assert.deepEqual(
  originalNodes[0].children?.[1].children?.map((node) => node.originalTitle),
  ['table10', 'beta', 'table2', 'Zulu', 'alpha'],
  'display sorting must not mutate the store order',
);

const groupedSources = [
  treeNode('Group', TreeNodeType.GROUP, {
    children: [
      treeNode('Second source', TreeNodeType.DATA_SOURCE),
      treeNode('First source', TreeNodeType.DATA_SOURCE),
    ],
  }),
];
const sortedGroupedSources = applyDatabaseObjectTreeSorting(groupedSources, true);
assert.deepEqual(
  sortedGroupedSources[0].children?.map((node) => node.originalTitle),
  ['Second source', 'First source'],
  'data source order inside a group must remain unchanged',
);

console.log('NewTree optional database object ordering tests passed');
