import zh from '@/i18n/zh-CN/common';
import en from '@/i18n/en-US/common';
import { LARGE_CELL_ERROR_CODE, LARGE_CELL_ERROR_MESSAGE } from './largeCellValue';
import { getLargeCellMessageKey } from './largeCellValueErrorMap';

function assertEqual(actual: any, expected: any, message: string) {
  if (actual !== expected) {
    throw new Error(`${message}: expected ${JSON.stringify(expected)}, got ${JSON.stringify(actual)}`);
  }
}

// 1) 后端所有错误码都必须有映射，否则会把 i18n key 直接展示给用户。
for (const code of Object.values(LARGE_CELL_ERROR_CODE)) {
  const messageKey = getLargeCellMessageKey(code);
  if (!messageKey) {
    throw new Error(`missing message mapping for ${code}`);
  }
  if (!(messageKey in en) || !(messageKey in zh)) {
    throw new Error(`i18n string missing for ${messageKey}`);
  }
}

// 2) 后端实际下发的 errorMessage 形如 "largeCellValue.tokenForbidden : no message."，也要能解析出映射。
assertEqual(
  getLargeCellMessageKey('largeCellValue.tokenForbidden : no message.'),
  LARGE_CELL_ERROR_MESSAGE.TOKEN_FORBIDDEN,
  'raw backend message resolves to a message key',
);
assertEqual(
  getLargeCellMessageKey('  largeCellValue.tokenExpired  '),
  LARGE_CELL_ERROR_MESSAGE.TOKEN_EXPIRED,
  'trimmed key resolves as well',
);

// 3) 未知错误码/文案不映射，交给上层走 fallback。
assertEqual(getLargeCellMessageKey(undefined), undefined, 'undefined has no mapping');
assertEqual(getLargeCellMessageKey('common.paramError'), undefined, 'unrelated code has no mapping');
assertEqual(getLargeCellMessageKey('some custom backend text'), undefined, 'free text has no mapping');

// 4) 映射表覆盖后端契约：后端当前抛出的 10 个 key 全部要有对应文案。
const BACKEND_KEYS = [
  'largeCellValue.tokenExpired',
  'largeCellValue.tokenForbidden',
  'largeCellValue.tokenRequired',
  'largeCellValue.rowNotFound',
  'largeCellValue.rowLocatorRequired',
  'largeCellValue.fullValueUnsupported',
  'largeCellValue.readFailed',
  'largeCellValue.downloadFailed',
  'largeCellValue.partialPreviewEditRejected',
  'largeCellValue.unsupportedFormat',
];
for (const key of BACKEND_KEYS) {
  const messageKey = getLargeCellMessageKey(key);
  if (!messageKey) {
    throw new Error(`backend key without mapping: ${key}`);
  }
}

console.log('largeCellValueMessage tests passed');
