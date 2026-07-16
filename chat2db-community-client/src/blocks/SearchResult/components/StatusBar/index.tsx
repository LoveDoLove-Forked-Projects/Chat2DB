import { memo, useMemo } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { useStyles } from './style';
import { IManageResultData } from '@/typings';
import { Dropdown } from 'antd';
import { ChevronDown } from 'lucide-react';
import { useGlobalStore } from '@/store/global';
import { DATA_TABLE_SETTINGS } from '@/constants/settings';
import type { SelectionMetricId } from '@/typings/settings';
import { formatSelectionMetric, summarizeSelection } from './selectionAggregation';

const METRIC_OPTIONS: Array<{ id: SelectionMetricId; label: Parameters<typeof i18n>[0] }> = [
  { id: 'none', label: 'common.selectionAggregate.none' },
  { id: 'count', label: 'common.selectionAggregate.count' },
  { id: 'sum', label: 'common.selectionAggregate.sum' },
  { id: 'average', label: 'common.selectionAggregate.average' },
  { id: 'minimum', label: 'common.selectionAggregate.minimum' },
  { id: 'maximum', label: 'common.selectionAggregate.maximum' },
  { id: 'nullCount', label: 'common.selectionAggregate.nullCount' },
  { id: 'nonNullCount', label: 'common.selectionAggregate.nonNullCount' },
  { id: 'uniqueCount', label: 'common.selectionAggregate.uniqueCount' },
  { id: 'nullPercentage', label: 'common.selectionAggregate.nullPercentage' },
  { id: 'nonNullPercentage', label: 'common.selectionAggregate.nonNullPercentage' },
  { id: 'uniquePercentage', label: 'common.selectionAggregate.uniquePercentage' },
  { id: 'earliest', label: 'common.selectionAggregate.earliest' },
  { id: 'latest', label: 'common.selectionAggregate.latest' },
];

interface IProps {
  className?: string;
  resultData: IManageResultData;
  selectedValues?: unknown[];
}

export default memo<IProps>((props) => {
  const { className, resultData, selectedValues = [] } = props;
  const { styles } = useStyles();
  const { dataTableSettings, updateDataTableSettings } = useGlobalStore((state) => ({
    dataTableSettings: state.dataTableSettings,
    updateDataTableSettings: state.updateDataTableSettings,
  }));
  const selectionMetrics = dataTableSettings.selectionMetrics || DATA_TABLE_SETTINGS.selectionMetrics!;
  const selectionSummary = useMemo(() => summarizeSelection(selectedValues), [selectedValues]);
  const menuItems = METRIC_OPTIONS.map((option) => ({ key: option.id, label: i18n(option.label) }));
  if (!resultData) return null;

  const { description, duration } = resultData;
  const dataLength = resultData.dataList?.length;

  const updateMetric = (index: number, metric: SelectionMetricId) => {
    const nextMetrics = [...selectionMetrics] as [SelectionMetricId, SelectionMetricId, SelectionMetricId];
    nextMetrics[index] = metric;
    updateDataTableSettings({
      ...dataTableSettings,
      selectionMetrics: nextMetrics,
    });
  };

  return (
    <div className={classnames(styles.statusBar, className)}>
      <div className={styles.resultSummary}>
        <span>{`【${i18n('common.text.result')}】${description}.`}</span>
        <span>{`【${i18n('common.text.timeConsuming')}】${duration}ms.`}</span>
        {!!dataLength && <span>{`【${i18n('common.text.searchRow')}】${dataLength} ${i18n('common.text.row')}.`}</span>}
      </div>
      <div className={styles.selectionSummary}>
        {selectionMetrics.map((metric, index) => {
          const option = METRIC_OPTIONS.find((item) => item.id === metric) || METRIC_OPTIONS[0];
          const value = formatSelectionMetric(metric, selectionSummary);
          return (
            <Dropdown
              key={index}
              trigger={['click']}
              menu={{
                items: menuItems,
                selectedKeys: [metric],
                selectable: true,
                onClick: ({ key }) => updateMetric(index, key as SelectionMetricId),
              }}
            >
              <button type="button" className={styles.metricButton}>
                <span className={styles.metricLabel}>{i18n(option.label)}</span>
                {value && <span className={styles.metricValue}>{value}</span>}
                <ChevronDown size={12} strokeWidth={1.75} />
              </button>
            </Dropdown>
          );
        })}
      </div>
    </div>
  );
});
