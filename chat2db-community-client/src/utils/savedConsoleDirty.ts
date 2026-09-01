export function hasUnsavedSavedConsoleChanges(
  value: string,
  hasSavedSqlRecord: boolean,
  lastSyncValue: string | undefined,
) {
  if (!hasSavedSqlRecord) {
    return Boolean(value.trim());
  }
  return value !== lastSyncValue;
}
