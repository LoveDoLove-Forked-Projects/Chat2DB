import { memo } from 'react';
import { createStyles } from 'antd-style';
import i18n from '@/i18n';

export const useStyles = createStyles(({ css, token }) => {
  return {
    aiPlaceholderContent: css`
      display: flex;
      align-items: center;
      color: ${token.colorTextQuaternary};
      height: 20px;
      line-height: 20px;
      span {
        letter-spacing: 1px;
        font-weight: 500;
      }
    `,
    button: css`
      display: flex;
      font-size: 12px;
      align-items: center;
      justify-content: center;
      background-color: ${token.colorFillTertiary};
      border-radius: 4px;
      margin: 0 4px;
      padding: 0 7px;
      transform: scale(0.9);
    `,
    aiText: css`
      margin-left: 2px;
    `,
  };
});

export default memo(() => {
  const { styles } = useStyles();
  return (
    <div className={styles.aiPlaceholderContent}>
      <span>{i18n('monaco.text.press')}</span>
      <div className={styles.button}>/</div>
      <span>{i18n('monaco.text.invoke')}</span>
      <span className={styles.aiText}>AI</span>
    </div>
  );
});
