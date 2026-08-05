import type { EditContext, IEditor, RectProps } from '@visactor/vtable-editors';
import type { IResultSetEditorOption } from '@/typings/database';
import { normalizeSelectEditorOptions, type SelectEditorTheme } from '../SelectIEditor';

const MAX_PICKER_HEIGHT = 224;
const MIN_PICKER_WIDTH = 160;

const selectedValuesFrom = (value: unknown) => {
  if (typeof value !== 'string' || value.length === 0) {
    return new Set<string>();
  }
  return new Set(value.split(','));
};

export class MultiSelectEditor implements IEditor<unknown> {
  readonly options: IResultSetEditorOption[];
  private readonly theme: SelectEditorTheme;
  private container: HTMLElement | null = null;
  private element: HTMLDivElement | null = null;
  private optionInputs: Array<{ input: HTMLInputElement; value: string }> = [];
  private originalValue: unknown = null;
  private currentValue: unknown = null;
  private changed = false;

  constructor(options: readonly IResultSetEditorOption[], theme: SelectEditorTheme) {
    this.options = normalizeSelectEditorOptions(options);
    this.theme = theme;
  }

  private createElement(value: unknown) {
    const picker = document.createElement('div');
    picker.tabIndex = -1;
    picker.setAttribute('role', 'listbox');
    picker.setAttribute('aria-multiselectable', 'true');
    picker.style.position = 'absolute';
    picker.style.boxSizing = 'border-box';
    picker.style.padding = '4px 0';
    picker.style.border = `1px solid ${this.theme.colorPrimary || this.theme.colorBorder || 'transparent'}`;
    picker.style.borderRadius = '4px';
    picker.style.outline = 'none';
    picker.style.overflowY = 'auto';
    picker.style.zIndex = '10';
    picker.style.color = this.theme.colorText || '';
    picker.style.backgroundColor = this.theme.colorBgContainer || '';
    picker.style.fontFamily = this.theme.fontFamily || '';
    picker.style.boxShadow = '0 3px 10px rgba(0, 0, 0, 0.16)';
    if (this.theme.fontSize) {
      picker.style.fontSize = `${this.theme.fontSize}px`;
    }

    const selectedValues = selectedValuesFrom(value);
    this.options.forEach((option) => {
      const label = document.createElement('label');
      label.style.display = 'flex';
      label.style.alignItems = 'center';
      label.style.gap = '8px';
      label.style.minHeight = '28px';
      label.style.padding = '2px 8px';
      label.style.cursor = 'pointer';

      const input = document.createElement('input');
      input.type = 'checkbox';
      input.checked = selectedValues.has(option.value);
      input.style.margin = '0';
      input.style.accentColor = this.theme.colorPrimary || '';
      input.addEventListener('change', this.handleChange);

      const text = document.createElement('span');
      text.textContent = option.label;
      text.style.minWidth = '0';
      text.style.overflowWrap = 'anywhere';

      label.appendChild(input);
      label.appendChild(text);
      picker.appendChild(label);
      this.optionInputs.push({ input, value: option.value });
    });

    this.element = picker;
    this.container?.appendChild(picker);
  }

  private handleChange = () => {
    this.currentValue = this.optionInputs
      .filter(({ input }) => input.checked)
      .map(({ value }) => value)
      .join(',');
    this.changed = true;
  };

  getValue() {
    return this.changed ? this.currentValue : this.originalValue;
  }

  setValue(value: unknown) {
    this.originalValue = value;
    this.currentValue = value;
    this.changed = false;
  }

  onStart({ container, value, referencePosition }: EditContext<unknown>) {
    this.container = container;
    this.optionInputs = [];
    this.setValue(value);
    this.createElement(value);
    if (referencePosition?.rect) {
      this.adjustPosition(referencePosition.rect);
    }
    this.element?.focus();
  }

  private adjustPosition(rect: RectProps) {
    if (!this.element) {
      return;
    }
    const estimatedHeight = Math.min(this.options.length * 32 + 8, MAX_PICKER_HEIGHT);
    const availableBelow = Math.max((this.container?.clientHeight || 0) - rect.top - rect.height, 0);
    const openAbove = availableBelow < Math.min(estimatedHeight, 96) && rect.top > availableBelow;
    const desiredWidth = Math.max(rect.width - 2, MIN_PICKER_WIDTH);
    const containerWidth = this.container?.clientWidth || 0;
    const width = containerWidth > 0 ? Math.min(desiredWidth, Math.max(containerWidth - 2, 0)) : desiredWidth;
    const left = containerWidth > 0 ? Math.min(rect.left + 1, Math.max(containerWidth - width - 1, 0)) : rect.left + 1;

    this.element.style.top = `${openAbove ? Math.max(rect.top - estimatedHeight - 2, 0) : rect.top + rect.height + 2}px`;
    this.element.style.left = `${left}px`;
    this.element.style.width = `${width}px`;
    this.element.style.maxHeight = `${MAX_PICKER_HEIGHT}px`;
  }

  onEnd() {
    this.optionInputs.forEach(({ input }) => input.removeEventListener('change', this.handleChange));
    if (this.element && this.container?.contains(this.element)) {
      this.container.removeChild(this.element);
    }
    this.element = null;
    this.container = null;
    this.optionInputs = [];
  }

  isEditorElement(target: HTMLElement) {
    return target === this.element || Boolean(this.element?.contains(target));
  }
}
