import { WorkspaceStore } from '../../store';
import { StateCreator } from 'zustand';
import { AIState } from './initialState';

export interface AIAction {
  addDefaultDataCollectionList: (param: {
    type: 'dashboard' | 'console' | 'chat';
    id: number;
    value: number;
  }) => void;
  increaseCreateAiDataCollectionTipsCount: () => void;
  openConsoleAiInput: (params: AIState['consoleAiInputParams']) => void;
  clearConsoleAiInputParams: () => void;
}

export const createAIAction: StateCreator<WorkspaceStore, [['zustand/devtools', never]], [], AIAction> = (
  set,
  get,
) => ({
  openConsoleAiInput: (consoleAiInputParams) => { 
    set({ consoleAiInputParams });
  },
  clearConsoleAiInputParams: () => { 
    set({ consoleAiInputParams: false });
  },
  addDefaultDataCollectionList: ({ type, id, value }) => {
    try {
      const newDefaultDataCollectionList = get().defaultDataCollectionList;
      newDefaultDataCollectionList[type][id] = value;
      set({
        defaultDataCollectionList: newDefaultDataCollectionList,
      });
    }
    catch {
      const newDefaultDataCollectionList = {
        dashboard: {},
        console: {},
        chat: {},
        [type]: {
          [id]: value,
        }
      }
      set({
        defaultDataCollectionList: newDefaultDataCollectionList,
      });
      console.log('error');
    }
  },
  increaseCreateAiDataCollectionTipsCount: () => { 
    set((state) => ({ createAiDataCollectionTipsCount: state.createAiDataCollectionTipsCount + 1 }));
  }
});
