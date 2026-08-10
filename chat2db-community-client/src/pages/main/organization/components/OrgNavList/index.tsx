import { useMemo } from 'react';
import { filterVisibleOrganizationPanels } from '@/edition-ui/merge';
import type {
  EditionOrganizationPanelContribution,
  EditionOrganizationPanelVisibilityContext,
} from '@/edition-ui/types';
import { useStyles } from './style';
import { useOrgStore } from '@/store/organization';
import OrgItem from '../OrgItem';
import { Menu, MenuProps } from 'antd';
import { AppstoreOutlined, MailOutlined, SettingOutlined } from '@ant-design/icons';
import i18n from '@/i18n';
import { OrgNavType } from '@/constants/organization';
import { OrgUserRoleCode } from '@/typings/enterprise/organization';

interface IProps {
  extensionPanels: readonly EditionOrganizationPanelContribution[];
  visibilityContext: EditionOrganizationPanelVisibilityContext;
  menuKey: string;
  onClickMenu: (key: string) => void;
}

type MenuItem = Required<MenuProps>['items'][number];

const OrgNavTypeList = ({ extensionPanels, visibilityContext, menuKey, onClickMenu }: IProps) => {
  const { styles } = useStyles();

  const { curOrg } = useOrgStore((state) => ({
    curOrg: state.curOrg,
  }));

  const items: MenuItem[] = useMemo(
    () => [
      {
        key: OrgNavType.TeamManagement,
        icon: <MailOutlined />,
        label: i18n('team.nav.team.management'),
        children: [
          { key: OrgNavType.TeamSettings, label: i18n('team.nav.team.setting') },
          { key: OrgNavType.MemberManagement, label: i18n('team.nav.member.management') },
          {
            key: OrgNavType.SubscriptionList,
            label: i18n('team.nav.subscription'),
          },
        ],
      },
      {
        key: OrgNavType.ApprovalList,
        icon: <AppstoreOutlined />,
        label: i18n('team.nav.approval'),
      },
      {
        key: OrgNavType.Permission,
        icon: <SettingOutlined />,
        label: i18n('team.nav.permission.management'),
        children: [
          { key: OrgNavType.Authorization, label: i18n('team.nav.permission.authorization') },
          { key: OrgNavType.ApplyList, label: i18n('team.nav.permission.apply') },
        ],
      },
      {
        key: OrgNavType.SQLAudit,
        icon: <SettingOutlined />,
        label: i18n('team.nav.sqlAudit'),
      },
      ...filterVisibleOrganizationPanels(extensionPanels, visibilityContext).map(({ id, icon, label }) => ({
        key: id,
        icon,
        label,
      })),
    ],
    [extensionPanels, visibilityContext],
  );

  const realItems = useMemo(() => {
    if (curOrg?.roleCodes?.includes(OrgUserRoleCode.SUPER_ADMIN)) {
      return items;
    }
    return items.filter((item) => item?.key !== OrgNavType.SQLAudit);
  }, [curOrg, items]);

  return (
    <div className={styles.wrapper}>
      <OrgItem org={curOrg} />
      <div className={styles.menuBox}>
        <Menu
          defaultOpenKeys={[OrgNavType.TeamManagement, OrgNavType.Permission]}
          selectedKeys={[menuKey]}
          mode="inline"
          items={realItems}
          className={styles.menuWrapper}
          onClick={(e) => {
            onClickMenu(e.key);
          }}
        />
      </div>
    </div>
  );
};

export default OrgNavTypeList;
