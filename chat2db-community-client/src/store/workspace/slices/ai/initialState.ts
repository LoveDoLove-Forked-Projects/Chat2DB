import { QuestionType } from '@/constants/chat';

export const DEFAULT_POSITION = { left: -999, top: -999 };

export interface DBParams {
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
  tableName?: string;
}

export interface defaultDataCollectionItem {
  [key: number]: number;
}

export interface AIState {
  consoleAiInputParams:
    | {
        questionType: QuestionType;
        /**
         * Only needs to be passed when the current Console information is not needed
         * For example, CRUD generation generates test data
         */
        messageContent?: any;
        matchTable?: boolean;
        value: string;
      }
    | false;
  // Store the default AI dataset value.
  defaultDataCollectionList: {
    dashboard: defaultDataCollectionItem;
    console: defaultDataCollectionItem;
    chat: defaultDataCollectionItem;
  };
  createAiDataCollectionTipsCount: number;
}

export const initAIState: AIState = {
  consoleAiInputParams: false,
  defaultDataCollectionList: {
    dashboard: {},
    console: {},
    chat: {},
  },
  createAiDataCollectionTipsCount: 0,
};
