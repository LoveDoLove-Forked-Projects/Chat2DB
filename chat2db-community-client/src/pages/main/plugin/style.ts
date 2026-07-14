import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    container: css`
      display: flex;
      height: 100%;
      width: 100%;
    `,
    containerRight: css`
      height: 100%;
    `,
  };
});
