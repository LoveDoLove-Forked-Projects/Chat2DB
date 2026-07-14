import { createStyles, css } from 'antd-style';

export const useStyles = createStyles(({ token }) => {
  return {
    wrapper: css`
      height: 50vh;
      overflow: hidden auto;
    `,
    container1: css`
      height: 50vh;
    `,
    container2: css`
      height: 50vh;
    `,
  };
});
