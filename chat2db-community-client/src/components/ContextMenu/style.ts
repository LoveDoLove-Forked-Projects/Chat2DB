import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => ({
  menu: css`
    padding: 6px;
    border: 1px solid ${token.colorBorderSecondary};
    border-radius: 8px;
    background: ${token.colorBgElevated};
    box-shadow: ${token.boxShadowSecondary};

    .ant-menu-item,
    .ant-menu-submenu-title {
      display: flex;
      height: 30px;
      margin: 0;
      padding: 0 10px;
      border-radius: 5px;
      color: ${token.colorText};
      font-size: 13px;
      line-height: 30px;
    }

    .ant-menu-title-content {
      flex: 1 1 auto;
      min-width: 0;
    }

    .ant-menu-item:hover,
    .ant-menu-submenu-title:hover {
      background: ${token.colorFillTertiary};
    }

    .ant-menu-item-danger {
      color: ${token.colorError};
    }

    .ant-menu-item-disabled {
      color: ${token.colorTextQuaternary} !important;
    }

    .ant-menu-item-divider {
      margin: 6px 8px;
      background: ${token.colorSplit};
    }
  `,
  defaultMenuWidth: css`
    min-width: 160px;
  `,
}));
