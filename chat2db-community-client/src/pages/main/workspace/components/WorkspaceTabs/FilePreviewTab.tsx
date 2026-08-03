import { memo, useEffect, useId, useMemo, useRef, useState } from 'react';
import { MinusOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Segmented, Tooltip } from 'antd';
import { Code, Columns2, Eye } from 'lucide-react';
import ReactMarkdown, { type Components } from 'react-markdown';
import rehypeKatex from 'rehype-katex';
import remarkGfm from 'remark-gfm';
import remarkMath from 'remark-math';
import { WorkspaceTabType } from '@/constants';
import type { EditorType } from '@/components/SQLEditor';
import jcefApi from '@/jcef';
import i18n from '@/i18n';
import { useWorkspaceStore } from '@/store/workspace';
import type { IBoundInfo } from '@/typings';
import SQLExecute from '../SQLExecute';
import 'katex/dist/katex.min.css';
import styles from './FilePreviewTab.less';

interface FilePreviewTabProps {
  file: IBoundInfo;
  boundInfo: IBoundInfo;
  workspaceTabId: string | number;
  workspaceTabsTitle?: string;
}

type MarkdownViewMode = 'source' | 'review' | 'split';
type ScrollSyncOwner = 'source' | 'review' | null;

const MIN_IMAGE_ZOOM = 0.25;
const MAX_IMAGE_ZOOM = 5;
const IMAGE_ZOOM_STEP = 0.25;

function MermaidDiagram({ source }: { source: string }) {
  const reactId = useId();
  const diagramId = useMemo(() => `chat2db-mermaid-${reactId.replace(/[^a-zA-Z0-9_-]/g, '')}`, [reactId]);
  const renderHostRef = useRef<HTMLDivElement>(null);
  const [svg, setSvg] = useState('');
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true;
    setSvg('');
    setError(false);
    import('mermaid')
      .then(async ({ default: mermaid }) => {
        mermaid.initialize({
          startOnLoad: false,
          securityLevel: 'strict',
          theme: 'neutral',
          fontFamily: 'inherit',
        });
        const parseResult = await mermaid.parse(source, { suppressErrors: true });
        if (!parseResult) {
          return undefined;
        }
        return mermaid.render(diagramId, source, renderHostRef.current || undefined);
      })
      .then((renderResult) => {
        if (active && renderResult) {
          const { svg: renderedSvg } = renderResult;
          setSvg(renderedSvg);
        } else if (active) {
          setError(true);
        }
      })
      .catch((renderError) => {
        console.error('render mermaid preview error', renderError);
        if (active) {
          setError(true);
        }
      });
    return () => {
      active = false;
    };
  }, [diagramId, source]);

  return (
    <>
      <div ref={renderHostRef} className={styles.mermaidRenderHost} aria-hidden="true" />
      {error && <pre className={styles.diagramError}>{i18n('workspace.filePreview.mermaidFailed')}</pre>}
      {!error && !svg && <div className={styles.diagramLoading}>{i18n('common.text.loading')}</div>}
      {!error && svg && <div className={styles.diagram} dangerouslySetInnerHTML={{ __html: svg }} />}
    </>
  );
}

function resolveRelativePath(markdownPath: string | undefined, source: string) {
  if (!markdownPath || !source || /^(?:[a-z]+:|#|\/)/i.test(source)) {
    return undefined;
  }
  const segments = markdownPath.replace(/\\/g, '/').split('/');
  segments.pop();
  for (const segment of source.replace(/\\/g, '/').split('/')) {
    if (!segment || segment === '.') {
      continue;
    }
    if (segment === '..') {
      segments.pop();
    } else {
      segments.push(segment);
    }
  }
  return segments.join('/');
}

function MarkdownImage({
  alt,
  src,
  file,
}: {
  alt?: string;
  src?: string;
  file: IBoundInfo;
}) {
  const [resolvedSource, setResolvedSource] = useState(src);
  const relativePath = resolveRelativePath(file.fileRelativePath, src || '');

  useEffect(() => {
    let active = true;
    if (!file.fileRootToken || !relativePath) {
      setResolvedSource(src);
      return;
    }
    jcefApi
      .readSqlDirectoryPreview({ rootToken: file.fileRootToken, relativePath })
      .then((preview) => {
        if (active) {
          setResolvedSource(preview.dataUrl);
        }
      })
      .catch((error) => {
        console.error('read markdown image error', error);
        if (active) {
          setResolvedSource(undefined);
        }
      });
    return () => {
      active = false;
    };
  }, [file.fileRootToken, relativePath, src]);

  if (!resolvedSource) {
    return <span className={styles.brokenImage}>{alt || i18n('workspace.filePreview.imageFailed')}</span>;
  }
  return <img alt={alt || ''} src={resolvedSource} loading="lazy" />;
}

const FilePreviewTab = memo(({ file, boundInfo, workspaceTabId, workspaceTabsTitle }: FilePreviewTabProps) => {
  const extension = (file.fileExtension || '').toLowerCase();
  const isMarkdown = extension === 'md' || extension === 'markdown';
  const [markdownViewMode, setMarkdownViewMode] = useState<MarkdownViewMode>('review');
  const [markdownContent, setMarkdownContent] = useState(file.ddl || '');
  const [imageZoom, setImageZoom] = useState(1);
  const [imageNaturalSize, setImageNaturalSize] = useState({ width: 0, height: 0 });
  const [imageViewportSize, setImageViewportSize] = useState({ width: 0, height: 0 });
  const markdownReviewRef = useRef<HTMLDivElement>(null);
  const imageViewportRef = useRef<HTMLDivElement>(null);
  const scrollSyncOwnerRef = useRef<ScrollSyncOwner>(null);
  const scrollSyncFrameRef = useRef<number | undefined>(undefined);
  const editorRef = useWorkspaceStore((state) => state.editorList?.[workspaceTabId]);
  const editor = editorRef?.getInstance?.();

  useEffect(() => {
    setMarkdownContent(file.ddl || '');
    setMarkdownViewMode('review');
    setImageZoom(1);
    setImageNaturalSize({ width: 0, height: 0 });
  }, [file.filePath]);

  useEffect(() => {
    const viewport = imageViewportRef.current;
    if (!viewport) {
      return undefined;
    }
    const updateViewportSize = () => {
      setImageViewportSize({
        width: viewport.clientWidth,
        height: viewport.clientHeight,
      });
    };
    updateViewportSize();
    const resizeObserver = new ResizeObserver(updateViewportSize);
    resizeObserver.observe(viewport);
    return () => resizeObserver.disconnect();
  }, [file.filePath, file.filePreviewMimeType]);

  useEffect(() => {
    if (
      markdownViewMode !== 'split' ||
      !editor ||
      typeof editor.onDidScrollChange !== 'function' ||
      typeof editor.getScrollHeight !== 'function' ||
      typeof editor.getLayoutInfo !== 'function' ||
      typeof editor.getScrollTop !== 'function'
    ) {
      return undefined;
    }
    const releaseScrollOwner = (owner: ScrollSyncOwner) => {
      if (scrollSyncFrameRef.current) {
        cancelAnimationFrame(scrollSyncFrameRef.current);
      }
      scrollSyncFrameRef.current = requestAnimationFrame(() => {
        if (scrollSyncOwnerRef.current === owner) {
          scrollSyncOwnerRef.current = null;
        }
      });
    };
    const syncReviewScroll = () => {
      try {
        if (scrollSyncOwnerRef.current === 'review') {
          return;
        }
        const reviewScroller = markdownReviewRef.current;
        if (!reviewScroller) {
          return;
        }
        const sourceMaxScroll = Math.max(0, editor.getScrollHeight() - editor.getLayoutInfo().height);
        const reviewMaxScroll = Math.max(0, reviewScroller.scrollHeight - reviewScroller.clientHeight);
        const ratio = sourceMaxScroll ? editor.getScrollTop() / sourceMaxScroll : 0;
        scrollSyncOwnerRef.current = 'source';
        reviewScroller.scrollTop = ratio * reviewMaxScroll;
        releaseScrollOwner('source');
      } catch (error) {
        scrollSyncOwnerRef.current = null;
        console.error('sync markdown review scroll error', error);
      }
    };
    let scrollDisposable: { dispose: () => void } | undefined;
    try {
      scrollDisposable = editor.onDidScrollChange((event: { scrollTopChanged: boolean }) => {
        if (event.scrollTopChanged) {
          syncReviewScroll();
        }
      });
      syncReviewScroll();
    } catch (error) {
      console.error('initialize markdown scroll sync error', error);
    }
    return () => {
      scrollDisposable?.dispose();
      if (scrollSyncFrameRef.current) {
        cancelAnimationFrame(scrollSyncFrameRef.current);
      }
      scrollSyncOwnerRef.current = null;
    };
  }, [editor, markdownViewMode]);

  const handleReviewScroll = () => {
    if (
      markdownViewMode !== 'split' ||
      !editor ||
      typeof editor.setScrollTop !== 'function' ||
      typeof editor.getScrollHeight !== 'function' ||
      typeof editor.getLayoutInfo !== 'function' ||
      scrollSyncOwnerRef.current === 'source'
    ) {
      return;
    }
    const reviewScroller = markdownReviewRef.current;
    if (!reviewScroller) {
      return;
    }
    try {
      const reviewMaxScroll = Math.max(0, reviewScroller.scrollHeight - reviewScroller.clientHeight);
      const sourceMaxScroll = Math.max(0, editor.getScrollHeight() - editor.getLayoutInfo().height);
      const ratio = reviewMaxScroll ? reviewScroller.scrollTop / reviewMaxScroll : 0;
      scrollSyncOwnerRef.current = 'review';
      editor.setScrollTop(ratio * sourceMaxScroll);
      if (scrollSyncFrameRef.current) {
        cancelAnimationFrame(scrollSyncFrameRef.current);
      }
      scrollSyncFrameRef.current = requestAnimationFrame(() => {
        if (scrollSyncOwnerRef.current === 'review') {
          scrollSyncOwnerRef.current = null;
        }
      });
    } catch (error) {
      scrollSyncOwnerRef.current = null;
      console.error('sync markdown source scroll error', error);
    }
  };

  const markdownComponents = useMemo<Components>(
    () => ({
      a: ({ href, children }) => (
        <a href={href} target="_blank" rel="noreferrer noopener">
          {children}
        </a>
      ),
      code: ({ className, children, ...props }) => {
        const language = /language-([^\s]+)/.exec(className || '')?.[1]?.toLowerCase();
        const source = String(children).replace(/\n$/, '');
        if (language === 'mermaid') {
          return <MermaidDiagram source={source} />;
        }
        return (
          <code className={className} {...props}>
            {children}
          </code>
        );
      },
      img: ({ alt, src }) => <MarkdownImage alt={alt} src={src} file={file} />,
    }),
    [file],
  );

  if (isMarkdown) {
    const review = (
      <div ref={markdownReviewRef} className={styles.markdownScroller} onScroll={handleReviewScroll}>
        <article className={styles.markdown}>
          <ReactMarkdown
            remarkPlugins={[remarkGfm, remarkMath]}
            rehypePlugins={[rehypeKatex]}
            components={markdownComponents}
          >
            {markdownContent}
          </ReactMarkdown>
        </article>
      </div>
    );
    const source = (
      <SQLExecute
        boundInfo={{ ...boundInfo, workspaceTabId }}
        type={WorkspaceTabType.LocalSQLFile as EditorType}
        initDDL={markdownContent}
        workspaceTabsTitle={workspaceTabsTitle}
        sqlActionEnabled={false}
        onEditorChange={setMarkdownContent}
      />
    );

    return (
      <div className={styles.markdownWorkspace}>
        <div className={styles.markdownToolbar}>
          <Segmented
            size="small"
            value={markdownViewMode}
            onChange={(value) => setMarkdownViewMode(value as MarkdownViewMode)}
            options={[
              {
                label: (
                  <Tooltip title={i18n('workspace.filePreview.source')}>
                    <span className={styles.viewModeIcon} aria-label={i18n('workspace.filePreview.source')}>
                      <Code size={16} />
                    </span>
                  </Tooltip>
                ),
                value: 'source',
              },
              {
                label: (
                  <Tooltip title={i18n('workspace.filePreview.preview')}>
                    <span className={styles.viewModeIcon} aria-label={i18n('workspace.filePreview.preview')}>
                      <Eye size={16} />
                    </span>
                  </Tooltip>
                ),
                value: 'review',
              },
              {
                label: (
                  <Tooltip title={i18n('workspace.filePreview.split')}>
                    <span className={styles.viewModeIcon} aria-label={i18n('workspace.filePreview.split')}>
                      <Columns2 size={16} />
                    </span>
                  </Tooltip>
                ),
                value: 'split',
              },
            ]}
          />
        </div>
        <div className={styles.markdownContent}>
          {markdownViewMode === 'source' && <div className={styles.sourcePane}>{source}</div>}
          {markdownViewMode === 'review' && <div className={styles.reviewPane}>{review}</div>}
          {markdownViewMode === 'split' && (
            <div className={styles.splitPane}>
              <div className={styles.sourcePane}>{source}</div>
              <div className={styles.reviewPane}>{review}</div>
            </div>
          )}
        </div>
      </div>
    );
  }

  if (file.filePreviewMimeType?.startsWith('image/') && file.filePreviewDataUrl) {
    const fitScale =
      imageNaturalSize.width && imageNaturalSize.height && imageViewportSize.width && imageViewportSize.height
        ? Math.min(
            1,
            imageViewportSize.width / imageNaturalSize.width,
            imageViewportSize.height / imageNaturalSize.height,
          )
        : 1;
    const renderedScale = fitScale * imageZoom;
    const renderedWidth = imageNaturalSize.width * renderedScale;
    const renderedHeight = imageNaturalSize.height * renderedScale;
    return (
      <div className={styles.imagePreview}>
        <div className={styles.imageToolbar}>
          <Tooltip title={i18n('setting.shortcut.zoomOut')}>
            <Button
              type="text"
              size="small"
              icon={<MinusOutlined />}
              aria-label={i18n('setting.shortcut.zoomOut')}
              disabled={imageZoom <= MIN_IMAGE_ZOOM}
              onClick={() => setImageZoom((zoom) => Math.max(MIN_IMAGE_ZOOM, zoom - IMAGE_ZOOM_STEP))}
            />
          </Tooltip>
          <Tooltip title={i18n('setting.shortcut.zoomReset')}>
            <Button type="text" size="small" className={styles.imageZoomValue} onClick={() => setImageZoom(1)}>
              {Math.round(imageZoom * 100)}%
            </Button>
          </Tooltip>
          <Tooltip title={i18n('setting.shortcut.zoomIn')}>
            <Button
              type="text"
              size="small"
              icon={<PlusOutlined />}
              aria-label={i18n('setting.shortcut.zoomIn')}
              disabled={imageZoom >= MAX_IMAGE_ZOOM}
              onClick={() => setImageZoom((zoom) => Math.min(MAX_IMAGE_ZOOM, zoom + IMAGE_ZOOM_STEP))}
            />
          </Tooltip>
        </div>
        <div ref={imageViewportRef} className={styles.imageScroller}>
          <div
            className={styles.imageCanvas}
            style={{
              width: Math.max(imageViewportSize.width, renderedWidth),
              height: Math.max(imageViewportSize.height, renderedHeight),
            }}
          >
            <img
              src={file.filePreviewDataUrl}
              alt={file.filePath || ''}
              style={imageNaturalSize.width ? { width: renderedWidth, height: renderedHeight } : undefined}
              onLoad={(event) =>
                setImageNaturalSize({
                  width: event.currentTarget.naturalWidth,
                  height: event.currentTarget.naturalHeight,
                })
              }
            />
          </div>
        </div>
      </div>
    );
  }

  if (file.filePreviewMimeType === 'application/pdf' && file.filePreviewDataUrl) {
    return (
      <object
        className={styles.pdfPreview}
        data={file.filePreviewDataUrl}
        type="application/pdf"
        aria-label={file.filePath || 'PDF'}
      >
        <div className={styles.previewFallback}>{i18n('workspace.filePreview.pdfUnsupported')}</div>
      </object>
    );
  }

  return <div className={styles.previewFallback}>{i18n('workspace.filePreview.unsupported')}</div>;
});

export default FilePreviewTab;
