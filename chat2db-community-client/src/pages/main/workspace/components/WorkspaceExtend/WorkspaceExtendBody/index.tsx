import { useMemo } from 'react';
import { extendConfig } from '../config';
import { useWorkspaceStore } from '@/store/workspace';
import { useStyles } from './style';
import { useAIStore } from '@/store/ai';
import AI from '@/blocks/AI';

export default () => {
  const { styles } = useStyles();

  const currentWorkspaceExtend = useWorkspaceStore((state) => state.currentWorkspaceExtend);
  const showPanel = useAIStore((state) => state.showPanel);

  const Component = useMemo(() => {
    return extendConfig.find((item) => item.code === currentWorkspaceExtend)?.components;
  }, [currentWorkspaceExtend]);

  if (showPanel) {
    return (
      <div className={styles.currentWorkspaceExtendBox}>
        <AI variant="panel" />
      </div>
    );
  }

  return Component ? (
    <div className={styles.currentWorkspaceExtendBox}>
      <Component />
    </div>
  ) : (
    false
  );
};
