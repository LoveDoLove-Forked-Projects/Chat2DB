import { memo, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useStyles } from './style';
import { IconButton, SearchBar } from '@chat2db/ui';
import { Button, Tooltip } from 'antd';
import { DatabaseBackup } from 'lucide-react';
import AddDatasourceBar from './components/AddDatasourceBar';
import TreeSetting from './components/TreeSetting';
import { useTreeStore } from '@/store/tree';
import { useOrgStore } from '@/store/workspaceContext';
import { useGlobalStore } from '@/store/global';
import i18n from '@/i18n';
import { searchTreeNodes } from '@/utils';
import { filterTreeNodesForDisplay } from '@/utils/filterTreeNodes';
import { hydrateTreeForSearch } from '@/utils/hydrateTreeForSearch';
import { TreeNodeType } from '@/constants';
import { clientRuntime } from '@client-runtime';
import { treeConfig } from '@/blocks/NewTree/treeConfig';
import createRequest from '@/service/base';
import sqlService, { ITableSearchResult } from '@/service/sql';
import type { TreeNodeData } from '@/typings';
import { useUpdateEffect } from 'ahooks';
import { debounce } from 'lodash';
import {
  STORAGE_MIGRATION_STATUS_EVENT,
  needsStorageMigration,
  type StorageMigrationStatus,
} from './storageMigrationPrompt';
import {
  ShortcutAction,
  ShortcutOverrides,
  getEffectiveShortcutConfigMap,
  isShortcutEventMatch,
} from '@/constants/shortcut';

interface ActionButton {
  key: string;
  icon: string;
  label: string;
  onClick: () => void;
  isHidden?: boolean;
}

interface WorkspaceLeftActionBarProps {
  active?: boolean;
  onLocateActiveTab?: () => void;
  locateActiveTabDisabled?: boolean;
}

type SearchBarHandle = { focus: () => void; blur: () => void };

const loadStorageMigrationStatus = createRequest<void, StorageMigrationStatus>('/api/system/storage-migration', {
  errorLevel: false,
});

function buildTableSearchTree(dataSource: TreeNodeData, matches: ITableSearchResult[]): TreeNodeData[] {
  const roots: TreeNodeData[] = [];
  const dataSourceParams = dataSource.extraParams;

  const ensureNode = (
    siblings: TreeNodeData[],
    treeNodeType: TreeNodeType,
    originalTitle: string,
    extraParams: TreeNodeData['extraParams'],
  ) => {
    const key = treeConfig[treeNodeType].createTreeNodeKey!(extraParams);
    let existing = siblings.find((item) => item.key === key);
    if (!existing) {
      existing = {
        key,
        originalTitle,
        title: null,
        treeNodeType,
        isLeaf: false,
        extraParams,
        children: [],
      };
      siblings.push(existing);
    }
    return existing;
  };

  matches.forEach((match) => {
    const extraParams = {
      ...dataSourceParams,
      databaseName: match.databaseName || undefined,
      schemaName: match.schemaName || undefined,
    };
    let siblings = roots;
    if (dataSourceParams.supportDatabase && match.databaseName) {
      const database = ensureNode(siblings, TreeNodeType.DATABASE, match.databaseName, extraParams);
      siblings = database.children as TreeNodeData[];
    }
    if (dataSourceParams.supportSchema && match.schemaName) {
      const schema = ensureNode(siblings, TreeNodeType.SCHEMA, match.schemaName, extraParams);
      siblings = schema.children as TreeNodeData[];
    }
    const tables = ensureNode(siblings, TreeNodeType.TABLES, i18n('common.text.tables'), extraParams);
    const tableParams = { ...extraParams, tableName: match.name };
    const tableKey = treeConfig[TreeNodeType.TABLE].createTreeNodeKey!(tableParams);
    if (!(tables.children as TreeNodeData[]).some((item) => item.key === tableKey)) {
      (tables.children as TreeNodeData[]).push({
        key: tableKey,
        originalTitle: match.name,
        title: null,
        treeNodeType: TreeNodeType.TABLE,
        isLeaf: false,
        describe: match.comment,
        extraParams: tableParams,
        decorativeParams: { comment: match.comment },
      });
    }
  });

  return roots;
}

const WorkspaceLeftActionBar = memo<WorkspaceLeftActionBarProps>(
  ({ active = true, onLocateActiveTab, locateActiveTabDisabled = false }) => {
    const searchBarRef = useRef<SearchBarHandle>(null);
    const searchSequenceRef = useRef(0);
    const { refreshTreeData, searchBarValue, setSearchBarValue, searchResultKeys, hiddenTreeNodeIds } = useTreeStore(
      (s) => ({
        refreshTreeData: s.refreshTreeData,
        searchBarValue: s.searchBarValue,
        setSearchBarValue: s.setSearchBarValue,
        searchResultKeys: s.searchResultKeys,
        hiddenTreeNodeIds: s.hiddenTreeNodeIds,
      }),
    );

    const { isEmbedIframe, setSettingPageActiveTab, shortcutOverrides } = useGlobalStore((s) => ({
      isEmbedIframe: s.isEmbedIframe,
      setSettingPageActiveTab: s.setSettingPageActiveTab,
      shortcutOverrides: s.shortcutOverrides,
    }));
    const shortcutConfig = useMemo(
      () => getEffectiveShortcutConfigMap(shortcutOverrides as ShortcutOverrides),
      [shortcutOverrides],
    );

    const { styles } = useStyles();
    const showStorageMigration = !isEmbedIframe && clientRuntime.showStorageMigration !== 'community';
    const [migrationPending, setMigrationPending] = useState(false);

    const { isAdmin } = useOrgStore((s) => {
      return {
        isAdmin: s.isAdmin,
      };
    });

    const buttonList = useMemo<ActionButton[]>(() => {
      return [
        {
          key: 'refresh',
          icon: 'icon-refresh',
          label: i18n('common.button.refresh'),
          onClick: refreshTreeData,
        },
      ];
    }, [refreshTreeData]);

    const searchBarOnChange = (e) => {
      setSearchBarValue(e.target.value);
    };

    const debouncedSearch = useCallback(
      debounce(async () => {
        const treeStore = useTreeStore.getState();
        const value = treeStore.regularSearchBarValue;
        const rawValue = treeStore.searchBarValue.trim();
        const searchSequence = ++searchSequenceRef.current;
        if (!value) {
          treeStore.setSearchResult(null);
          treeStore.setSearchResultKeys(null);
          return;
        }
        const visibleTreeData = filterTreeNodesForDisplay(treeStore.treeData || [], {
          hiddenTreeNodeIds: treeStore.hiddenTreeNodeIds,
        });
        const { matchedNodes, matchedKeys, parentIdsWithMatches } = searchTreeNodes(visibleTreeData, value);
        treeStore.setSearchResult(matchedNodes);
        treeStore.setSearchResultKeys(matchedKeys);
        treeStore.setExpandedKeys([...parentIdsWithMatches, ...treeStore.expandedKeys]);

        if (rawValue.length < 2 || clientRuntime.showStorageMigration === 'community') {
          return;
        }

        const hydratedTreeData = await hydrateTreeForSearch(visibleTreeData, rawValue, async (node, searchValue) => {
          const matches = await sqlService.searchTableList({
            dataSourceId: node.extraParams.dataSourceId!,
            searchKey: searchValue,
            limit: 100,
          });
          return buildTableSearchTree(node, matches);
        });
        const latestTreeStore = useTreeStore.getState();
        if (searchSequence !== searchSequenceRef.current || latestTreeStore.searchBarValue.trim() !== rawValue) {
          return;
        }
        const hydratedResult = searchTreeNodes(hydratedTreeData, latestTreeStore.regularSearchBarValue);
        latestTreeStore.setSearchResult(hydratedResult.matchedNodes);
        latestTreeStore.setSearchResultKeys(hydratedResult.matchedKeys);
        latestTreeStore.setExpandedKeys([
          ...hydratedResult.parentIdsWithMatches,
          ...latestTreeStore.expandedKeys,
        ]);
      }, 300),
      [],
    );

    useUpdateEffect(() => {
      debouncedSearch();
      return () => debouncedSearch.cancel();
    }, [searchBarValue, hiddenTreeNodeIds, debouncedSearch]);

    useEffect(() => {
      if (!active) {
        return;
      }

      const searchArea = document.getElementById('tree-search-area');
      const handleKeyDown = (event: KeyboardEvent) => {
        if (isShortcutEventMatch(event, shortcutConfig[ShortcutAction.WorkspaceTreeSearch].binding)) {
          event.preventDefault();
          searchBarRef.current?.focus?.();
        }
      };

      searchArea?.addEventListener('keydown', handleKeyDown);
      return () => {
        searchArea?.removeEventListener('keydown', handleKeyDown);
      };
    }, [active, shortcutConfig]);

    useEffect(() => {
      if (!active || !showStorageMigration) {
        setMigrationPending(false);
        return;
      }
      let disposed = false;
      void loadStorageMigrationStatus()
        .then((status) => {
          if (!disposed) {
            setMigrationPending(needsStorageMigration(status));
          }
        })
        .catch(() => {
          if (!disposed) {
            setMigrationPending(false);
          }
        });
      return () => {
        disposed = true;
      };
    }, [active, showStorageMigration]);

    useEffect(() => {
      if (!showStorageMigration) {
        return;
      }
      const handleStatus = (event: Event) => {
        const status = (event as CustomEvent<StorageMigrationStatus>).detail;
        if (status) {
          setMigrationPending(needsStorageMigration(status));
        }
      };
      window.addEventListener(STORAGE_MIGRATION_STATUS_EVENT, handleStatus);
      return () => window.removeEventListener(STORAGE_MIGRATION_STATUS_EVENT, handleStatus);
    }, [showStorageMigration]);

    const showAddDatasourceBar = useMemo(() => {
      return isAdmin && !isEmbedIframe;
    }, [isAdmin, isEmbedIframe]);

    const showTreeSetting = useMemo(() => {
      return !isEmbedIframe;
    }, [isEmbedIframe]);

    return (
      <div>
        <div className={styles.searchRow}>
          <SearchBar
            ref={searchBarRef}
            className={styles.searchBar}
            searchAreaId="tree-search-area"
            placeholder={i18n('common.text.search')}
            value={searchBarValue}
            onChange={searchBarOnChange}
            suffix={
              <span className={styles.searchMatchCount}>
                {searchBarValue && searchResultKeys ? searchResultKeys.length : null}
              </span>
            }
          />
        </div>
        <div className={styles.workspaceLeftActionBar}>
          {showAddDatasourceBar && <AddDatasourceBar />}
          {buttonList.map((item) => {
            if (item.isHidden) {
              return null;
            }
            return (
              <Tooltip title={item.label} mouseEnterDelay={1} key={item.key}>
                <span>
                  <IconButton size="sm" onClick={item.onClick} code={item.icon} />
                </span>
              </Tooltip>
            );
          })}
          {showStorageMigration && migrationPending ? (
            <Button
              className={styles.storageMigrationButton}
              danger
              icon={<DatabaseBackup aria-hidden="true" size={14} />}
              onClick={() => setSettingPageActiveTab('storageMigration')}
              size="small"
              type="text"
            >
              {i18n('workspace.action.storageMigrationPending')}
            </Button>
          ) : null}
          <div className={styles.rightActions}>
            {onLocateActiveTab && (
              <Tooltip title={i18n('workspace.tips.locateActiveTab')} mouseEnterDelay={1}>
                <span>
                  <IconButton
                    size="sm"
                    code="icon-miaozhun"
                    disabled={locateActiveTabDisabled}
                    onClick={onLocateActiveTab}
                  />
                </span>
              </Tooltip>
            )}
            {showTreeSetting && <TreeSetting />}
          </div>
        </div>
      </div>
    );
  },
);

export default WorkspaceLeftActionBar;
