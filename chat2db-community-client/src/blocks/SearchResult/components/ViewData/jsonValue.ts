export const isJsonObjectOrArray = (value: unknown): boolean => {
  if (typeof value !== 'string') {
    return false;
  }
  const normalized = value.trim();
  if (!normalized || !['{', '['].includes(normalized[0])) {
    return false;
  }
  try {
    const parsed = JSON.parse(normalized);
    return parsed !== null && typeof parsed === 'object';
  } catch {
    return false;
  }
};
