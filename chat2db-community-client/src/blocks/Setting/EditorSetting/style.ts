import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    container: css`
      display: flex;
      gap: 20px;
      width: 100%;
    `,
    formWrapper: css`
      flex: 1;
      min-width: 240px;
    `,
    editorWrapper: css`
      /* max-height: calc(100vh - 120px); */
      /* max-height: calc(100vh - 60px); */
      /* border: 1px solid ${token.colorBorderSecondary}; */
      /* padding-top: 8px; */
      flex: 1;
    `,
  };
});
