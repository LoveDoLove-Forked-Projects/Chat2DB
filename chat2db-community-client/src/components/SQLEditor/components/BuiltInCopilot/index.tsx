import React, { memo, useEffect, useRef } from 'react';
import AIFloatLayer from '@/pages/main/workspace/components/AIFloatLayer';
import { useStyles } from './style';
import { useSize } from 'ahooks';
import { IconfontSvg } from '@chat2db/ui';

interface IProps {
  className?: string;
  handleEsc?: () => void;
  onResize?: (height: number) => void;
}

export default memo<IProps>(({ handleEsc, onResize }) => {
  const builtInCopilotRef = useRef<HTMLDivElement>(null);
  const { styles } = useStyles();
  const isFirstRender = useRef(true);
  const size = useSize(builtInCopilotRef);

  console.log('[DEBUG:SQLEditor-BuiltInCopilot] Component rendered');

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      console.log('[DEBUG:Keyboard] SQLEditor-BuiltInCopilot - keydown event', {
        code: e.code,
        key: e.key,
        target: e.target,
        activeElement: document.activeElement
      });
      if (e.code === 'Escape') {
        console.log('[DEBUG:Keyboard] SQLEditor-BuiltInCopilot - ESC detected, calling handleEsc');
        handleEsc && handleEsc();
      }
    };
    console.log('[DEBUG:SQLEditor-BuiltInCopilot] Adding document keydown listener');
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      console.log('[DEBUG:SQLEditor-BuiltInCopilot] Removing document keydown listener');
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [handleEsc]);

  useEffect(() => {
    if (!isFirstRender.current && size?.height) {
      onResize && onResize(size?.height);
    }
    isFirstRender.current = false;
  }, [onResize, size?.height]);

  return (
    <div ref={builtInCopilotRef} className={styles.builtInCopilot}>
      <div className={styles.closeButton} onClick={handleEsc}>
        <IconfontSvg size={10} code="icon-close" />
      </div>
      <AIFloatLayer />
    </div>
  );
});
