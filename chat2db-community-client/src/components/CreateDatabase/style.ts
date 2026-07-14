import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    monacoEditorBox: css`
      height: 200px;
      border: 1px solid ${token.colorBorder};
      border-radius: 4px;
      overflow: hidden;
      position: relative;
    `,
    errorBox: css`
      margin-top: 10px;
    `,
    previewBox: css`
      position: relative;
      display: flex;
      margin-bottom: 4px;
    `,
    previewText: css`
      background: ${token.colorBgBase};
      flex-shrink: 0;
      margin-right: 10px;
    `,
    previewLine: css`
      flex: 1;
      position: relative;
      &::after {
        position: absolute;
        left: 0;
        right: 0px;
        top: 50%;
        content: '';
        width: 100%;
        height: 1px;
        background: ${token.colorBorder};
        transform: translateY(-50%);
      }
    `,
    createDatabaseDom: css`
      padding-top: 20px;
    `,
  };
});
