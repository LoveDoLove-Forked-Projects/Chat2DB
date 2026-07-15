import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    statusBar: css`
      /* On Windows this area conflicts with the table scrollbar, so both selection and dragging are disabled. */
      user-select: none;
      -webkit-user-drag: none;
      position: relative;
      height: 26px;
      box-sizing: border-box;
      padding: 4px 8px;
      font-size: 12px;
      display: flex;
      justify-content: start;
      align-items: center;
      border-top: 1px solid ${token.colorBorderLayout};
      background-color: ${token.colorFillQuaternary};
      overflow: hidden;
      flex-shrink: 0;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow-x: auto;
      & > span {
        margin-right: 16px;
      }
    `,
  };
});
