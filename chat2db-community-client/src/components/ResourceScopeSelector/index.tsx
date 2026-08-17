import { memo, useEffect, useMemo } from 'react';
import { Select } from 'antd';
import { IconfontSvg } from '@chat2db/ui';
import { databaseMap, type DatabaseTypeCode } from '@/constants';
import i18n from '@/i18n';
import { useTreeStore } from '@/store/tree';
import { useStyles } from './style';

export interface ResourceScopeValue {
  dataSourceId?: number;
  dataSourceAlias?: string;
  dataSourceType?: DatabaseTypeCode;
}

interface ResourceScopeSelectorProps {
  value?: ResourceScopeValue[];
  onChange?: (value: ResourceScopeValue[]) => void;
  readOnly?: boolean;
}

const ResourceScopeSelector = memo((props: ResourceScopeSelectorProps) => {
  const { value = [], onChange, readOnly = false } = props;
  const { styles } = useStyles();
  const { dataSourceList, getDataSourceList } = useTreeStore((state) => ({
    dataSourceList: state.dataSourceList,
    getDataSourceList: state.getDataSourceList,
  }));

  const options = useMemo(
    () =>
      (dataSourceList || []).map((dataSource) => {
        const dataSourceType = dataSource.extraParams?.databaseType;
        return {
          label: (
            <span className={styles.dataSourceOption}>
              <IconfontSvg code={databaseMap[dataSourceType]?.icon} />
              <span>{dataSource.originalTitle}</span>
            </span>
          ),
          title: dataSource.originalTitle,
          value: Number(dataSource.id),
          dataSourceAlias: dataSource.originalTitle,
          dataSourceType,
        };
      }),
    [dataSourceList, styles.dataSourceOption],
  );

  useEffect(() => {
    if (!readOnly) {
      getDataSourceList();
    }
  }, [getDataSourceList, readOnly]);

  if (readOnly) {
    if (!value.length) {
      return <span className={styles.globalScope}>{i18n('knowledgeManagement.label.globalScope')}</span>;
    }
    return (
      <div className={styles.scopeSummaryList}>
        {value.map((scope, index) => (
          <div
            className={styles.scopeSummary}
            key={`${scope.dataSourceId}-${index}`}
          >
            <IconfontSvg code={databaseMap[scope.dataSourceType!]?.icon} />
            <span>{scope.dataSourceAlias}</span>
          </div>
        ))}
      </div>
    );
  }

  const handleChange = (dataSourceIds: number[]) => {
    const currentScopeMap = new Map(value.map((scope) => [scope.dataSourceId, scope]));
    const optionMap = new Map(options.map((option) => [option.value, option]));
    onChange?.(
      dataSourceIds.map((dataSourceId) => {
        const currentScope = currentScopeMap.get(dataSourceId);
        const option = optionMap.get(dataSourceId);
        return {
          dataSourceId,
          dataSourceAlias: option?.dataSourceAlias || currentScope?.dataSourceAlias,
          dataSourceType: option?.dataSourceType || currentScope?.dataSourceType,
        };
      }),
    );
  };

  return (
    <Select
      mode="multiple"
      allowClear
      showSearch
      optionFilterProp="title"
      className={styles.selector}
      placeholder={i18n('knowledgeManagement.label.globalScope')}
      value={value.map((scope) => scope.dataSourceId).filter((id): id is number => id !== undefined)}
      options={options}
      onChange={handleChange}
    />
  );
});

export default ResourceScopeSelector;
