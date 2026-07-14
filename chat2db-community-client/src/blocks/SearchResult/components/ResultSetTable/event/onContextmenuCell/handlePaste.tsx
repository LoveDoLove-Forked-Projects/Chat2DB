import * as VTable from '@visactor/vtable';
import { readClipboard } from '@/utils/clipboard';
import { normalizeCreateRowPasteValues, normalizePasteTargetRange } from '@/blocks/SearchResult/utils';
import type { OperationRecordUtils } from '../../hooks/useOperationRecord';

// const regex = /<tr[^>]*>(.*?)<\/tr>/gs; // matching<tr>tag and its content
const regex = /<tr[^>]*>([\s\S]*?)<\/tr>/g; // for webpack3
// const cellRegex = /<td[^>]*>(.*?)<\/td>/gs; // matching<td>tag and its content
const cellRegex = /<td[^>]*>([\s\S]*?)<\/td>/g; // for webpack3

type PasteOperationRecordUtils = Pick<OperationRecordUtils, 'isCreateRow'>;

function getSelectedPasteRange(tableInstance: VTable.ListTable, rangeIndex: number) {
  const range = tableInstance.stateManager.select.ranges[rangeIndex];
  return normalizePasteTargetRange({
    startCol: range.start.col,
    startRow: range.start.row,
    endCol: range.end.col,
    endRow: range.end.row,
  });
}

function normalizePasteValues(
  tableInstance: VTable.ListTable,
  operationRecordUtils: PasteOperationRecordUtils | undefined,
  col: number,
  row: number,
  values: (string | number)[][],
) {
  return normalizeCreateRowPasteValues({
    values,
    startCol: col,
    startRow: row,
    columns: tableInstance.columns,
    getRowId: (targetRow, targetCol) => tableInstance.getRecordByCell(targetCol, targetRow)?.CHAT2DB_ROW_NUMBER,
    isCreateRow: operationRecordUtils?.isCreateRow,
  });
}

function _pasteValue(
  pastedData: string,
  tableInstance: VTable.ListTable,
  operationRecordUtils?: PasteOperationRecordUtils,
) {
  const ranges = tableInstance.stateManager.select.ranges;
  const selectRangeLength = ranges.length;
  const { col, row, maxCol, maxRow } = getSelectedPasteRange(tableInstance, selectRangeLength - 1);
  let pasteValuesColCount = 0;
  let pasteValuesRowCount = 0;
  let values: (string | number)[][] = [];
  const rows = pastedData.split('\n'); // Split data into rows
  rows.forEach((rowCells: any) => {
    const cells = rowCells.split('\t'); // Split row data into cells
    const rowValues: (string | number)[] = [];
    values.push(rowValues);
    cells.forEach((cell: string, cellIndex: number) => {
      let _cell = cell;
      // Remove the '\r' at the end of cell data
      if (cellIndex === cells.length - 1) {
        _cell = cell.trim();
      }
      rowValues.push(_cell);
    });
    pasteValuesColCount = Math.max(pasteValuesColCount, rowValues?.length ?? 0);
  });
  pasteValuesRowCount = values.length ?? 0;
  values = handlePasteValues(values, pasteValuesRowCount, pasteValuesColCount, maxRow - row + 1, maxCol - col + 1);
  values = normalizePasteValues(tableInstance, operationRecordUtils, col, row, values);
  tableInstance.changeCellValues(col, row, values, true);
}

function handlePasteValues(
  values: (string | number)[][],
  rowCount: number,
  colCount: number,
  selectedRowCount: number,
  selectedColCount: number,
) {
  if (selectedColCount > colCount || selectedRowCount > rowCount) {
    if (selectedColCount % colCount === 0 && selectedRowCount % rowCount === 0) {
      const toPasteValues: (string | number)[][] = [];
      // Loops through the target range, pasting the copied values into each cell one by one
      for (let i = 0; i < selectedRowCount; i++) {
        const rowPasteValue: (string | number)[] = [];
        toPasteValues.push(rowPasteValue);
        for (let j = 0; j < selectedColCount; j++) {
          const copiedRow = i % rowCount;
          const copiedCol = j % colCount;
          rowPasteValue.push(values[copiedRow][copiedCol]);
        }
      }
      return toPasteValues;
    }
    return values;
  }
  return values;
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function pasteHtmlToTable(
  item: ClipboardItem,
  tableInstance: VTable.ListTable,
  operationRecordUtils?: PasteOperationRecordUtils,
) {
  const ranges = tableInstance.stateManager.select.ranges;
  const selectRangeLength = ranges.length;
  const { col, row, maxCol, maxRow } = getSelectedPasteRange(tableInstance, selectRangeLength - 1);
  let pasteValuesColCount = 0;
  let pasteValuesRowCount = 0;
  let values: (string | number)[][] = [];
  item.getType('text/html').then((blob: any) => {
    blob.text().then((pastedData: any) => {
      // parses html data
      if (pastedData && /(<table)|(<TABLE)/g.test(pastedData)) {
        // const matches = pastedData.matchAll(regex);
        const matches = Array.from(pastedData.matchAll(regex));
        for (const match of matches as RegExpMatchArray[]) {
          const rowContent = match[1]; // Get<tr>tag
          const cellMatches: RegExpMatchArray[] = Array.from(rowContent.matchAll(cellRegex)); // Get<td>tag
          const rowValues = cellMatches.map((cellMatch) => {
            return (
              cellMatch[1]
                .replace(/(<(?!br)([^>]+)>)/gi, '') // except <br> tags HTML tags are replaced with empty strings
                .replace(/<br(\s*|\/)>[\r\n]?/gim, '\n') // will be in the string <br> tags and any whitespace characters and slashes that may follow them are replaced with newlines \n
                // .replace(/<br>/g, '\n') // replacement<br>label is newline character
                // .replace(/<(?:.|\n)*?>/gm, '') // RemoveHTMLTags
                //Convert HTML entity characters in a string to original characters
                .replace(/&amp;/g, '&')
                .replace(/&lt;/g, '<')
                .replace(/&gt;/g, '>')
                .replace(/&#9;/gi, '\t')
                .replace(/&nbsp;/g, ' ')
            );
            // .trim(); // Remove leading and trailing spaces
          });
          values.push(rowValues);
          pasteValuesColCount = Math.max(pasteValuesColCount, rowValues?.length ?? 0);
        }
        pasteValuesRowCount = values.length ?? 0;
        values = handlePasteValues(
          values,
          pasteValuesRowCount,
          pasteValuesColCount,
          maxRow - row + 1,
          maxCol - col + 1,
        );
        values = normalizePasteValues(tableInstance, operationRecordUtils, col, row, values);
        tableInstance.changeCellValues(col, row, values, true);
      } else {
        navigator.clipboard.read().then((clipboardItems) => {
          for (const _item of clipboardItems) {
            if (_item.types.includes('text/plain')) {
              _item.getType('text/plain').then((_blob: Blob) => {
                _blob.text().then((res) => _pasteValue(res, tableInstance, operationRecordUtils));
              });
            }
          }
        });
      }
    });
  });
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function pasteTextToTable(
  item: ClipboardItem,
  tableInstance: VTable.ListTable,
  operationRecordUtils?: PasteOperationRecordUtils,
) {
  // if only 'text/plain'
  const ranges = tableInstance.stateManager.select.ranges;
  const selectRangeLength = ranges.length;
  const { col, row, maxCol, maxRow } = getSelectedPasteRange(tableInstance, selectRangeLength - 1);
  let pasteValuesColCount = 0;
  let pasteValuesRowCount = 0;
  // const values: (string | number)[][] = [];
  item.getType('text/plain').then((blob: any) => {
    blob.text().then((pastedData: any) => {
      const rows = pastedData.replace(/\r(?!\n)/g, '\r\n').split('\r\n'); // Line break formats in text are uniformly processed.
      let values: (string | number)[][] = [];
      if (rows.length > 1 && rows[rows.length - 1] === '') {
        rows.pop();
      }
      rows.forEach((rowCells: any) => {
        const cells = rowCells.split('\t'); // Split row data into cells
        const rowValues: (string | number)[] = [];
        values.push(rowValues);
        cells.forEach((cell: string) => {
          let _cell = cell;
          if (cell.includes('\n')) {
            _cell = cell
              .replace(/^"(.*)"$/, '$1') // Remove the double quotes at the beginning and end of the string and retain the content within the double quotes
              .replace(/["]*/g, (match) =>
                // Replace consecutive double quotes with half the number of double quotes
                new Array(Math.floor(match.length / 2))
                  .fill('"')
                  .join(''),
              );
          }
          rowValues.push(_cell);
        });
        pasteValuesColCount = Math.max(pasteValuesColCount, rowValues?.length ?? 0);
      });
      pasteValuesRowCount = values.length ?? 0;
      values = handlePasteValues(values, pasteValuesRowCount, pasteValuesColCount, maxRow - row + 1, maxCol - col + 1);
      values = normalizePasteValues(tableInstance, operationRecordUtils, col, row, values);
      tableInstance.changeCellValues(col, row, values, true);
    });
  });
}

// TODO: There is a problem with the pasting here. The original null of the node will be pasted as an empty string.
const handlePaste = async (tableInstance: VTable.ListTable, operationRecordUtils?: PasteOperationRecordUtils) => {
  if (tableInstance.editorManager?.editingEditor) {
    return;
  }
  if (tableInstance.stateManager.select.ranges?.length > 0) {
    // if (navigator.clipboard?.read) {
    //   // Read clipboard data
    //   navigator.clipboard.read().then((clipboardItems) => {
    //     for (const item of clipboardItems) {
    //       // Prioritize processing of html format data
    //       if (item.types.includes('text/html')) {
    //         pasteHtmlToTable(item, tableInstance);
    //       } else if (item.types.length === 1 && item.types[0] === 'text/plain') {
    //         pasteTextToTable(item, tableInstance);
    //       } else {
    //         // Other situations
    //       }
    //     }
    //   });
    // } else {
    const { col, row } = getSelectedPasteRange(tableInstance, 0);
    // const clipboardData = e.clipboardData || window.Clipboard;
    // const pastedData = clipboardData.getData('text');
    const pastedData = await readClipboard();
    const rows = pastedData.split('\n'); // Split data into rows
    const values: (string | number)[][] = [];
    rows.forEach((rowCells: any) => {
      const cells = rowCells.split('\t'); // Split row data into cells
      const rowValues: (string | number)[] = [];
      values.push(rowValues);
      cells.forEach((cell: string, cellIndex: number) => {
        let _cell = cell;
        // Remove the '\r' at the end of cell data
        if (cellIndex === cells.length - 1) {
          _cell = cell.trim();
        }
        rowValues.push(_cell);
      });
    });
    const normalizedValues = normalizePasteValues(tableInstance, operationRecordUtils, col, row, values);
    tableInstance.changeCellValues(col, row, normalizedValues);
  }
  // }
};

export default handlePaste;
