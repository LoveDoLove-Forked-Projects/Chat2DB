import assert from 'node:assert/strict';

const globalObj = globalThis as unknown as Record<string, unknown>;
globalObj.__RUNTIME_ENV__ = 'community';
globalObj.__ENV__ = 'test';
globalObj.window = {};

if (typeof globalThis.navigator === 'undefined') {
  Object.defineProperty(globalThis, 'navigator', {
    value: { userAgent: 'Mac' },
    configurable: true,
  });
}

async function run() {
  const [{ getEffectiveShortcutConfigMap, ShortcutAction }, { resolveShortcutDispatch }] = await Promise.all([
    import('@/constants/shortcut'),
    import('./shortcutDispatch'),
  ]);
  const event = {
    key: 's',
    code: 'KeyS',
    metaKey: false,
    ctrlKey: true,
    altKey: false,
    shiftKey: false,
  } as KeyboardEvent;
  const shortcutConfig = getEffectiveShortcutConfigMap({
    [ShortcutAction.SqlSave]: { binding: 'Ctrl + S' },
  });

  assert.deepEqual(
    resolveShortcutDispatch(event, shortcutConfig, { editableTarget: false, workspaceActive: true }),
    { kind: 'workspace-save' },
    'non-editor workspace focus routes the save shortcut to the active editor',
  );
  assert.equal(
    resolveShortcutDispatch(event, shortcutConfig, { editableTarget: true, workspaceActive: true }),
    undefined,
    'editor-owned shortcuts remain inside the editor',
  );
  assert.equal(
    resolveShortcutDispatch(event, shortcutConfig, { editableTarget: false, workspaceActive: false }),
    undefined,
    'workspace save must not affect a hidden editor from another page',
  );

  const conflictingConfig = getEffectiveShortcutConfigMap({
    [ShortcutAction.SqlSave]: { binding: 'Ctrl + S' },
    [ShortcutAction.SwitchToChat]: { binding: 'Ctrl + S' },
  });
  assert.deepEqual(
    resolveShortcutDispatch(event, conflictingConfig, { editableTarget: false, workspaceActive: true }),
    { kind: 'global', action: ShortcutAction.SwitchToChat },
    'global shortcuts retain precedence over a SQL-editor shortcut with the same binding',
  );

  console.log('shortcut dispatch tests passed');
}

void run();
