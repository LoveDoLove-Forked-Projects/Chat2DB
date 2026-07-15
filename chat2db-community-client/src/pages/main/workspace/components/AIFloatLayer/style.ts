import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, prefixCls }) => {
  return {

    container: css`
      position: relative;
      background-color: transparent;
      width: auto !important;
      margin: 0 !important;

      & .${prefixCls}-modal-content {
        box-shadow: none;
        background-color: transparent;
        border: none;
        width: 680px !important;
      }
      & .${prefixCls}-modal-body {
        padding-block: 0 !important;
        padding-inline: 0 !important;
      }
    `,
    wrapContainer: css`
      backdrop-filter: none;
    `,
    aiChatContainer: css`
      border-radius: 4px;
      textarea {
        font-size: 13px;
      }
    `,
    renderMarkdownBox: css`
      padding: 4px 10px 10px 10px;
      max-height: 50vh;
      overflow-y: auto;
    `,
  };
});
