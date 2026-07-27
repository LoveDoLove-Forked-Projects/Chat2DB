import React, { useEffect, useImperativeHandle, ForwardedRef, forwardRef, useCallback } from 'react';
import { useStyles } from './style';
import classnames from 'classnames';
import { ITheme, Terminal } from 'xterm';
import { FitAddon } from 'xterm-addon-fit'; // Fits the terminal to its container.
// import { WebLinksAddon } from 'xterm-addon-web-links';
import 'xterm/css/xterm.css'; // Import styles.

interface IProps {
  className?: string;
  value?: string;
  xtermHeaderSlot?: React.ReactNode;
  onData?: (data: string) => void;
  onResize?: (columns: number, rows: number) => void;
  readOnly?: boolean;
  theme?: ITheme;
}

export interface IXtermRef {
  xtermWrite: (value: string) => void;
}

export default forwardRef((props: IProps, ref: ForwardedRef<IXtermRef>) => {
  const { styles } = useStyles();
  const terminalRef = React.useRef<HTMLDivElement>(null);
  const { className, xtermHeaderSlot, onData, onResize, readOnly = true, theme } = props;
  const xtermRef = React.useRef<Terminal | null>(null);
  const fitAddonRef = React.useRef<FitAddon | null>(null);
  const onDataRef = React.useRef(onData);
  const onResizeRef = React.useRef(onResize);

  onDataRef.current = onData;
  onResizeRef.current = onResize;

  const resizeFitAddon = useCallback(() => {
    fitAddonRef.current?.fit();
    const terminal = xtermRef.current;
    if (terminal) {
      onResizeRef.current?.(terminal.cols, terminal.rows);
    }
  }, []);

  const initXterm = () => {
    const xterm = new Terminal({
      convertEol: true, // Move the cursor to the start of the next line on EOL.
      disableStdin: readOnly,
      cursorStyle: 'block', // Cursor style.
      cursorBlink: !readOnly,
      theme,
      // Set the font.
      fontSize: 14,
    });
    fitAddonRef.current = new FitAddon();
    xterm.loadAddon(fitAddonRef.current);
    xterm.open(terminalRef.current!);
    fitAddonRef.current.fit();
    xterm.onData((data) => onDataRef.current?.(data));
    xtermRef.current = xterm;
    onResizeRef.current?.(xterm.cols, xterm.rows);

    window.addEventListener('resize', resizeFitAddon);
    const resizeObserver = new ResizeObserver(resizeFitAddon);
    resizeObserver.observe(terminalRef.current!);
    (xterm as Terminal & { chat2dbResizeObserver?: ResizeObserver }).chat2dbResizeObserver = resizeObserver;
    return xterm;
  };

  useEffect(() => {
    initXterm();
    return () => {
      window.removeEventListener('resize', resizeFitAddon);
      const terminalWithObserver = xtermRef.current as
        | (Terminal & { chat2dbResizeObserver?: ResizeObserver })
        | null;
      terminalWithObserver?.chat2dbResizeObserver?.disconnect();
      // Dispose the Terminal (and its loaded FitAddon / onData listener)
      // so the instance, DOM, and renderer are torn down on unmount.
      xtermRef.current?.dispose();
      xtermRef.current = null;
      fitAddonRef.current = null;
    };
  }, [resizeFitAddon]);

  useEffect(() => {
    if (xtermRef.current) {
      xtermRef.current.options.disableStdin = readOnly;
      xtermRef.current.options.cursorBlink = !readOnly;
    }
  }, [readOnly]);

  useEffect(() => {
    if (xtermRef.current) {
      xtermRef.current.options.theme = theme;
    }
  }, [theme]);

  const xtermWrite = (value: string) => {
    xtermRef.current?.write(value);
  };

  useImperativeHandle(ref, () => ({
    xtermWrite,
  }));

  return (
    <div
      className={classnames(styles.terminalContainerBox, className)}
      style={{ backgroundColor: theme?.background, color: theme?.foreground }}
    >
      {xtermHeaderSlot}
      <div className={styles.terminalContainer} ref={terminalRef} />
    </div>
  );
});
