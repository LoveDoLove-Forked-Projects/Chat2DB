import type { RedisDataItem } from '@/typings/redis';

const REDIS_KEY_SEPARATOR = ':';
const nameCollator = new Intl.Collator(undefined, {
  numeric: true,
  sensitivity: 'base',
});

interface MutableRedisKeyGroup {
  key: string;
  title: string;
  count: number;
  groups: Map<string, MutableRedisKeyGroup>;
  leaves: RedisKeyTreeNode[];
}

export interface RedisKeyTreeNode {
  key: string;
  title: string;
  kind: 'group' | 'key';
  count?: number;
  rowIndex?: number;
  redisKey?: string;
  selectable?: boolean;
  isLeaf?: boolean;
  children?: RedisKeyTreeNode[];
}

function groupNodeKey(path: string[]) {
  return `redis-group:${path.map(encodeURIComponent).join('/')}`;
}

export function redisKeyNodeKey(redisKey: string) {
  return `redis-key:${encodeURIComponent(redisKey)}`;
}

function createMutableGroup(key: string, title: string): MutableRedisKeyGroup {
  return {
    key,
    title,
    count: 0,
    groups: new Map(),
    leaves: [],
  };
}

function compareTreeNodes(left: RedisKeyTreeNode, right: RedisKeyTreeNode) {
  if (left.kind !== right.kind) {
    return left.kind === 'group' ? -1 : 1;
  }
  return nameCollator.compare(left.title, right.title);
}

function finalizeGroup(group: MutableRedisKeyGroup): RedisKeyTreeNode {
  const children = [
    ...Array.from(group.groups.values()).map(finalizeGroup),
    ...group.leaves,
  ].sort(compareTreeNodes);

  return {
    key: group.key,
    title: group.title,
    kind: 'group',
    count: group.count,
    selectable: false,
    isLeaf: false,
    children,
  };
}

export function buildRedisKeyTree(items: RedisDataItem[]) {
  const root = createMutableGroup('redis-root', '');

  items.forEach((item, rowIndex) => {
    if (!item.name) {
      return;
    }

    const segments = item.name.split(REDIS_KEY_SEPARATOR);
    const groupSegments = segments.slice(0, -1);
    const canGroup = groupSegments.length > 0 && groupSegments.every(Boolean);
    let parent = root;

    if (canGroup) {
      const path: string[] = [];
      groupSegments.forEach((segment) => {
        path.push(segment);
        let group = parent.groups.get(segment);
        if (!group) {
          group = createMutableGroup(groupNodeKey(path), segment);
          parent.groups.set(segment, group);
        }
        group.count += 1;
        parent = group;
      });
    }

    parent.leaves.push({
      key: redisKeyNodeKey(item.name),
      title: item.name,
      kind: 'key',
      rowIndex,
      redisKey: item.name,
      isLeaf: true,
    });
  });

  return [
    ...Array.from(root.groups.values()).map(finalizeGroup),
    ...root.leaves,
  ].sort(compareTreeNodes);
}

export function collectRedisGroupKeys(nodes: RedisKeyTreeNode[]) {
  const keys: string[] = [];

  const visit = (treeNodes: RedisKeyTreeNode[]) => {
    treeNodes.forEach((node) => {
      if (node.kind !== 'group') {
        return;
      }
      keys.push(node.key);
      if (node.children) {
        visit(node.children);
      }
    });
  };

  visit(nodes);
  return keys;
}
