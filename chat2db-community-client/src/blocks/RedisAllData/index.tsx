import React, { memo, useState, useRef, useMemo, useEffect } from 'react';
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

const REDIS_SCAN_COUNT = 1000;
const INITIAL_SCAN_CURSOR = '0';
const EDIT_PANE_COLLAPSED_SIZE = 0;
const EDIT_PANE_DEFAULT_SIZE = 320;
const EDIT_PANE_COLLAPSE_THRESHOLD = 50;
const SplitPaneAny = SplitPane as any;

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
  const baseTableRef = useRef<BaseTableRef>(null);
  const scanRequestIdRef = useRef(0);
  const detailRequestIdRef = useRef(0);
  const hasEditTarget = selectedRows.length === 1;

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
        transitionValue: (value) => {
          if (value === undefined || value === null) {
            return '-';
          }
          if (value === -1) {
            return i18n('redis.noExpirationTime');
          }
          if (value < 0) {
            return '-';
          }
          return formatTime(value);
        },
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
    baseTableRef.current?.scrollToTop();
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
