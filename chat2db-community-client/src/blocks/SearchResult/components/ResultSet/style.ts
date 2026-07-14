import { createStyles } from 'antd-style';
import {hexToRgba} from '@/utils/color';

export const useStyles = createStyles(({ css, token }) => {

  const tableLoadingBg = hexToRgba(token.colorFill, 20);

  return {
    container: css`
      height: 100%;
      display: flex;
      flex-direction: column;
      position: relative;
      &:focus-visible {
        outline: none;
      }
    `,
    resultSetTableContainer: css`
      flex: 1;
      height: 0px !important;
      position: relative;
    `,
    tableLoading: css`
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      overflow: hidden;
      z-index: 1;
      background-color: ${tableLoadingBg};
    `,
    stopExecuteSql: css`
      cursor: pointer;
      margin-top: 30px;
      &:hover {
        color: ${token.colorPrimary};
      }
    `,
  };
});
