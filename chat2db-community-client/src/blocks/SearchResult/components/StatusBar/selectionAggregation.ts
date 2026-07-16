import type { SelectionMetricId } from '@/typings/settings';

export interface SelectionAggregation {
  count: number;
  nullCount: number;
  nonNullCount: number;
  uniqueCount: number;
  numericCount: number;
  sum: number;
  minimum: number | string | null;
  maximum: number | string | null;
  earliest: number | null;
  latest: number | null;
}

const STRICT_NUMBER = /^[+-]?(?:\d+\.?\d*|\.\d+)(?:e[+-]?\d+)?$/i;
const DATE_LIKE = /^\d{4}[-/]\d{1,2}[-/]\d{1,2}(?:[T\s].*)?$/;

const isNullValue = (value: unknown) => value === null || value === undefined;

const toNumber = (value: unknown): number | null => {
  if (typeof value === 'number') {
    return Number.isFinite(value) ? value : null;
  }
  if (typeof value !== 'string') {
    return null;
  }
  const normalized = value.trim();
  if (!normalized || !STRICT_NUMBER.test(normalized)) {
    return null;
  }
  const result = Number(normalized);
  return Number.isFinite(result) ? result : null;
};

const toTimestamp = (value: unknown): number | null => {
  if (value instanceof Date) {
    const timestamp = value.getTime();
    return Number.isFinite(timestamp) ? timestamp : null;
  }
  if (typeof value !== 'string' || !DATE_LIKE.test(value.trim())) {
    return null;
  }
  const timestamp = Date.parse(value);
  return Number.isFinite(timestamp) ? timestamp : null;
};

const uniqueKey = (value: unknown): string => {
  if (typeof value === 'object') {
    try {
      return `object:${JSON.stringify(value)}`;
    } catch {
      return `object:${String(value)}`;
    }
  }
  return `${typeof value}:${String(value)}`;
};

export const summarizeSelection = (values: unknown[]): SelectionAggregation => {
  let nullCount = 0;
  let numericCount = 0;
  let sum = 0;
  let numericMinimum: number | null = null;
  let numericMaximum: number | null = null;
  let stringMinimum: string | null = null;
  let stringMaximum: string | null = null;
  let earliest: number | null = null;
  let latest: number | null = null;
  const uniqueValues = new Set<string>();

  values.forEach((value) => {
    if (isNullValue(value)) {
      nullCount += 1;
      return;
    }

    uniqueValues.add(uniqueKey(value));
    const stringValue = String(value);
    stringMinimum =
      stringMinimum === null || stringValue.localeCompare(stringMinimum) < 0 ? stringValue : stringMinimum;
    stringMaximum =
      stringMaximum === null || stringValue.localeCompare(stringMaximum) > 0 ? stringValue : stringMaximum;

    const numericValue = toNumber(value);
    if (numericValue !== null) {
      numericCount += 1;
      sum += numericValue;
      numericMinimum = numericMinimum === null ? numericValue : Math.min(numericMinimum, numericValue);
      numericMaximum = numericMaximum === null ? numericValue : Math.max(numericMaximum, numericValue);
    }

    const timestamp = toTimestamp(value);
    if (timestamp !== null) {
      earliest = earliest === null ? timestamp : Math.min(earliest, timestamp);
      latest = latest === null ? timestamp : Math.max(latest, timestamp);
    }
  });

  return {
    count: values.length,
    nullCount,
    nonNullCount: values.length - nullCount,
    uniqueCount: uniqueValues.size,
    numericCount,
    sum,
    minimum: numericCount ? numericMinimum : stringMinimum,
    maximum: numericCount ? numericMaximum : stringMaximum,
    earliest,
    latest,
  };
};

const formatNumber = (value: number) => {
  if (Number.isInteger(value)) {
    return String(value);
  }
  return String(Number(value.toFixed(6)));
};

const formatPercentage = (value: number, total: number) => {
  if (!total) {
    return '-';
  }
  return `${formatNumber((value / total) * 100)}%`;
};

const formatDate = (timestamp: number | null) => {
  if (timestamp === null) {
    return '-';
  }
  const date = new Date(timestamp);
  const pad = (value: number) => String(value).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(
    date.getMinutes(),
  )}:${pad(date.getSeconds())}`;
};

export const formatSelectionMetric = (metric: SelectionMetricId, summary: SelectionAggregation): string => {
  switch (metric) {
    case 'none':
      return '';
    case 'count':
      return String(summary.count);
    case 'sum':
      return summary.numericCount ? formatNumber(summary.sum) : '-';
    case 'average':
      return summary.numericCount ? formatNumber(summary.sum / summary.numericCount) : '-';
    case 'minimum':
      return summary.minimum === null
        ? '-'
        : typeof summary.minimum === 'number'
        ? formatNumber(summary.minimum)
        : summary.minimum;
    case 'maximum':
      return summary.maximum === null
        ? '-'
        : typeof summary.maximum === 'number'
        ? formatNumber(summary.maximum)
        : summary.maximum;
    case 'nullCount':
      return String(summary.nullCount);
    case 'nonNullCount':
      return String(summary.nonNullCount);
    case 'uniqueCount':
      return String(summary.uniqueCount);
    case 'nullPercentage':
      return formatPercentage(summary.nullCount, summary.count);
    case 'nonNullPercentage':
      return formatPercentage(summary.nonNullCount, summary.count);
    case 'uniquePercentage':
      return formatPercentage(summary.uniqueCount, summary.count);
    case 'earliest':
      return formatDate(summary.earliest);
    case 'latest':
      return formatDate(summary.latest);
    default:
      return '-';
  }
};
