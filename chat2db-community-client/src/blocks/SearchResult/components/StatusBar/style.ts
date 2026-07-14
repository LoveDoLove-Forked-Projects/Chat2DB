import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    statusBar: css`
      /* On the windows side, this area will conflict with the horizontal scroll bar of the table. Adding user-select: none will not work, so use js to disable dragging. */
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
