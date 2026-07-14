import React, { memo, useEffect, useMemo, useRef } from 'react';
import AIFloatLayer from '@/pages/main/workspace/components/AIFloatLayer';
import { useStyles } from './style';

interface IProps {
  className?: string;
  handleEsc?: () => void;
  onResize?: (height) => void;
}

export default memo<IProps>((props) => {
  const { handleEsc, onResize } = props;
  const builtInCopilotRef = useRef<HTMLDivElement>(null);
  const isFirstRender = useRef(true);
  const { styles } = useStyles();

  console.log('[DEBUG:BuiltInCopilot] Component rendered', { handleEsc: !!handleEsc, onResize: !!onResize });

  const monitorEsc = useMemo(() => {
    return () => {
      console.log('[DEBUG:BuiltInCopilot] monitorEsc function called - about to set document.onkeydown');
      console.log('[DEBUG:BuiltInCopilot] document.onkeydown before:', typeof document.onkeydown);
      document.onkeydown = function (e) {
        console.log('[DEBUG:BuiltInCopilot] document.onkeydown handler triggered', {
          key: e.key,
          code: e.code,
          target: e.target,
          activeElement: document.activeElement
        });
        if (e.code === 'Escape') {
          console.log('[DEBUG:BuiltInCopilot] ESC key detected, calling handleEsc');
          handleEsc && handleEsc();
        }
      };
      console.log('[DEBUG:BuiltInCopilot] document.onkeydown after:', typeof document.onkeydown);
    };
  }, [handleEsc]);

  useEffect(() => {
    console.log('[DEBUG:BuiltInCopilot] useEffect - adding keydown listener');
    document.addEventListener('keydown', monitorEsc);
    return () => {
      console.log('[DEBUG:BuiltInCopilot] useEffect cleanup - removing keydown listener');
      document.removeEventListener('keydown', monitorEsc);
    };
  }, [monitorEsc]);

  useEffect(() => {
    const divElement = builtInCopilotRef.current;
    if (divElement) {
      const resizeObserver = new ResizeObserver((entries) => {
        for (const entry of entries) {
          const height = entry.target.clientHeight;
          if (!isFirstRender.current) {
            onResize && onResize(height);
          }
        }
      });
      resizeObserver.observe(divElement);
      isFirstRender.current = false;
      return () => {
        resizeObserver.unobserve(divElement);
      };
    }
  }, [onResize, isFirstRender]);

  return (
    <div ref={builtInCopilotRef} className={styles.builtInCopilot}>
      <AIFloatLayer />
    </div>
  );
});
