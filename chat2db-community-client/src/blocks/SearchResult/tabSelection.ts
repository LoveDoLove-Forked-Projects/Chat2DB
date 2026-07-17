import { IManageResultData } from '@/typings';

export const CONSOLE_TAB_ID = 'execution-console';
export const ABSTRACT_TAB_ID = 'abstract';
export const MESSAGES_TAB_ID = 'messages';

export function getResultIdentity(item: IManageResultData) {
  return item.uuid || item.extra?.resultKey || item.extra?.historyKey;
}

export function hasTabularResult(item: IManageResultData) {
  return (item.headerList?.length || 0) > 1;
}

export function hasLegacyResultTab(item: IManageResultData) {
  return hasTabularResult(item) || !item.success;
}

export function shouldOpenLegacyMessagesTab(item: IManageResultData) {
  return item.extra?.messageOnly || (!!item.extra?.messages?.length && !hasLegacyResultTab(item));
}

export function getPreferredActiveTabId(item: IManageResultData | undefined, consoleMode: boolean) {
  if (!item) {
    return consoleMode ? CONSOLE_TAB_ID : '';
  }
  if (!consoleMode && shouldOpenLegacyMessagesTab(item)) {
    return MESSAGES_TAB_ID;
  }
  if (consoleMode && !item.success) {
    return CONSOLE_TAB_ID;
  }
  if (consoleMode ? hasTabularResult(item) : hasLegacyResultTab(item)) {
    return item.uuid || (consoleMode ? CONSOLE_TAB_ID : ABSTRACT_TAB_ID);
  }
  return consoleMode ? CONSOLE_TAB_ID : ABSTRACT_TAB_ID;
}

export function resolveAvailableActiveTabId(currentActiveTabId: string, availableTabIds: string[]) {
  if (!availableTabIds.length) {
    return '';
  }
  if (currentActiveTabId && availableTabIds.includes(currentActiveTabId)) {
    return currentActiveTabId;
  }
  return availableTabIds[0];
}
