import type { StateCreator } from 'zustand/vanilla';
import { GlobalStore } from '../../store';
import { CommonState } from './initialState';
import { isDesktop, isHashHistoryEnv } from '@/utils/env';

export interface CommonAction {
  /**
   * APP Service status
   */
  setServiceStatus: (data: CommonState['serviceStatus']) => void;
  /**
   * Set APP title bar right component
   */
  setAppTitleBarRightComponent: (data: CommonState['appTitleBarRightComponent']) => void;
  /**
   * Set main page active tab
   */
  setMainPageActiveTab: ({page, pathName, searchParams}: {page: string, pathName?: string, searchParams?: Record<string, string>}) => void;
  /**
   * set focused content
   */
  setFocusedContent: (data: CommonState['focusedContent']) => void;
  /**
   * set systemErrorMessage
   */
  setSystemErrorMessage: (data: CommonState['systemErrorMessageApi']) => void;
  setSettingPageActiveTab: (data: CommonState['settingPageActiveTab']) => void;
  setAppUrlConfig: (data: CommonState['appUrlConfig']) => void;
  // Open the unified confirmation box
  openUnifiedConfirmationModal: (data: CommonState['unifiedConfirmationModalInfo']) => void;
  // Set whether to embed iframe
  setIsEmbedIframe: (data: CommonState['isEmbedIframe']) => void;
}

export const createCommonAction: StateCreator<GlobalStore, [['zustand/devtools', never]], [], CommonAction> = (
  set,
  get,
) => ({
  setServiceStatus: (data) => {
    set({
      serviceStatus: data,
    });
  },
  setAppTitleBarRightComponent: (data) => {
    set({
      appTitleBarRightComponent: data,
    });
  },
  setMainPageActiveTab: ({page, pathName, searchParams}: {page: string, pathName?: string, searchParams?: Record<string, string>}) => {
    // When carrying searchParams or pathName, the URL needs to be updated even if the page is the same (such as switching sessions)
    if (page === get().mainPageActiveTab && !searchParams && !pathName) return;

    if (page === 'connections') {
      page = 'workspace';
    }

    let url: any = null;
    if (isHashHistoryEnv || isDesktop) {
      url = new URL(window.location.href);
      const hashPath = pathName || `/${page}`;
      url.hash = hashPath.startsWith('#') ? hashPath : `#${hashPath.startsWith('/') ? hashPath : `/${hashPath}`}`;
      if (url.protocol !== 'file:') {
        url.pathname = '/';
      }
    } else {
      url = new URL(window.location.href);
      url.pathname = pathName || page;
    }

    // Clean up application-level searchParams and write new ones
    url.searchParams.delete('sessionId');
    if (searchParams) {
      Object.entries(searchParams).forEach(([key, value]) => {
        if (value) {
          url.searchParams.set(key, value);
        }
      });
    }

    window.history.pushState({}, '', url.toString());
    set({
      mainPageActiveTab: page,
    });
  },
  setFocusedContent: (data) => {
    set({
      focusedContent: data,
    });
  },
  setSystemErrorMessage: (data) => {
    set({
      systemErrorMessageApi: data,
    });
  },
  setSettingPageActiveTab: (data) => {
    // If true is passed in, set the first tab to open.
    set({
      settingPageActiveTab: data,
    });
  },
  setAppUrlConfig: (data) => {
    set({
      appUrlConfig: data,
    });
  },
  openUnifiedConfirmationModal: (data) => {
    set({
      unifiedConfirmationModalInfo: data,
    });
  },
  setIsEmbedIframe: (data) => {
    set({
      isEmbedIframe: data,
    });
  },
});
