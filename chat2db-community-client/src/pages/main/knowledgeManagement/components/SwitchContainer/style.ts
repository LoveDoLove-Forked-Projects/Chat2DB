import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    container: css`
      flex: 1;
      height: 100%;
      width: 0px;
      padding: 16px;
      box-sizing: border-box;
    `,
  };
});
