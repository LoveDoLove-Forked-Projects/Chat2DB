import React from 'react';

const MarkdownCodeBlockContext = React.createContext(false);

export function MarkdownPre({ children }: React.ComponentProps<'pre'>) {
  return <MarkdownCodeBlockContext.Provider value>{children}</MarkdownCodeBlockContext.Provider>;
}

export function useIsMarkdownCodeBlock() {
  return React.useContext(MarkdownCodeBlockContext);
}

export function resolveMarkdownCode(
  className: string | undefined,
  isBlock: boolean,
): { kind: 'inline' } | { kind: 'block'; language: string } {
  const language = /language-(\w+)/.exec(className || '')?.[1];
  if (!isBlock && !language) {
    return { kind: 'inline' };
  }
  return { kind: 'block', language: language || 'text' };
}
