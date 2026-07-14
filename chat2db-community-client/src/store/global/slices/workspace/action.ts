import { StateCreator } from 'zustand';
import { GlobalStore } from '../../store';
import { WorkspaceState } from './initialState';

export interface WorkspaceAction {
  createConsole: () => void;
  removeConsole: (id: string) => void;
}

export const createWorkspaceAction: StateCreator<GlobalStore, [['zustand/devtools', never]], [], WorkspaceAction> = (
  set,
  get,
) => ({
  createConsole: () => {
    // Implement the logic for creating a console
  },
  removeConsole: (id: string) => {
    // Implement the logic for removing the console
  },
});
