import { useStyles } from './style';
import { OrgNavType } from '@/constants/organization';
import { filterVisibleOrganizationPanels } from '@/edition-ui/merge';
import type {
  EditionOrganizationPanelContribution,
  EditionOrganizationPanelVisibilityContext,
} from '@/edition-ui/types';
import MemberManagement from '../MemberManagement';
import OrgSettings from '../OrgSettings';
import Approve from '../Approval';
import Authorization from '../Permission/authorization';
import ApplyList from '../Permission/apply';
import SQLAudit from '../SQLAudit';
import SubscriptionList from '../SubscriptionList';

interface IProps {
  extensionPanels: readonly EditionOrganizationPanelContribution[];
  visibilityContext: EditionOrganizationPanelVisibilityContext;
  menuKey: string;
}

const OrgContainer = ({ extensionPanels, visibilityContext, menuKey }: IProps) => {
  const { styles } = useStyles();

  const renderContent = () => {
    switch (menuKey) {
      case OrgNavType.TeamSettings:
        return <OrgSettings />;
      case OrgNavType.MemberManagement:
        return <MemberManagement />;
      case OrgNavType.SubscriptionList:
        return <SubscriptionList />;
      case OrgNavType.ApprovalList:
        return <Approve />;
      case OrgNavType.Authorization:
        return <Authorization />;
      case OrgNavType.ApplyList:
        return <ApplyList />;
      case OrgNavType.SQLAudit:
        return <SQLAudit />;
      default:
        return (
          filterVisibleOrganizationPanels(extensionPanels, visibilityContext).find((panel) => panel.id === menuKey)
            ?.panel || null
        );
    }
  };

  return <div className={styles.wrapper}>{renderContent()}</div>;
};

export default OrgContainer;
