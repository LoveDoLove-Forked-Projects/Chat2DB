import { applyPasteData, parsePasteData, PasteSelection, PasteTable } from './pasteData';
import { GENERATED_VALUE } from '@/blocks/SearchResult/utils';

function assertEqual(actual: any, expected: any, message: string) {
  const actualJson = JSON.stringify(actual);
  const expectedJson = JSON.stringify(expected);
  if (actualJson !== expectedJson) {
    throw new Error(`${message}: expected ${expectedJson}, got ${actualJson}`);
  }
}

function createTable(rowCount = 10, colCount = 10) {
  const calls: Array<{ col: number; row: number; value: string | null }> = [];
  const table: PasteTable = {
    rowCount,
    colCount,
    changeCellValue: (col, row, value) => {
      calls.push({ row, col, value });
    },
  };
  return { table, calls };
}

const multiLineSql = 'select \n  * \nfrom \n  ai_chat_message;';
const multiLineSqlWithTabIndent = 'select\n\t*\nfrom\n\tai_chat_message;';

assertEqual(
  parsePasteData(multiLineSql, 1),
  {
    type: 'cell',
    value: multiLineSql,
  },
  'single selected cell keeps plain multiline text as one cell value',
);

assertEqual(
  parsePasteData("''", 1),
  {
    type: 'cell',
    value: '',
  },
  "single selected cell keeps legacy empty-string sentinel",
);

assertEqual(
  parsePasteData(multiLineSqlWithTabIndent, 1),
  {
    type: 'cell',
    value: multiLineSqlWithTabIndent,
  },
  'single selected cell keeps tab-indented multiline text as one cell value',
);

assertEqual(
  parsePasteData(' \n ', 1),
  {
    type: 'cell',
    value: null,
  },
  'single selected cell keeps legacy blank-to-null behavior',
);

assertEqual(
  parsePasteData('a\tb\r\nc\td', 4),
  {
    type: 'grid',
    rows: [
      ['a', 'b'],
      ['c', 'd'],
    ],
  },
  'multi-cell selection parses CRLF TSV as grid data',
);

{
  const { table, calls } = createTable();
  const selection: PasteSelection = [[{ row: 2, col: 3 }]];

  applyPasteData(table, selection, multiLineSql);

  assertEqual(
    calls,
    [{ row: 2, col: 3, value: multiLineSql }],
    'single selected body cell writes the complete multiline value once',
  );
}

{
  const { table, calls } = createTable();
  const selection: PasteSelection = [[{ row: 0, col: 0 }]];

  applyPasteData(table, selection, multiLineSql);

  assertEqual(
    calls,
    [{ row: 1, col: 1, value: multiLineSql }],
    'single selected header or row-number cell redirects to the first body cell',
  );
}

{
  const { table, calls } = createTable();
  const selection: PasteSelection = [
    [
      { row: 1, col: 1 },
      { row: 1, col: 2 },
    ],
    [
      { row: 2, col: 1 },
      { row: 2, col: 2 },
    ],
  ];

  applyPasteData(table, selection, 'a\tb\nc\td');

  assertEqual(
    calls,
    [
      { row: 1, col: 1, value: 'a' },
      { row: 1, col: 2, value: 'b' },
      { row: 2, col: 1, value: 'c' },
      { row: 2, col: 2, value: 'd' },
    ],
    'multi-cell selection keeps TSV grid paste behavior',
  );
}

{
  const { table, calls } = createTable();
  table.columns = [
    { originalData: { name: 'id', autoIncrement: 1 } as any },
    { originalData: { name: 'name' } as any },
  ];
  table.getRecordByCell = () => ({ CHAT2DB_ROW_NUMBER: 'new-row' });

  const selection: PasteSelection = [
    [
      { row: 3, col: 1 },
      { row: 3, col: 2 },
    ],
  ];

  applyPasteData(table, selection, '42\tAlice', {
    isCreateRow: (rowId) => rowId === 'new-row',
  });

  assertEqual(
    calls,
    [
      { row: 3, col: 1, value: GENERATED_VALUE },
      { row: 3, col: 2, value: 'Alice' },
    ],
    'multi-cell paste keeps generated sentinel for auto-increment columns on create rows',
  );
}

{
  const { table, calls } = createTable();
  table.columns = [{ originalData: { name: 'id', autoIncrement: 1 } as any }];
  table.getRecordByCell = () => ({ CHAT2DB_ROW_NUMBER: 'existing-row' });

  const selection: PasteSelection = [[{ row: 3, col: 1 }]];

  applyPasteData(table, selection, '42', {
    isCreateRow: (rowId) => rowId === 'new-row',
  });

  assertEqual(
    calls,
    [{ row: 3, col: 1, value: '42' }],
    'single-cell paste preserves auto-increment value on existing rows',
  );
}

console.log('onPasteData paste helper tests passed');
