import type { IResultSetEditorOption } from '@/typings/database';
import { SelectEditor, type SelectEditorTheme } from '@/blocks/CanvasTable/editor/SelectIEditor';

const RESULT_SET_EDITOR_MAP: Record<string, string> = {
  DATE: 'custom-date-editor',
  TIME: 'custom-time-editor',
  DATETIME: 'custom-datetime-editor',
  TIMESTAMP: 'custom-timestamp-editor',
};

export const resolveResultSetEditor = (
  editorType?: string,
  editorOptions?: readonly IResultSetEditorOption[],
  theme: SelectEditorTheme = {},
) => {
  if (editorType === 'SELECT' && editorOptions?.length) {
    const editor = new SelectEditor(editorOptions, theme);
    if (editor.options.length) {
      return editor;
    }
  }
  return editorType ? RESULT_SET_EDITOR_MAP[editorType] || 'custom-input-editor' : 'custom-input-editor';
};
