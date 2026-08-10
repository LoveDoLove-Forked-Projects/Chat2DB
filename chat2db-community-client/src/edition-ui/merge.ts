import type { INavItem } from '@/typings/main';
import type {
  EditionMainNavigationContribution,
  EditionOrganizationPanelContribution,
  EditionOrganizationPanelVisibilityContext,
} from './types';

const dedupeContributions = <T extends { id: string }>(
  contributions: readonly T[],
  reservedIds: Iterable<string>,
): T[] => {
  const seenIds = new Set(reservedIds);

  return contributions.filter((contribution) => {
    if (!contribution.id || seenIds.has(contribution.id)) {
      return false;
    }
    seenIds.add(contribution.id);
    return true;
  });
};

export const mergeMainNavigationItems = (
  coreItems: readonly INavItem[],
  contributions: readonly EditionMainNavigationContribution[],
): INavItem[] => {
  const extensionItems = dedupeContributions(
    contributions,
    coreItems.map((item) => `${item.key}`),
  ).map(({ id, ...item }) => ({ ...item, key: id }));

  return [...coreItems, ...extensionItems];
};

export const mergeOrganizationPanelContributions = (
  contributions: readonly EditionOrganizationPanelContribution[],
  corePanelIds: Iterable<string>,
): EditionOrganizationPanelContribution[] => dedupeContributions(contributions, corePanelIds);

export const filterVisibleOrganizationPanels = (
  contributions: readonly EditionOrganizationPanelContribution[],
  context: EditionOrganizationPanelVisibilityContext,
): EditionOrganizationPanelContribution[] =>
  contributions.filter((contribution) => contribution.isVisible?.(context) ?? true);
