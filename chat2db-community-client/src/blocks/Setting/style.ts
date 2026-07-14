import { createStyles, keyframes } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  // makes an animation for settingBox, which changes to full screen in 0.2s.
  const settingBoxAnimation = keyframes`
    from {
      top: 10px;
      left: 10px;
      right: 10px;
      bottom: 10px;
    }
    to {
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
    }
  `
  return {
    settingBox: css`
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: ${token.colorBgBase};
      /* animation: ${settingBoxAnimation} 0.5s forwards; */
    `,
    header: css`
      height: 50px;
      border-bottom: 1px solid ${token.colorBorderLayout};
      padding: 0 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    `,
    headerTitle: css`
      line-height: 50px;
      font-size: 28px;
      font-weight: 500;
    `,
    headerClose: css`
    `,
    content: css`
      display: flex;
      height: calc(100% - 50px);
    `,
    left: css`
      width: 200px;
      background-color: ${token.colorBgBase};
      padding: 10px 6px;
      position: sticky;
      top: 0;
      display: flex;
      flex-direction: column;
      gap: 6px;
    `,
    rightSlot: css`
      flex: 1;
      display: flex;
      justify-content: flex-end;
    `,
    rightSlotAbout: css`
      i {
        font-size: 18px;
        color: var(--color-error);
      }
    `,
    menuContent: css`
      min-height: 400px;
      box-sizing: border-box;
      overflow-y: auto;
      padding: 20px 30px;
      flex: 1;
    `,
  };
});
