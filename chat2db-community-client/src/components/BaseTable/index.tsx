import React, {
  memo,
  useState,
  useMemo,
  forwardRef,
  ForwardedRef,
  useImperativeHandle,
  useEffect,
  useRef,
} from 'react';
import { useStyles } from './style';
import { useTableStyles } from '@/styles/table';
import i18n from '@/i18n';
import { BaseTable as ALiBaseTable, ArtColumn, useTablePipeline, features } from 'ali-react-table';
import { Tooltip, Spin } from 'antd';
import { IconButton, EditText, Empty } from '@chat2db/ui';
import { copyToClipboard, isEqualMemo } from '@/utils';

interface IProps {
  className?: string;
  // Table data.
  tableData?: any[] | null;
  // Table column configuration.
  columns: {
    title: string;
    name: string;
    lock?: boolean;
    onlyNumber?: boolean;
    editable?: boolean | undefined | ((rowData: any) => boolean | undefined);
    render?: (value: any, rowData: any, index: number) => React.ReactNode;
    transitionValue?: (value: any, rowData: any) => string;
  }[];
  // Column width.
  sizes?: number[];
  // Whether to show tooltips.
  tooltip?: boolean;
  // Whether the first column is draggable.
  immutableFirstColumn?: boolean;
  // Whether columns are draggable.
  draggableColumn?: boolean;
  // Whether cells are editable.
  editable?: boolean;
  // Whether the header is required.
  hasHeader?: boolean;
  // Whether the table is loading.
  loading?: boolean;
  // Cell-edit callback.
  onChangeCell?: (index, columnName, value) => void;
  // Selected rows.
  selectedRows?: number[];
  // Selection-change callback.
  onSelectedRowsChange?: (selectedRows: number[]) => void;
}

export interface BaseTableRef {
  scrollTo: (index: number) => void;
  scrollToTop: () => void;
  scrollToBottom: () => void;
  scrollToRight: () => void;
  scrollToLeft: () => void;
}

const BaseTable = forwardRef((props: IProps, ref: ForwardedRef<BaseTableRef>) => {
  const {
    className,
    tableData,
    columns: entheticColumns,
    sizes: entheticSizes,
    immutableFirstColumn,
    tooltip = false,
    draggableColumn = true,
    hasHeader = true,
    editable = false,
    onChangeCell,
    selectedRows,
    onSelectedRowsChange,
    loading,
  } = props;
  const { styles, cx, theme } = useStyles();
  const { styles: tableStyles } = useTableStyles();
  const [sizes, setColumnResize] = useState<number[]>(entheticSizes || []);
  const supportBaseTableBoxRef = React.createRef<HTMLDivElement>();
  const aLiBaseTableRef = useRef<any>(null);

  useEffect(() => {
    return () => {
      aLiBaseTableRef.current = null;
    };
  }, []);

  // Table column configuration.
  const columns: ArtColumn[] = useMemo(() => {
    return entheticColumns.map((columnConfig) => {
      //
      const render = (value, rowData, index) => {
        let _value = value;
        if (columnConfig.transitionValue) {
          _value = columnConfig.transitionValue(value, rowData);
        }

        const isSelected = selectedRows?.includes(index) || false;

        return (
          <TableCell
            columnConfig={columnConfig}
            rowData={rowData}
            index={index}
            tooltip={tooltip}
            editable={editable}
            onChangeCell={onChangeCell}
            value={_value}
            isSelected={isSelected}
            setSelectedRows={onSelectedRowsChange}
            styles={styles}
            cx={cx}
          />
        );
      };

      return {
        title: columnConfig.title,
        name: columnConfig.name,
        code: columnConfig.name,
        lock: columnConfig.lock || false,
        render: columnConfig.render || render,
      };
    });
  }, [entheticColumns, selectedRows]);

  // Table rendering configuration.
  const pipeline = useTablePipeline()
    .input({ dataSource: tableData || [], columns })
    .use(
      features.columnResize({
        handleHoverBackground: theme.colorPrimaryBgHover,
        handleActiveBackground: theme.colorPrimaryBgHover,
        minSize: 60,
        maxSize: 500,
        sizes,
        onChangeSizes: (_sizes) => {
          if (draggableColumn) {
            if (immutableFirstColumn) {
              _sizes[0] = sizes[0];
            }
            setColumnResize(_sizes);
          }
        },
      }),
    );

  const scrollTo = (index) => {
    supportBaseTableBoxRef.current?.scrollTo({
      top: index * 32,
      behavior: 'smooth',
    });
  };

  const scrollToTop = () => {
    supportBaseTableBoxRef.current?.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  };

  const scrollToBottom = () => {
    supportBaseTableBoxRef.current?.scrollTo({
      top: supportBaseTableBoxRef.current.scrollHeight,
      behavior: 'smooth',
    });
  };

  const scrollToRight = () => {
    const tables = supportBaseTableBoxRef.current?.querySelectorAll('table');
    const tableBody = supportBaseTableBoxRef.current?.querySelector('.art-table-body');
    const secondTable = tables?.[1];
    if (secondTable && tableBody) {
      tableBody.scrollLeft = secondTable.scrollWidth;
    }
  };

  const scrollToLeft = () => {
    const tables = supportBaseTableBoxRef.current?.querySelectorAll('table');
    const secondTable = tables?.[1];
    if (secondTable) {
      secondTable.scrollLeft = 0;
    }
  };

  useImperativeHandle(ref, () => ({
    scrollTo,
    scrollToTop,
    scrollToBottom,
    scrollToRight,
    scrollToLeft,
  }));

  return (
    <div className={cx(tableStyles.supportBaseTableBox, className)} ref={supportBaseTableBoxRef}>
      {loading ? (
        <div className={styles.spinBox}>
          <Spin />
        </div>
      ) : (
        <>
          <ALiBaseTable
            ref={aLiBaseTableRef}
            className={tableStyles.baseTable}
            components={{ EmptyContent: () => <Empty title={i18n('common.text.noData')} /> }}
            isStickyHeader
            estimatedRowHeight={32}
            stickyTop={32}
            // useVirtual={false}
            hasHeader={hasHeader}
            {...pipeline.getProps()}
          />
        </>
      )}
    </div>
  );
});

export default memo(BaseTable, (prevProps, nextProps) => {
  return isEqualMemo(
    [prevProps.tableData, nextProps.tableData],
    [prevProps.columns, nextProps.columns],
    [prevProps.selectedRows, nextProps.selectedRows],
  );
});

export interface TableCellProps {
  columnConfig: any;
  rowData: any;
  value: any;
  index: number;
  tooltip?: boolean;
  editable?: boolean;
  onChangeCell?: (index, columnName, value) => void;
  isSelected?: boolean;
  setSelectedRows?: (selectedRows: number[]) => void;
  styles: any;
  cx: any;
}

const TableCell = memo<TableCellProps>(
  ({
    columnConfig,
    value,
    rowData,
    index,
    tooltip,
    editable,
    onChangeCell,
    isSelected,
    setSelectedRows,
    styles,
    cx,
  }) => {
    // const { styles } = useStyles({ isSelected });
        // Delay tooltip rendering to avoid virtual-scroll performance issues.
    const [goodTiming, setGoodTiming] = useState(false);

    useEffect(() => {
      const timer = setTimeout(() => {
        setGoodTiming(true);
      }, 500);

      return () => {
        clearTimeout(timer);
      };
    }, []);

  // Determine whether the cell is controlled in edit/read mode or uncontrolled.
    const isEditable = () => {
      if (typeof columnConfig.editable === 'function') {
        return columnConfig.editable(rowData);
      }

      if (columnConfig.editable === true) {
        return undefined;
      }

      if (columnConfig.editable === false) {
        return false;
      }

      if (editable === true) {
        return undefined;
      }

      return false;
    };

    const handelClickCell = () => {
      setSelectedRows?.([index]);
    };

    const renderTooltipTitle = (_value) => {
      return (
        <div className={styles.tooltipTitle}>
          {_value}
          <Tooltip title={i18n('common.button.copy')} mouseEnterDelay={1}>
            <IconButton
              onClick={() => {
                copyToClipboard(_value);
              }}
              className={styles.copyButton}
              code="icon-copy"
              size="xs"
            />
          </Tooltip>
        </div>
      );
    };

    const editing = useMemo(() => {
      return isEditable();
    }, [editable, columnConfig]);

    const editText = useMemo(() => {
      return (
        <EditText
          onlyNumber={columnConfig.onlyNumber}
          editing={editing}
          className={cx(styles.tableCell, { [styles.isSelectedTableCell]: isSelected })}
          textClassName={styles.editTextTextClass}
          inputClassName={styles.editTextInputClass}
          hoverShowBorder={editing !== false}
          placeholder={value === null ? '<null>' : undefined}
          onClick={() => {
            handelClickCell();
          }}
          onBlur={(_value) => {
            onChangeCell?.(index, columnConfig.name, _value);
          }}
        >
          {value}
        </EditText>
      );
    }, [value, editing, isSelected]);

    if (!goodTiming) {
      return <div className={styles.plainText}>{value}</div>;
    }

    return tooltip ? (
      <Tooltip placement="topLeft" title={renderTooltipTitle(value)} mouseEnterDelay={0.5}>
        {editText}
      </Tooltip>
    ) : (
      editText
    );
    // return editText;
  },
  (prevProps, nextProps) => {
    return isEqualMemo(
      [prevProps.columnConfig, nextProps.columnConfig],
      [prevProps.value, nextProps.value],
      [prevProps.isSelected, nextProps.isSelected],
    );
  },
);
