import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    builtInCopilot: css`
      padding: 4px 2px 4px 0px;
      position: relative;
    `,
    closeButton: css`
      position: absolute;
      top: 0px;
      right: 0px;
      width: 12px;
      height: 12px;
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1;
      border-radius: 50%;
      overflow: hidden;
      background-color: ${token.colorBorderSecondary};
      color: ${token.colorWhite};
      &:hover {
        background-color: ${token.colorError};
        cursor: pointer;
      }
    `,
  };
});
