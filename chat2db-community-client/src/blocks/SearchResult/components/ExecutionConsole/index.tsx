import { memo, useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { Button, Tooltip } from 'antd';
import { ArrowDownToLine, Copy, Sparkles, Trash2 } from 'lucide-react';
import { staticMessage } from '@chat2db/ui';
import i18n from '@/i18n';
import { copyToClipboard } from '@/utils/copy';
import { useAIStore } from '@/store/ai';
import { useGlobalStore } from '@/store/global';
import { useWorkspaceStore } from '@/store/workspace';
import { QuestionType } from '@/constants/chat';
import type {
  SqlExecutionLogContext,
  SqlExecutionLogMessageOutput,
  SqlExecutionLogRecord,
  SqlExecutionLogResultOutput,
} from '@/service/sqlExecutionLog';
import { useStyles } from './style';

interface IProps {
  records: SqlExecutionLogRecord[];
  onClear: () => void;
  onOpenResult: (resultKey: string) => void;
  isResultAvailable: (resultKey: string) => boolean;
}

export default memo<IProps>(({ records, onClear, onOpenResult, isResultAvailable }) => {
  const { styles, cx } = useStyles();
  const scrollRef = useRef<HTMLDivElement>(null);
  const [followTail, setFollowTail] = useState(true);
  const setCurrentWorkspaceExtend = useWorkspaceStore((state) => state.setCurrentWorkspaceExtend);

  useEffect(() => {
    if (!followTail) return;
    const frame = window.requestAnimationFrame(() => {
      const container = scrollRef.current;
      if (container) container.scrollTop = container.scrollHeight;
    });
    return () => window.cancelAnimationFrame(frame);
  }, [records, followTail]);

  const plainText = useMemo(() => buildPlainText(records), [records]);

  const handleCopy = async () => {
    await copyToClipboard(plainText);
    staticMessage.success(i18n('common.button.copySuccessfully'));
  };

  const handleScroll = () => {
    const container = scrollRef.current;
    if (!container) return;
    const distanceFromBottom = container.scrollHeight - container.scrollTop - container.clientHeight;
    setFollowTail(distanceFromBottom < 24);
  };

  const handleFollowTail = () => {
    setFollowTail(true);
    const container = scrollRef.current;
    if (container) container.scrollTop = container.scrollHeight;
  };

  const handleAIDiagnose = (record: SqlExecutionLogRecord, errorMessage: string) => {
    const page = useGlobalStore.getState().mainPageActiveTab as 'workspace' | 'dashboard' | 'chat' | 'stream';
    setCurrentWorkspaceExtend(null);
    useAIStore.getState().setCascaderData(page, record.context);
    useAIStore.getState().setShowPanel(true);
    window.setTimeout(() => {
      window.dispatchEvent(
        new CustomEvent('stream:prefillMessage', {
          detail: {
            input: i18n('ai.sqlDebug.prefill', record.sql || '', errorMessage),
            questionType: QuestionType.SQL_DEBUG,
          },
        }),
      );
    }, 100);
  };

  return (
    <div className={styles.console}>
      <div className={styles.toolbar}>
        <div className={styles.toolbarSpacer} />
        <Tooltip title={i18n('common.button.copyConsole')}>
          <Button type="text" className={styles.iconButton} icon={<Copy size={15} />} onClick={handleCopy} />
        </Tooltip>
        <Tooltip title={i18n('common.button.clearConsole')}>
          <Button type="text" className={styles.iconButton} icon={<Trash2 size={15} />} onClick={onClear} />
        </Tooltip>
        <Tooltip title={i18n('common.button.followConsole')}>
          <Button
            type="text"
            className={cx(styles.iconButton, followTail && styles.activeIconButton)}
            icon={<ArrowDownToLine size={15} />}
            onClick={handleFollowTail}
          />
        </Tooltip>
      </div>
      <div className={styles.scrollArea} ref={scrollRef} onScroll={handleScroll}>
        {records.map((record, recordIndex) => {
          const showContext =
            recordIndex === 0 || contextKey(records[recordIndex - 1].context) !== contextKey(record.context);
          return (
            <div className={styles.record} key={record.id}>
              {showContext && (
                <ConsoleLine
                  className={styles.contextLine}
                  timestamp={record.startedAtEpochMs}
                  content={formatContext(record.context)}
                />
              )}
              <div className={styles.line}>
                <TimeCell value={record.startedAtEpochMs} />
                <div className={styles.sqlContent}>
                  <span className={styles.prompt}>{record.context.schemaName || record.context.databaseName || 'SQL'}&gt;</span>
                  <pre className={styles.sql}>{record.sql}</pre>
                </div>
              </div>
              {record.outputs.map((output) =>
                output.kind === 'message' ? (
                  <MessageLine
                    key={output.id}
                    output={output}
                    record={record}
                    onAIDiagnose={handleAIDiagnose}
                  />
                ) : (
                  <ResultLine
                    key={output.id}
                    output={output}
                    record={record}
                    isResultAvailable={isResultAvailable}
                    onOpenResult={onOpenResult}
                    onAIDiagnose={handleAIDiagnose}
                  />
                ),
              )}
              {record.status === 'running' && (
                <ConsoleLine
                  className={styles.runningLine}
                  timestamp={record.startedAtEpochMs}
                  content={
                    <span className={styles.runningContent}>
                      <span className={styles.runningDot} />
                      {i18n('common.text.currentExecution')}
                    </span>
                  }
                />
              )}
              {record.status === 'cancelled' && (
                <ConsoleLine
                  className={styles.cancelledLine}
                  timestamp={record.finishedAtEpochMs || record.startedAtEpochMs}
                  content={i18n('common.text.executionCancelled')}
                />
              )}
              {record.status === 'success' && record.outputs.length === 0 && (
                <ConsoleLine
                  className={styles.successLine}
                  timestamp={record.finishedAtEpochMs || record.startedAtEpochMs}
                  content={`${i18n('common.text.executionCompleted')} · ${formatMilliseconds(record.durationMs)}`}
                />
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
});

function MessageLine({
  output,
  record,
  onAIDiagnose,
}: {
  output: SqlExecutionLogMessageOutput;
  record: SqlExecutionLogRecord;
  onAIDiagnose: (record: SqlExecutionLogRecord, message: string) => void;
}) {
  const { styles, cx } = useStyles();
  return (
    <div className={styles.line}>
      <TimeCell value={output.occurredAtEpochMs} />
      <div className={cx(styles.message, styles[`message${output.level}`])}>
        <span className={styles.level}>{output.level}</span>
        <span className={styles.messageText}>{output.message}</span>
        {output.level === 'ERROR' && (
          <Button
            type="link"
            size="small"
            className={styles.inlineAction}
            icon={<Sparkles size={13} />}
            onClick={() => onAIDiagnose(record, output.message)}
          >
            {i18n('common.text.aiDiagnose')}
          </Button>
        )}
      </div>
    </div>
  );
}

function ResultLine({
  output,
  record,
  isResultAvailable,
  onOpenResult,
  onAIDiagnose,
}: {
  output: SqlExecutionLogResultOutput;
  record: SqlExecutionLogRecord;
  isResultAvailable: (resultKey: string) => boolean;
  onOpenResult: (resultKey: string) => void;
  onAIDiagnose: (record: SqlExecutionLogRecord, message: string) => void;
}) {
  const { styles, cx } = useStyles();
  const available = !!output.resultKey && isResultAvailable(output.resultKey);
  const summary = resultSummary(output);
  return (
    <div className={styles.line}>
      <TimeCell value={output.occurredAtEpochMs} />
      <div className={cx(styles.resultLine, !output.success && styles.resultError)}>
        {available ? (
          <button className={styles.resultLink} onClick={() => onOpenResult(output.resultKey!)}>
            {summary}
          </button>
        ) : (
          <span>{summary}</span>
        )}
        {!!output.resultKey && !available && <span className={styles.released}> · {i18n('common.text.resultReleased')}</span>}
        {output.success && <span className={styles.metrics}>{formatMetrics(output)}</span>}
        {!output.success && output.message && (
          <Button
            type="link"
            size="small"
            className={styles.inlineAction}
            icon={<Sparkles size={13} />}
            onClick={() => onAIDiagnose(record, output.message!)}
          >
            {i18n('common.text.aiDiagnose')}
          </Button>
        )}
      </div>
    </div>
  );
}

function ConsoleLine({
  timestamp,
  content,
  className,
}: {
  timestamp: number;
  content: ReactNode;
  className?: string;
}) {
  const { styles, cx } = useStyles();
  return (
    <div className={cx(styles.line, className)}>
      <TimeCell value={timestamp} />
      <div>{content}</div>
    </div>
  );
}

function TimeCell({ value }: { value: number }) {
  const { styles } = useStyles();
  return <time className={styles.timestamp}>[{formatTimestamp(value)}]</time>;
}

function resultSummary(output: SqlExecutionLogResultOutput) {
  if (!output.success) return output.message || i18n('common.text.failure');
  if (typeof output.updateCount === 'number') return i18n('common.text.affectedRows', output.updateCount);
  if (typeof output.rowCount === 'number') return i18n('common.text.rowsReturned', output.rowCount);
  return i18n('common.text.executionCompleted');
}

function formatMetrics(output: SqlExecutionLogResultOutput) {
  const metrics = output.executionMetrics;
  const details: string[] = [];
  if (typeof metrics?.executeDurationMs === 'number') {
    details.push(i18n('common.text.executeDuration', metrics.executeDurationMs));
  }
  if (typeof metrics?.fetchDurationMs === 'number') {
    details.push(i18n('common.text.fetchDuration', metrics.fetchDurationMs));
  }
  const total = formatMilliseconds(output.durationMs);
  return details.length ? ` · ${total} (${details.join(' · ')})` : ` · ${total}`;
}

function formatMilliseconds(value?: number) {
  return `${Math.max(0, value || 0)} ms`;
}

function formatTimestamp(value: number) {
  const date = new Date(value);
  const pad = (part: number) => String(part).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(
    date.getMinutes(),
  )}:${pad(date.getSeconds())}`;
}

function formatContext(context: SqlExecutionLogContext) {
  const source = context.dataSourceName || (context.dataSourceId ? `#${context.dataSourceId}` : 'SQL');
  return [source, context.databaseName, context.schemaName].filter(Boolean).join(' / ');
}

function contextKey(context: SqlExecutionLogContext) {
  return [context.dataSourceId, context.dataSourceName, context.databaseName, context.schemaName].join('|');
}

function buildPlainText(records: SqlExecutionLogRecord[]) {
  return records
    .flatMap((record, index) => {
      const lines: string[] = [];
      if (index === 0 || contextKey(records[index - 1].context) !== contextKey(record.context)) {
        lines.push(`[${formatTimestamp(record.startedAtEpochMs)}] ${formatContext(record.context)}`);
      }
      lines.push(`[${formatTimestamp(record.startedAtEpochMs)}] ${record.context.schemaName || record.context.databaseName || 'SQL'}> ${record.sql}`);
      record.outputs.forEach((output) => {
        const text = output.kind === 'message' ? `${output.level} ${output.message}` : `${resultSummary(output)}${formatMetrics(output)}`;
        lines.push(`[${formatTimestamp(output.occurredAtEpochMs)}] ${text}`);
      });
      if (record.status === 'cancelled') {
        lines.push(`[${formatTimestamp(record.finishedAtEpochMs || record.startedAtEpochMs)}] ${i18n('common.text.executionCancelled')}`);
      }
      return lines;
    })
    .join('\n');
}
