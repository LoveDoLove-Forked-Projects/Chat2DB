import {
  canSubmitLargeCellEdit,
  LARGE_CELL_BINARY_FORMAT,
  LARGE_CELL_REQUEST_FORMAT,
  LARGE_VALUE_TYPE,
  getLargeCellDisplayValue,
  getLargeCellEditorLimit,
  getLargeCellImagePreviewSrc,
  getLargeCellLoadedBytes,
  getLargeCellRequestFormat,
  getNextLargeCellChunkLimit,
} from './largeCellValue';

function assertEqual(actual: any, expected: any, message: string) {
  const actualJson = JSON.stringify(actual);
  const expectedJson = JSON.stringify(expected);
  if (actualJson !== expectedJson) {
    throw new Error(`${message}: expected ${expectedJson}, got ${actualJson}`);
  }
}

assertEqual(
  getLargeCellRequestFormat(LARGE_VALUE_TYPE.TEXT, LARGE_CELL_BINARY_FORMAT.HEX),
  LARGE_CELL_REQUEST_FORMAT.TEXT,
  'text values request text chunks',
);
assertEqual(
  getLargeCellRequestFormat(LARGE_VALUE_TYPE.JSON, LARGE_CELL_BINARY_FORMAT.BASE64),
  LARGE_CELL_REQUEST_FORMAT.TEXT,
  'json values request text chunks',
);
assertEqual(
  getLargeCellRequestFormat(LARGE_VALUE_TYPE.BINARY, LARGE_CELL_BINARY_FORMAT.HEX),
  LARGE_CELL_REQUEST_FORMAT.HEX,
  'binary values can request hex chunks',
);
assertEqual(
  getLargeCellRequestFormat(LARGE_VALUE_TYPE.IMAGE, LARGE_CELL_BINARY_FORMAT.BASE64),
  LARGE_CELL_REQUEST_FORMAT.BASE64,
  'images can request base64 chunks',
);

assertEqual(
  getLargeCellLoadedBytes([
    { value: 'abc', offset: 0, nextOffset: 3, eof: false },
    { value: 'def', offset: 3, nextOffset: 6, eof: true },
  ]),
  6,
  'loaded byte count follows chunk offsets',
);

assertEqual(
  getLargeCellDisplayValue([{ value: 'abc', offset: 0, nextOffset: 3, eof: false }], '[BLOB] 20.00 MB'),
  'abc',
  'loaded chunks replace preview text',
);

assertEqual(getLargeCellEditorLimit(LARGE_VALUE_TYPE.TEXT), 10 * 1024 * 1024, 'text editor limit is 10 MB');
assertEqual(getLargeCellEditorLimit(LARGE_VALUE_TYPE.BINARY), 50 * 1024 * 1024, 'binary editor limit is 50 MB');

assertEqual(
  getNextLargeCellChunkLimit({
    loadedSize: 50 * 1024 * 1024 - 2,
    editorLimit: 50 * 1024 * 1024,
    binaryFormat: LARGE_CELL_BINARY_FORMAT.BASE64,
  }),
  0,
  'base64 load-all stops when the remaining byte budget cannot form a 3-byte group',
);

assertEqual(
  getNextLargeCellChunkLimit({ loadedSize: 0, editorLimit: 10, binaryFormat: LARGE_CELL_BINARY_FORMAT.BASE64 }),
  9,
  'base64 chunk limits align to a 3-byte boundary',
);

assertEqual(
  canSubmitLargeCellEdit({
    isLargeValue: true,
    displayMode: LARGE_VALUE_TYPE.TEXT,
    eof: false,
    loadedSize: 1024,
    editorLimit: 10 * 1024,
  }),
  false,
  'partial text large values cannot be submitted',
);

assertEqual(
  canSubmitLargeCellEdit({
    isLargeValue: true,
    displayMode: LARGE_VALUE_TYPE.BINARY,
    eof: true,
    loadedSize: 1024,
    editorLimit: 10 * 1024,
  }),
  false,
  'binary large values cannot be submitted through Monaco',
);

assertEqual(
  canSubmitLargeCellEdit({
    isLargeValue: false,
    displayMode: LARGE_VALUE_TYPE.TEXT,
    eof: false,
    loadedSize: 0,
    editorLimit: 10 * 1024,
  }),
  true,
  'normal small cells keep existing edit behavior',
);

assertEqual(
  getLargeCellImagePreviewSrc({
    displayMode: LARGE_VALUE_TYPE.IMAGE,
    eof: true,
    contentType: 'image/png',
    chunks: [{ value: 'aW1hZ2U=', offset: 0, nextOffset: 5, eof: true, encoding: LARGE_CELL_REQUEST_FORMAT.BASE64 }],
  }),
  'data:image/png;base64,aW1hZ2U=',
  'complete base64 image chunks produce a preview data URL',
);

assertEqual(
  getLargeCellImagePreviewSrc({
    displayMode: LARGE_VALUE_TYPE.IMAGE,
    eof: false,
    chunks: [{ value: 'aW1h', offset: 0, nextOffset: 3, eof: false, encoding: LARGE_CELL_REQUEST_FORMAT.BASE64 }],
  }),
  '',
  'partial image chunks do not produce a preview',
);

console.log('large cell viewer helper tests passed');
