import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    message: css`
      max-width: 100%;
    `,
  };
});
