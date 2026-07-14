import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    container: css``,
    tableTop: css`
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 16px 0;
    `,
    modalForm: css``
  };
});
