import * as monaco from 'monaco-editor';
import type { DatabaseTypeCode } from '../../../constants/common';
import { canUseBackendCompletion, canUseBackendEditorHints } from '@/utils/databaseJudgments';

// Frontend routing capability: these databases consume backend completion results
// directly, so local snippets/providers should stay out of the completion list.
const backendCompletionModels = new WeakSet<monaco.editor.ITextModel>();

export function isBackendCompletionDatabaseType(databaseType: DatabaseTypeCode | null | undefined): boolean {
  return canUseBackendCompletion(databaseType);
}

export function isBackendEditorHintsDatabaseType(databaseType: DatabaseTypeCode | null | undefined): boolean {
  return canUseBackendEditorHints(databaseType);
}

export function setBackendCompletionModel(
  model: monaco.editor.ITextModel | null | undefined,
  enabled: boolean,
): void {
  if (!model) {
    return;
  }
  if (enabled) {
    backendCompletionModels.add(model);
    return;
  }
  backendCompletionModels.delete(model);
}

export function isBackendCompletionModel(model: monaco.editor.ITextModel | null | undefined): boolean {
  return !!model && backendCompletionModels.has(model);
}
