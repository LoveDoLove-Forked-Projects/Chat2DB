import { LARGE_CELL_ERROR_MESSAGE } from './largeCellValue';

// 后端把 i18n key 作为错误码和错误消息返回（形如 "largeCellValue.tokenForbidden : no message."），
// 这里集中维护「后端 key → 前端文案 key」的映射，供展示层翻译。
type LargeCellMessageKey = (typeof LARGE_CELL_ERROR_MESSAGE)[keyof typeof LARGE_CELL_ERROR_MESSAGE];

export const LARGE_CELL_MESSAGE_BY_CODE: Record<string, LargeCellMessageKey> = {
  'largeCellValue.tokenExpired': LARGE_CELL_ERROR_MESSAGE.TOKEN_EXPIRED,
  'largeCellValue.tokenForbidden': LARGE_CELL_ERROR_MESSAGE.TOKEN_FORBIDDEN,
  'largeCellValue.tokenRequired': LARGE_CELL_ERROR_MESSAGE.TOKEN_REQUIRED,
  'largeCellValue.rowNotFound': LARGE_CELL_ERROR_MESSAGE.ROW_NOT_FOUND,
  'largeCellValue.rowLocatorRequired': LARGE_CELL_ERROR_MESSAGE.ROW_LOCATOR_REQUIRED,
  'largeCellValue.fullValueUnsupported': LARGE_CELL_ERROR_MESSAGE.FULL_VALUE_UNSUPPORTED,
  'largeCellValue.readFailed': LARGE_CELL_ERROR_MESSAGE.LOAD_FAILED,
  'largeCellValue.downloadFailed': LARGE_CELL_ERROR_MESSAGE.DOWNLOAD_FAILED,
  'largeCellValue.partialPreviewEditRejected': LARGE_CELL_ERROR_MESSAGE.PARTIAL_PREVIEW_EDIT_REJECTED,
  'largeCellValue.unsupportedFormat': LARGE_CELL_ERROR_MESSAGE.UNSUPPORTED_FORMAT,
};

export function getLargeCellMessageKey(
  message?: string | null,
): LargeCellMessageKey | undefined {
  if (!message) {
    return undefined;
  }
  const code = String(message).split(':')[0].trim();
  return LARGE_CELL_MESSAGE_BY_CODE[code];
}
