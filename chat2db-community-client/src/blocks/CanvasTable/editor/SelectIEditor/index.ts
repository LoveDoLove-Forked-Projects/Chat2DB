import type { EditContext, IEditor, RectProps } from '@visactor/vtable-editors';
import type { IResultSetEditorOption } from '@/typings/database';

export interface SelectEditorTheme {
  colorBgContainer?: string;
  colorText?: string;
  colorBorder?: string;
  colorPrimary?: string;
  fontFamily?: string;
  fontSize?: number;
}

const ORIGINAL_VALUE = Symbol('original-value');
const DEFAULT_VALUE = 'CHAT2DB_UPDATE_TABLE_DATA_USER_FILLED_DEFAULT';
const GENERATED_VALUE = 'CHAT2DB_UPDATE_TABLE_DATA_USER_FILLED_GENERATED';

export const normalizeSelectEditorOptions = (options?: readonly IResultSetEditorOption[]): IResultSetEditorOption[] => {
  if (!Array.isArray(options)) {
    return [];
  }

  return options
    .filter(
      (option): option is IResultSetEditorOption =>
        Boolean(option) && typeof option.label === 'string' && typeof option.value === 'string',
    )
    .map((option) => ({ label: option.label, value: option.value }));
};

const getOriginalValueLabel = (value: unknown) => {
  if (value === null) {
    return '<null>';
  }
  if (value === DEFAULT_VALUE) {
    return '<default>';
  }
  if (value === GENERATED_VALUE) {
    return '<generated>';
  }
  if (value === undefined) {
    return '';
  }
  return String(value);
};

export class SelectEditor implements IEditor<unknown> {
  readonly options: IResultSetEditorOption[];
  private readonly theme: SelectEditorTheme;
  private container: HTMLElement | null = null;
  private element: HTMLSelectElement | null = null;
  private successCallback: (() => void) | null = null;
  private renderedValues: Array<string | typeof ORIGINAL_VALUE> = [];
  private originalValue: unknown = null;
  private currentValue: unknown = null;
  private changed = false;

  constructor(options: readonly IResultSetEditorOption[], theme: SelectEditorTheme) {
    this.options = normalizeSelectEditorOptions(options);
    this.theme = theme;
  }

  private appendOption(select: HTMLSelectElement, label: string, selected: boolean) {
    const option = document.createElement('option');
    option.value = String(this.renderedValues.length);
    option.textContent = label;
    option.selected = selected;
    option.style.color = this.theme.colorText || '';
    option.style.backgroundColor = this.theme.colorBgContainer || '';
    select.appendChild(option);
    return option;
  }

  private createElement(value: unknown) {
    const select = document.createElement('select');
    select.style.position = 'absolute';
    select.style.boxSizing = 'border-box';
    select.style.padding = '0 5px';
    select.style.borderRadius = '0';
    select.style.border = `1px solid ${this.theme.colorPrimary || this.theme.colorBorder || 'transparent'}`;
    select.style.outline = 'none';
    select.style.color = this.theme.colorText || '';
    select.style.backgroundColor = this.theme.colorBgContainer || '';
    select.style.fontFamily = this.theme.fontFamily || '';
    if (this.theme.fontSize) {
      select.style.fontSize = `${this.theme.fontSize}px`;
    }

    const selectedOptionIndex = this.options.findIndex((option) => option.value === value);
    if (selectedOptionIndex < 0) {
      const originalOption = this.appendOption(select, getOriginalValueLabel(value), true);
      originalOption.disabled = true;
      this.renderedValues.push(ORIGINAL_VALUE);
    }

    this.options.forEach((option, index) => {
      this.appendOption(select, option.label, index === selectedOptionIndex);
      this.renderedValues.push(option.value);
    });

    select.addEventListener('change', this.handleChange);
    this.element = select;
    this.container?.appendChild(select);
  }

  private handleChange = () => {
    if (!this.element) {
      return;
    }
    const selectedValue = this.renderedValues[this.element.selectedIndex];
    if (selectedValue === ORIGINAL_VALUE || selectedValue === undefined) {
      return;
    }
    this.currentValue = selectedValue;
    this.changed = true;
    this.successCallback?.();
  };

  getValue() {
    return this.changed ? this.currentValue : this.originalValue;
  }

  setValue(value: unknown) {
    this.originalValue = value;
    this.currentValue = value;
    this.changed = false;
  }

  onStart({ container, value, referencePosition, endEdit }: EditContext<unknown>) {
    this.container = container;
    this.successCallback = endEdit;
    this.renderedValues = [];
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
    this.element.style.top = `${rect.top + 1}px`;
    this.element.style.left = `${rect.left + 1}px`;
    this.element.style.width = `${Math.max(rect.width - 2, 0)}px`;
    this.element.style.height = `${Math.max(rect.height - 2, 0)}px`;
  }

  onEnd() {
    if (this.element) {
      this.element.removeEventListener('change', this.handleChange);
      if (this.container?.contains(this.element)) {
        this.container.removeChild(this.element);
      }
    }
    this.element = null;
    this.container = null;
    this.successCallback = null;
    this.renderedValues = [];
  }

  isEditorElement(target: HTMLElement) {
    return target === this.element || Boolean(this.element?.contains(target));
  }
}
