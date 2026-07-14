import { contextBridge, ipcRenderer, IpcRendererEvent } from 'electron';
import { isLinux, isWin, isMac } from './utils';
import path from 'path';

contextBridge.exposeInMainWorld('electronApi', {
  startServerForSpawn: async () => {
    console.log('Starting java Services...');
    ipcRenderer.send('start-server-for-spawn');
  },

  requestSend: (data: any) => {
    ipcRenderer.send('stdin-write', data);
  },

  sendSSERequest: (data: any) => {
    ipcRenderer.send('stdin-write', data);
  },

  ipcRenderer: {
    on: (channel: string, listener: (...args: any[]) => void) => ipcRenderer.on(channel, listener),
    removeAllListeners: (channel: string) => ipcRenderer.removeAllListeners(channel),
    removeListener: (channel: string, listener: (...args: any[]) => void) => ipcRenderer.removeListener(channel, listener),
  },

  on: (channel: string, func: (...args: any[]) => void) => {
    const wrappedListener = (_event: IpcRendererEvent, ...args: any[]) => func(...args);
    ipcRenderer.on(channel, wrappedListener);
    return wrappedListener; // Return the wrapped listener so that it can be used to unsubscribe later
  },

  restartApp: () => {
    ipcRenderer.send('restart-app');
  },
  registerAppMenu: (menuProps) => {
    ipcRenderer.send('register-app-menu', menuProps);
  },
  setMaximize: () => {
    ipcRenderer.send('set-maximize');
  },
  // Get whether the current window is maximized
  isMaximized: () => {
    ipcRenderer.send('is-maximized');
  },
  closeWindow: () => {
    ipcRenderer.send('close-window');
  },
  // Minimize window
  minimizeWindow: () => {
    ipcRenderer.send('minimize-window');
  },
  // Check for updates
  appCheckUpdate: (hotUpdateConfig: any) => {
    ipcRenderer.send('app-check-update', hotUpdateConfig);
  },
  restartInstallationNow: () => {
    ipcRenderer.send('restart-installation-now');
  },
  // Get whether the environment is mac, windows or linux
  getPlatform: () => {
    return {
      isLinux,
      isWin,
      isMac,
    };
  },
  openLog: () => {
    ipcRenderer.send('open-log');
  },
  openDevTools: () => {
    ipcRenderer.send('open-dev-tools');
  },
  // Get the path of the current file
  getCurrentPath: () => {
    return ipcRenderer.sendSync('get-current-path');
  },
  // Get user language
  getUserLanguage: () => {
    return ipcRenderer.sendSync('get-user-language');
  },
  // Splice path
  joinPath: (...args: any) => {
    return path.join(...args);
  },
  // openPath
  openPath: (path: string) => {
    ipcRenderer.send('open-path', path);
  },
  // Given a file path, open the folder where the file is located and select the file
  openFile: (data: any) => {
    ipcRenderer.send('open-file-location', data);
  },
  getMacAddress: () => {
    return ipcRenderer.invoke('get-mac-address');
  },
  updateAutoUpdaterConfig: (config: any) => {
    ipcRenderer.send('update-autoUpdater-config', config);
  },
  getFileUrl: (data: any) => {
    return ipcRenderer.invoke('get-file-url', data);
  },
  // saveFile
  saveFile: (data: any) => {
    ipcRenderer.send('save-file', data);
  },
  // update-file-content
  updateFileContent: (data: any) => {
    ipcRenderer.send('update-file-content', data);
  },
  // Cancel listening to the send event of webContents
  cancelListenWebContentsSend: () => {
    ipcRenderer.send('cancel-listen-webContents-send');
  },
  reactReady: () => {
    ipcRenderer.send('react-ready');
  },
  openLocalFile: (data: string) => {
    ipcRenderer.send('open-local-file', data);
  },
  openChildWindow: (data: any) => {
    ipcRenderer.send('open-child-window', data);
  },

  scanPlugin: () => {
    ipcRenderer.send('scan-plugins');
  },
  installPlugin: (data: any) => {
    ipcRenderer.send('install-plugin', data);
  },

  openPlugin: (props: { pluginName: string; token: string }) => {
    ipcRenderer.send('open-plugin', props);
  },
  webFrameSetZoom: (data: { action: 'zoomIn' | 'zoomOut' | 'zoomReset' }) => {
    ipcRenderer.send('webframe-set-zoom', data);
  },
  // Get whether the current window is maximized
  isWindowMaximized: () => {
    return ipcRenderer.invoke('is-window-maximized');
  },

  // Monitor window status changes
  onWindowStateChange: (callback) => {
    const wrappedCallback = (_event, ...args) => callback(...args);
    ipcRenderer.on('window-state-change', wrappedCallback);
    return wrappedCallback; // Return the wrapped callback function for subsequent removal of the listener
  },

  // Remove window state change monitoring
  removeWindowStateChange: (callback) => {
    ipcRenderer.removeListener('window-state-change', callback);
  },
});
