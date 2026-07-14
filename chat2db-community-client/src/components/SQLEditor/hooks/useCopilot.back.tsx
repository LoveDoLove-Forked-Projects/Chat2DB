import React, { useEffect, useRef } from 'react';
import AppTheme from '@/components/AppTheme';
import * as ReactDOM from 'react-dom/client';
import BuiltInCopilot from '../components/BuiltInCopilot';
import * as monaco from 'monaco-editor';
import { v4 as uuid } from 'uuid';
import { MonacoEditorRef } from '../editor/MonacoEditor';
import PlaceholderContentWidget from '../components/AIPlaceholder/PlaceholderContentWidget';

interface IProps {
  editorRef: React.RefObject<MonacoEditorRef>;
  placeholderContentWidget: PlaceholderContentWidget | null;
  canAI: boolean;
}

const useArouseCopilot = ({ editorRef, canAI, placeholderContentWidget }: IProps) => {
  const overlayDomRef = useRef<HTMLDivElement>();
  const editorZoneIdRef = useRef<string | null>(null);

  useEffect(() => {
    if (!canAI) return;
    if (!editorRef.current) return;
    if (!placeholderContentWidget) return;

    const editorIns = editorRef.current.getInstance() ?? null;
    if (!editorIns) return;

    const slashDisposable = editorIns.addAction({
      id: 'slash-insertion',
      label: 'slash',
      keybindings: [monaco.KeyCode.Slash, monaco.KeyCode.Backslash],
      run: (ed) => {
        const position = ed.getPosition();
        if (!position) return;

        // Get the current line content.
        const lineContent = ed.getModel()?.getLineContent(position.lineNumber) || '';

        // Check whether the line is empty or the cursor is at the start.
        const isEmptyOrStart = lineContent.trim() === '' || position.column === 1;

        if (isEmptyOrStart) {
          arouseCopilot({});
        } else {
          ed.trigger('keyboard', 'type', { text: '/' });
        }
      },
    });

    // Add an extra input listener.
    const modelContentDisposable = editorIns.onDidChangeModelContent((e) => {
      const position = editorIns.getPosition();
      if (!position) return;

      const model = editorIns.getModel();
      if (!model) return;

      // Get the current line content.
      const lineContent = model.getLineContent(position.lineNumber);

      // Handle a slash just entered at the start of the line.
      if (/^[/／]$/.test(lineContent) && position.column === 2) {
        // Remove the entered slash.
        editorIns.executeEdits('', [
          {
            range: new monaco.Range(position.lineNumber, 1, position.lineNumber, 2),
            text: '',
          },
        ]);
        arouseCopilot({});
      }
    });

    const keywordDisposable = editorIns.onKeyDown((e: monaco.IKeyboardEvent) => {
      if (!editorZoneIdRef.current) return;

      if (e.code === 'Escape' || e.code === 'Enter') {
        e.stopPropagation();
        clearBuiltInCopilot();
      }
    });

    return () => {
      if (slashDisposable) {
        slashDisposable.dispose();
      }
      if (modelContentDisposable) {
        modelContentDisposable.dispose();
      }

      if (keywordDisposable) {
        keywordDisposable.dispose();
      }
    };
  }, [placeholderContentWidget, canAI]);

  // Clear the previous BuiltInCopilot.
  const clearBuiltInCopilot = () => {
    // Reset the ref.
    overlayDomRef.current = undefined;

    const editorIns = editorRef.current?.getInstance() ?? null;
    if (!editorIns) return;

    // Cleanup is needed only when a zoneId exists.
    if (!editorZoneIdRef.current) return;

    try {
      // Remove the view zone.
      editorIns.changeViewZones((changeAccessor: any) => {
        changeAccessor.removeZone(editorZoneIdRef.current);
      });

      // Clear the overlay widget.
      const overlayDom = document.getElementById('overlayId');
      if (overlayDom) {
        // Unmount the React component before removing the DOM element.
        ReactDOM.createRoot(overlayDom).unmount();
        overlayDom.remove();
      }

      // Reset the editor state.
      editorZoneIdRef.current = null;
      editorIns.focus();
      editorIns.layout();
    } catch (error) {
      console.error('Failed to clear built-in copilot:', error);
    }
  };

  // Create the BuiltInCopilot container.
  const createBuiltInCopilotOverlayWidget = () => {
    const editorIns = editorRef.current?.getInstance() ?? null;
    if (!editorIns) return;

    const overlayDom = document.createElement('div');
    overlayDom.id = 'overlayId';
    overlayDom.style.cssText = 'left: 55px; right: 28px;';

    try {
      const onResize = (height: number) => {
        if (!editorZoneIdRef.current) return;

        editorIns.changeViewZones((changeAccessor: any) => {
          changeAccessor.removeZone(editorZoneIdRef.current);
        });
        editorZoneIdRef.current = null;
        arouseCopilot({ height, clear: false });
      };

      ReactDOM.createRoot(overlayDom).render(
        <AppTheme>
          <BuiltInCopilot handleEsc={clearBuiltInCopilot} onResize={onResize} />
        </AppTheme>,
      );

      editorIns.addOverlayWidget({
        getId: () => 'overlay.zone.widget',
        getDomNode: () => overlayDom,
        getPosition: () => null,
      });

      return overlayDom;
    } catch (error) {
      console.error('Failed to create copilot overlay:', error);
      overlayDom.remove();
      return null;
    }
  };

  const arouseCopilot = ({ height = 40, clear = true }) => {
    placeholderContentWidget?.dispose();

    const editorIns = editorRef.current?.getInstance() ?? null;

    if (!canAI || !editorIns) return;

    if (clear) {
      // Clear the previous BuiltInCopilot.
      clearBuiltInCopilot();
      // Add the overlay widget.
      overlayDomRef.current = createBuiltInCopilotOverlayWidget();
    }

    // Get the current cursor line.
    const position = editorIns.getPosition();
    const lineNumber = position ? position.lineNumber : 1;

    // Add a view zone to position the overlay widget.
    editorIns.changeViewZones((changeAccessor) => {
      const zoneNode = document.createElement('div');
      zoneNode.id = uuid();
      editorZoneIdRef.current = changeAccessor.addZone({
        afterLineNumber: lineNumber - 1,
        heightInLines: height / 20, // Allow room for padding.
        domNode: zoneNode,
        onDomNodeTop: (top) => {
          if (overlayDomRef.current) {
            overlayDomRef.current.style.top = top + 'px';
          }
        },
      });
    });
  };

  // Expose the currently active BuiltInCopilot controls.
  return {
    // isArouseCopilot: !!editorRef.current?.getInstance()?.zoneId,
    arouseCopilot,
    clearBuiltInCopilot,
  };
};

export default useArouseCopilot;
