import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => ({
  container: css`
    display: flex;
    max-height: min(82vh, calc(100vh - 116px));
    max-height: min(82dvh, calc(100dvh - 116px));
    min-width: 0;
    flex-direction: column;
  `,
  scrollContent: css`
    min-height: 0;
    overflow-y: auto;
    padding-right: 4px;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 999px;
      background-color: transparent;
    }

    &:hover::-webkit-scrollbar-thumb {
      background-color: ${token.colorFill};
    }
  `,
  stepPanel: css`
    min-width: 0;
  `,
  sectionBlock: css`
    margin-bottom: 14px;
  `,
  sectionTitle: css`
    margin-bottom: 8px;
    font-size: 14px;
    font-weight: 600;
  `,
  errorAlert: css`
    margin-bottom: 12px;
  `,
  targetTableSelect: css`
    width: 100%;
  `,
  parseStatus: css`
    display: flex;
    min-height: 22px;
    gap: 8px;
    align-items: center;
    margin-bottom: 10px;
    font-size: 12px;
    color: ${token.colorTextSecondary};
  `,
  parseStatusError: css`
    color: ${token.colorError};
    overflow-wrap: anywhere;
  `,
  previewState: css`
    display: flex;
    min-height: min(240px, 32vh);
    min-height: min(240px, 32dvh);
    align-items: center;
    justify-content: center;
  `,
  previewError: css`
    display: flex;
    max-width: calc(100% - 32px);
    flex-direction: column;
    align-items: center;
    gap: 10px;
    color: ${token.colorError};
    line-height: 1.6;
    text-align: center;
    white-space: normal;
    overflow-wrap: anywhere;
  `,
  summary: css`
    .ant-descriptions-item-label {
      width: 136px;
    }
  `,
  summaryValue: css`
    overflow-wrap: anywhere;
    white-space: pre-wrap;
  `,
  modeRow: css`
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 16px;
  `,
  modeRisk: css`
    font-size: 12px;
    color: ${token.colorTextTertiary};
  `,
  preCheckBox: css`
    margin-top: 16px;
    padding: 12px;
    border: 1px solid ${token.colorBorderSecondary};
    border-radius: 8px;
    background: ${token.colorFillQuaternary};
  `,
  preCheckTitle: css`
    margin-bottom: 8px;
    font-size: 13px;
    font-weight: 600;
  `,
  preCheckLine: css`
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: ${token.colorTextSecondary};
  `,
  preCheckHint: css`
    overflow-wrap: anywhere;
    color: ${token.colorTextTertiary};
  `,
  preCheckGrid: css`
    display: grid;
    grid-template-columns: auto 1fr;
    gap: 6px 12px;
    font-size: 12px;
    color: ${token.colorTextSecondary};
  `,
  preCheckLabel: css`
    color: ${token.colorTextTertiary};
    white-space: nowrap;
  `,
  preCheckIssues: css`
    ul {
      margin: 6px 0 0;
      padding-left: 18px;
    }

    li {
      overflow-wrap: anywhere;
    }
  `,
  policySection: css`
    margin-top: 16px;
  `,
  policyRow: css`
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  `,
  policyField: css`
    display: flex;
    align-items: center;
    gap: 8px;
  `,
  policyLabel: css`
    font-size: 13px;
    color: ${token.colorTextSecondary};
  `,
  policySelect: css`
    width: 220px;
  `,
  policyHint: css`
    margin-top: 6px;
    font-size: 12px;
    color: ${token.colorTextTertiary};
  `,
  templateBar: css`
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    align-items: center;
    justify-content: flex-end;
    margin-bottom: 10px;
  `,
  templateSelect: css`
    width: 240px;
  `,
  templateOption: css`
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
  `,
  templateOptionName: css`
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  `,
  templateOptionDelete: css`
    flex: none;
    color: ${token.colorTextSecondary};
  `,
  footerLeft: css`
    display: flex;
    align-items: center;
    gap: 8px;
  `,
  footer: css`
    z-index: 2;
    display: flex;
    flex: none;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding-top: 12px;
    border-top: 1px solid ${token.colorBorderSecondary};
    background: ${token.colorBgElevated};
  `,
  footerRight: css`
    display: flex;
    align-items: center;
    gap: 8px;
  `,
}));
