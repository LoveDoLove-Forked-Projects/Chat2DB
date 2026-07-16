import { ForwardedRef, forwardRef, memo, useImperativeHandle, useMemo, useState } from 'react';
import { Button, Input, Tooltip } from 'antd';
import { Modal } from '@chat2db/ui';
import i18n from '@/i18n';
import Iconfont from '@/components/Iconfont';
import { IManageResultData, IResultCell } from '@/typings';
import { ITableInstance } from '@/blocks/CanvasTable/typings';
import { useStyles } from './style';

interface IOpenRowDetailParams {
  tableInstance: ITableInstance;
  col: number;
  row: number;
}

export interface IViewDataParams {
  tableInstance: ITableInstance;
  col: number;
  row: number;
  field: string;
  cellMeta?: IResultCell;
}

export interface IChangeDataParams extends IViewDataParams {
  value: string | null;
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
  onViewData?: (params: IViewDataParams) => void;
  onChangeData?: (params: IChangeDataParams) => void;
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
  const { resultData, onViewData, onChangeData } = props;
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

  const handleViewData = (item: IRowDetailItem) => {
    if (!rowDetail) {
      return;
    }
    onViewData?.({
      tableInstance: rowDetail.tableInstance,
      col: item.col,
      row: rowDetail.row,
      field: item.field,
      cellMeta: item.cellMeta,
    });
  };

  const handleValueChange = (col: number, value: string) => {
    setRowDetail((current) => {
      if (!current) {
        return current;
      }
      return {
        ...current,
        items: current.items.map((item) => (item.col === col ? { ...item, value } : item)),
      };
    });
  };

  const handleValueBlur = (item: IRowDetailItem) => {
    if (!rowDetail || !resultData.canEdit || item.largeValue || !onChangeData) {
      return;
    }
    onChangeData({
      tableInstance: rowDetail.tableInstance,
      col: item.col,
      row: rowDetail.row,
      field: item.field,
      value: item.value === null || item.value === undefined ? null : String(item.value),
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
                <Input
                  value={item.value === null || item.value === undefined ? '' : String(item.value)}
                  placeholder={isNull ? '<null>' : undefined}
                  readOnly={!resultData.canEdit || item.largeValue || !onChangeData}
                  className={cx(styles.valueInput, isNull && styles.nullValue)}
                  onChange={(event) => handleValueChange(item.col, event.target.value)}
                  onBlur={() => handleValueBlur(item)}
                />
              </div>
              <div className={styles.action}>
                {onViewData && (
                  <Tooltip title={i18n('common.button.viewData')}>
                    <Button
                      type="text"
                      aria-label={i18n('common.button.viewData')}
                      data-row-detail-action="true"
                      icon={<Iconfont code="&#xe788;" />}
                      className={styles.actionButton}
                      onClick={() => handleViewData(item)}
                    />
                  </Tooltip>
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
