import { formatSelectionMetric, summarizeSelection } from './selectionAggregation';

function assertEqual(actual: any, expected: any, message: string) {
  const actualJson = JSON.stringify(actual);
  const expectedJson = JSON.stringify(expected);
  if (actualJson !== expectedJson) {
    throw new Error(`${message}: expected ${expectedJson}, got ${actualJson}`);
  }
}

const numericSummary = summarizeSelection([1, '2', 3.5, null, undefined, 'not-a-number']);
assertEqual(numericSummary.count, 6, 'count includes every selected body cell');
assertEqual(numericSummary.numericCount, 3, 'numeric strings participate in numeric aggregates');
assertEqual(formatSelectionMetric('sum', numericSummary), '6.5', 'sum uses numeric values only');
assertEqual(formatSelectionMetric('average', numericSummary), '2.166667', 'average is rounded for display');
assertEqual(formatSelectionMetric('minimum', numericSummary), '1', 'minimum uses the numeric subset');
assertEqual(formatSelectionMetric('maximum', numericSummary), '3.5', 'maximum uses the numeric subset');
assertEqual(formatSelectionMetric('nullCount', numericSummary), '2', 'null and undefined are null values');
assertEqual(formatSelectionMetric('nonNullCount', numericSummary), '4', 'non-null count excludes null values');
assertEqual(formatSelectionMetric('nullPercentage', numericSummary), '33.333333%', 'null percentage uses all cells');

const uniqueSummary = summarizeSelection(['a', 'a', 'b', null]);
assertEqual(formatSelectionMetric('uniqueCount', uniqueSummary), '2', 'unique count ignores null values');
assertEqual(formatSelectionMetric('uniquePercentage', uniqueSummary), '50%', 'unique percentage uses all cells');

const dateSummary = summarizeSelection(['2025-03-01', '2024-01-02 10:20:30', 1735689600000, '12345']);
assertEqual(
  formatSelectionMetric('earliest', dateSummary).startsWith('2024-01-02'),
  true,
  'earliest accepts date-like strings',
);
assertEqual(
  formatSelectionMetric('latest', dateSummary).startsWith('2025-03-01'),
  true,
  'numeric identifiers are not interpreted as dates',
);

assertEqual(formatSelectionMetric('sum', summarizeSelection(['a', 'b'])), '-', 'missing numeric data uses a dash');

console.log('selection aggregation helper tests passed');

