import { PersistOptions, devtools, persist } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { createWithEqualityFn } from 'zustand/traditional';
import { StateCreator } from 'zustand/vanilla';

interface XXXXState {

}

const initialState: XXXXState = {

};

export interface XXXXAction {

}

export type XXXXStore = XXXXState & XXXXAction;

export const createUserAction: StateCreator<XXXXStore, [['zustand/devtools', never]], [], XXXXAction> = (set, get) => ({

});

const createStore: StateCreator<XXXXStore, [['zustand/devtools', never]]> = (...parameters) => ({
  ...initialState,
  ...createUserAction(...parameters),
});

// local-storage Options
const persistOptions: PersistOptions<XXXXStore> = {
  name: 'Chat2DB_XXXX_Store',
};

export const useXXXXStore = createWithEqualityFn<XXXXStore>()(
  persist(
    devtools(createStore, {
      name: 'Chat2DB_User_Store',
    }),
    persistOptions
  ),
  shallow,
);

// Clean store
export const clearXXXXStore = () => {
  useXXXXStore.setState(initialState);
};
