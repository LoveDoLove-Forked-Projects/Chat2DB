import assert from 'node:assert/strict';
import { canHandleWebFrameZoom, handleWebFrameZoom } from './jcefZoom';

const globalObj = globalThis as unknown as {
  window: {
    javaQuery?: (query: {
      request: string;
      onSuccess: (response: string) => void;
      onFailure: (errorCode: number, errorMessage: string) => void;
    }) => number;
  };
};

globalObj.window = {};

assert.equal(canHandleWebFrameZoom(), false);
handleWebFrameZoom('in');

let requestPayload = '';
globalObj.window.javaQuery = (query) => {
  requestPayload = query.request;
  query.onSuccess(JSON.stringify({ data: true }));
  return 1;
};

assert.equal(canHandleWebFrameZoom(), true);
handleWebFrameZoom('reset');

assert.deepEqual(JSON.parse(requestPayload), {
  requestUrl: 'web-frame-set-zoom',
  method: 'client-command',
  message: JSON.stringify({ action: 'zoomReset' }),
});

console.log('All JCEF zoom tests passed successfully!');
