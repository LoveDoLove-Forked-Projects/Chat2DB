import { isJsonObjectOrArray } from './jsonValue';

const assertEqual = (actual: boolean, expected: boolean, message: string) => {
  if (actual !== expected) {
    throw new Error(`${message}: expected ${expected}, got ${actual}`);
  }
};

assertEqual(isJsonObjectOrArray('{"id":1}'), true, 'JSON objects are detected');
assertEqual(isJsonObjectOrArray('[1,2,3]'), true, 'JSON arrays are detected');
assertEqual(isJsonObjectOrArray(' { "nested": [1] } '), true, 'surrounding whitespace is ignored');
assertEqual(isJsonObjectOrArray('{invalid}'), false, 'invalid JSON is rejected');
assertEqual(isJsonObjectOrArray('123'), false, 'numeric text is not treated as structured JSON');
assertEqual(isJsonObjectOrArray('"text"'), false, 'JSON string primitives do not show formatting tools');
assertEqual(isJsonObjectOrArray(''), false, 'empty values are not JSON');

console.log('JSON value detection tests passed');
