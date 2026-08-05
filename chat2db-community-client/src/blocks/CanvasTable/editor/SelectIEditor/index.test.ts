import assert from 'node:assert/strict';
import type { EditContext } from '@visactor/vtable-editors';
import { SelectEditor } from './index';

class FakeOptionElement {
  value = '';
  textContent: string | null = null;
  selected = false;
  disabled = false;
  style: Record<string, string> = {};
}

class FakeSelectElement {
  style: Record<string, string> = {};
  children: FakeOptionElement[] = [];
  selectedIndex = -1;
  focused = false;
  pickerOpened = false;
  throwOnShowPicker = false;
  private changeListener: (() => void) | null = null;

  set innerHTML(_value: string) {
    throw new Error('SelectEditor must not use innerHTML');
  }

  appendChild(option: FakeOptionElement) {
    this.children.push(option);
    if (option.selected) {
      this.selectedIndex = this.children.length - 1;
    }
    return option;
  }

  addEventListener(type: string, listener: () => void) {
    if (type === 'change') {
      this.changeListener = listener;
    }
  }

  removeEventListener(type: string, listener: () => void) {
    if (type === 'change' && this.changeListener === listener) {
      this.changeListener = null;
    }
  }

  focus() {
    this.focused = true;
  }

  showPicker() {
    this.pickerOpened = true;
    if (this.throwOnShowPicker) {
      throw new Error('showPicker requires user activation');
    }
  }

  contains(target: unknown) {
    return target === this;
  }

  select(index: number) {
    this.selectedIndex = index;
    this.changeListener?.();
  }

  hasChangeListener() {
    return Boolean(this.changeListener);
  }
}

class FakeContainerElement {
  children: FakeSelectElement[] = [];

  appendChild(element: FakeSelectElement) {
    this.children.push(element);
    return element;
  }

  contains(element: FakeSelectElement) {
    return this.children.includes(element);
  }

  removeChild(element: FakeSelectElement) {
    this.children = this.children.filter((child) => child !== element);
    return element;
  }
}

let nextSelectElement: FakeSelectElement | null = null;
const fakeDocument = {
  createElement(tagName: string) {
    if (tagName === 'select') {
      const select = nextSelectElement || new FakeSelectElement();
      nextSelectElement = null;
      return select;
    }
    if (tagName === 'option') {
      return new FakeOptionElement();
    }
    throw new Error(`Unexpected element: ${tagName}`);
  },
};

const originalDocumentDescriptor = Object.getOwnPropertyDescriptor(globalThis, 'document');
Object.defineProperty(globalThis, 'document', {
  configurable: true,
  value: fakeDocument,
});

const options = [
  { label: 'Pending', value: 'PENDING' },
  { label: '<img src=x onerror=alert(1)>', value: 'DANGEROUS_LABEL' },
];

const startEditor = (editor: SelectEditor, value: unknown, endEdit: () => void) => {
  const container = new FakeContainerElement();
  editor.onStart({
    container: container as unknown as HTMLElement,
    value,
    referencePosition: { rect: { top: 10, left: 20, width: 120, height: 30 } },
    endEdit,
    col: 1,
    row: 1,
  } as EditContext<unknown>);
  return { container, select: container.children[0] };
};

try {
  let endEditCount = 0;
  const editor = new SelectEditor(options, {
    colorBgContainer: '#fff',
    colorText: '#111',
    colorPrimary: '#1677ff',
    fontFamily: 'sans-serif',
    fontSize: 13,
  });
  const { container, select } = startEditor(editor, null, () => {
    endEditCount += 1;
  });

  assert.equal(editor.getValue(), null, 'onStart preserves an unmodified null value');
  assert.equal(select.children[0].textContent, '<null>', 'null uses the existing result-grid label');
  assert.equal(
    select.children[2].textContent,
    '<img src=x onerror=alert(1)>',
    'database labels are assigned as text content',
  );
  assert.equal(select.focused, true, 'onStart focuses the select element');
  assert.equal(select.pickerOpened, true, 'onStart opens the select picker immediately');
  assert.equal(select.style.top, '11px', 'onStart positions the editor inside the cell');
  assert.equal(select.style.width, '118px', 'onStart sizes the editor inside the cell');
  assert.equal(select.hasChangeListener(), true, 'onStart binds the change listener');

  select.select(1);
  assert.equal(editor.getValue(), 'PENDING', 'selecting an option updates the editor value');
  assert.equal(endEditCount, 1, 'selecting an option commits the VTable edit');

  editor.onEnd();
  assert.equal(container.children.length, 0, 'onEnd removes the select element');
  assert.equal(select.hasChangeListener(), false, 'onEnd removes the change listener');

  const rejectedPickerEditor = new SelectEditor(options, {});
  const rejectedPickerSelect = new FakeSelectElement();
  rejectedPickerSelect.throwOnShowPicker = true;
  nextSelectElement = rejectedPickerSelect;
  assert.doesNotThrow(
    () => startEditor(rejectedPickerEditor, 'PENDING', () => undefined),
    'a rejected showPicker call does not prevent editing',
  );
  assert.equal(rejectedPickerSelect.pickerOpened, true, 'onStart attempts to open the picker');
  rejectedPickerEditor.onEnd();

  const unsupportedPickerEditor = new SelectEditor(options, {});
  const unsupportedPickerSelect = new FakeSelectElement();
  (unsupportedPickerSelect as Partial<Pick<HTMLSelectElement, 'showPicker'>>).showPicker = undefined;
  nextSelectElement = unsupportedPickerSelect;
  assert.doesNotThrow(
    () => startEditor(unsupportedPickerEditor, 'PENDING', () => undefined),
    'a browser without showPicker still starts editing',
  );
  assert.equal(unsupportedPickerSelect.focused, true, 'the unsupported browser fallback still focuses the select');
  unsupportedPickerEditor.onEnd();

  const unchangedValues: Array<[unknown, string]> = [
    ['NOT_IN_METADATA', 'NOT_IN_METADATA'],
    ['CHAT2DB_UPDATE_TABLE_DATA_USER_FILLED_DEFAULT', '<default>'],
    ['CHAT2DB_UPDATE_TABLE_DATA_USER_FILLED_GENERATED', '<generated>'],
  ];
  unchangedValues.forEach(([value, label]) => {
    const unchangedEditor = new SelectEditor(options, {});
    const unchangedLifecycle = startEditor(unchangedEditor, value, () => undefined);
    assert.equal(unchangedEditor.getValue(), value, `${label} remains unchanged without a selection`);
    assert.equal(unchangedLifecycle.select.children[0].textContent, label, `${label} uses a safe placeholder label`);
    unchangedEditor.onEnd();
  });

  let firstCommitCount = 0;
  let secondCommitCount = 0;
  const firstEditor = new SelectEditor([{ label: 'First', value: 'FIRST' }], {});
  const secondEditor = new SelectEditor([{ label: 'Second', value: 'SECOND' }], {});
  const firstLifecycle = startEditor(firstEditor, 'UNKNOWN_FIRST', () => {
    firstCommitCount += 1;
  });
  const secondLifecycle = startEditor(secondEditor, 'UNKNOWN_SECOND', () => {
    secondCommitCount += 1;
  });

  firstLifecycle.select.select(1);
  assert.equal(firstEditor.getValue(), 'FIRST', 'the first column uses its own option list');
  assert.equal(secondEditor.getValue(), 'UNKNOWN_SECOND', 'editing one column does not change another editor instance');
  assert.equal(firstCommitCount, 1, 'the first editor invokes only its own callback');
  assert.equal(secondCommitCount, 0, 'the second editor callback remains untouched');
  assert.equal(secondLifecycle.select.children[1].textContent, 'Second', 'the second column keeps its own label');
  firstEditor.onEnd();
  secondEditor.onEnd();

  console.log('SelectEditor lifecycle tests passed');
} finally {
  if (originalDocumentDescriptor) {
    Object.defineProperty(globalThis, 'document', originalDocumentDescriptor);
  } else {
    Reflect.deleteProperty(globalThis, 'document');
  }
}
