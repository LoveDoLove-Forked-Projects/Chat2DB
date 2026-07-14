import logger from './logger';

const CHAT2DB_IPC_REQUEST = 'CHAT2DB_IPC_REQUEST:';
const CHAT2DB_IPC_REQUEST_END = ':CHAT2DB_IPC_REQUEST_END';
const CHAT2DB_IPC_RESPONSE = 'CHAT2DB_IPC_RESPONSE:';
const CHAT2DB_IPC_RESPONSE_END = ':CHAT2DB_IPC_RESPONSE_END';

let integrateData = '';
let residualData = '';

export const stdinWrite = (requestData: any, javaServerChild: any) => {
  // If data is an object, convert it into a json string. If it is a string, use it directly (for convenience of use in the console, strings will not be passed in the actual project)
  // const requestData = typeof data === 'object' ? JSON.stringify(data) : data;
  const commandData = `${CHAT2DB_IPC_REQUEST}${requestData}${CHAT2DB_IPC_REQUEST_END}\n`;
  logger.info('REQUEST:' + commandData);
  javaServerChild?.stdin?.write(commandData);
};

export const findMatches = (str: any) => {
  const regex = /\s*CHAT2DB_IPC_RESPONSE:([\s\S]*?):CHAT2DB_IPC_RESPONSE_END\s*/g;
  let match;
  const results = [];

  while ((match = regex.exec(str)) !== null) {
    results.push(match[1]);
  }

  return results;
};

export const javaServerChildStdoutOn = (javaServerChild: any, mainWindow: any) => {
  javaServerChild.stdout.on('data', (buffer: any) => {
    let dataJson = buffer.toString('utf8');
    integrateData += dataJson;
    const matches = findMatches(integrateData);
    if (matches.length === 0) {
      return;
    }
    matches.forEach((dataJson) => {
      // Check if it is an SSE message
      if (dataJson.startsWith('sse:')) {
        try {
          // Handling SSE messages
          const data = dataJson.substring(4);
          const parseData = JSON.parse(data);
          const uuid = parseData.uuid;
          mainWindow.webContents.send(`push-sse-message-${uuid}`, data);
        } catch (error) {
          console.error('push-sse-message', error);
        }
      } else {
        // Handle ordinary messages
        mainWindow.webContents.send('push-message-flow', dataJson);
      }
    });
    // Find the last one in integrateData: CHAT2DB_IPC_RESPONSE_END and save the subsequent data to residualData.
    const lastEndIndex = integrateData.lastIndexOf(CHAT2DB_IPC_RESPONSE_END);
    if (lastEndIndex !== -1) {
      residualData = integrateData.slice(lastEndIndex + CHAT2DB_IPC_RESPONSE_END.length);
    }
    integrateData = residualData;
    residualData = '';
  });
}