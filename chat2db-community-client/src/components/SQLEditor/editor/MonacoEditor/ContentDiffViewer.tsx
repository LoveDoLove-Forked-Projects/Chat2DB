import * as monaco from 'monaco-editor';
import { ContentDiffKind } from '../../type';

export interface ContentDiffInlineViewState {
  afterLineNumber: number;
  baselineText: string;
  currentText: string;
  heightInLines: number;
  onNext?: () => void;
  onPrevious?: () => void;
  onRevert?: () => void;
}

export interface ContentDiffInlineView {
  dispose: () => void;
}

type DiffLineKind = ContentDiffKind.Deleted | ContentDiffKind.Added;

export const createContentDiffInlineView = (
  editor: monaco.editor.IStandaloneCodeEditor,
  value: ContentDiffInlineViewState,
  onClose: () => void,
): ContentDiffInlineView => {
  const container = document.createElement('div');
  const panel = document.createElement('div');
  const toolbar = document.createElement('div');
  const body = document.createElement('div');
  const revertButton = createToolbarIconButton('content-diff-inline-action', 'icon-huigun', 'Revert this change');
  const nextButton = createToolbarIconButton('content-diff-inline-action', 'icon-down-arrow', 'Next change');
  const previousButton = createToolbarIconButton('content-diff-inline-action', 'icon-up-arrow', 'Previous change');
  const closeButton = createToolbarIconButton('content-diff-inline-close', 'icon-close', 'Close diff');
  const lineHeight = editor.getOption(monaco.editor.EditorOption.lineHeight) || 20;
  const heightInPx = Math.min(Math.max(value.heightInLines * lineHeight + 36, 96), 360);
  const panelWidth = getPanelWidth(editor, value);
  let zoneId: string | null = null;
  let disposed = false;

  container.className = 'content-diff-inline-viewer';
  panel.className = 'content-diff-inline-panel';
  panel.style.width = `${panelWidth}px`;
  toolbar.className = 'content-diff-inline-toolbar';
  body.className = 'content-diff-inline-body';
  revertButton.disabled = !value.onRevert;
  nextButton.disabled = !value.onNext;
  previousButton.disabled = !value.onPrevious;
  toolbar.appendChild(revertButton);
  toolbar.appendChild(nextButton);
  toolbar.appendChild(previousButton);
  toolbar.appendChild(closeButton);
  panel.appendChild(toolbar);
  panel.appendChild(body);
  container.appendChild(panel);
  renderDiffRows(body, value.baselineText, ContentDiffKind.Deleted, lineHeight);
  renderDiffRows(body, value.currentText, ContentDiffKind.Added, lineHeight);

  editor.changeViewZones((accessor) => {
    zoneId = accessor.addZone({
      afterLineNumber: value.afterLineNumber,
      domNode: container,
      heightInPx,
    });
  });

  const closeOnEscape = (event: monaco.IKeyboardEvent) => {
    if (event.keyCode !== monaco.KeyCode.Escape) {
      return;
    }

    event.preventDefault();
    event.stopPropagation();
    dispose();
  };
  const escapeDisposable = editor.onKeyDown(closeOnEscape);
  const handleContainerKeyDown = (event: KeyboardEvent) => {
    if (event.key !== 'Escape') {
      return;
    }

    event.preventDefault();
    event.stopPropagation();
    dispose();
  };
  const handleRevert = () => {
    runContentDiffViewerSafely(() => value.onRevert?.());
  };
  const handleNext = () => {
    runContentDiffViewerSafely(() => value.onNext?.());
  };
  const handlePrevious = () => {
    runContentDiffViewerSafely(() => value.onPrevious?.());
  };

  const dispose = () => {
    if (disposed) {
      return;
    }
    disposed = true;
    runContentDiffViewerSafely(() => escapeDisposable.dispose());
    closeButton.removeEventListener('click', dispose);
    revertButton.removeEventListener('click', handleRevert);
    nextButton.removeEventListener('click', handleNext);
    previousButton.removeEventListener('click', handlePrevious);
    container.removeEventListener('keydown', handleContainerKeyDown);
    if (zoneId) {
      runContentDiffViewerSafely(() =>
        editor.changeViewZones((accessor) => {
          accessor.removeZone(zoneId!);
        }),
      );
      zoneId = null;
    }
    runContentDiffViewerSafely(onClose);
  };

  closeButton.addEventListener('click', dispose);
  revertButton.addEventListener('click', handleRevert);
  nextButton.addEventListener('click', handleNext);
  previousButton.addEventListener('click', handlePrevious);
  container.addEventListener('keydown', handleContainerKeyDown);
  requestAnimationFrame(() =>
    runContentDiffViewerSafely(() => {
      editor.layout();
      editor.revealLineInCenter(value.afterLineNumber);
    }),
  );

  return { dispose };
};

const createToolbarIconButton = (className: string, iconCode: string, title: string) => {
  const button = document.createElement('button');
  const icon = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  const use = document.createElementNS('http://www.w3.org/2000/svg', 'use');

  button.type = 'button';
  button.className = className;
  button.title = title;
  icon.classList.add('content-diff-inline-action-icon');
  icon.setAttribute('aria-hidden', 'true');
  use.setAttribute('href', `#${iconCode}`);
  use.setAttributeNS('http://www.w3.org/1999/xlink', 'xlink:href', `#${iconCode}`);
  icon.appendChild(use);
  button.appendChild(icon);

  return button;
};

const renderDiffRows = (container: HTMLElement, text: string, kind: DiffLineKind, lineHeight: number) => {
  if (!text) {
    return;
  }

  text.split('\n').forEach((line) => {
    const row = document.createElement('div');
    const sign = document.createElement('span');
    const code = document.createElement('span');

    row.className = `content-diff-inline-row content-diff-inline-row-${kind}`;
    row.style.minHeight = `${lineHeight}px`;
    row.style.lineHeight = `${lineHeight}px`;
    sign.className = 'content-diff-inline-sign';
    sign.textContent = kind === ContentDiffKind.Deleted ? '-' : '+';
    code.className = 'content-diff-inline-code';
    code.textContent = line || ' ';

    row.appendChild(sign);
    row.appendChild(code);
    container.appendChild(row);
  });
};

const getPanelWidth = (editor: monaco.editor.IStandaloneCodeEditor, value: ContentDiffInlineViewState) => {
  const layoutInfo = editor.getLayoutInfo();
  const fontInfo = editor.getOption(monaco.editor.EditorOption.fontInfo);
  const maxLineLength = Math.max(
    ...[value.baselineText, value.currentText].flatMap((text) => text.split('\n')).map((line) => line.length),
    0,
  );
  const contentWidth = 28 + maxLineLength * fontInfo.typicalHalfwidthCharacterWidth + 36;
  const minWidth = 280;
  const maxWidth = Math.min(720, Math.max(280, layoutInfo.contentWidth - 24));

  return Math.ceil(Math.min(Math.max(contentWidth, minWidth), maxWidth));
};

const runContentDiffViewerSafely = (task: () => void) => {
  try {
    task();
  } catch {
    // Content diff viewer is auxiliary and must not interrupt editor workflows.
  }
};
