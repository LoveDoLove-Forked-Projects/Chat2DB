import type { IViewTableParams } from '@/typings';

// Separator that cannot appear in a datasource, database, schema, or table name.
const TARGET_KEY_SEPARATOR = '\u0000';

/**
 * Identity of the table a browse request targets.
 *
 * The workspace tab layer keeps every open tab mounted and rebuilds the tab
 * bodies whenever unrelated workspace state changes (active tab, datasource
 * list, tab list), so `IViewTableParams` is a new object on most parent
 * renders. Loading must be keyed on this identity instead of the params object
 * reference, otherwise every tab switch re-issues the table browse request.
 */
export function getViewTableTargetKey(params?: IViewTableParams | null): string {
  if (!params) {
    return '';
  }
  return [
    params.dataSourceId ?? '',
    params.databaseName ?? '',
    params.schemaName ?? '',
    params.tableName ?? '',
  ].join(TARGET_KEY_SEPARATOR);
}
