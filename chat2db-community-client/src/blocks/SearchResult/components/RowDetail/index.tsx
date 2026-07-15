import { ForwardedRef, forwardRef, memo, useImperativeHandle, useMemo, useState } from 'react';
import { Button } from 'antd';
import { Modal } from '@chat2db/ui';
import i18n from '@/i18n';
import { IManageResultData, IResultCell } from '@/typings';
import { ITableInstance } from '@/blocks/CanvasTable/typings';
import { useStyles } from './style';

interface IOpenRowDetailParams {
  tableInstance: ITableInstance;
  col: number;
  row: number;
}

export interface IViewFullValueParams {
  tableInstance: ITableInstance;
  col: number;
  row: number;
  cellMeta?: IResultCell;
}

interface IRowDetailItem {
  field: string;
  col: number;
  value: string | number | boolean | null;
  largeValue?: boolean;
  cellMeta?: IResultCell;
}

interface IRowDetailState {
  rowNumber?: string | number | null;
  tableInstance: ITableInstance;
  row: number;
  items: IRowDetailItem[];
}

interface IProps {
  resultData: IManageResultData;
  // View the complete large field content and reuse the ViewData pop-up window
  onViewFullValue?: (params: IViewFullValueParams) => void;
}

export interface RowDetailRef {
  openModal: (params: IOpenRowDetailParams) => void;
}

const formatValue = (value: any, cellMeta?: IResultCell) => {
  if (cellMeta?.largeValue) {
    // Large fields contain only a backend preview; the full content is loaded on demand.
    return cellMeta.value ?? null;
  }
  if (value === null || value === undefined) {
    return null;
  }
  if (typeof value === 'object') {
    return JSON.stringify(value, null, 2);
  }
  return value;
};

const RowDetail = forwardRef((props: IProps, ref: ForwardedRef<RowDetailRef>) => {
  const { resultData, onViewFullValue } = props;
  const { styles, cx } = useStyles();
  const [rowDetail, setRowDetail] = useState<IRowDetailState | null>(null);

  const headerMap = useMemo(() => {
    return resultData.headerList || [];
  }, [resultData.headerList]);

  const openModal = ({ tableInstance, col, row }: IOpenRowDetailParams) => {
    const recordCol = col > 0 ? col : 1;
    const record = tableInstance.getRecordByCell(recordCol, row);
    if (!record) {
      return;
    }

    const cellMetaList: IResultCell[] = record.__CHAT2DB_CELL_META__ || [];
    const items = headerMap.slice(1).map((header, index) => {
      const fieldCol = index + 1;
      const cellMeta = cellMetaList[fieldCol];
      const columnTitle = tableInstance.columns?.[fieldCol - 1]?.title;
      return {
        field: header?.name || (columnTitle ? String(columnTitle) : String(fieldCol)),
        col: fieldCol,
        value: formatValue(record[fieldCol], cellMeta),
        largeValue: cellMeta?.largeValue,
        cellMeta,
      };
    });

    setRowDetail({
      rowNumber: record.CHAT2DB_ROW_NUMBER,
      tableInstance,
      row,
      items,
    });
  };

  useImperativeHandle(
    ref,
    () => ({
      openModal,
    }),
    [headerMap],
  );

  const handleViewFullValue = (item: IRowDetailItem) => {
    if (!rowDetail) {
      return;
    }
    onViewFullValue?.({
      tableInstance: rowDetail.tableInstance,
      col: item.col,
      row: rowDetail.row,
      cellMeta: item.cellMeta,
    });
  };

  return (
    <Modal
      title={`${i18n('common.button.viewRowDetail')}${
        rowDetail?.rowNumber !== null && rowDetail?.rowNumber !== undefined && rowDetail?.rowNumber !== ''
          ? ` #${rowDetail.rowNumber}`
          : ''
      }`}
      open={!!rowDetail}
      onCancel={() => setRowDetail(null)}
      width="60vw"
      maskClosable={false}
      destroyOnClose={true}
      footer={null}
    >
      <div className={styles.container}>
        {rowDetail?.items.map((item, index) => {
          const isNull = item.value === null || item.value === undefined;
          return (
            <div className={styles.item} key={`${item.field}-${index}`}>
              <div className={styles.field}>{item.field}</div>
              <div className={styles.valueWrapper}>
                <div className={cx(styles.value, isNull && styles.nullValue)}>
                  {isNull ? '<null>' : String(item.value)}
                </div>
                {item.largeValue && onViewFullValue && (
                  <Button size="small" type="link" className={styles.viewFullValue} onClick={() => handleViewFullValue(item)}>
                    {i18n('common.button.viewFullValue')}
                  </Button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </Modal>
  );
});

export default memo(RowDetail);
