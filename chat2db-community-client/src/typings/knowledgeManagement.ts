import type { DatabaseTypeCode } from '@/constants';

export interface KnowledgeResourceScope {
  dataSourceId: number;
  dataSourceAlias?: string;
  dataSourceType?: DatabaseTypeCode;
}

export interface KnowledgeManagementRecord {
  id: number | string;
  promptType: 'KNOWLEDGE_TERM' | 'BUSINESS_LOGIC' | 'SQL_TEMPLATE';
  promptName: string;
  promptContent: string;
  createUserId?: number;
  createUserName?: string;
  resourceScopes: KnowledgeResourceScope[];
}

export interface KnowledgeManagementSaveRequest {
  promptId?: number | string;
  promptType: KnowledgeManagementRecord['promptType'];
  promptName: string;
  promptContent: string;
  resourceScopes: Array<Pick<KnowledgeResourceScope, 'dataSourceId'>>;
}
