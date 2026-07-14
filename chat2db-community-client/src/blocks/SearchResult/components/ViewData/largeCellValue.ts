import { ILargeCellChunk, LargeValueType } from '@/typings/database';
import i18n from '@/i18n';

export type LargeCellBinaryFormat = 'hex' | 'base64';
export type LargeCellRequestFormat = 'text' | 'hex' | 'base64';
export type LargeCellDownloadFormat = 'raw' | 'text' | 'hex';

export const LARGE_VALUE_TYPE = {
  TEXT: 'TEXT',
  JSON: 'JSON',
  BINARY: 'BINARY',
  IMAGE: 'IMAGE',
  UNKNOWN: 'UNKNOWN',
} as const satisfies Record<LargeValueType, LargeValueType>;

export const LARGE_CELL_BINARY_FORMAT = {
  HEX: 'hex',
  BASE64: 'base64',
} as const;

export const LARGE_CELL_REQUEST_FORMAT = {
  TEXT: 'text',
  HEX: 'hex',
  BASE64: 'base64',
} as const;

export const LARGE_CELL_DOWNLOAD_FORMAT = {
  RAW: 'raw',
  TEXT: 'text',
  HEX: 'hex',
} as const;

export const LARGE_CELL_ERROR_CODE = {
  TOKEN_EXPIRED: 'largeCellValue.tokenExpired',
  FULL_VALUE_UNSUPPORTED: 'largeCellValue.fullValueUnsupported',
} as const;

export const LARGE_CELL_ERROR_MESSAGE = {
  TOKEN_EXPIRED: 'common.largeCellValue.error.tokenExpired',
  LOAD_FAILED: 'common.largeCellValue.error.loadFailed',
  DOWNLOAD_FAILED: 'common.largeCellValue.error.downloadFailed',
  FULL_VALUE_UNSUPPORTED: 'common.largeCellValue.error.fullValueUnsupported',
} as const;

const LARGE_CELL_MESSAGE_BY_CODE = {
  [LARGE_CELL_ERROR_CODE.FULL_VALUE_UNSUPPORTED]: LARGE_CELL_ERROR_MESSAGE.FULL_VALUE_UNSUPPORTED,
} as const;

export const LARGE_CELL_TEXT_EDITOR_LIMIT = 10 * 1024 * 1024;
export const LARGE_CELL_BINARY_EDITOR_LIMIT = 50 * 1024 * 1024;
export const LARGE_CELL_MAX_CHUNK_SIZE = 256 * 1024;

export function isBinaryDisplayMode(displayMode?: LargeValueType | string) {
  return displayMode === LARGE_VALUE_TYPE.BINARY || displayMode === LARGE_VALUE_TYPE.IMAGE;
}

export function isImageDisplayMode(displayMode?: LargeValueType | string) {
  return displayMode === LARGE_VALUE_TYPE.IMAGE;
}

export function isJsonDisplayMode(displayMode?: LargeValueType | string) {
  return displayMode === LARGE_VALUE_TYPE.JSON;
}

export function isTextDownloadMode(displayMode?: LargeValueType | string) {
  return displayMode === LARGE_VALUE_TYPE.TEXT || displayMode === LARGE_VALUE_TYPE.JSON;
}

export function getInitialBinaryFormat(valueType?: LargeValueType | string): LargeCellBinaryFormat {
  return isImageDisplayMode(valueType) ? LARGE_CELL_BINARY_FORMAT.BASE64 : LARGE_CELL_BINARY_FORMAT.HEX;
}

export function getEditorLanguage(displayMode?: LargeValueType | string) {
  return isJsonDisplayMode(displayMode) ? 'json' : 'plaintext';
}

export function getLargeCellEditorLimit(displayMode?: LargeValueType | string) {
  return isBinaryDisplayMode(displayMode) ? LARGE_CELL_BINARY_EDITOR_LIMIT : LARGE_CELL_TEXT_EDITOR_LIMIT;
}

export function getLargeCellRequestFormat(
  displayMode: LargeValueType | string | undefined,
  binaryFormat: LargeCellBinaryFormat,
): LargeCellRequestFormat {
  return isBinaryDisplayMode(displayMode) ? binaryFormat : LARGE_CELL_REQUEST_FORMAT.TEXT;
}

export function getLargeCellDownloadFormat(displayMode?: LargeValueType | string): LargeCellDownloadFormat {
  return isTextDownloadMode(displayMode) ? LARGE_CELL_DOWNLOAD_FORMAT.TEXT : LARGE_CELL_DOWNLOAD_FORMAT.RAW;
}

export function getLargeCellLoadedBytes(chunks: ILargeCellChunk[]) {
  return chunks.reduce((sum, chunk) => sum + Math.max(0, chunk.nextOffset - chunk.offset), 0);
}

export function getLargeCellDisplayValue(chunks: ILargeCellChunk[], preview?: string | null) {
  const loaded = chunks.map((chunk) => chunk.value || '').join('');
  return loaded || preview || '';
}

export function getNextLargeCellChunkLimit(params: {
  loadedSize: number;
  editorLimit: number;
  binaryFormat: LargeCellBinaryFormat;
}) {
  const remaining = params.editorLimit - params.loadedSize;
  if (remaining <= 0) {
    return 0;
  }
  if (params.binaryFormat === LARGE_CELL_BINARY_FORMAT.BASE64 && remaining < 3) {
    return 0;
  }
  const nextLimit = Math.min(LARGE_CELL_MAX_CHUNK_SIZE, remaining);
  if (params.binaryFormat === LARGE_CELL_BINARY_FORMAT.BASE64) {
    return nextLimit - (nextLimit % 3);
  }
  return nextLimit;
}

export function canSubmitLargeCellEdit(params: {
  isLargeValue: boolean;
  displayMode?: LargeValueType | string;
  eof?: boolean;
  loadedSize: number;
  editorLimit: number;
}) {
  if (!params.isLargeValue) {
    return true;
  }
  return !isBinaryDisplayMode(params.displayMode) && !!params.eof && params.loadedSize <= params.editorLimit;
}

export function getLargeCellImagePreviewSrc(params: {
  displayMode?: LargeValueType | string;
  chunks: ILargeCellChunk[];
  eof?: boolean;
  contentType?: string;
}) {
  if (!isImageDisplayMode(params.displayMode) || !params.eof) {
    return '';
  }
  const hasOnlyBase64Chunks = params.chunks.length > 0
    && params.chunks.every((chunk) => chunk.encoding === LARGE_CELL_REQUEST_FORMAT.BASE64);
  if (!hasOnlyBase64Chunks) {
    return '';
  }
  const mimeType = params.contentType?.startsWith('image/') && params.contentType !== 'image/*'
    ? params.contentType
    : 'image/png';
  return `data:${mimeType};base64,${getLargeCellDisplayValue(params.chunks)}`;
}

export function isLargeCellTokenExpiredError(error: any) {
  return error?.errorCode === LARGE_CELL_ERROR_CODE.TOKEN_EXPIRED;
}

export function getLargeCellDisplayMessage(message?: string | null) {
  if (!message) {
    return '';
  }
  const messageKey = LARGE_CELL_MESSAGE_BY_CODE[message as keyof typeof LARGE_CELL_MESSAGE_BY_CODE];
  return messageKey ? i18n(messageKey) : message;
}

export function getLargeCellErrorMessage(
  error: any,
  fallback: (typeof LARGE_CELL_ERROR_MESSAGE)[keyof typeof LARGE_CELL_ERROR_MESSAGE],
) {
  if (isLargeCellTokenExpiredError(error)) {
    return i18n(LARGE_CELL_ERROR_MESSAGE.TOKEN_EXPIRED);
  }
  return getLargeCellDisplayMessage(error?.errorMessage || error?.message) || i18n(fallback);
}
