import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    container: css`
      height: 100%;
      display: flex;
      flex-direction: column;
      min-height: 0;
    `,
    recordHeader: css`
      flex-shrink: 0;
      min-height: 32px;
      padding: 7px 10px;
      overflow: hidden;
      color: ${token.colorTextSecondary};
      font-size: 12px;
      line-height: 18px;
      white-space: nowrap;
      text-overflow: ellipsis;
    `,
    fields: css`
      flex: 1;
      min-height: 0;
      overflow: auto;
    `,
    item: css`
      display: grid;
      grid-template-columns: minmax(112px, 34%) minmax(0, 1fr) 40px;
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
