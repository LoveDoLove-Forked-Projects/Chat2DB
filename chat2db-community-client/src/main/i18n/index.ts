import { app } from 'electron';
import zh_CN from './zh-CN';
import en_US from './en-US';
import ja_JP from './ja-JP';

const locale = {
  'zh-CN': zh_CN,
  'en-US': en_US,
  'ja': ja_JP,
};

function i18n(key: keyof typeof en_US, ...args: any[]) {
  let currentLang = app.getLocale();
  if (!['zh-CN', 'ja'].includes(currentLang)) {
    currentLang = 'en-US';
  }
  
  const langSet: Record<string, string> = locale[currentLang];

  let result = langSet[key];
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

export default i18n;
export { i18n };
