import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    leftContainer: css`
      display: relative;
      height: 100%;
    `,
  };
});
