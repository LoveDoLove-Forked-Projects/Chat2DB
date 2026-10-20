import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css }) => {
  return {
    checkboxBody: css`
      .ant-form-item {
        margin-bottom: 0;
      }
      .ant-form-item-control-input{
        min-height: 30px;
      }
    `,
    exportLocationBox: css`
      display: flex;
      align-items: center;
      gap: 4px;
    `,
    iconButton: css`
      flex-shrink: 0;
      border-radius: 6px !important;
    `,
    form: css`
      padding-top: 20px;
    `,
    exportCsvField: css`
      display: flex;
      min-width: 0;
      flex-direction: column;
      gap: 4px;
    `,
    customCharacterInput: css`
      padding: 4px 8px;
    `,
  };
});
