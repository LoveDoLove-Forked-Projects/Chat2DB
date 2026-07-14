import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    viewDDL: css`
      height: 100%;
      max-height: 100%;
      overflow-y: auto;
    `,
  };
});
