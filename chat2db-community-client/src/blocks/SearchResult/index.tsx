import React, {
  memo,
  useCallback,
  useEffect,
  useMemo,
  useState,
  forwardRef,
  ForwardedRef,
  useImperativeHandle,
  useRef,
} from 'react';
import classnames from 'classnames';
import CustomTabs, { ITabItem } from '@/components/Tabs';
import Iconfont from '@/components/Iconfont';
import { IManageResultData } from '@/typings';
import SearchResultItem from './components/SearchResultItem';
import Abstract from '@/components/Abstract';
import i18n from '@/i18n';
import { useStyles } from './style';
import { Empty, EmptyImage, IconfontSvg } from '@chat2db/ui';
import SQLPreview from '@/components/SQLPreview';
import ExecutionMessages, { IExecutionMessageItem } from './components/ExecutionMessages';

interface IProps {
  className?: string;
  resultDataList: IManageResultData[];
  historyResultDataList?: IManageResultData[];
  resultBatchKey?: number;
  viewTable?: boolean;
  onResultDataListChange?: (params: {
    resultDataList: IManageResultData[];
    historyResultDataList: IManageResultData[];
  }) => void;
}

export interface ISearchResultRef {
  handleDemo: () => void;
}

function getResultIdentity(item: IManageResultData) {
  return item.uuid || item.extra?.resultKey || item.extra?.historyKey;
}

function getResultVersion(item: IManageResultData) {
  return [
    getResultIdentity(item),
    item.extra?.executionSequence,
    item.extra?.statementSequence,
    item.extra?.resultSequence,
    item.extra?.resultKey,
    item.resultSetId,
    item.duration,
    item.dataList?.length,
    item.extra?.messages?.length,
    item.success,
  ].join('|');
}

function hasResultTab(item: IManageResultData) {
  return (item.headerList?.length || 0) > 1 || !item.success;
}

function shouldOpenMessagesTab(item: IManageResultData) {
  return item.extra?.messageOnly || (!!item.extra?.messages?.length && !hasResultTab(item));
}

const SearchResult = forwardRef((props: IProps, ref: ForwardedRef<ISearchResultRef>) => {
  const { className, viewTable = false } = props;
  const { styles } = useStyles();
  const [resultDataList, setResultDataList] = useState<IManageResultData[] | null>(props.resultDataList);
  const [historyResultDataList, setHistoryResultDataList] = useState<IManageResultData[]>(
    props.historyResultDataList || [],
  );
  const [showHistory, setShowHistory] = useState(false);
  const [activeTabId, setActiveTabId] = useState<string>('');
  const knownResultVersionMapRef = useRef<Map<string, string>>(new Map());

  useImperativeHandle(ref, () => ({
    handleDemo: () => {},
  }));

  useEffect(() => {
    setShowHistory(false);
  }, [props.resultBatchKey]);

  useEffect(() => {
    const nextResultDataList = props.resultDataList || [];
    const previousResultVersions = knownResultVersionMapRef.current;
    const changedResults = nextResultDataList.filter((item) => {
      const resultKey = getResultIdentity(item);
      if (!resultKey) {
        return false;
      }
      return previousResultVersions.get(resultKey) !== getResultVersion(item);
    });
    const latestChangedResult = changedResults[changedResults.length - 1];
    const hasChangedResult = !!latestChangedResult;

    knownResultVersionMapRef.current = new Map(
      nextResultDataList
        .map((item) => [getResultIdentity(item), getResultVersion(item)] as const)
        .filter((entry): entry is readonly [string, string] => !!entry[0]),
    );
    setResultDataList(nextResultDataList);

    if (latestChangedResult && hasChangedResult && shouldOpenMessagesTab(latestChangedResult)) {
      setActiveTabId('messages');
    } else if (latestChangedResult && hasChangedResult && hasResultTab(latestChangedResult)) {
      setActiveTabId(latestChangedResult.uuid || '');
    } else if (latestChangedResult && hasChangedResult) {
      setActiveTabId('abstract');
    } else if (!nextResultDataList.length) {
      setActiveTabId('');
    } else if (
      activeTabId &&
      activeTabId !== 'abstract' &&
      activeTabId !== 'messages' &&
      !nextResultDataList.some((item) => item.uuid === activeTabId)
    ) {
      setActiveTabId('');
    }
  }, [props.resultDataList, activeTabId]);

  useEffect(() => {
    const nextHistoryResultDataList = props.historyResultDataList || [];
    setHistoryResultDataList(nextHistoryResultDataList);
    if (!nextHistoryResultDataList.length && showHistory) {
      setShowHistory(false);
    }
  }, [props.historyResultDataList, showHistory]);

  const onChange = useCallback((uuid) => {
    setActiveTabId(uuid);
  }, []);

  const tabsList = useMemo(() => {
    const visibleResultDataList = showHistory
      ? [...(resultDataList || []), ...(historyResultDataList || [])]
      : resultDataList || [];
    if (!visibleResultDataList?.length) return [];
    // also needs to have an independent tab, so that error information can be viewed in the tab and AI repair can be triggered.
    const newResultDataList = visibleResultDataList?.filter((d) => d.headerList?.length > 1 || !d.success);

    const tabsListRes =
      newResultDataList?.map((queryResultData, index) => {
        return {
          prefixIcon: (
            <Iconfont
              key={index}
              className={classnames(styles[queryResultData.success ? 'successIcon' : 'failIcon'], styles.statusIcon)}
              code={queryResultData.success ? '\ue605' : '\ue87c'}
            />
          ),
          popover: (
            <SQLPreview
              source="search-result-tab-popover"
              sql={`${
                queryResultData.comment ? `-- ${queryResultData.comment}\n` : ''
              }${queryResultData.originalSql?.replaceAll('\r\n', '\n')}`}
            />
          ),
          label:
            queryResultData.displayName || queryResultData.comment || i18n('common.text.executionResult', index + 1),
          key: queryResultData.uuid!,
          children: <SearchResultItem viewTable={viewTable || queryResultData.canEdit} resultData={queryResultData} />,
        };
      }) || [];

    if (!activeTabId && tabsListRes!.length) {
      setActiveTabId(tabsListRes![0].key);
    }

    return tabsListRes;
  }, [resultDataList, historyResultDataList, showHistory]);

  const executionMessages = useMemo<IExecutionMessageItem[]>(() => {
    if (!resultDataList?.length) {
      return [];
    }
    return resultDataList.flatMap((item, index) =>
      (item.extra?.messages || []).map((message) => ({
        ...message,
        comment: item.comment,
        resultSetId: item.resultSetId,
        executionIndex: index + 1,
      })),
    );
  }, [resultDataList]);

  const historyExecutionMessages = useMemo<IExecutionMessageItem[]>(() => {
    if (!historyResultDataList.length) {
      return [];
    }
    return historyResultDataList.flatMap((item, index) =>
      (item.extra?.messages || []).map((message) => ({
        ...message,
        comment: item.comment,
        resultSetId: item.resultSetId,
        executionIndex: index + 1,
      })),
    );
  }, [historyResultDataList]);

  const abstract = useMemo(() => {
    if (!resultDataList?.length) {
      return undefined;
    }
    return {
      prefixIcon: <IconfontSvg className={styles.abstractIcon} size="sm" code="icon-terminal" />,
      popover: i18n('common.text.overview'),
      label: i18n('common.text.overview'),
      key: 'abstract',
      children: <Abstract data={resultDataList!} />,
      canClosed: false,
    };
  }, [resultDataList]);

  const messageTab = useMemo(() => {
    if (!executionMessages.length) {
      return undefined;
    }
    return {
      prefixIcon: <IconfontSvg className={styles.abstractIcon} size="sm" code="icon-terminal" />,
      popover: i18n('common.title.message'),
      label: `${i18n('common.title.message')} (${executionMessages.length})`,
      key: 'messages',
      children: <ExecutionMessages data={executionMessages} />,
      canClosed: false,
    };
  }, [executionMessages, styles.abstractIcon]);

  const historyMessageTab = useMemo(() => {
    if (!showHistory || !historyExecutionMessages.length) {
      return undefined;
    }
    return {
      prefixIcon: <IconfontSvg className={styles.abstractIcon} size="sm" code="icon-terminal" />,
      popover: i18n('common.text.historyMessages'),
      label: `${i18n('common.text.historyMessages')} (${historyExecutionMessages.length})`,
      key: 'history-messages',
      children: <ExecutionMessages data={historyExecutionMessages} />,
      canClosed: false,
    };
  }, [historyExecutionMessages, showHistory, styles.abstractIcon]);

  const onEdit = useCallback(
    (type: 'add' | 'remove', data: ITabItem[], list?: ITabItem[]) => {
      if (type === 'remove') {
        const isCloseAll = list === undefined;
        if (isCloseAll) {
          const nextResultDataList: IManageResultData[] = [];
          const nextHistoryResultDataList: IManageResultData[] = [];
          setResultDataList(nextResultDataList);
          setHistoryResultDataList(nextHistoryResultDataList);
          props.onResultDataListChange?.({
            resultDataList: nextResultDataList,
            historyResultDataList: nextHistoryResultDataList,
          });
          return;
        }
        const closedKeys = new Set((data || []).map((item) => item.key));
        const newResultDataList = resultDataList?.filter((d) => {
          return data.findIndex((item) => item.key === d.uuid) === -1;
        });
        const newHistoryResultDataList = historyResultDataList.filter((d) => !closedKeys.has(d.uuid || ''));

        const nextResultDataList = newResultDataList || [];
        const nextHistoryResultDataList = newHistoryResultDataList || [];
        setResultDataList(nextResultDataList);
        setHistoryResultDataList(nextHistoryResultDataList);
        props.onResultDataListChange?.({
          resultDataList: nextResultDataList,
          historyResultDataList: nextHistoryResultDataList,
        });
      }
    },
    [resultDataList, historyResultDataList, props.onResultDataListChange],
  );

  const tabsItems = useMemo(() => {
    const staticTabs = [abstract, messageTab, historyMessageTab].filter(Boolean) as ITabItem[];
    if (!tabsList.length && staticTabs.length) {
      // This check keeps the tab visible when only a static tab exists.
      if (!activeTabId || !staticTabs.some((item) => item.key === activeTabId)) {
        setActiveTabId(String(staticTabs[0].key));
      }
      return staticTabs;
    }
    return [...staticTabs, ...tabsList];
  }, [tabsList, abstract, messageTab, historyMessageTab, activeTabId]);

  useEffect(() => {
    if (!tabsItems.length) {
      if (activeTabId) {
        setActiveTabId('');
      }
      return;
    }
    if (!activeTabId || !tabsItems.some((item) => item.key === activeTabId)) {
      setActiveTabId(String(tabsItems[0].key));
    }
  }, [tabsItems, activeTabId]);

  return (
    <div className={classnames(className, styles.searchResult)}>
      {!!historyResultDataList.length && !viewTable && (
        <div className={styles.historyBar}>
          <button className={styles.historyButton} onClick={() => setShowHistory((value) => !value)}>
            {showHistory
              ? i18n('common.button.hideHistoryResult')
              : `${i18n('common.button.viewHistoryResult')} (${historyResultDataList.length})`}
          </button>
        </div>
      )}
      {tabsItems?.length ? (
        <CustomTabs
          hideAdd
          activeKey={activeTabId}
          className={styles.tabs}
          onChange={onChange as any}
          onEdit={onEdit as any}
          items={tabsItems}
          concealTabHeader={viewTable}
          height={30}
          tabMaxWidth="200px"
        />
      ) : (
        <div className={styles.noData}>
          <Empty image={EmptyImage.Common} title={i18n('common.text.noData')} />
        </div>
      )}
    </div>
  );
});

export default memo(SearchResult);
