import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    title: css`
      font-size: 14px;
      margin-bottom: 10px;
      i {
        margin-left: 10px;
        color: ${token.colorPrimary};
      }
    `,
    baseSettingBox: css`
      display: flex;
      flex-direction: column;
      gap: 30px;
    `,
    backgroundList: css`
      display: flex;
      gap: 20px;
    `,
    themeItemBox: css`
      display: flex;
      flex-direction: column;
      align-items: center;
    `,
    themeBox: css`
      width: 80px;
      height: 52px;
      margin-bottom: 6px;
      overflow: hidden;
      border-radius: 4px;
      background-size: cover;
      background-repeat: no-repeat;
      cursor: pointer;
    `,
    activeThemeBox: css`
      box-shadow: inset 0 0 0 2px ${token.colorPrimary};
      border-radius: 4px;
    `,
    themeName: css`
      text-align: center;
      font-size: 12px;
    `,
    primaryColorList: css`
      display: flex;
      flex-wrap: wrap;
      margin-bottom: 20px;
    `,
    customFontBox: css`
      display: flex;
      gap: 20px;
    `,
  };
});
