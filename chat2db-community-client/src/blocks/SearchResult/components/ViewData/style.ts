import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    container: css``,
    monacoEditor: css`
      border: 1px solid ${token.colorBorder};
      border-radius: 4px;
      overflow: hidden;
      height: 60vh;
      display: flex;
      flex-direction: column;
    `,
    toolbar: css`
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 8px;
      padding: 4px 8px;
      background-color: ${token.colorBgContainer};
      border-bottom: 1px solid ${token.colorBorder};
    `,
    alert: css`
      border-radius: 0;
      border-left: 0;
      border-right: 0;
    `,
    meta: css`
      flex-shrink: 0;
      padding: 4px 8px;
      color: ${token.colorTextSecondary};
      border-bottom: 1px solid ${token.colorBorderSecondary};
      font-size: 12px;
    `,
    imagePreview: css`
      flex-shrink: 0;
      max-height: 180px;
      padding: 8px;
      overflow: auto;
      border-bottom: 1px solid ${token.colorBorderSecondary};
      background: ${token.colorFillQuaternary};

      img {
        display: block;
        max-width: 100%;
        max-height: 160px;
        object-fit: contain;
      }
    `,
    editorContainer: css`
      flex: 1;
      height: 0px;
    `,
  };
});
