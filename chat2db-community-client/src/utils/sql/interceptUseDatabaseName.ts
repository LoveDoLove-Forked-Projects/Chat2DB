/**
 * Intercept use databaseName
 * This sql only intercepts sql in use xxx; or use xxx; format. SQL that does not conform to this format will not be intercepted.
 * If intercepted, return databaseName, otherwise return false
 */
export function interceptUseDatabaseName(sql: string) {
  const reg = /^use\s+[\w-]+;?$/i;
  return reg.test(sql) ? sql.replace(/^use\s+|;$/gi, '') : false;
}
