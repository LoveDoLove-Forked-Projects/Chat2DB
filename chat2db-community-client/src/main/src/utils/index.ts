import { DEV_WEB_URL } from '../constants';
import path from 'path';
import url from 'url';

const isLinux = process.platform == 'linux';
const isWin = process.platform == 'win32';
const isMac = process.platform == 'darwin';
const isProd = process.env.NODE_ENV == 'production';
const isOffline = process.env.DNETWORK_STATUS == 'OFFLINE';

/**
 * Load main process front-end resources
 * @param {*} mainWindow
 */
function loadMainResource(mainWindow: any) {
  if (!isProd) {
    //📢 What is used here is the web url, not the real desktop front-end nature
    mainWindow.loadURL(DEV_WEB_URL);
    mainWindow.webContents.openDevTools();
  } else {
    mainWindow.loadURL(
      url.format({
        pathname: path.join(__dirname, `../dist/index.html`),
        protocol: 'file:',
        slashes: true,
      }),
    );
  }
}

// Splice feedUrl according to different systems and chips
function getFeedUrl(os: any) {
  let feedUrl = 'https://cdn.chat2db-ai.com/download/latest/';

  if (isOffline) { 
    feedUrl = 'https://cdn.chat2db-ai.com/offline/latest/';
  }
  
  if (isLinux) {
    if (os.arch() === 'arm64') {
      feedUrl = feedUrl + 'linux/arm64/';
    } else {
      feedUrl = feedUrl + 'linux/x86_64/';
    }
  } else if (isWin) {
    feedUrl += 'windows/';
  } else if (isMac) {
    if (os.arch() === 'arm64') {
      feedUrl = feedUrl + 'mac/arm64/';
    } else {
      feedUrl = feedUrl + 'mac/x64/';
    }
  }
  return feedUrl;
}

export { loadMainResource, isLinux, isWin, isMac, isProd, getFeedUrl };
  
