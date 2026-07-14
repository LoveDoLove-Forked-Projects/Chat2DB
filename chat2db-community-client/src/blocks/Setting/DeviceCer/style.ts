import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    wrapper: css`
      min-width: 480px;
    `,
    mainTitle: css`
      font-size: 28px;
      font-weight: 700;
      margin-bottom: 48px;
    `,

    title: css``,

    optionTime: css`
      font-size: 12px;
    `,
    optionLicense: css`
      font-size: 12px;
      color: ${token.colorTextSecondary};
    `,

    modalWrapper: css`
      padding-top: 16px;
      padding-bottom: 8px;
      background: ${token.colorBgBase};
      display: flex;
      flex-direction: column;
      gap: 24px;
    `,
    modalTitle: css`
      font-size: 14px;
      font-weight: 700;
      color: ${token.colorTextHeading};
    `,
    modalTips: css`
      color: ${token.colorTextTertiary};
      font-size: 12px;
      & > div {
        line-height: 20px;
      }
      & .ant-btn {
        height: 20px;
        font-size: 12px;
        padding: 0;
      }
    `,
    modalBulletPoint: css`
      color: ${token.colorTextTertiary};
      margin-right: 4px;
      flex-shrink: 0;
    `,
    warning: css`
      color: ${token.colorError};
    `,
    link: css`
      color: ${token.colorPrimary};
      font-size: 12px;
      &:hover {
        color: ${token.colorPrimaryHover};
        cursor: pointer;
        text-decoration: underline;
      }
    `,
  };
});
