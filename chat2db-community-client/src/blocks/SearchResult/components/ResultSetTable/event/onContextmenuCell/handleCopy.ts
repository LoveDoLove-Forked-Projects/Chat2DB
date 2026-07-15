import * as VTable from '@visactor/vtable';
import { copyToClipboard } from '@/utils';
import { isValid } from '@/utils/check';

// Copies the specified value or the currently selected value.
const handleCopy = (tableInstance: VTable.ListTable, value?: string) => {
  if (value) {
    copyToClipboard(value);
    return;
  }

  const copyData = tableInstance.getCopyValue() || '';
  if (isValid(copyData)) {
    copyToClipboard(copyData);
    tableInstance.fireListeners('copy_data', {
      cellRange: tableInstance.stateManager.select.ranges,
      copyData,
    });
  }
};

export default handleCopy;
