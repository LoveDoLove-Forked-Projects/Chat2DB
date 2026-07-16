import { memo, useState, useRef, useMemo, useEffect, type Key } from 'react';
import { Empty, Segmented, Spin, Tooltip, Tree, type TreeProps } from 'antd';
import { Folder, FolderOpen, KeyRound, List, ListTree } from 'lucide-react';
import i18n from '@/i18n';
import { useStyles } from './style';
import { RedisDataItem } from '@/typings/redis';
import { RedisFieldType } from '@/constants/redis';
import EditData from './EditData';
import BaseTable, { BaseTableRef } from '@/components/BaseTable';
import redisServer from '@/service/nonRelationalDatabase/redis';
import SplitPane from 'react-split-pane';
import { ToolbarBtn, SearchBar } from '@chat2db/ui';
import openUnifiedDeletion from '@/utils/staticModal/unifiedDeletion';
import {
  buildRedisKeyTree,
  collectRedisGroupKeys,
  redisKeyNodeKey,
  type RedisKeyTreeNode,
} from './redisKeyTree';

const REDIS_SCAN_COUNT = 1000;
const INITIAL_SCAN_CURSOR = '0';
const EDIT_PANE_COLLAPSED_SIZE = 0;
const EDIT_PANE_DEFAULT_SIZE = 320;
const EDIT_PANE_COLLAPSE_THRESHOLD = 50;
const SplitPaneAny = SplitPane as any;
type RedisKeyViewMode = 'list' | 'tree';

function formatTime(time: number) {
  if (time < 60) {
    return `${Math.floor(time)} ${i18n('common.text.second')}`;
  } else if (time < 60 * 60) {
    return `${Math.floor(time / 60)} ${i18n('common.text.minute')}`;
  } else if (time < 24 * 60 * 60) {
    return `${Math.floor(time / 60 / 60)} ${i18n('common.text.hour')}`;
  } else {
    return `${Math.floor(time / 60 / 60 / 24)} ${i18n('common.text.day')}`;
  }
}

function formatRedisTtl(value?: number | null) {
  if (value === undefined || value === null || value < -1) {
    return '-';
  }
  if (value === -1) {
    return i18n('redis.noExpirationTime');
  }
  return formatTime(value);
}

function isRedisDataItemLoaded(redisDataItem: RedisDataItem) {
  if (redisDataItem.isDraftFE) {
    return true;
  }
  if (redisDataItem.type === RedisFieldType.STRING) {
    return redisDataItem.value !== undefined;
  }
  if (redisDataItem.type === RedisFieldType.STREAM) {
    return redisDataItem.streamValues !== undefined;
  }
  return (
    redisDataItem.values !== undefined ||
    redisDataItem.listValues !== undefined ||
    redisDataItem.hashValues !== undefined ||
    redisDataItem.zsValues !== undefined
  );
}

function mergeRedisKeys(current: RedisDataItem[], next: RedisDataItem[]) {
  const itemMap = new Map<string | null, RedisDataItem>();
  current.forEach((item) => {
    itemMap.set(item.name, item);
  });
  next.forEach((item) => {
    if (!itemMap.has(item.name)) {
      itemMap.set(item.name, item);
    }
  });
  return Array.from(itemMap.values());
}

interface IProps {
  className?: string;
  uniqueData: {
    dataSourceId: string;
    dataSourceName: string;
    databaseName: string;
  };
}

const RedisAllData = (props) => {
  const { className, uniqueData } = props;
  const { styles, cx } = useStyles();
  const [tableData, setTableData] = useState<RedisDataItem[] | null>(null);
  const [selectedRows, setSelectedRows] = useState<number[]>([]);
  const [curRedisDataItem, setRedisDataItem] = useState<RedisDataItem | null>(null);
  const [editPaneSize, setEditPaneSize] = useState(EDIT_PANE_COLLAPSED_SIZE);
  const [searchBarValue, setSearchBarValue] = useState('');
  const [presenceDraft, setPresenceDraft] = useState(false);
  const [scanCursor, setScanCursor] = useState(INITIAL_SCAN_CURSOR);
  const [scanComplete, setScanComplete] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const [detailLoadingKey, setDetailLoadingKey] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<RedisKeyViewMode>('list');
  const [expandedTreeKeys, setExpandedTreeKeys] = useState<Key[]>([]);
  const [appliedSearchKey, setAppliedSearchKey] = useState('');
  const baseTableRef = useRef<BaseTableRef>(null);
  const scanRequestIdRef = useRef(0);
  const detailRequestIdRef = useRef(0);
  const treeExpansionInitializedRef = useRef(false);
  const knownRedisGroupKeysRef = useRef<Set<string>>(new Set());
  const hasEditTarget = selectedRows.length === 1;
  const redisKeyTreeData = useMemo(() => buildRedisKeyTree(tableData || []), [tableData]);
  const redisGroupKeys = useMemo(() => collectRedisGroupKeys(redisKeyTreeData), [redisKeyTreeData]);
  const selectedRedisKey = hasEditTarget ? tableData?.[selectedRows[0]]?.name : null;
  const selectedTreeKeys = selectedRedisKey ? [redisKeyNodeKey(selectedRedisKey)] : [];

  useEffect(() => {
    getTableData();
  }, []);

  useEffect(() => {
    if (selectedRows.length === 1 && tableData) {
      const selectedItem = tableData[selectedRows[0]];
      if (!selectedItem) {
        setRedisDataItem(null);
        return;
      }
      if (isRedisDataItemLoaded(selectedItem)) {
        setRedisDataItem(selectedItem);
        return;
      }
      getRedisDataItemDetail(selectedItem);
    } else {
      detailRequestIdRef.current += 1;
      setDetailLoadingKey(null);
      setRedisDataItem(null);
    }
  }, [selectedRows, tableData]);

  useEffect(() => {
    if (selectedRows.length === 1) {
      setEditPaneSize((currentSize) =>
        currentSize === EDIT_PANE_COLLAPSED_SIZE ? EDIT_PANE_DEFAULT_SIZE : currentSize,
      );
      return;
    }
    setEditPaneSize(EDIT_PANE_COLLAPSED_SIZE);
  }, [selectedRows]);

  useEffect(() => {
    if (viewMode !== 'tree') {
      treeExpansionInitializedRef.current = false;
      return;
    }

    if (redisGroupKeys.length === 0) {
      treeExpansionInitializedRef.current = false;
      knownRedisGroupKeysRef.current = new Set();
      setExpandedTreeKeys([]);
      return;
    }

    if (!treeExpansionInitializedRef.current || appliedSearchKey.trim()) {
      setExpandedTreeKeys(redisGroupKeys);
      treeExpansionInitializedRef.current = true;
    } else {
      const addedGroupKeys = redisGroupKeys.filter((key) => !knownRedisGroupKeysRef.current.has(key));
      if (addedGroupKeys.length > 0) {
        setExpandedTreeKeys((currentKeys) => Array.from(new Set([...currentKeys, ...addedGroupKeys])));
      }
    }

    knownRedisGroupKeysRef.current = new Set(redisGroupKeys);
  }, [appliedSearchKey, redisGroupKeys, viewMode]);

  const getRedisDataItemDetail = (redisDataItem: RedisDataItem) => {
    if (!redisDataItem.name) {
      setRedisDataItem(redisDataItem);
      return;
    }
    const keyName = redisDataItem.name;
    const requestId = detailRequestIdRef.current + 1;
    detailRequestIdRef.current = requestId;
    setRedisDataItem(null);
    setDetailLoadingKey(keyName);
    redisServer
      .queryRedisKeyDetail({
        dataSourceId: uniqueData.dataSourceId,
        databaseName: uniqueData.databaseName,
        keyName,
      })
      .then((res) => {
        if (detailRequestIdRef.current !== requestId) {
          return;
        }
        if (!res || (res as any).type === 'none') {
          setTableData((current) => current?.filter((item) => item.name !== keyName) || []);
          setSelectedRows([]);
          setRedisDataItem(null);
          return;
        }
        const nextRedisDataItem = { ...redisDataItem, ...res };
        setTableData((current) => {
          return (
            current?.map((item) => {
              if (item.name === keyName) {
                return nextRedisDataItem;
              }
              return item;
            }) || []
          );
        });
        setRedisDataItem(nextRedisDataItem);
      })
      .finally(() => {
        if (detailRequestIdRef.current === requestId) {
          setDetailLoadingKey(null);
        }
      });
  };

  const loadScanBatch = (reset = false) => {
    if (!reset && (scanComplete || loadingMore)) {
      return;
    }
    const requestId = scanRequestIdRef.current + 1;
    scanRequestIdRef.current = requestId;
    const cursor = reset ? INITIAL_SCAN_CURSOR : scanCursor;
    if (reset) {
      setAppliedSearchKey(searchBarValue);
      detailRequestIdRef.current += 1;
      setTableData(null);
      setPresenceDraft(false);
      setRedisDataItem(null);
      setSelectedRows([]);
      setDetailLoadingKey(null);
      setScanCursor(INITIAL_SCAN_CURSOR);
      setScanComplete(false);
      setLoadingMore(false);
    } else {
      setLoadingMore(true);
    }
    redisServer
      .scanRedisKeys({
        dataSourceId: uniqueData.dataSourceId,
        databaseName: uniqueData.databaseName,
        searchKey: searchBarValue,
        cursor,
        count: REDIS_SCAN_COUNT,
      })
      .then((res) => {
        if (scanRequestIdRef.current !== requestId) {
          return;
        }
        const nextKeys = (res?.keys || []).map((item) => {
          return { ...item };
        });
        setScanCursor(res?.nextCursor || INITIAL_SCAN_CURSOR);
        setScanComplete(res?.complete || !res?.hasMore);
        setTableData((current) => {
          if (reset || !current) {
            return nextKeys;
          }
          return mergeRedisKeys(current, nextKeys);
        });
      })
      .catch(() => {
        if (scanRequestIdRef.current !== requestId) {
          return;
        }
        if (reset) {
          setTableData([]);
        }
      })
      .finally(() => {
        if (scanRequestIdRef.current === requestId) {
          setLoadingMore(false);
        }
      });
  };

  const getTableData = () => {
    loadScanBatch(true);
  };

  // Table column configuration.
  const columns = useMemo(() => {
    return [
      {
        title: i18n('redis.keyName'),
        name: 'name',
        lock: true,
      },
      {
        title: i18n('redis.type'),
        name: 'type',
      },
      // {
      //   title: i18n('redis.size'),
      //   name: 'size',
      // },
      {
        title: 'TTL',
        name: 'ttl',
        transitionValue: formatRedisTtl,
      },
    ];
  }, []);

  const handleAdd = () => {
    const newKey = {
      name: null,
      type: RedisFieldType.STRING,
      value: '',
      ttl: -1,
      isDraftFE: true,
    };
    detailRequestIdRef.current += 1;
    const newTableData = [newKey, ...(tableData || [])];
    setTableData(newTableData);
    setSelectedRows([0]);
    setPresenceDraft(true);
    setViewMode('list');
    baseTableRef.current?.scrollToTop();
  };

  const handleTreeSelect: TreeProps<RedisKeyTreeNode>['onSelect'] = (_selectedKeys, info) => {
    if (info.node.kind === 'key' && info.node.rowIndex !== undefined) {
      setSelectedRows([info.node.rowIndex]);
    }
  };

  const handleViewModeChange = (value: string | number) => {
    const nextViewMode = value as RedisKeyViewMode;
    if (nextViewMode === 'tree') {
      treeExpansionInitializedRef.current = false;
    }
    setViewMode(nextViewMode);
  };

  const handleDelete = () => {
    const { name, isDraftFE } = tableData?.[selectedRows?.[0]] || {};
    if (isDraftFE) {
      setTableData(tableData?.filter((item) => !item.isDraftFE) || []);
      setSelectedRows([]);
      setPresenceDraft(false);
      setRedisDataItem(null);
      return;
    }
    if (name) {
      openUnifiedDeletion({
        title: `${i18n('redis.tip.deleteKey', name)}`,
        okCallBack: () => {
          redisServer
            .deleteRedisData({
              dataSourceId: uniqueData.dataSourceId,
              databaseName: uniqueData.databaseName,
              keyName: tableData![selectedRows[0]].name,
            })
            .then(() => {
              setSelectedRows([]);
              setRedisDataItem(null);
              getTableData();
            });
        },
      });
    }
  };

  const editDataSubmitSuccess = (_redisDataItem) => {
    if (!_redisDataItem) {
      return;
    }
    // Replace the selected row in tableData with the current data.
    const newTableData =
      tableData?.map((item, index) => {
        if (index === selectedRows[0]) {
          const newRedisDataItem = {
            ..._redisDataItem,
          };
          setRedisDataItem(newRedisDataItem);
          return newRedisDataItem;
        }
        return item;
      }) || [];
    setTableData(newTableData);
    setPresenceDraft(false);
  };

  return (
    <SplitPaneAny
      split="horizontal"
      onChange={(newSize) => {
        if (!hasEditTarget) {
          return;
        }
        setEditPaneSize(newSize < EDIT_PANE_COLLAPSE_THRESHOLD ? EDIT_PANE_COLLAPSED_SIZE : newSize);
      }}
      className={cx(
        { ResizerSizeIsZeroTop: editPaneSize === EDIT_PANE_COLLAPSED_SIZE },
        { ResizerHidden: !hasEditTarget },
        styles.redisAllData,
        className,
      )}
      size={hasEditTarget ? editPaneSize : EDIT_PANE_COLLAPSED_SIZE}
      minSize={EDIT_PANE_COLLAPSED_SIZE}
      primary="second"
      allowResize={hasEditTarget}
      pane1Style={{ height: '0px' }}
      pane2Style={{ display: hasEditTarget ? 'block' : 'none' }}
    >
      <div className={styles.upperBox}>
        <div className={styles.operationAllDataBar}>
          <div className={styles.left}>
            <ToolbarBtn
              prefixIcon="icon-add"
              text={i18n('redis.button.addKey')}
              disabled={presenceDraft}
              onClick={handleAdd}
            />
            <ToolbarBtn
              prefixIcon="icon-minus"
              text={i18n('redis.button.deleteKey')}
              disabled={selectedRows[0] === undefined}
              onClick={handleDelete}
            />
            <ToolbarBtn prefixIcon="icon-refresh" text={i18n('common.button.refresh')} onClick={getTableData} />
            <ToolbarBtn
              text={
                loadingMore
                  ? i18n('common.text.loading')
                  : scanComplete
                  ? i18n('common.text.noMore')
                  : i18n('redis.button.loadMore')
              }
              disabled={tableData === null || scanComplete || loadingMore || presenceDraft}
              onClick={() => loadScanBatch(false)}
            />
          </div>
          <div className={styles.right}>
            <Segmented
              className={styles.viewMode}
              size="small"
              value={viewMode}
              onChange={handleViewModeChange}
              options={[
                {
                  value: 'list',
                  label: (
                    <Tooltip title={i18n('redis.viewMode.list')}>
                      <span className={styles.viewModeIcon} aria-label={i18n('redis.viewMode.list')}>
                        <List size={14} />
                      </span>
                    </Tooltip>
                  ),
                },
                {
                  value: 'tree',
                  label: (
                    <Tooltip title={i18n('redis.viewMode.tree')}>
                      <span className={styles.viewModeIcon} aria-label={i18n('redis.viewMode.tree')}>
                        <ListTree size={14} />
                      </span>
                    </Tooltip>
                  ),
                },
              ]}
            />
            <SearchBar
              className={styles.searchBar}
              placeholder={i18n('common.text.search')}
              value={searchBarValue}
              onChange={(e) => {
                setSearchBarValue(e.target.value);
              }}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  getTableData();
                }
              }}
            />
          </div>
        </div>
        {viewMode === 'list' ? (
          <BaseTable
            ref={baseTableRef}
            className={styles.tableBox}
            loading={tableData === null}
            tableData={tableData}
            columns={columns}
            sizes={[160, 60, 120]}
            selectedRows={selectedRows}
            onSelectedRowsChange={setSelectedRows}
          />
        ) : (
          <div className={styles.treeBox}>
            <div className={styles.treeHeader} role="row">
              <span className={styles.treeHeaderCell}>{i18n('redis.keyName')}</span>
              <span className={styles.treeHeaderCell}>{i18n('redis.type')}</span>
              <span className={styles.treeHeaderCell}>TTL</span>
            </div>
            <div className={styles.treeScroll}>
              {tableData === null ? (
                <Spin />
              ) : redisKeyTreeData.length === 0 ? (
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={i18n('common.text.noData')} />
              ) : (
                <Tree<RedisKeyTreeNode>
                  blockNode
                  showIcon
                  virtual={false}
                  treeData={redisKeyTreeData}
                  expandedKeys={expandedTreeKeys}
                  selectedKeys={selectedTreeKeys}
                  onExpand={(keys) => setExpandedTreeKeys(keys)}
                  onSelect={handleTreeSelect}
                  icon={(node) =>
                    node.kind === 'group' ? (
                      node.expanded ? (
                        <FolderOpen size={14} />
                      ) : (
                        <Folder size={14} />
                      )
                    ) : (
                      <KeyRound size={14} />
                    )
                  }
                  titleRender={(node) => {
                    const redisDataItem =
                      node.rowIndex !== undefined && tableData ? tableData[node.rowIndex] : undefined;
                    return (
                      <span className={cx(styles.treeRow, { [styles.treeGroupRow]: node.kind === 'group' })}>
                        <span className={styles.treeKeyCell} title={node.redisKey || node.title}>
                          <span className={styles.treeTitleText}>{node.title}</span>
                          {node.kind === 'group' && <span className={styles.treeCount}>({node.count})</span>}
                        </span>
                        <span className={styles.treeMetaCell}>{redisDataItem?.type || ''}</span>
                        <span className={styles.treeMetaCell}>
                          {redisDataItem ? formatRedisTtl(redisDataItem.ttl) : ''}
                        </span>
                      </span>
                    );
                  }}
                />
              )}
            </div>
          </div>
        )}
      </div>
      <div className={styles.editDataSide}>
        {curRedisDataItem ? (
          <EditData
            dataSourceId={uniqueData.dataSourceId}
            databaseName={uniqueData.databaseName}
            redisDataItem={curRedisDataItem}
            submitSuccess={editDataSubmitSuccess}
          />
        ) : (
          <div className={styles.emptyStatus}>
            {detailLoadingKey ? i18n('common.text.loading') : i18n('redis.editData.emptyStatus')}
          </div>
        )}
      </div>
    </SplitPaneAny>
  );
};

export default memo<IProps>(RedisAllData);
