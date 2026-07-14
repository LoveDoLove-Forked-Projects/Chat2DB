import { shell, app, dialog, BrowserWindow, Menu } from 'electron';
import os from 'os';
import path from 'path';
import { isMac } from './utils';
import { i18n } from '../i18n';
import { APP_CONSTANTS } from './constants';
import logger from './logger';

const registerAppMenu = (mainWindow: any, orgs?: any) => {
  if (!isMac) {
    Menu.setApplicationMenu(null);
    return;
  }
  const menuBar: any = [
    {
      label: 'Chat2DB',
      submenu: [
        {
          label: i18n('menu.label.aboutChat2DB'),
          click() {
            dialog.showMessageBox({
              title: i18n('menu.label.aboutChat2DB'),
              message: `${i18n('menu.label.aboutChat2DB')} v${orgs?.version || app.getVersion()}`,
              detail: i18n('menu.detail.aboutChat2DB'),
              icon: './logo/icon.png',
            });
          },
        },
        { type: 'separator' },
        {
          label: i18n('menu.label.restart'),
          click() {
            app.relaunch();
            app.quit();
          },
        },
        {
          label: i18n('menu.label.quit'),
          accelerator: process.platform === 'darwin' ? 'Cmd+Q' : 'Alt+F4',
          click() {
            app.quit();
          },
        },
      ],
    },
    {
      label: i18n('menu.label.edit'),
      submenu: [
        { label: i18n('menu.edit.undo'), role: 'undo' },
        { label: i18n('menu.edit.redo'), role: 'redo' },
        { type: 'separator' },
        { label: i18n('menu.edit.cut'), role: 'cut' },
        { label: i18n('menu.edit.copy'), role: 'copy' },
        { label: i18n('menu.edit.paste'), role: 'paste' },
        { label: i18n('menu.edit.selectAll'), role: 'selectAll' },
      ],
    },
    {
      label: i18n('menu.label.view'),
      submenu: [
        {
          label: i18n('menu.view.refresh'),
          click() {
            mainWindow.webContents.send('refresh-page');
          },
        },
        { type: 'separator' },
        // {
        //   label: i18n('menu.view.zoomIn'),
        //   accelerator: 'CmdOrCtrl+=',
        //   role: 'zoomIn',
        // },
        // {
        //   label: i18n('menu.view.zoomOut'),
        //   accelerator: 'CmdOrCtrl+-',
        //   role: 'zoomOut',
        // },
        // {
        //   label: i18n('menu.view.zoomReset'),
        //   accelerator: 'CmdOrCtrl+0',
        //   role: 'resetZoom',
        // },
        { type: 'separator' },
        { label: i18n('menu.view.toggleFullScreen'), role: 'togglefullscreen' },
      ],
    },
    {
      label: i18n('menu.label.help'),
      submenu: [
        {
          label: i18n('menu.help.openLog'),
          accelerator: process.platform === 'darwin' ? 'Cmd+Shift+T' : 'Ctrl+Shift+T',
          click() {
            const fileName = `${APP_CONSTANTS.CHAT2DB_DIRECTORY_NAME}/chat2db-enterprise/logs/application.log`;
            const url = path.join(os.homedir(), fileName);
            shell.openPath(url).then((str) => console.log('err:', str));
          },
        },
        {
          label: i18n('menu.help.openConsole'),
          click() {
            const focusedWindow: any = BrowserWindow.getFocusedWindow();
            focusedWindow && focusedWindow.toggleDevTools();
          },
        },
        {
          label: i18n('menu.help.visitWebsite'),
          click() {
            const url = 'https://chat2db.ai';
            shell.openExternal(url);
          },
        },
        {
          label: i18n('menu.help.viewDocs'),
          click() {
            const url = 'https://docs.chat2db.ai';
            shell.openExternal(url);
          },
        },
        {
          label: i18n('menu.help.viewChangelog'),
          click() {
            const url = 'https://docs.chat2db.ai/changelog/';
            shell.openExternal(url);
          },
        },
      ],
    },
  ];
  Menu.setApplicationMenu(Menu.buildFromTemplate(menuBar));
};

export default registerAppMenu;
