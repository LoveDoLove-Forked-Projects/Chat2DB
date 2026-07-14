import { copyToClipboard } from '@/utils';
import { isValid } from '@/utils/check';
import * as VTable from '@visactor/vtable';

// Copies the specified value or the currently selected value
const handleCopy = (tableInstance: VTable.ListTable, value?: string) => {
  if (value) { 
    copyToClipboard(value || '');
    return;
  }
  const copyData = tableInstance.getCopyValue() || '';
  if (isValid(copyData)) {
    copyToClipboard(copyData || '');
    /* The copied code below is the Vtable code. I don’t know the necessity of doing this. */
    // if (navigator.clipboard?.write) {
    //   // Convert the copied data to html format
    //   const setDataToHTML = (data: string) => {
    //     const result = ['<table>'];
    //     const META_HEAD = [
    //       '<meta name="author" content="Visactor"/>', // can be used latervtableQuick copy and paste between.
    //       //white-space:normal, continuous whitespace characters will be merged into one space, and the text will be automatically wrapped and displayed according to the width of the container.
    //       //mso-data-placement:same-cell, exclusive to excel, displays all data in the same cell instead of spreading the data to multiple cells by default.
    //       '<style type="text/css">td{white-space:normal}br{mso-data-placement:same-cell}</style>',
    //     ].join('');
    //     const rows = data.split('\r\n'); // Split data into rows
    //     rows.forEach((rowCells: any, rowIndex: number) => {
    //       const cells = rowCells.split('\t'); // Split row data into cells
    //       const rowValues: string[] = [];
    //       if (rowIndex === 0) {
    //         result.push('<tbody>');
    //       }
    //       cells.forEach((cell: string) => {
    //         // Cell data processing
    //         const parsedCellData = !cell
    //           ? ' '
    //           : cell
    //               .toString()
    //               .replace(/&/g, '&amp;') // replace & with &amp; to prevent XSS attacks
    //               .replace(/'/g, '&#39;') // replace ' with &#39; to prevent XSS attacks
    //               .replace(/</g, '&lt;') // replace < with &lt; to prevent XSS attacks
    //               .replace(/>/g, '&gt;') // replace > with &gt; to prevent XSS attacks
    //               .replace(/\n/g, '<br>') // replace \n with <br> to prevent XSS attacks
    //               .replace(/(<br(\s*|\/)>(\r\n|\n)?|\r\n|\n)/g, '<br>\r\n') //   replace <br> with <br>\r\n to prevent XSS attacks
    //               .replace(/\x20{2,}/gi, (substring: string | any[]) => {
    //                 // Excel continuous space serialization
    //                 return `<span style="mso-spacerun: yes">${'&nbsp;'.repeat(substring.length - 1)} </span>`;
    //               }) // replace 2 or more spaces with &nbsp; to prevent XSS attacks
    //               .replace(/\t/gi, '&#9;'); //   replace \t with &#9; to prevent XSS attacks

    //         rowValues.push(`<td>${parsedCellData}</td>`);
    //       });
    //       result.push('<tr>', ...rowValues, '</tr>');

    //       if (rowIndex === rows.length - 1) {
    //         result.push('</tbody>');
    //       }
    //     });
    //     result.push('</table>');
    //     return [META_HEAD, result.join('')].join('');
    //   };
    //   const dataHTML = setDataToHTML(copyData);
    //   navigator.clipboard.write([
    //     new ClipboardItem({
    //       'text/html': new Blob([dataHTML], { type: 'text/html' }),
    //       'text/plain': new Blob([copyData], { type: 'text/plain' }),
    //     }),
    //   ]);
    // }
    // else {
    //   if (browser.IE) {
    //     (window as any).clipboardData.setData('Text', data); // IE
    //   } else {
    //     (e as any).clipboardData.setData('text/plain', data); // Chrome, Firefox
    //   }
    // }
    tableInstance.fireListeners('copy_data', {
      cellRange: tableInstance.stateManager.select.ranges,
      copyData: copyData,
    });
  }
};

export default handleCopy;
