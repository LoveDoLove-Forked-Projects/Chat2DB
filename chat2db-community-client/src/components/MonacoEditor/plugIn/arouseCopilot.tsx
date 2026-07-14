import React, { useEffect } from 'react';
import * as ReactDOM from 'react-dom/client';
import BuiltInCopilot from '../components/BuiltInCopilot';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import AppTheme from '@/components/AppTheme';
import { v4 as uuid } from 'uuid';

const useArouseCopilot = ({ editorIns, canAI, placeholderContentWidget, isFocus }) => {
  const overlayDomRef = React.useRef<HTMLDivElement>();
  useEffect(() => {
    if (!editorIns) return;

    let disposable;
  // Do not trigger AI while the editor lacks focus.
    if (!isFocus) {
      disposable?.dispose();
      return;
    }
  // Register the shortcut.
    disposable = editorIns.addAction({
      id: 'slash-insertion',
      label: 'slash',
      keybindings: [monaco.KeyCode.Slash],
      run: (ed) => {
        const position = ed.getPosition();
    // Activate AI on an empty line or when the cursor is at the line start.
        // const lineContent = ed.getModel().getLineContent(position.lineNumber);
        //  || lineContent.slice(0, position.column - 1).trim() === ''
        if (position.column === 1) {
          arouseCopilot({});
        } else {
          ed.trigger('keyboard', 'type', { text: '/' });
        }
      },
    });

    // editorIns.onDidChangeModelContent((e) => {
    //   const changes = e.changes;
    //   const lastChange = changes[changes.length - 1];
    //   if (lastChange.text === '/') {
    //     const position = editorIns.getPosition();
    //     console.log(position);
    //     if (position.column === 2) {
    //       arouseCopilot({});
      //       editorIns.getModel().undo(); // Add this call.
    //     }
    //   }
    // });

  // Dispose.
    return () => {
      if (disposable) {
        disposable.dispose();
      }
      // clearBuiltInCopilot();
    };
  }, [editorIns, isFocus]);

  // Clear the previous BuiltInCopilot.
  const clearBuiltInCopilot = () => {
    overlayDomRef.current = undefined;

    if (editorIns.zoneId) {
      editorIns.changeViewZones((changeAccessor: any) => {
        changeAccessor.removeZone(editorIns.zoneId);
      });
      editorIns.zoneId = null;
      // Clear the overlay widget.
      const overlayDom = document.getElementById('overlayId');
      if (overlayDom) {
        // ReactDOM.unmountComponentAtNode(overlayDom);
        ReactDOM.createRoot(overlayDom).unmount();
        overlayDom.remove();
      }

      // Return focus to the editor after clearing.
      editorIns.focus();
      editorIns.layout();
    }
  };

  // Create the BuiltInCopilot container.
  const createBuiltInCopilotOverlayWidget = () => {
    const handleEsc = () => {
      clearBuiltInCopilot();
    };
    const onResize = (height: number) => {
  // Recreate the view zone at the new height when BuiltInCopilot is resized.
      editorIns.changeViewZones((changeAccessor: any) => {
        changeAccessor.removeZone(editorIns.zoneId);
      });
      editorIns.zoneId = null;
      arouseCopilot({
        height: height,
        clear: false,
      });
    };
    const overlayDom = document.createElement('div');
    overlayDom.id = 'overlayId';
    overlayDom.style.left = '52px';
    overlayDom.style.right = '14px';
    ReactDOM.createRoot(overlayDom).render(
      <AppTheme>
        <BuiltInCopilot handleEsc={handleEsc} onResize={onResize} />
      </AppTheme>,
    );
    const overlayWidget = {
      getId: () => 'overlay.zone.widget',
      getDomNode: () => overlayDom,
      getPosition: () => null,
    };
    editorIns.addOverlayWidget(overlayWidget);
    return overlayDom;
  };

  // Add the keyboard listener.
  const addKeyDownListener = () => {
    console.log('[DEBUG:Keyboard] arouseCopilot - Adding Monaco onKeyDown listener');
    editorIns.onKeyDown((e: any) => {
      console.log('[DEBUG:Keyboard] arouseCopilot - Monaco onKeyDown triggered', {
        code: e.code,
        keyCode: e.keyCode,
        browserEvent: e.browserEvent,
        hasZoneId: !!editorIns.zoneId
      });
    // Clear BuiltInCopilot on Escape or Enter.
      if ((e.code === 'Escape' || e.code === 'Enter') && editorIns.zoneId) {
        console.log('[DEBUG:Keyboard] arouseCopilot - ESC or Enter detected, clearing BuiltInCopilot');
        e.stopPropagation();
        clearBuiltInCopilot();
      }
    });
  };

  const arouseCopilot = ({ height = 40, clear = true }) => {
    if (!canAI || !editorIns) return;
  // Add the keyboard listener.
    addKeyDownListener();
    if (clear) {
  // Clear the previous BuiltInCopilot.
      clearBuiltInCopilot();
  // Add the overlay widget.
      overlayDomRef.current = createBuiltInCopilotOverlayWidget();
    }

  // Get the line containing the cursor.
    const position = editorIns.getPosition();
    const lineNumber = position ? position.lineNumber : 1;

  // Add a view zone to position the overlay widget.
    editorIns.changeViewZones((changeAccessor) => {
      const zoneNode = document.createElement('div');
      zoneNode.id = uuid();
      editorIns.zoneId = changeAccessor.addZone({
        afterLineNumber: lineNumber - 1,
        heightInLines: height / 20, // Reserve padding.
        domNode: zoneNode,
        onDomNodeTop: (top) => {
          if (overlayDomRef.current) {
            overlayDomRef.current.style.top = top + 'px';
          }
        },
      });
    });

    placeholderContentWidget?.dispose();
  };

// Export whether BuiltInCopilot is currently active.
  return {
    isArouseCopilot: !!editorIns?.zoneId,
    arouseCopilot,
    clearBuiltInCopilot,
  };
};

export default useArouseCopilot;
