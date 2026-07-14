import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css }) => {
  return {
    modelSelect: css`
      opacity: 0.5;
      font-size: 11px;
      & .ant-select-selector {
        padding-left: 0px !important;
        font-size: 12px !important;
      }
    `,
    popupSelect: css`
      & .ant-select-item {
        font-size: 12px !important;
        min-height: 0px !important;
        padding: 3px 6px !important;
      }
    `,
    dropdownDivider: css`
      margin: 4px 0 !important;
    `,
    customModelEntry: css`
      font-size: 12px;
      line-height: 20px;
      padding: 4px 10px 8px;
      cursor: pointer;
      color: var(--color-primary);
      &:hover {
        background-color: var(--color-fill-tertiary);
      }
    `,
  };
});
