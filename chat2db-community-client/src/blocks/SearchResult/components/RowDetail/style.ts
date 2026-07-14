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
      grid-template-columns: minmax(160px, 30%) minmax(0, 1fr);
      border-bottom: 1px solid ${token.colorBorderSecondary};

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
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 8px;
    `,
    value: css`
      min-width: 0;
      flex: 1;
      padding: 8px 12px;
      color: ${token.colorText};
      white-space: pre-wrap;
      word-break: break-word;
      font-family: ${token.fontFamilyCode};
    `,
    viewFullValue: css`
      flex-shrink: 0;
      margin: 4px 8px 0 0;
      padding: 0;
      height: auto;
      font-size: 12px;
    `,
    nullValue: css`
      color: ${token.colorTextTertiary};
      font-family: ${token.fontFamily};
    `,
  };
});
