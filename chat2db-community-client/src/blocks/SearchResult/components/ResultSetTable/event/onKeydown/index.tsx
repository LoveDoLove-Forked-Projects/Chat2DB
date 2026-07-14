import * as VTable from '@visactor/vtable';

const onKeydown = (tableInstance: VTable.ListTable) => {
  // listens to the event of _tableInstance
  tableInstance.on('keydown', (e) => {
    tableInstance.getSelectedCellInfos()?.forEach((cellInfo) => {
      // tableInstance.setRecords()
    });
  });
};

export default onKeydown;
