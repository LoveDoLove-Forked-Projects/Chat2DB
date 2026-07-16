import type { TreeNodeData } from '@/typings';
import { TreeNodeType } from '@/constants/tree';

const nameCollator = new Intl.Collator(undefined, {
  numeric: true,
  sensitivity: 'base',
});

export function compareNames(left: string, right: string) {
  return nameCollator.compare(left, right);
}

const sortableObjectNodeTypes = new Set([
  TreeNodeType.AI_DATA_COLLECTION,
  TreeNodeType.AI_DATA_COLLECTION_TABLE,
  TreeNodeType.AI_DATA_COLLECTION_VIEW,
  TreeNodeType.DATABASE_ACCOUNT,
  TreeNodeType.DATABASE,
  TreeNodeType.SCHEMA,
  TreeNodeType.TABLE,
  TreeNodeType.COLUMN,
  TreeNodeType.KEY,
  TreeNodeType.INDEX,
  TreeNodeType.VIEW,
  TreeNodeType.VIEWCOLUMN,
  TreeNodeType.FUNCTION,
  TreeNodeType.PROCEDURE,
  TreeNodeType.TRIGGER,
  TreeNodeType.SAVE_CONSOLE,
]);

function sortDatabaseObjectNodes(nodes: TreeNodeData[]): TreeNodeData[] {
  const nodesWithSortedChildren = nodes.map((node) => {
    if (!node.children) {
      return node;
    }
    return {
      ...node,
      children: sortDatabaseObjectNodes(node.children),
    };
  });

  if (!nodesWithSortedChildren.every((node) => sortableObjectNodeTypes.has(node.treeNodeType))) {
    return nodesWithSortedChildren;
  }

  return nodesWithSortedChildren.sort((left, right) => {
    const pinnedOrder =
      Number(Boolean(right.decorativeParams?.pinned)) - Number(Boolean(left.decorativeParams?.pinned));
    return pinnedOrder || compareNames(left.originalTitle, right.originalTitle);
  });
}

export function applyDatabaseObjectTreeSorting(nodes: TreeNodeData[], enabled: boolean) {
  return enabled ? sortDatabaseObjectNodes(nodes) : nodes;
}
