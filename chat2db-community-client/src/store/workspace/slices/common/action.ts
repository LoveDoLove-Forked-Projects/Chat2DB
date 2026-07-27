import type { StateCreator } from 'zustand/vanilla';
import { WorkspaceStore } from '../../store';
import { CommonState } from './initialState';
import { useGlobalStore } from '@/store/global';
import jcefApi from '@/jcef';
import { randomLargeLong } from '@/utils';
import { WorkspaceTabType } from '@/constants';

export interface CommonAction {
  setCurrentConnectionDetails: (data: CommonState['currentConnectionDetails']) => void;
  setCurrentWorkspaceExtend: (workspaceExtend: CommonState['currentWorkspaceExtend']) => void;
  setCurrentWorkspaceGlobalExtend: (workspaceGlobalExtend: CommonState['currentWorkspaceGlobalExtend']) => void;
  readFile: (
    filePath: string,
    fileExtension?: string,
    context?: { rootToken?: string; relativePath?: string; previewFile?: boolean },
  ) => void;
}

export const createCommonAction: StateCreator<WorkspaceStore, [['zustand/devtools', never]], [], CommonAction> = (
  set,
  get,
) => ({
  setCurrentConnectionDetails: (data) => {
    set({ currentConnectionDetails: data });
  },
  setCurrentWorkspaceExtend: (workspaceExtend) => {
    set({ currentWorkspaceExtend: workspaceExtend });
  },
  setCurrentWorkspaceGlobalExtend: (workspaceGlobalExtend) => {
    set({ currentWorkspaceGlobalExtend: workspaceGlobalExtend });
  },
  readFile: (filePath, fileExtension, context) => {
    const contentPromise =
      context?.previewFile && context.rootToken
        ? jcefApi.readSqlDirectoryPreview({
            rootToken: context.rootToken,
            relativePath: context.relativePath || '',
          })
        : jcefApi.readFile(filePath).then((ddl) => ({ ddl }));

    contentPromise.then((fileContent) => {
      useGlobalStore.getState().setMainPageActiveTab({ page: 'workspace' });
      const workspaceTabList = get().workspaceTabList;
      const nextUniqueData = {
        filePath,
        fileExtension,
        fileRootToken: context?.rootToken,
        fileRelativePath: context?.relativePath,
        ...(context?.previewFile
          ? {
              filePreviewDataUrl: 'dataUrl' in fileContent ? fileContent.dataUrl : undefined,
              filePreviewMimeType: 'mimeType' in fileContent ? fileContent.mimeType : undefined,
              ddl: undefined,
            }
          : {
              ddl: 'ddl' in fileContent ? fileContent.ddl : '',
              filePreviewDataUrl: undefined,
              filePreviewMimeType: undefined,
            }),
      };
      if (workspaceTabList?.some((tab) => tab.uniqueData?.filePath === filePath)) {
        const tab: any = workspaceTabList.find((_tab) => _tab.uniqueData?.filePath === filePath);
        if (tab) {
          tab.uniqueData = {
            ...tab.uniqueData,
            ...nextUniqueData,
            fileExtension: fileExtension || tab.uniqueData?.fileExtension,
          };
          get().setActiveConsoleId(tab.id);
          get().setWorkspaceTabList([...workspaceTabList]);
        }
      } else {
        setTimeout(() => {
          get().addWorkspaceTab({
            id: randomLargeLong(),
            type: WorkspaceTabType.LocalSQLFile,
            title: filePath,
            uniqueData: nextUniqueData,
          });
        }, 0);
      }
    });
  },
});
