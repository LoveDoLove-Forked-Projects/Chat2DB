import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => ({
  container: css`
    display: flex;
    flex-direction: column;
    gap: 32px;
  `,
  shellSelect: css`
    width: min(360px, 100%);
  `,
  hint: css`
    margin-top: 10px;
    color: ${token.colorTextTertiary};
    font-size: 12px;
  `,
  themeGrid: css`
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
    gap: 14px;
    max-width: 920px;
  `,
  themeCard: css`
    display: flex;
    min-height: 128px;
    flex-direction: column;
    gap: 14px;
    padding: 16px;
    text-align: left;
    border: 2px solid transparent;
    border-radius: 8px;
    box-shadow: inset 0 0 0 1px rgb(127 127 127 / 25%);
    cursor: pointer;
    transition:
      border-color 0.15s ease,
      transform 0.15s ease;

    &:hover {
      transform: translateY(-1px);
    }

    &:focus-visible {
      outline: 2px solid ${token.colorPrimary};
      outline-offset: 2px;
    }
  `,
  activeThemeCard: css`
    border-color: ${token.colorPrimary};
  `,
  themeTitle: css`
    font-weight: 600;
  `,
  colorRow: css`
    display: flex;
    gap: 6px;

    > span {
      width: 18px;
      height: 18px;
      border: 1px solid rgb(127 127 127 / 25%);
      border-radius: 50%;
    }
  `,
  commandPreview: css`
    font-family: SFMono-Regular, Consolas, 'Liberation Mono', monospace;
    font-size: 12px;
  `,
}));
