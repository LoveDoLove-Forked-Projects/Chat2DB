import { createStyles, css, keyframes } from 'antd-style';

export const useStyles = createStyles(({ token }) => {
  const moveGradient = keyframes`
  0% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 0deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  5% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 18deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  10% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 36deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  15% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 54deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  20% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 72deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  25% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 90deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  30% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 108deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  35% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 126deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  40% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 144deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  45% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 162deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  50%{
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 180deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  55% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 198deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  60% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 216deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  65% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 234deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  70% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 252deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  75% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 270deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  80% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 288deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  85% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 306deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  90% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 324deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  95% {
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 342deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
  100%{
    background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),conic-gradient(from 360deg,rgba(115,90,255,0.5) -12deg,rgba(0,127,255,0.5) 44deg,rgba(90,239,255,0.5) 148deg,rgba(211,153,255,0.5) 192deg,rgba(255,133,56,0.5) 255deg,rgba(255,116,236,0.5) 316deg,rgba(115,90,255,0.5) 348deg,rgba(0,127,255,0.5) 404deg);
  }
`;
  return {
    aiMoveGradient: css`
      border: 1px solid transparent;
      background: none;
      background-clip: padding-box, border-box;
      background-origin: padding-box, border-box;
      background-image: linear-gradient(to right, ${token.colorBgBase}, ${token.colorBgBase}),
        radial-gradient(
          circle at 0 5%,
          rgba(255, 116, 236, 0.7),
          rgba(133, 112, 255, 0.7) 20%,
          rgba(133, 112, 255, 0.7) 30%,
          rgba(90, 239, 255, 0.7) 60%,
          rgba(90, 239, 255, 0.7) 80%,
          rgba(46, 150, 255, 1) 100%
        );
      animation: 3s linear 0s infinite normal none running ${moveGradient};
      &:focus,
      &:hover {
        border: 1px solid transparent;
      }
    `,
  };
});
