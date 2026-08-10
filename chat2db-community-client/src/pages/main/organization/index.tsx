import editionUiExtension from '@edition-ui';
import { useEffect, useMemo } from 'react';
import SplitPane from 'react-split-pane';
import { OrgNavType } from '@/constants/organization';
import { filterVisibleOrganizationPanels, mergeOrganizationPanelContributions } from '@/edition-ui/merge';
import { useOrgStore } from '@/store/organization';
import OrgContainer from './components/OrgContainer';
import OrgNavList from './components/OrgNavList';

const organizationPanels = mergeOrganizationPanelContributions(
  editionUiExtension.organizationPanels ?? [],
  Object.values(OrgNavType),
);

const Organization = () => {
  const { curOrg, isAdmin, isOwner, orgNav, setOrgNav } = useOrgStore((s) => ({
    curOrg: s.curOrg,
    isAdmin: s.isAdmin,
    isOwner: s.isOwner,
    orgNav: s.orgNav,
    setOrgNav: s.setOrgNav,
  }));
  const visibilityContext = useMemo(
    () => ({
      organizationId: curOrg?.id,
      roleCodes: curOrg?.roleCodes || [],
      isAdmin,
      isOwner,
    }),
    [curOrg?.id, curOrg?.roleCodes, isAdmin, isOwner],
  );
  const visibleOrganizationPanels = useMemo(
    () => filterVisibleOrganizationPanels(organizationPanels, visibilityContext),
    [visibilityContext],
  );

  useEffect(() => {
    const isCorePanel = Object.values(OrgNavType).includes(orgNav as OrgNavType);
    const isVisibleExtensionPanel = visibleOrganizationPanels.some((panel) => panel.id === orgNav);
    if (!isCorePanel && !isVisibleExtensionPanel) {
      setOrgNav(OrgNavType.TeamSettings);
    }
  }, [orgNav, setOrgNav, visibleOrganizationPanels]);

  const handleMenuChange = (key: string) => {
    setOrgNav(key);
  };

  return (
    <SplitPane size={220} pane2Style={{ width: '0px' }} minSize={220} maxSize={300} split="vertical" primary="first">
      <OrgNavList
        extensionPanels={organizationPanels}
        visibilityContext={visibilityContext}
        onClickMenu={handleMenuChange}
        menuKey={orgNav}
      />
      <OrgContainer extensionPanels={organizationPanels} visibilityContext={visibilityContext} menuKey={orgNav} />
    </SplitPane>
  );
};

export default Organization;
