import { useCallback, useEffect, useState } from 'react';
import { Button, Space, Table, Tag, Tooltip, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import i18n from '@/i18n';
import sqlService, { IActiveTransactionItem } from '@/service/sql';
import {
  canOpenTransactionSession,
  getActiveTransactionRowKey,
  getLiveTransactionAge,
  getTransactionSessionThreadId,
} from './activeTransactionUtils';

/**
 * Active InnoDB transaction list (MYSQL-OPS-002). Read-only; full visibility of other
 * users' transactions and their SQL requires the PROCESS privilege, which is surfaced as
 * an explicit unavailable state instead of a misleading blank list.
 */
interface ActiveTransactionsContentProps {
  dataSourceId: number;
  databaseName?: string;
  schemaName?: string;
  onOpenSession?: (threadId: number) => void;
}

const ActiveTransactionsContent = ({
  dataSourceId,
  databaseName,
  schemaName,
  onOpenSession,
}: ActiveTransactionsContentProps) => {
  const [data, setData] = useState<IActiveTransactionItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [snapshotAtMs, setSnapshotAtMs] = useState(() => Date.now());
  const [nowMs, setNowMs] = useState(() => Date.now());

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    sqlService
      .getActiveTransactionList({ dataSourceId, databaseName, schemaName })
      .then((list) => {
        const loadedAt = Date.now();
        setData(list || []);
        setSnapshotAtMs(loadedAt);
        setNowMs(loadedAt);
      })
      .catch((e) => {
        setData([]);
        setError(e?.message || i18n('common.text.failure'));
      })
      .finally(() => setLoading(false));
  }, [dataSourceId, databaseName, schemaName]);

  useEffect(() => {
    load();
  }, [load]);

  useEffect(() => {
    const timer = window.setInterval(() => setNowMs(Date.now()), 1000);
    return () => window.clearInterval(timer);
  }, []);

  const openSession = useCallback(
    (record: IActiveTransactionItem, target: 'owner' | 'blocker') => {
      const threadId = getTransactionSessionThreadId(record, target);
      if (threadId == null || !onOpenSession) {
        return;
      }
      onOpenSession(threadId);
    },
    [onOpenSession],
  );

  const renderUnavailable = (label: string) => <Tag color="default">{label}</Tag>;

  const columns: ColumnsType<IActiveTransactionItem> = [
    { title: i18n('workspace.ops.transactionId'), dataIndex: 'trxId', width: 120 },
    {
      title: i18n('workspace.ops.transactionState'),
      dataIndex: 'state',
      width: 120,
      filters: [
        { text: 'RUNNING', value: 'RUNNING' },
        { text: 'LOCK WAIT', value: 'LOCK WAIT' },
      ],
      onFilter: (value, record) => record.state === value,
      sorter: (a, b) => String(a.state || '').localeCompare(String(b.state || '')),
    },
    {
      title: i18n('workspace.ops.transactionStarted'),
      dataIndex: 'startedAt',
      width: 170,
      sorter: (a, b) => String(a.startedAt || '').localeCompare(String(b.startedAt || '')),
    },
    {
      title: i18n('workspace.ops.transactionAge'),
      dataIndex: 'ageSeconds',
      width: 90,
      render: (v: number | null) => {
        const liveAge = getLiveTransactionAge(v, snapshotAtMs, nowMs);
        return liveAge == null ? '-' : i18n('workspace.ops.secondsFormat', liveAge);
      },
      sorter: (a, b) => (a.ageSeconds || 0) - (b.ageSeconds || 0),
    },
    { title: i18n('workspace.ops.transactionIsolation'), dataIndex: 'isolationLevel', width: 130 },
    {
      title: i18n('workspace.ops.rowsLocked'),
      dataIndex: 'rowsLocked',
      width: 100,
      sorter: (a, b) => (a.rowsLocked || 0) - (b.rowsLocked || 0),
    },
    {
      title: i18n('workspace.ops.rowsModified'),
      dataIndex: 'rowsModified',
      width: 110,
      sorter: (a, b) => (a.rowsModified || 0) - (b.rowsModified || 0),
    },
    {
      title: i18n('workspace.ops.threadId'),
      dataIndex: 'threadId',
      width: 120,
      render: (_value, record) =>
        record.sessionAvailable === false ? renderUnavailable(i18n('workspace.ops.sessionUnavailable')) : record.threadId,
      sorter: (a, b) => (a.threadId || 0) - (b.threadId || 0),
    },
    { title: i18n('workspace.ops.user'), dataIndex: 'user', width: 110 },
    { title: i18n('workspace.ops.host'), dataIndex: 'host', width: 140 },
    { title: i18n('workspace.ops.database'), dataIndex: 'db', width: 120 },
    {
      title: i18n('workspace.ops.waitedLock'),
      dataIndex: 'waitingLockId',
      width: 220,
      render: (_value, record) => {
        if (record.lockMetadataState === 'UNAVAILABLE') {
          return renderUnavailable(i18n('workspace.ops.lockMetadataUnavailable'));
        }
        if (!record.lockWaitAvailable) {
          return '-';
        }
        return (
          <Space direction="vertical" size={0}>
            <Typography.Text copyable>{record.waitingLockId}</Typography.Text>
            <Typography.Text type="secondary">
              {[record.waitingObject, record.waitingIndex, record.waitingLockMode].filter(Boolean).join(' / ')}
            </Typography.Text>
          </Space>
        );
      },
    },
    {
      title: i18n('workspace.ops.blocker'),
      dataIndex: 'blockingTrxId',
      width: 240,
      render: (_value, record) => {
        if (!record.lockWaitAvailable) {
          return '-';
        }
        return (
          <Space direction="vertical" size={0}>
            <Typography.Text copyable>{record.blockingTrxId}</Typography.Text>
            <Typography.Text type="secondary">
              {[record.blockingUser, record.blockingHost, record.blockingDb].filter(Boolean).join(' / ') ||
                i18n('workspace.ops.sessionUnavailable')}
            </Typography.Text>
          </Space>
        );
      },
    },
    {
      title: i18n('workspace.ops.query'),
      dataIndex: 'query',
      width: 260,
      ellipsis: true,
      render: (value: string | null, record) =>
        value ? (
          <Typography.Text ellipsis={{ tooltip: value }}>{value}</Typography.Text>
        ) : (
          renderUnavailable(
            record.queryState === 'UNAVAILABLE'
              ? i18n('workspace.ops.queryUnavailable')
              : i18n('workspace.ops.emptyValue'),
          )
        ),
    },
    {
      title: i18n('workspace.ops.sessionAction'),
      key: 'sessionAction',
      fixed: 'right',
      width: 180,
      render: (_value, record) => (
        <Space>
          <Tooltip
            title={
              canOpenTransactionSession(record, 'owner')
                ? i18n('workspace.ops.openSession')
                : i18n('workspace.ops.sessionUnavailable')
            }
          >
            <Button
              size="small"
              disabled={!canOpenTransactionSession(record, 'owner')}
              onClick={() => openSession(record, 'owner')}
            >
              {i18n('workspace.ops.ownerSession')}
            </Button>
          </Tooltip>
          <Tooltip
            title={
              canOpenTransactionSession(record, 'blocker')
                ? i18n('workspace.ops.openBlockingSession')
                : i18n('workspace.ops.sessionUnavailable')
            }
          >
            <Button
              size="small"
              disabled={!canOpenTransactionSession(record, 'blocker')}
              onClick={() => openSession(record, 'blocker')}
            >
              {i18n('workspace.ops.blockerSession')}
            </Button>
          </Tooltip>
        </Space>
      ),
    },
  ];
  const lockMetadataMessage = data.find((item) => item.lockMetadataState === 'UNAVAILABLE')?.lockMetadataMessage;

  return (
    <div>
      <div style={{ marginBottom: 8, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span>
          {error
            ? `${i18n('workspace.ops.permissionRequired')} ${i18n('workspace.ops.processPrivilegeHint')}`
            : i18n('workspace.ops.activeTransactionCount', data.length)}
        </span>
        <Button size="small" onClick={load} loading={loading}>
          {i18n('common.button.refresh')}
        </Button>
      </div>
      {!error && lockMetadataMessage && (
        <div style={{ marginBottom: 8, color: 'var(--text-color-secondary)' }}>
          {i18n('workspace.ops.lockMetadataDegraded')} {lockMetadataMessage}
        </div>
      )}
      {error ? (
        <div style={{ color: 'var(--text-color-danger)' }}>{error}</div>
      ) : (
        <Table
          size="small"
          rowKey={getActiveTransactionRowKey}
          columns={columns}
          dataSource={data}
          loading={loading}
          pagination={false}
          scroll={{ x: 1900 }}
        />
      )}
    </div>
  );
};

export default ActiveTransactionsContent;
