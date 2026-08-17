import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => ({
  selector: css`
    width: 100%;
    min-width: 280px;
  `,
  globalScope: css`
    display: inline-flex;
    align-items: center;
    min-height: 24px;
    color: ${token.colorTextSecondary};
  `,
  scopeSummaryList: css`
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  `,
  scopeSummary: css`
    display: flex;
    max-width: 100%;
    align-items: center;
    gap: 5px;
    border-radius: 4px;
    padding: 2px 7px;
    background: ${token.colorFillSecondary};

    span {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  `,
  dataSourceOption: css`
    display: inline-flex;
    min-width: 0;
    align-items: center;
    gap: 6px;

    > span:last-child {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  `,
}));
