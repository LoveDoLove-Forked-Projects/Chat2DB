import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => ({
  console: css`
    height: 100%;
    display: flex;
    flex-direction: column;
    background: ${token.colorBgContainer};
    color: ${token.colorText};
  `,
  toolbar: css`
    height: 30px;
    flex: 0 0 30px;
    display: flex;
    align-items: center;
    padding: 0 6px;
    border-bottom: 1px solid ${token.colorBorderSecondary};
  `,
  toolbarSpacer: css`
    flex: 1;
  `,
  iconButton: css`
    width: 26px;
    min-width: 26px;
    height: 26px;
    padding: 0;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: ${token.colorTextSecondary};
  `,
  activeIconButton: css`
    color: ${token.colorPrimary};
    background: ${token.colorFillSecondary};
  `,
  scrollArea: css`
    flex: 1;
    min-height: 0;
    overflow: auto;
    padding: 10px 14px 18px;
    font-family: Menlo, Monaco, Consolas, 'Courier New', monospace;
    font-size: 12px;
    line-height: 1.65;
  `,
  record: css`
    min-width: 0;
    margin-bottom: 10px;
  `,
  line: css`
    display: grid;
    grid-template-columns: 154px minmax(0, 1fr);
    align-items: start;
    min-height: 20px;
  `,
  timestamp: css`
    color: ${token.colorTextQuaternary};
    white-space: nowrap;
    user-select: none;
  `,
  contextLine: css`
    margin: 6px 0 4px;
    padding-top: 6px;
    border-top: 1px solid ${token.colorBorderSecondary};
    color: ${token.colorTextSecondary};
  `,
  sqlContent: css`
    display: flex;
    align-items: flex-start;
    min-width: 0;
  `,
  prompt: css`
    flex: none;
    margin-right: 8px;
    color: ${token.colorPrimary};
    font-weight: 600;
  `,
  sql: css`
    min-width: 0;
    margin: 0;
    color: ${token.colorText};
    font: inherit;
    white-space: pre-wrap;
    overflow-wrap: anywhere;
    letter-spacing: 0;
  `,
  message: css`
    display: flex;
    align-items: flex-start;
    gap: 8px;
    min-width: 0;
  `,
  level: css`
    flex: none;
    width: 42px;
    font-size: 11px;
    font-weight: 600;
  `,
  messageText: css`
    min-width: 0;
    white-space: pre-wrap;
    overflow-wrap: anywhere;
  `,
  messageINFO: css`
    color: ${token.colorTextSecondary};
  `,
  messageWARN: css`
    color: ${token.colorWarningText};
  `,
  messageERROR: css`
    color: ${token.colorErrorText};
  `,
  resultLine: css`
    color: ${token.colorSuccessText};
    min-width: 0;
  `,
  resultError: css`
    color: ${token.colorErrorText};
  `,
  resultLink: css`
    border: 0;
    padding: 0;
    background: transparent;
    color: ${token.colorLink};
    font: inherit;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  `,
  metrics: css`
    color: ${token.colorTextSecondary};
  `,
  released: css`
    color: ${token.colorTextQuaternary};
  `,
  inlineAction: css`
    height: 20px;
    margin-left: 8px;
    padding: 0 4px;
    font-size: 12px;
  `,
  runningLine: css`
    color: ${token.colorTextSecondary};
  `,
  runningContent: css`
    display: inline-flex;
    align-items: center;
    gap: 7px;
  `,
  runningDot: css`
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: ${token.colorPrimary};
    animation: console-pulse 1.2s ease-in-out infinite;

    @keyframes console-pulse {
      0%, 100% { opacity: 0.35; }
      50% { opacity: 1; }
    }
  `,
  successLine: css`
    color: ${token.colorSuccessText};
  `,
  cancelledLine: css`
    color: ${token.colorWarningText};
  `,
}));
