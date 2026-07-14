import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    currentWorkspaceExtendBox: css`
      height: 100%;
      border-right: 1px solid ${token.colorBorderLayout};
      overflow: hidden;
    `,
  };
});
