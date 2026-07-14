import { devtools } from 'zustand/middleware';
import { DashboardState, initialState } from './initialState';
import { CommonAction, createCommonAction } from './slices/common/action';
import { StateCreator, create } from 'zustand';
import { SettingAction, createSettingAction } from './slices/setting/action';

export type DashboardAction = CommonAction & SettingAction;
export type DashboardStore = DashboardState & DashboardAction;

const createStore: StateCreator<DashboardStore, [['zustand/devtools', never]]> = (...parameters) => ({
  ...initialState,
  ...createCommonAction(...parameters),
  ...createSettingAction(...parameters),
});

export const useDashboardStore = create(
  devtools(createStore, {
    name: 'Chat2DB_Dashboard_Store',
  }),
);

export const clearDashboardStore = () => {
  useDashboardStore.setState(initialState);
};
