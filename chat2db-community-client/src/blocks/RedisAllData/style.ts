import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    redisAllData: css`
      height: 100%;
      display: flex;
      flex-direction: column;
    `,
    upperBox: css`
      display: flex;
      flex-direction: column;
      width: 100%;
      position: relative;
      z-index: 0;
      height: 100%;
    `,
    emptyStatus: css`
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100%;
    `,
    operationAllDataBar: css`
      height: 34px;
      flex-shrink: 0;
      display: flex;
      align-items: center;
      padding: 0px 4px 0px 4px;
      border-bottom: 1px solid ${token.colorBorder};
    `,
    left: css`
      flex: 1;
      display: flex;
      align-items: center;
      gap: 4px;
      color: ${token.colorTextSecondary};
    `,
    right: css`
      display: flex;
      align-items: center;
      gap: 6px;
      width: 420px;
      max-width: 48%;
      height: 24px;
      flex-shrink: 0;
    `,
    viewMode: css`
      flex-shrink: 0;
    `,
    viewModeIcon: css`
      width: 18px;
      height: 18px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
    `,
    searchBar: css`
      flex: 1;
      height: 24px;
      border-radius: 4px;
    `,
    tableCell: css`
      flex: 1;
      padding: 0px 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    `,
    tableBox: css`
      width: 100%;
      height: 100%;
    `,
    treeBox: css`
      flex: 1;
      min-height: 0;
      overflow: hidden;
      display: flex;
      flex-direction: column;
      background: ${token.colorBgContainer};
    `,
    treeHeader: css`
      min-height: 30px;
      display: grid;
      grid-template-columns: minmax(0, 1fr) 120px 180px;
      align-items: center;
      flex-shrink: 0;
      color: ${token.colorTextSecondary};
      font-weight: 500;
      background: ${token.colorFillAlter};
      border-bottom: 1px solid ${token.colorBorder};
    `,
    treeHeaderCell: css`
      min-width: 0;
      height: 30px;
      display: flex;
      align-items: center;
      padding: 0 10px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      border-right: 1px solid ${token.colorSplit};

      &:last-child {
        border-right: 0;
      }
    `,
    treeScroll: css`
      flex: 1;
      min-height: 0;
      overflow: auto;
      display: flex;
      align-items: stretch;

      > .ant-spin,
      > .ant-empty {
        margin: auto;
      }

      > .ant-tree {
        width: 100%;
        background: transparent;
      }

      .ant-tree-treenode {
        width: 100%;
        min-width: 0;
        padding: 0;
        align-items: stretch;
        border-bottom: 1px solid ${token.colorSplit};
      }

      .ant-tree-indent-unit,
      .ant-tree-switcher,
      .ant-tree-node-content-wrapper {
        min-height: 28px;
      }

      .ant-tree-switcher {
        width: 22px;
        line-height: 28px;
      }

      .ant-tree-node-content-wrapper {
        flex: 1;
        min-width: 0;
        display: flex;
        align-items: center;
        padding: 0 8px 0 2px;
        line-height: 28px;
        border-radius: 0;
      }

      .ant-tree-iconEle {
        flex-shrink: 0;
        display: inline-flex;
        align-items: center;
        justify-content: center;
      }

      .ant-tree-title {
        flex: 1;
        min-width: 0;
        display: flex;
      }
    `,
    treeRow: css`
      flex: 1;
      width: 100%;
      min-width: 0;
      display: grid;
      grid-template-columns: minmax(0, 1fr) 120px 180px;
      align-items: center;
    `,
    treeGroupRow: css`
      color: ${token.colorText};
      font-weight: 500;
    `,
    treeKeyCell: css`
      min-width: 0;
      display: flex;
      align-items: center;
      gap: 5px;
      padding-right: 8px;
    `,
    treeTitleText: css`
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    `,
    treeCount: css`
      flex-shrink: 0;
      color: ${token.colorTextSecondary};
      font-weight: 400;
      font-variant-numeric: tabular-nums;
    `,
    treeMetaCell: css`
      min-width: 0;
      height: 28px;
      display: flex;
      align-items: center;
      padding: 0 10px;
      overflow: hidden;
      color: ${token.colorTextSecondary};
      text-overflow: ellipsis;
      white-space: nowrap;
      border-left: 1px solid ${token.colorSplit};
    `,
    editDataSide: css`
      height: 100%;
      padding: 10px 0px;
      /* border-top: 1px solid ${token.colorBorder}; */
      width: 100%;
      overflow-y: auto;
      overflow-x: hidden;
      box-sizing: border-box;
    `,
  };
});
