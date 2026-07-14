import { InputType, AuthenticationType, SSHAuthenticationType } from './enum';
import { DatabaseTypeCode, OperationColumn } from '@/constants';
import { LangType } from '@/constants/settings';

export type ISelect = {
  value?: AuthenticationType | SSHAuthenticationType | string | boolean;
  label?: string;
  onChange?: (value: IConnectionConfig) => IConnectionConfig;
  rest?: {
    [key in string]: any
  }
  items?: IFormItem[];
};

export interface IFormItem {
  defaultValue: any;
  inputType: InputType;
  labelName: {
    [LangType.EN_US]: string;
    [LangType.ZH_CN]: string;
    [LangType.JA_JP]: string;
  },
  name: string;
  required: boolean;
  selected?: any;
  selects?: ISelect[];
  labelTextAlign?: 'right';
  disabled?: boolean;
  placeholder?: {
    [LangType.EN_US]: string;
    [LangType.ZH_CN]: string;
    [LangType.JA_JP]: string;
  };
  styles?: {
    width?: string; // Form width; percentages are recommended and the default is 100%.
    labelWidth?: {
      [LangType.EN_US]: string;
      [LangType.ZH_CN]: string;
      [LangType.JA_JP]: string;
    }; // Form-label width for English; pixels are recommended and the default is 70px.
    labelAlign?: string; // Label alignment; defaults to left.
  },
  hidden?: boolean;
  fileTypes?: string[];
}

// Data-source connection form JSON configuration.
export type IConnectionConfig = {
  type: DatabaseTypeCode;
  baseInfo: {
    items: IFormItem[];
    pattern: RegExp;
    template: string;
    excludes?: OperationColumn[];
  },
  driver?: {
    items: IFormItem[];
  }
  ssh: {
    items: IFormItem[];
  },
  extendInfo?: {
    key: string;
    value: any;
  }[],
  // TODO: Resolve form settings, then form.item settings, then defaults; global settings are not yet included.
  styles?: {
    width?: string; // Form width; percentages are recommended and the default is 100%.
    labelWidthEN?: string; // English label width; pixels are recommended and the default is 70px.
    labelWidthCN?: string; // Chinese label width; pixels are recommended and the default is 100px.
    labelAlign?: 'left' | 'right'; // Label alignment; defaults to left.
  }
};
