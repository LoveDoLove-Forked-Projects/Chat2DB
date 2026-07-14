import { TreeNodeType } from '@/constants';
import { runtimeEditionConfig } from '@/constants/runtimeEdition';
import { useTreeStore } from '@/store/tree';
import { TreeNodeData } from '@/typings';
import { useMemo } from 'react';

export default function useTrimTreeData(props?: {
  leafNodes?: string[];
  hiddenNoPermission?: boolean;
  excludeNodes?: string[];
}): TreeNodeData[] | null {
  const { leafNodes, hiddenNoPermission, excludeNodes } = props || {};
  const { treeData, searchResult, hiddenTreeNodeIds } = useTreeStore((state) => ({
    treeData: state.treeData,
    searchResult: state.searchResult,
    hiddenTreeNodeIds: state.hiddenTreeNodeIds,
  }));

  const filterDatabaseAndSchema = (data: TreeNodeData[], hiddenIds?: string[] | null) => {
    return data
      .map((node) => {
        // creates a shallow copy of the node
        const newNode = { ...node };
        const _hiddenIds = newNode.extraParams?.dataSourceId
          ? hiddenTreeNodeIds?.[newNode.extraParams.dataSourceId]
          : null;

        // Exclude specified nodes
        if (excludeNodes?.includes(newNode.treeNodeType)) {
          return null;
        }

        if (
          !runtimeEditionConfig.aiDataCollection &&
          (newNode.treeNodeType === TreeNodeType.AI_DATA_COLLECTIONS ||
            newNode.treeNodeType === TreeNodeType.AI_DATA_COLLECTION ||
            newNode.treeNodeType === TreeNodeType.AI_DATA_COLLECTION_TABLE ||
            newNode.treeNodeType === TreeNodeType.AI_DATA_COLLECTION_VIEW)
        ) {
          return null;
        }

        // Exclude data sources without permission
        if (
          hiddenNoPermission &&
          newNode.treeNodeType === TreeNodeType.DATA_SOURCE &&
          !newNode.extraParams?.hasPermission
        ) {
          return null;
        }

        // removes the children of the specified leaf node
        if (leafNodes?.includes(newNode.treeNodeType)) {
          newNode.isLeaf = true;
          newNode.children = undefined;
        }

        if (newNode?.children) {
          // recursively processes child nodes and assigns them to new children
          newNode.children = filterDatabaseAndSchema(newNode.children, hiddenIds || _hiddenIds);
        }

        // If it is a database or schema node and it is in the hidden list, return null
        if (newNode.treeNodeType === TreeNodeType.DATABASE || newNode.treeNodeType === TreeNodeType.SCHEMA) {
          const _data = (hiddenIds || _hiddenIds)?.includes(newNode.key) ? null : newNode;
          return _data;
        }
        return newNode;
      })
      .filter(Boolean) as TreeNodeData[]; // filters out null values
  };

  // filtered tree data
  const filteredTreeData = useMemo(() => {
    // initial data
    const _treeData = searchResult || treeData;

    if (!_treeData) {
      return null;
    }

    // filters the displayed database and schema
    const filteredDatabaseAndSchema = filterDatabaseAndSchema(_treeData);

    return filteredDatabaseAndSchema;
  }, [searchResult, treeData, hiddenTreeNodeIds]);

  return filteredTreeData;
}
