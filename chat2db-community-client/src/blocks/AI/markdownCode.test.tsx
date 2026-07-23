import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import ReactMarkdown from 'react-markdown';
import { MarkdownPre, resolveMarkdownCode, useIsMarkdownCodeBlock } from './markdownCode';

function ProbeCode({ className, children }: { className?: string; children?: React.ReactNode }) {
  const code = resolveMarkdownCode(className, useIsMarkdownCodeBlock());
  return (
    <code data-kind={code.kind} data-language={code.kind === 'block' ? code.language : undefined}>
      {children}
    </code>
  );
}

function render(markdown: string) {
  return renderToStaticMarkup(
    <ReactMarkdown
      components={{
        pre: MarkdownPre,
        code: ProbeCode as React.ComponentType<React.HTMLAttributes<HTMLElement>>,
      }}
    >
      {markdown}
    </ReactMarkdown>,
  );
}

const fence = '`'.repeat(3);

assert.match(render('Use `SELECT 1` here.'), /<code data-kind="inline">SELECT 1<\/code>/);
assert.match(
  render(`${fence}\nSELECT 1\nFROM dual\n${fence}`),
  /^<code data-kind="block" data-language="text">SELECT 1\nFROM dual\n<\/code>$/,
);
assert.match(
  render('    SELECT 1\n    FROM dual'),
  /^<code data-kind="block" data-language="text">SELECT 1\nFROM dual\n<\/code>$/,
);
assert.match(
  render(`${fence}sql\nSELECT 1\n${fence}`),
  /^<code data-kind="block" data-language="sql">SELECT 1\n<\/code>$/,
);

console.log('AI Markdown code context tests passed');
