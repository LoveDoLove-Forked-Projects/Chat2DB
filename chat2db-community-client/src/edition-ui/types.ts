import type { SettingMenuItem } from '@/blocks/Setting/SettingLayout';
import type { SettingMenuProfile } from '@/constants/runtimeEdition';
import type { LangType } from '@/constants/settings';
import type { INavItem } from '@/typings/main';
import type { ReactNode } from 'react';

export interface EditionSettingMenuContext {
  language: LangType;
  profile: SettingMenuProfile;
}

export type EditionMainNavigationContribution = Omit<INavItem, 'key'> & {
  /** Stable identifier used as both the contribution identity and navigation key. */
  id: string;
};

export interface EditionOrganizationPanelVisibilityContext {
  organizationId?: number;
  roleCodes: readonly string[];
  isAdmin: boolean;
  isOwner: boolean;
}

export interface EditionOrganizationPanelContribution {
  /** Stable identifier used as both the contribution identity and organization navigation key. */
  id: string;
  label: ReactNode;
  icon?: ReactNode;
  panel: ReactNode;
  /** Controls presentation only. The backing API must enforce its own authorization. */
  isVisible?: (context: EditionOrganizationPanelVisibilityContext) => boolean;
}

export interface EditionUiExtension {
  settingMenuItems?: (context: EditionSettingMenuContext) => readonly SettingMenuItem[];
  mainNavigationItems?: readonly EditionMainNavigationContribution[];
  organizationPanels?: readonly EditionOrganizationPanelContribution[];
}
