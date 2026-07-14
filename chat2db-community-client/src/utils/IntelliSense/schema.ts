import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import i18n from '@/i18n';
import { SORT_TEXT } from '@/components/MonacoEditor/providers/completionProvider';

export const resetSenseSchema = () => {
  intelliSenseSchema.dispose();
};

let intelliSenseSchema = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: () => {
    return { suggestions: [] };
  },
});

const registerIntelliSenseSchema = (schemaList: Array<{ name: string }>) => {
  resetSenseSchema();
  intelliSenseSchema = monaco.languages.registerCompletionItemProvider('sql', {
    // triggerCharacters: [' ', '.'],
    provideCompletionItems: (model, position) => {
      return {
        suggestions: (schemaList || []).map(({ name }) => ({
          label: {
            label: name,
            description: 'Schema',
          },
          insertText: name,
          sortText: SORT_TEXT.SCHEMA,
          kind: monaco.languages.CompletionItemKind.Property,
        })),
      };
    },
  });
};

export { intelliSenseSchema, registerIntelliSenseSchema };
