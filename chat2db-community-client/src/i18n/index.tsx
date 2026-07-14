import { runtimeEditionConfig } from '@/constants/runtimeEdition';
import { LangType } from '@/constants/settings';
import { useGlobalStore } from '@/store/global';
import { getUserComputerLanguage } from '@/utils';
import React, { Fragment } from 'react';
import en_US from './en-US';
import ja_JP from './ja-JP';
import zh_CN from './zh-CN';

const locale = {
  [LangType.EN_US]: en_US,
  [LangType.ZH_CN]: zh_CN,
  [LangType.JA_JP]: ja_JP,
};

function i18n(key: keyof typeof en_US, ...args: any[]) {
  const currentLang: LangType = useGlobalStore.getState().baseSetting.language;
  let langSet: Record<string, string> = locale[currentLang] || locale[getUserComputerLanguage()];
  const fallbackLangSet = locale[LangType.EN_US];
  const isCN = useGlobalStore.getState().appConfig.isCN;
  // Force English for users outside China.
  if (runtimeEditionConfig.languageRegionRestricted && !isCN && currentLang === LangType.ZH_CN) {
    langSet = locale[LangType.EN_US];
  }
  let result = langSet[key] ?? fallbackLangSet[key];
  if (result === undefined) {
    return `[${key}]`;
  } else {
    args.forEach((arg, i) => {
      result = result.replace(new RegExp(`\\{${i + 1}\\}`, 'g'), arg);
    });
    if (args.length) {
      result = result.replace(/\{(.+?)\|(.+?)\}/g, (_, singular, plural) => {
        const n = args[0];
        return n == 1 ? singular : plural;
      });
    }
    return result;
  }
}

function i18nElement(key: keyof typeof en_US, ...args: React.ReactNode[]) {
  const currentLang: LangType = useGlobalStore.getState().baseSetting.language;
  let langSet: Record<string, string> = locale[currentLang] || locale[getUserComputerLanguage()];
  const fallbackLangSet = locale[LangType.EN_US];
  const isCN = useGlobalStore.getState().appConfig.isCN;
  // Force English for users outside China.
  if (runtimeEditionConfig.languageRegionRestricted && !isCN && currentLang === LangType.ZH_CN) {
    langSet = locale[LangType.EN_US];
  }
  const str = langSet[key] ?? fallbackLangSet[key];
  if (str === undefined) {
    return `[${key}]`;
  } else {
    const result: React.ReactNode[] = [];
    str.split(/(\{\d\})/).forEach((item, i) => {
      if (/^\{\d\}$/.test(item)) {
        result.push(<Fragment key={i}>{args[parseInt(item.substring(1, item.length - 1)) - 1]}</Fragment>);
      } else {
        result.push(
          <Fragment key={i}>
            {item.replace(/\{(.+?)\|(.+?)\}/g, (_, singular, plural) => {
              const n = args[0];
              return n == 1 ? singular : plural;
            })}
          </Fragment>,
        );
      }
    });
    return result;
  }
}

export default i18n;
export { i18n, i18nElement };
export type { en_US };
