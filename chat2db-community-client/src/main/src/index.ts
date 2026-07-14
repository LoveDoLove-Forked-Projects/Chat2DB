import { app, BrowserWindow, shell, ipcMain, globalShortcut, dialog, screen, webFrame } from 'electron';
import store from './store';
import path from 'path';
import os from 'os';
import fs from 'fs';
import { loadMainResource, getFeedUrl } from './utils';
import logger from './logger';
import { autoUpdater } from 'electron-updater';
import { spawn, exec } from 'child_process';
import { JAVA_APP_NAME, JAVA_PATH } from './constants';
import registerAppMenu from './menu';
import { downloadPlugin, launchPlugin, scanPlugins, stopPlugin } from './plugin';
import { APP_CONSTANTS } from './constants';
import { stdinWrite, javaServerChildStdoutOn } from './service';

let mainWindow: any = null;
let pluginWindow: any = null;

const feedUrl = getFeedUrl(os); // Update package location
// const PROTOCOL = 'chat2db-pro';
const APP_NAME = process.env.APP_NAME || 'chat2db-pro';
let isQuitting = false;
let javaServerChild: any = null;
let registerForAutoUpdater = false;
let quitAndInstall = false;
let reactIsReady = false;
// openFile object
let openFileData: any = null;

process.on('uncaughtException', (error) => {
  logger.info('Uncaught Exception:', error);
});

if (process.defaultApp) {
  if (process.argv.length >= 2) {
    app.setAsDefaultProtocolClient(APP_NAME, process.execPath, [path.resolve(process.argv[1])]);
  }
} else {
  app.setAsDefaultProtocolClient(APP_NAME);
}

app.commandLine.appendSwitch('--disable-gpu-sandbox');

function ensureWindowInBounds(window: BrowserWindow) {
  const { x, y, width, height } = window.getBounds();
  const { workArea } = screen.getPrimaryDisplay();

  let newX = x;
  let newY = y;

  if (x < workArea.x) {
    newX = workArea.x;
  } else if (x + width > workArea.x + workArea.width) {
    newX = workArea.x + workArea.width - width;
  }

  if (y < workArea.y) {
    newY = workArea.y;
  } else if (y + height > workArea.y + workArea.height) {
    newY = workArea.y + workArea.height - height;
  }

  if (newX !== x || newY !== y) {
    window.setBounds({ x: newX, y: newY, width, height });
    store.set('windowBounds', { x: newX, y: newY, width, height });
  }
}

function createWindow() {
  const { width, height, x, y } = store.get('windowBounds', { width: 1440, height: 800 }) as any;
  const isMaximized = store.get('isMaximized', true) as boolean;

  const options = {
    x,
    y,
    height,
    width,
    minWidth: 480,
    minHeight: 320,
    show: false,
    frame: false, // No borders
    titleBarStyle: 'hidden',
    webPreferences: {
      webSecurity: false,
      spellcheck: false, // Disable spell checker
      nodeIntegration: true,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js'),
    },
  };

  mainWindow = new BrowserWindow(options as any);
  ensureWindowInBounds(mainWindow); // Make sure the window is within the screen bounds

  if (isMaximized) {
    mainWindow.maximize();
  }

  mainWindow.show();
  logger.info('mainWindow Created');
  // Create pallet
  // createTray(mainWindow);

  // Load application-----
  loadMainResource(mainWindow);

  mainWindow.webContents.setWindowOpenHandler(({ url }: any) => {
    shell.openExternal(url);
    return { action: 'deny' };
  });

  mainWindow.on('close', (event: any) => {
    event.preventDefault(); // Prevent default shutdown behavior
    if (process.platform === 'darwin') {
      // If it is full screen at this time, launch full screen
      if (mainWindow.isFullScreen()) {
        mainWindow.setFullScreen(false); // Exit full screen state
      }
      mainWindow.hide(); // Hide window
      // If you exit and install, you also need to exit directly on the mac side.
      if (quitAndInstall) {
        app.quit();
      }
    } else {
      app.quit();
    }
  });

  mainWindow.on('resize', () => {
    // Save window size only in non-maximized state
    if (!mainWindow.isMaximized()) {
      store.set('windowBounds', mainWindow.getBounds());
    }
    // Save maximized state
    store.set('isMaximized', mainWindow.isMaximized());
    // Notify the rendering process of window state changes
    mainWindow.webContents.send('window-state-change', mainWindow.isMaximized());
  });

  mainWindow.on('move', () => {
    // Save window position only in non-maximized state
    if (!mainWindow.isMaximized()) {
      store.set('windowBounds', mainWindow.getBounds());
    }
  });
}

ipcMain.on('close-window', () => {
  app.quit();
});

// Get single instance lock
const gotTheLock = app.requestSingleInstanceLock();

logger.log('gotTheLock', gotTheLock, app.name);

if (!gotTheLock) {
  // If the acquisition fails, it means that there is already an instance running, and you can exit directly.
  app.quit();
} else {
  app.on('second-instance', (event, commandLine, workingDirectory) => {
    if (process.platform !== 'darwin') {
      handleArgvFromWeb(commandLine);
    }
    if (mainWindow) {
      if (mainWindow.isMinimized()) mainWindow.restore();
      mainWindow.focus();
    }

    // Open sql file
    if (commandLine.length >= 4) {
      const filePath = commandLine[3];
      handleFileOpen(filePath);
    }
  });

  app.on('ready', () => {
    logger.log('app ready');
    createWindow();
    registerAppMenu(mainWindow);

    // Handling file paths in command line arguments
    if (process.platform === 'win32' && process.argv.length >= 2) {
      const filePath = process.argv[1];
      if (filePath) {
        handleFileOpen(filePath);
      }
    }
  });
}

// macOS
app.on('open-url', (event, urlStr) => {
  event.preventDefault(); // Block default behavior
  if (mainWindow?.webContents) {
    handleUrlFromWeb(urlStr);
  }
});

const handleUrlFromWeb = (urlStr: any) => {
  showAppAndFocus();
  mainWindow.webContents.send('open-link', urlStr);
};

const handleArgvFromWeb = (argv: any) => {
  const prefix = `${APP_NAME}:`;
  const offset = app.isPackaged ? 1 : 2;
  const url = argv.find((arg: any, i: any) => i >= offset && arg.startsWith(prefix));
  logger.log(url);
  if (url) {
    handleUrlFromWeb(url);
  }
};

// Display the app and move it to the screen where the app is located, and let the app gain focus
function showAppAndFocus() {
  if (!mainWindow.isVisible()) {
    mainWindow.show();
  }
  mainWindow.focus();
}

function handleFileOpen(filePath: string) {
  fs.readFile(filePath, 'utf8', (err, data) => {
    if (err) {
      logger.log(err);
      return;
    }
    // If the app is hidden, show the app
    showAppAndFocus();
    const fileName = path.basename(filePath);
    openFileData = { filePath, fileName, data };
    if (reactIsReady) {
      mainWindow.webContents.send('read-file-content', openFileData);
      openFileData = null;
    }
    // If not ready, wait for react to be ready before sending
  });
}

// Cancel listening to read-file-content, read-file-content can be any event
ipcMain.on('cancel-listen-webContents-send', (e, data) => {
  mainWindow.removeAllListeners(data);
});

// react-ready
ipcMain.on('react-ready', () => {
  reactIsReady = true;
  if (openFileData) {
    mainWindow.webContents.send('read-file-content', openFileData);
    openFileData = null;
  }
});

// Events before app ready
app.on('will-finish-launching', () => {
  // Listen to file open events
  app.on('open-file', (event, filePath) => {
    event.preventDefault();
    handleFileOpen(filePath);
  });
});

app.on('activate', () => {
  if (!mainWindow) {
    createWindow();
  } else {
    if (mainWindow.isMinimized()) {
      mainWindow.restore();
    }
    if (mainWindow.isVisible()) {
      mainWindow.focus();
    } else {
      mainWindow.show();
    }
  }
});

app.on('before-quit', (event) => {
  if (!isQuitting) {
    event.preventDefault();
    let data = { actionType: 'execute', requestUrl: `/api/system/stop`, method: 'post' };
    stdinWrite(data, javaServerChild);
    setTimeout(() => {
      if (javaServerChild) {
        javaServerChild.kill();
      }
      isQuitting = true;
      app.exit(0); // Manually exit the application
    }, 200);
  }
});

// Restart APP
ipcMain.on('restart-app', () => {
  app.relaunch();
  app.quit();
});

// Enlarge or restore window
ipcMain.on('set-maximize', () => {
  if (mainWindow.isMaximized()) {
    mainWindow.unmaximize();
  } else {
    mainWindow.maximize();
  }
  // Save maximized state
  store.set('isMaximized', mainWindow.isMaximized());
});

ipcMain.on('register-app-menu', (event, orgs) => {
  // registerAppMenu(mainWindow, orgs);
});

// Minimize window
ipcMain.on('minimize-window', () => {
  mainWindow.minimize();
});

// Get whether the current window is maximized
ipcMain.on('is-maximized', () => {
  return mainWindow.isMaximized();
});

// open-log
ipcMain.on('open-log', () => {
  const fileName = `${APP_CONSTANTS.CHAT2DB_DIRECTORY_NAME}/chat2db-enterprise/logs/application.log`;
  const url = path.join(os.homedir(), fileName);
  shell.openPath(url).then((str) => console.log('err:', str));
});

// open-dev-tools
ipcMain.on('open-dev-tools', () => {
  mainWindow.webContents.openDevTools();
});

ipcMain.on('app-check-update', (e, data) => {
  checkForUpdates(data);
});

ipcMain.on('stdin-write', (event, data) => {
  stdinWrite(data, javaServerChild);
});

// Get the path of the current file
ipcMain.on('get-current-path', (event) => {
  event.returnValue = app.getAppPath();
});

ipcMain.on('get-user-language', (event) => {
  event.returnValue = app.getLocale();
});

// open-path
ipcMain.on('open-path', (event, data) => {
  function openFolder(filePath: string) {
    const folderPath = path.dirname(filePath);
    switch (process.platform) {
      case 'darwin':
        exec(`open "${folderPath}"`);
        break;
      case 'win32':
        exec(`start "" "${folderPath}"`);
        break;
      default:
        exec(`xdg-open "${folderPath}"`);
    }
  }
  openFolder(data);
});

ipcMain.handle('get-mac-address', () => {
  const interfaces = os.networkInterfaces();
  const macAddresses: { [key: string]: string } = {};

  for (const [interfaceName, interfaceInfos] of Object.entries(interfaces)) {
    if (!interfaceInfos) continue;

    for (const info of interfaceInfos) {
      if (info.mac && info.mac !== '00:00:00:00:00:00') {
        macAddresses[interfaceName] = info.mac;
        break; // Use the first valid MAC address for this interface
      }
    }
  }

  logger.info('macAddresses', macAddresses);
  return macAddresses;
});

// Select a folder
ipcMain.handle('get-file-url', (event, data) => {
  return dialog.showOpenDialogSync({
    properties: data || ['selectDirectory'],
  });
});

// Open a local file in the app
ipcMain.on('open-local-file', (event, data) => {
  handleFileOpen(data);
});

// save-file
ipcMain.on('save-file', (event: any, data: any) => {
  const { fileName, fileContent, fileType } = data;

  dialog
    .showSaveDialog(mainWindow, {
      defaultPath: `${fileName}.${fileType}`, // Set default file name and type
      filters: [
        { name: fileType.toUpperCase(), extensions: [fileType] }, // File type filter
      ],
    })
    .then((result: any) => {
      if (result.canceled || !result.filePath) {
        return;
      }

      const selectedFilePath = result.filePath;

      // write file
      fs.writeFile(selectedFilePath, fileContent, (err) => {
        if (err) {
          logger.log('Error saving file:', err);
          event.reply('save-file-response', { success: false, error: err.message });
        } else {
          logger.log('File saved successfully:', selectedFilePath);
          event.reply('save-file-response', { success: true, filePath: selectedFilePath });

          // Open the directory where the file is located
          shell.showItemInFolder(selectedFilePath);
        }
      });
    })
    .catch((err: any) => {
      logger.log('Error in save dialog:', err);
      event.reply('save-file-response', { success: false, error: err.message });
    });
});

// Change the contents of a file
ipcMain.on('update-file-content', (event, data) => {
  const { filePath, fileContent } = data;

  // write file
  fs.writeFile(filePath, fileContent, (err) => {
    if (err) {
      logger.log('Error saving file:', err);
      event.reply('save-file-response', { success: false, error: err.message });
    } else {
      logger.log('File saved successfully:', filePath);
      event.reply('save-file-response', { success: true, filePath });
    }
  });
});

// open-file-location
ipcMain.on('open-file-location', (event, data) => {
  shell.showItemInFolder(data);
});

ipcMain.on('start-server-for-spawn', async (event) => {
  logger.info(
    'process.env DNETWORK_STATUS DSPRING_PROFILES_ACTIVE',
    process.env.DNETWORK_STATUS,
    process.env.DSPRING_PROFILES_ACTIVE,
  );

  const javaPath = path.join(__dirname, `../${JAVA_APP_NAME}`);

  javaServerChild = spawn(path.join(__dirname, `../${JAVA_PATH}`), [
    '-noverify',
    `-Dspring.profiles.active=${process.env.DSPRING_PROFILES_ACTIVE || 'test'}`,
    '-Dserver.address=127.0.0.1',
    '-Dchat2db.mode=DESKTOP',
    '-Dfile.encoding=UTF-8',
    '-Dchat2db.gui=false',
    '-Djava.net.preferIPv4Stack=true',
    `-Dproject.path=${javaPath}`,
    '-Xms128M',
    ...(process.env.DNETWORK_STATUS ? [`-Dchat2db.network.status=${process.env.DNETWORK_STATUS}`] : []),
    `-javaagent:${javaPath}`,
    // '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005',
    '-jar',
    javaPath,
  ]);

  javaServerChildStdoutOn(javaServerChild, mainWindow);
});

ipcMain.on('webframe-set-zoom', (event, data) => {
  if (data.action === 'zoomIn') {
    mainWindow.webContents.setZoomFactor(mainWindow.webContents.getZoomFactor() + 0.05);
  } else if (data.action === 'zoomOut') {
    mainWindow.webContents.setZoomFactor(mainWindow.webContents.getZoomFactor() - 0.05);
  } else if (data.action === 'zoomReset') {
    mainWindow.webContents.setZoomFactor(1);
  }
});

const sendUpdateMessage = (status: any, data: any) => {
  mainWindow.webContents.send('app-hot-update-message', { status, data });
};

ipcMain.on('update-autoUpdater-config', (event, hotUpdateConfig: any) => {
  // Whether to automatically download update packages
  autoUpdater.autoDownload = hotUpdateConfig.autoDownload;
  // Whether to automatically install when the application exits
  // TODO: There will be this problem on windows - don't use it for now
  // Clicking the close window button at the bottom of windows causes the application to be uninstalled and not reinstalled.
  // autoUpdater.autoInstallOnAppQuit = hotUpdateConfig.autoInstall;
});

const checkForUpdates = (hotUpdateConfig: any) => {
  if (!autoUpdater) return;

  // Whether to automatically run the installation package (silent installation)
  autoUpdater.autoRunAppAfterInstall = false;
  // Whether to automatically download update packages
  autoUpdater.autoDownload = hotUpdateConfig.autoDownload;
  // Whether to automatically install when the application exits
  autoUpdater.autoInstallOnAppQuit = false;

  // Configure the installation package remote server
  if (!registerForAutoUpdater) {
    autoUpdater.setFeedURL(feedUrl);
    // The following are the events that occur throughout the life cycle of automatic updates

    // Under inspection
    autoUpdater.on('checking-for-update', function (message: any) {
      sendUpdateMessage('checkingForUpdate', message);
    } as any);
    // Updateable data found
    autoUpdater.on('update-available', function (message) {
      sendUpdateMessage('updateAvailable', message);
    });
    // No data to update
    autoUpdater.on('update-not-available', function (message) {
      sendUpdateMessage('notAvailable', message);
    });
    // Update download progress event
    autoUpdater.on('download-progress', function (progressObj) {
      sendUpdateMessage('updating', progressObj);
    });
    // Update error
    autoUpdater.on('error', function (message) {
      sendUpdateMessage('updateFailed', message);
    });
    // Update download completion event
    autoUpdater.on('update-downloaded', function () {
      sendUpdateMessage('updated', true);
      // Register an event to be triggered when the installation package download is completed
      ipcMain.on('restart-installation-now', (e, arg) => {
        quitAndInstall = true;
        autoUpdater.quitAndInstall();
      });
    } as any);
    registerForAutoUpdater = true;
  }

  // If it is not removed, manualTrigger cannot get new data.
  if (hotUpdateConfig.manualTrigger) {
    autoUpdater.removeAllListeners('update-not-available');
    autoUpdater.on('update-not-available', function (message) {
      sendUpdateMessage('notAvailable', { ...message, manualTrigger: true });
    });
  }

  // Perform automatic update checks
  autoUpdater.checkForUpdates();
};

ipcMain.on('scan-plugins', async (event) => {
  const plugins = await scanPlugins();
  logger.info('plugins', plugins);
  mainWindow.webContents.send('plugins-scanned', plugins);
});

ipcMain.on('install-plugin', async (event, pluginName) => {
  logger.info('install-plugin', pluginName);
  try {
    await downloadPlugin(pluginName);
    mainWindow.webContents.send('message-from-main', {
      pluginName,
      status: 'success',
    });
    logger.info('插件下载成功');
  } catch (error) {
    mainWindow.webContents.send('message-from-main', {
      pluginName,
      status: 'error',
    });
    logger.info('插件下载失败');
    logger.info('Error in plugin download process:', error);
  }
});

ipcMain.on('open-plugin', (event, { pluginName, token }) => {
  logger.info('open-plugin', pluginName);

  if (!pluginWindow || pluginWindow.isDestroyed()) {
    // Create new window
    pluginWindow = new BrowserWindow({
      width: 1440,
      height: 1080,
      webPreferences: {
        contextIsolation: true,
        preload: path.join(__dirname, 'preload.js'),
      },
    });

    launchPlugin({ pluginWindow, pluginName, token });

    pluginWindow.on('closed', () => {
      stopPlugin({ pluginWindow, pluginName });
      pluginWindow = null;
    });
  } else if (pluginWindow.isMinimized()) {
    // If the window is minimized, restore it
    pluginWindow.restore();
  } else {
    // If the window is simply hidden, show it
    pluginWindow.show();
    // If on another screen
    pluginWindow.focus();
  }
});

// Add new IPC handler
ipcMain.handle('is-window-maximized', () => {
  return mainWindow.isMaximized();
});
