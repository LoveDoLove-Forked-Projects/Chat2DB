import { memo, useEffect, useMemo, forwardRef, useImperativeHandle, ForwardedRef, useCallback } from 'react';
import { useStyles } from './style';
import CanvasTable from '@/blocks/CanvasTable';
import { ITableInstance } from '@/blocks/CanvasTable/typings';
import { IManageResultData } from '@/typings/database';
import onContextmenuCell from './event/onContextmenuCell';
import onChangeCellValue from './event/onChangeCellValue';
import onCopyData from './event/onCopyData';
import onPasteData from './event/onPasteData';
import dataTreating from './utils/dataTreating';
import useOperationRecord, { OperationRecordUtils } from './hooks/useOperationRecord';
import useFilterAndSort from './hooks/useFilterAndSort';
import useHeaderTooltip from './hooks/useHeaderTooltip';
import { ITableOperationUtils } from './typings';
import { useGlobalStore } from '@/store/global';

interface IProps {
  className?: string;
  resultData: IManageResultData;
  // There are operational changes in the table
  onOperationChange?: (hasOperationRecord: any) => void;
  // table
  onTableOperationUtils: ITableOperationUtils;
  tableInstance: ITableInstance | null;
  setTableInstance: (tableInstance: ITableInstance) => void;
  setOrderByText?: (orderByText: string) => void;
  onFilterCountChange?: (count: number) => void;
}

export interface ResultSetTableRef {
  operationRecordUtils: OperationRecordUtils;
  tableInstance: ITableInstance | null;
  activeFilterCount: number;
  clearAllFilters: () => void;
}

const ResultSetTable = forwardRef((props: IProps, ref: ForwardedRef<ResultSetTableRef>) => {
  const { resultData, onOperationChange, onTableOperationUtils, tableInstance, setTableInstance } = props;
  const { styles, theme } = useStyles();

  // Registry data manipulation method
  const { operationRecordUtils, hasOperationRecord, reCalculateCellStyle } = useOperationRecord({
    tableInstance,
    theme,
  });

  const { dataTableSettings } = useGlobalStore((s) => ({
    dataTableSettings: s.dataTableSettings,
  }));

  // Filter and sort
  const { activeFilterCount, clearAllFilters } = useFilterAndSort({
    theme,
    tableInstance,
    resultData,
    sortAfter: reCalculateCellStyle,
    filterAfter: reCalculateCellStyle,
    setOrderByText: props.setOrderByText,
  });
  const headerTooltip = useHeaderTooltip({ tableInstance });

  const [columns, records] = useMemo(() => {
    return dataTreating({ data: resultData, theme, dataTableSettings });
  }, [resultData, theme.appearance, dataTableSettings]);

  useEffect(() => {
    onOperationChange?.(hasOperationRecord);
  }, [hasOperationRecord]);

  useEffect(() => {
    props.onFilterCountChange?.(activeFilterCount);
  }, [activeFilterCount]);

  useEffect(() => {
    if (!tableInstance || !operationRecordUtils) return;
    // monitors the right mouse click on a cell
    const { id: onContextmenuCellId } = onContextmenuCell({
      resultData,
      tableInstance,
      operationRecordUtils,
      onTableOperationUtils,
    });
    // monitors cell value changes
    const onChangeCellValueId = onChangeCellValue(tableInstance, operationRecordUtils.handleCellValueChange);
    // monitors copied data
    return () => {
      tableInstance?.off(onContextmenuCellId);
      tableInstance?.off(onChangeCellValueId);
    };
  }, [tableInstance, operationRecordUtils]);

  // callback after initialization is completed
  const onInit = useCallback((_tableInstance) => {
    setTableInstance(_tableInstance);
  }, []);

  useImperativeHandle(ref, () => {
    return {
      operationRecordUtils,
      tableInstance,
      activeFilterCount,
      clearAllFilters,
    };
  }, [operationRecordUtils, tableInstance, activeFilterCount, clearAllFilters]);

  const onCopy = useCallback(() => {
    if (!tableInstance) return;
    onCopyData(tableInstance);
  }, [tableInstance]);

  const onPaste = useCallback(() => {
    if (!tableInstance) return;
    onPasteData(tableInstance, operationRecordUtils);
  }, [tableInstance, operationRecordUtils]);

  return (
    <>
      <CanvasTable
        columns={columns}
        records={records}
        onInit={onInit}
        className={styles.canvasTable}
        onCopy={onCopy}
        onPaste={onPaste}
        options={{
          rowSeriesNumber: {
            title: undefined,
            width: 'auto' as any,
            disableColumnResize: true,
          },
          keyboardOptions: {
            copySelected: false, // Start copying
            pasteValueToCell: false, // Turn on paste
            selectAllOnCtrlA: true, // Turn on all selections
          },
          frozenColCount: 1, // Number of frozen columns
        }}
      />
      {/* plug-in area */}
      <>{headerTooltip}</>
    </>
  );
});

export default memo(ResultSetTable);
