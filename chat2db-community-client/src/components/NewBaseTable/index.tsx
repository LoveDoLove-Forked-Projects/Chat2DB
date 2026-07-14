import React, { memo, useState, useMemo, forwardRef, ForwardedRef, useImperativeHandle, useEffect } from 'react';
import { useStyles } from './style';
import { useTableStyles } from '@/styles/table';
import i18n from '@/i18n';
import { Tooltip, Spin, ConfigProvider } from 'antd';
import type { GetProp, TableProps } from 'antd';
import { IconButton, EditText, Empty } from '@chat2db/ui';
import { copyToClipboard, isEqualMemo } from '@/utils';
import { Table } from 'antd';
import { useSize } from 'ahooks';

type ColumnsType<
  T extends {
    onlyNumber?: boolean;
    editable?: boolean | undefined | ((rowData: any) => boolean | undefined);
    transitionValue?: (value: any, rowData: any) => string;
  },
> = GetProp<TableProps<T>, 'columns'>;

interface IProps {
  className?: string;
  // Table data.
  dataSource?: any[];
  // Table column configuration.
  columns: ColumnsType<any>;
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
  // Whether the table is loading.
  loading?: boolean;
  // Cell-edit callback.
  onChangeCell?: (index, columnName, value) => void;
  // Selected rows.
  selectedRows?: number[];
  // Selection-change callback.
  onSelectedRowsChange?: (selectedRows: number[]) => void;
  // Highlighted rows.
  highlightRows?: number[];
  // Highlighted columns.
  highlightColumns?: number[];
}

export interface BaseTableRef {
  scrollTo: (index: number) => void;
  scrollToTop: () => void;
  scrollToBottom: () => void;
  scrollToRight: () => void;
  scrollToLeft: () => void;
}

const NewBaseTable = forwardRef((props: IProps, ref: ForwardedRef<BaseTableRef>) => {
  const {
    className,
    dataSource,
    columns,
    sizes: entheticSizes,
    tooltip = false,
    immutableFirstColumn,
    draggableColumn = true,
    editable = false,
    onChangeCell,
    selectedRows,
    onSelectedRowsChange,
    loading,
    highlightRows,
    highlightColumns,
  } = props;
  const { styles, cx, theme } = useStyles();
  const { styles: tableStyles } = useTableStyles();
  const [sizes, setColumnResize] = useState<number[]>(entheticSizes || []);
  const supportBaseTableBoxRef = React.createRef<HTMLDivElement>();

  // Table column configuration.
  const convertedColumns: ColumnsType<any> = useMemo(() => {
    return columns.map((columnConfig: any, columnIndex) => {
      const isHighlightColumns = highlightColumns?.includes(columnIndex);

      const render = (value, rowData, rowIndex) => {
        let _value = value;
        if (columnConfig.transitionValue) {
          _value = columnConfig.transitionValue(value, rowData);
        }

        // const isSelected = selectedRows?.includes(index) || false;

        // Determine whether highlighting is needed.
        const isHighlightRows = highlightRows?.includes(rowIndex);

        return (
          <div
            className={cx(
              styles.plainText,
              { isHighlightRows: isHighlightRows },
              { isHighlightColumns: isHighlightColumns },
            )}
          >
            {_value}
          </div>
        );

        // return (
        //   <TableCell
        //     columnConfig={columnConfig}
        //     rowData={rowData}
        //     index={index}
        //     tooltip={tooltip}
        //     editable={editable}
        //     onChangeCell={onChangeCell}
        //     value={_value}
        //     // isSelected={isSelected}
        //     setSelectedRows={onSelectedRowsChange}
        //     styles={styles}
        //     cx={cx}
        //   />
        // );
      };

      return {
        ...columnConfig,
        title: (
          <div className={cx(styles.plainText, { isHighlightColumns: isHighlightColumns })}>{columnConfig.title}</div>
        ),
        render,
      };
    });
  }, [columns, selectedRows, highlightRows, highlightColumns]);

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

  const supportBaseTableBoxSize = useSize(supportBaseTableBoxRef);

  return (
    <div className={cx(styles.supportBaseTableBox, className)} ref={supportBaseTableBoxRef}>
      {loading ? (
        <div className={styles.spinBox}>
          <Spin />
        </div>
      ) : (
        supportBaseTableBoxSize && (
          <ConfigProvider
            theme={{
              components: {
                Table: {
                  headerBorderRadius: 0,
                },
              },
            }}
          >
            <Table
              className={tableStyles.baseTable}
              virtual
              bordered
              dataSource={dataSource}
              columns={convertedColumns}
              pagination={false}
              scroll={{
                x: supportBaseTableBoxSize.width - 1, // 1 is the border width.
                y: supportBaseTableBoxSize.height - 32, // 32 is the header height.
              }}
            />
          </ConfigProvider>
        )
      )}
    </div>
  );
});

export default memo(NewBaseTable, (prevProps, nextProps) => {
  return isEqualMemo(
    [prevProps.dataSource, nextProps.dataSource],
    [prevProps.highlightRows, nextProps.highlightRows],
    [prevProps.highlightColumns, nextProps.highlightColumns],
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
        // Delay tooltip rendering to avoid virtual-scroll performance issues.
    const [goodTiming, setGoodTiming] = useState(false);

    // useEffect(() => {
    //   const timer = setTimeout(() => {
    //     setGoodTiming(true);
    //   }, 500);

    //   return () => {
    //     clearTimeout(timer);
    //   };
    // }, []);

    // console.log('goodTiming', goodTiming);

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

    // console.log('goodTiming', goodTiming);

    return tooltip ? (
      <Tooltip placement="topLeft" title={renderTooltipTitle(value)} mouseEnterDelay={0.5}>
        {editText}
      </Tooltip>
    ) : (
      editText
    );
  },
  (prevProps, nextProps) => {
    return isEqualMemo(
      [prevProps.columnConfig, nextProps.columnConfig],
      [prevProps.value, nextProps.value],
      [prevProps.isSelected, nextProps.isSelected],
    );
  },
);
