import { Fragment, memo, useEffect, useState, useRef } from 'react';
import WorkspaceExtendBody from '../WorkspaceExtend/WorkspaceExtendBody';
import WorkspaceExtendNav from '../WorkspaceExtend/WorkspaceExtendNav';
import { useWorkspaceStore } from '@/store/workspace';
import SplitPane from 'react-split-pane';
import ExportProgressBar from '@/blocks/ImportAndExport/components/ExportProgressBar';
import { canImportExport } from '@/utils/env';
// import DragFileToApp from '@/components/DragFileToApp';

// ----- components -----
import WorkspaceTabs from '../WorkspaceTabs';
import { useStyles } from './style';

const WorkspaceRight = memo(() => {
  const [size, setSize] = useState(0);
  const [maxPanelSize, setMaxPanelSize] = useState(0);
  const draggablePanelRef = useRef<HTMLDivElement>(null);

  const { styles } = useStyles();

  const { panelRight, panelRightWidth, setPanelRightWidth } = useWorkspaceStore((state) => {
    return {
      panelRight: state.layout.panelRight,
      panelRightWidth: state.layout.panelRightWidth,
      setPanelRightWidth: state.setPanelRightWidth,
    };
  });

  useEffect(() => {
    if (!draggablePanelRef.current) return;

    const resizeObserver = new ResizeObserver((entries) => {
      for (const entry of entries) {
        setMaxPanelSize(entry.contentRect.width * 0.8);
      }
    });

    resizeObserver.observe(draggablePanelRef.current);
    return () => resizeObserver.disconnect();
  }, []);

  useEffect(() => {
    if (!panelRight) {
      setSize(0);
    } else {
      setSize(panelRightWidth || 320);
    }
  }, [panelRight, panelRightWidth]);

  return (
    <div className={styles.workspaceRight}>
      <div className={styles.draggablePanel} ref={draggablePanelRef}>
        <SplitPane
          split="vertical"
          size={size}
          pane1Style={{ width: '0px' }}
          minSize={150}
          maxSize={maxPanelSize}
          primary="second"
          allowResize={panelRight}
          onChange={(newSize) => {
            setPanelRightWidth(newSize);
          }}
        >
          {/* <DragFileToApp className={styles.masterScope} onDropCallback={handleDropCallback} fileType="sql">
            <div className={styles.masterScopeMain}>
              <WorkspaceTabs />
            </div>
            <div className={styles.masterScopeBottom}>{canImportExport && <ExportProgressBar />}</div>
          </DragFileToApp> */}
          <div className={styles.masterScope}>
            <div className={styles.masterScopeMain}>
              <WorkspaceTabs />
            </div>
            <div className={styles.masterScopeBottom}>{canImportExport && <ExportProgressBar />}</div>
          </div>
          <Fragment>{panelRight && <WorkspaceExtendBody />}</Fragment>
        </SplitPane>
      </div>
      <WorkspaceExtendNav />
    </div>
  );
});

export default WorkspaceRight;
