import React, { memo, useMemo, useRef, useEffect, useState } from 'react';
import { useStyles } from './style';
import { IconfontSvg } from '@chat2db/ui';

interface IProps {
  className?: string;
  children: React.ReactNode;
  onUnfold?: () => void;
  onPackUp?: () => void;
  direction?: 'vertical' | 'horizontal';
}

export default memo<IProps>((props) => {
  const { className, children, onUnfold, onPackUp, direction = 'vertical' } = props;
  const { styles, cx } = useStyles(direction);
  const splitPaneUnpackRef = useRef<HTMLDivElement>(null);
  const [size, setSize] = useState(0);

  useEffect(() => {
    const resizeObserver = new ResizeObserver(() => {
      const currentSize =
        direction === 'vertical' ? splitPaneUnpackRef.current?.clientHeight : splitPaneUnpackRef.current?.clientWidth;
      setSize(currentSize || 0);
    });
    resizeObserver.observe(splitPaneUnpackRef.current as Element);
    return () => {
      resizeObserver.disconnect();
    };
  }, [direction]);

  const iconCode = useMemo(() => {
    return size === 0 ? 'icon-chevron-up' : 'icon-chevron-bottom';
  }, [size]);

  const handleClick = () => {
    if (size === 0) {
      onUnfold?.();
    } else {
      onPackUp?.();
    }
  };

  return (
    <div className={cx(styles.splitPaneUnpack, className)} ref={splitPaneUnpackRef}>
      <div className="operatingHandleBox">
        <div className="operatingHandle" onClick={handleClick}>
          <IconfontSvg className="operatingHandleIcon" code={iconCode} size="xs" />
        </div>
      </div>
      {children}
    </div>
  );
});
