import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    container: css`
      max-height: 68vh;
      overflow: auto;
      border: 1px solid ${token.colorBorderSecondary};
      border-radius: 4px;
    `,
    item: css`
      display: grid;
      grid-template-columns: minmax(160px, 30%) minmax(0, 1fr) 44px;
      border-bottom: 1px solid ${token.colorBorderSecondary};

      &:focus-within [data-row-detail-action='true'] {
        opacity: 1;
        pointer-events: auto;
      }

      &:last-child {
        border-bottom: 0;
      }
    `,
    field: css`
      min-width: 0;
      padding: 8px 12px;
      color: ${token.colorTextSecondary};
      background-color: ${token.colorFillQuaternary};
      border-right: 1px solid ${token.colorBorderSecondary};
      word-break: break-word;
    `,
    valueWrapper: css`
      min-width: 0;
      min-height: 38px;
    `,
    valueInput: css`
      width: 100%;
      height: 100%;
      min-height: 38px;
      padding: 8px 12px;
      color: ${token.colorText};
      font-family: ${token.fontFamilyCode};
      background: transparent;
      border: 0;
      border-radius: 0;
      box-shadow: none;

      &:focus {
        box-shadow: inset 0 0 0 1px ${token.colorPrimary};
      }
    `,
    action: css`
      display: flex;
      align-items: center;
      justify-content: center;
    `,
    actionButton: css`
      width: 32px;
      height: 32px;
      padding: 0;
      color: ${token.colorTextSecondary};
      opacity: 0;
      pointer-events: none;
      transition: opacity 120ms ease;

      &:hover {
        color: ${token.colorText} !important;
      }
    `,
    nullValue: css`
      color: ${token.colorTextTertiary};
      font-family: ${token.fontFamily};

      &::placeholder {
        color: ${token.colorTextTertiary};
      }
    `,
  };
});
