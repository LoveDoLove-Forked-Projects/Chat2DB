import { runtimeEditionConfig } from '@/constants/runtimeEdition';
import { del, get, set } from 'idb-keyval';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { createWithEqualityFn } from 'zustand/traditional';
import { StateCreator } from 'zustand/vanilla';

interface IndexDBState {
  indexDB: any;
}

const initialState: IndexDBState = {
  indexDB: null,
};

export interface IndexDBAction {
  setValue: (key: string, value: any) => Promise<void>;
  getValue: (key: string) => Promise<any>;
  deleteValue: (key: string) => Promise<void>;
}

export type IndexDBStore = IndexDBState & IndexDBAction;

export const createIndexDBAction: StateCreator<
  IndexDBStore,
  [['zustand/devtools', never]],
  [],
  IndexDBAction
> = () => ({
  setValue: (key, value) => {
    return set(`${runtimeEditionConfig.indexedDbKeyPrefix}:${key}`, value);
  },
  getValue: (key) => {
    return get(`${runtimeEditionConfig.indexedDbKeyPrefix}:${key}`);
  },
  deleteValue: (key) => {
    return del(`${runtimeEditionConfig.indexedDbKeyPrefix}:${key}`);
  },
});

const createStore: StateCreator<IndexDBStore, [['zustand/devtools', never]]> = (...parameters) => ({
  ...initialState,
  ...createIndexDBAction(...parameters),
});

export const useIndexDBStore = createWithEqualityFn<IndexDBStore>()(
  // persist(
  devtools(createStore, {
    name: 'Chat2DB_indexDB_Store',
  }),
  // ),
  shallow,
);

// Clean store
export const clearIndexDBStore = () => {
  useIndexDBStore.setState(initialState);
};
