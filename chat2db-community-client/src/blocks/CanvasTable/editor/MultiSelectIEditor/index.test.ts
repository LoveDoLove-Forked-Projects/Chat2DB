import assert from 'node:assert/strict';
import type { EditContext } from '@visactor/vtable-editors';
import { MultiSelectEditor } from './index';

type Listener = () => void;

class FakeElement {
  style: Record<string, string> = {};
  children: FakeElement[] = [];
  textContent: string | null = null;
  tabIndex = 0;
  focused = false;
  attributes: Record<string, string> = {};
  private listeners = new Map<string, Listener>();

  appendChild(element: FakeElement) {
    this.children.push(element);
    return element;
  }

  removeChild(element: FakeElement) {
    this.children = this.children.filter((child) => child !== element);
    return element;
  }

  contains(target: unknown): boolean {
    return target === this || this.children.some((child) => child.contains(target));
  }

  setAttribute(name: string, value: string) {
    this.attributes[name] = value;
  }

  addEventListener(type: string, listener: Listener) {
    this.listeners.set(type, listener);
  }

  removeEventListener(type: string, listener: Listener) {
    if (this.listeners.get(type) === listener) {
      this.listeners.delete(type);
    }
  }

  dispatch(type: string) {
    this.listeners.get(type)?.();
  }

  focus() {
    this.focused = true;
  }
}

class FakeInputElement extends FakeElement {
  type = '';
  checked = false;
}

class FakeContainerElement extends FakeElement {
  clientWidth = 500;
  clientHeight = 300;
}

const fakeDocument = {
  createElement(tagName: string) {
    return tagName === 'input' ? new FakeInputElement() : new FakeElement();
  },
};

const originalDocumentDescriptor = Object.getOwnPropertyDescriptor(globalThis, 'document');
Object.defineProperty(globalThis, 'document', {
  configurable: true,
  value: fakeDocument,
});

const options = [
  { label: 'Alpha', value: 'ALPHA' },
  { label: '<img src=x onerror=alert(1)>', value: 'BETA' },
  { label: 'Gamma', value: 'GAMMA' },
];

const startEditor = (editor: MultiSelectEditor, value: unknown) => {
  const container = new FakeContainerElement();
  editor.onStart({
    container: container as unknown as HTMLElement,
    value,
    referencePosition: { rect: { top: 10, left: 20, width: 120, height: 30 } },
    endEdit: () => undefined,
    col: 1,
    row: 1,
  } as EditContext<unknown>);
  return { container, picker: container.children[0] };
};

const optionInput = (picker: FakeElement, index: number) => picker.children[index].children[0] as FakeInputElement;
const optionText = (picker: FakeElement, index: number) => picker.children[index].children[1];

try {
  const editor = new MultiSelectEditor(options, {
    colorBgContainer: '#fff',
    colorText: '#111',
    colorPrimary: '#1677ff',
    fontFamily: 'sans-serif',
    fontSize: 13,
  });
  const { container, picker } = startEditor(editor, 'ALPHA,GAMMA');

  assert.equal(editor.getValue(), 'ALPHA,GAMMA', 'onStart preserves an unmodified SET value');
  assert.equal(picker.focused, true, 'onStart opens and focuses the multi-select picker');
  assert.equal(picker.attributes.role, 'listbox', 'the picker exposes listbox semantics');
  assert.equal(picker.attributes['aria-multiselectable'], 'true', 'the picker exposes multi-select semantics');
  assert.equal(optionInput(picker, 0).checked, true, 'the first stored SET member is selected');
  assert.equal(optionInput(picker, 1).checked, false, 'an absent SET member is not selected');
  assert.equal(optionInput(picker, 2).checked, true, 'the last stored SET member is selected');
  assert.equal(
    optionText(picker, 1).textContent,
    '<img src=x onerror=alert(1)>',
    'database labels are assigned as text content',
  );
  assert.equal(picker.style.top, '42px', 'the picker opens immediately below the edited cell');
  assert.equal(picker.style.width, '160px', 'the picker has a usable minimum width');

  optionInput(picker, 1).checked = true;
  optionInput(picker, 1).dispatch('change');
  assert.equal(editor.getValue(), 'ALPHA,BETA,GAMMA', 'selected members are serialized in metadata order');

  optionInput(picker, 0).checked = false;
  optionInput(picker, 0).dispatch('change');
  optionInput(picker, 1).checked = false;
  optionInput(picker, 1).dispatch('change');
  optionInput(picker, 2).checked = false;
  optionInput(picker, 2).dispatch('change');
  assert.equal(editor.getValue(), '', 'clearing every member produces the MySQL empty SET value');

  assert.equal(editor.isEditorElement(optionInput(picker, 0) as unknown as HTMLElement), true);
  editor.onEnd();
  assert.equal(container.children.length, 0, 'onEnd removes the multi-select picker');

  const nullEditor = new MultiSelectEditor(options, {});
  const nullLifecycle = startEditor(nullEditor, null);
  assert.equal(nullEditor.getValue(), null, 'an untouched null remains null');
  nullEditor.onEnd();
  assert.equal(nullLifecycle.container.children.length, 0);

  const unknownEditor = new MultiSelectEditor(options, {});
  startEditor(unknownEditor, 'UNKNOWN');
  assert.equal(unknownEditor.getValue(), 'UNKNOWN', 'an untouched unknown value remains unchanged');
  unknownEditor.onEnd();

  console.log('MultiSelectEditor lifecycle tests passed');
} finally {
  if (originalDocumentDescriptor) {
    Object.defineProperty(globalThis, 'document', originalDocumentDescriptor);
  } else {
    Reflect.deleteProperty(globalThis, 'document');
  }
}
