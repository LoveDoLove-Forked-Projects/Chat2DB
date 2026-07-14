import { promises as fs } from 'fs';
import * as fsSync from 'fs';
import path from 'path';
import os from 'os';
import logger from './logger';
import https from 'https';
import { pipeline } from 'stream/promises';
import url from 'url';
import { exec } from 'child_process';
import AdmZip from 'adm-zip';
import { APP_CONSTANTS, PLUGIN_CDN_URL, PLUGIN_DIRECTORY_NAME, PLUGIN_META_FILE_NAME } from './constants';
import { isWin } from './utils';

/**
 * @returns Promise<string> Plug-in directory path
 */
async function getPluginDirectory() {
  const homeDirectory = os.homedir();
  const pluginDirectory = path.join(homeDirectory, APP_CONSTANTS.CHAT2DB_DIRECTORY_NAME, PLUGIN_DIRECTORY_NAME);

  try {
    await fs.access(pluginDirectory);
    logger.info(`Plugin directory exists: ${pluginDirectory}`);
  } catch (error) {
    logger.info(`Creating plugin directory: ${pluginDirectory}`);
    await fs.mkdir(pluginDirectory, { recursive: true });
  }

  return pluginDirectory;
}

/**
 * Scan the plug-in directory and return meta data of all plug-ins
 * @returns map<string, object> Mapping of plug-in name and meta data
 */
async function scanPlugins() {
  const pluginDirectory = await getPluginDirectory();
  const pluginObj = {};

  logger.info('Scanning plugin directory:', pluginDirectory);
  try {
    // Read plugin directory
    const files = await fs.readdir(pluginDirectory);

    for (const file of files) {
      const pluginPath = path.join(pluginDirectory, file);
      const stat = await fs.stat(pluginPath);

      // Check if it is a directory
      if (stat.isDirectory()) {
        const metaPath = path.join(pluginPath, PLUGIN_META_FILE_NAME);

        logger.info('Scanning MetaPath', metaPath);
        try {
          // Read meta.json file
          const metaContent = await fs.readFile(metaPath, 'utf-8');
          const metaData = JSON.parse(metaContent);

          logger.info('Scanning MetaData', metaData);
          // Use name as key and store the entire meta data as value in map
          if (metaData.name) {
            const pluginName = metaData.name;
            Object.assign(pluginObj, { [pluginName]: { ...metaData, path: metaPath } });
          }
          logger.info('Scanning pluginObj', pluginObj);
        } catch (error) {
          logger.error(`Error reading meta.json for plugin ${file}:`, error);
        }
      }
    }

    return pluginObj;
  } catch (error) {
    logger.error('Error scanning plugin directory:', error);
    return new Map();
  }
}

/**
 * Download the plugin from CDN, unzip it, and clean it
 * @param pluginName plugin name
 * @returns Promise<string> The decompressed plug-in directory path
 */
async function downloadPlugin(pluginName: string): Promise<string> {
  const platform = os.platform();
  const arch = os.arch();
  const pluginDir = await getPluginDirectory();
  const extractDir = path.join(pluginDir, pluginName);
  const pluginFileName = getPluginFileName(pluginName, platform, arch);

  // Make sure the plugin directory exists
  await fs.mkdir(extractDir, { recursive: true });

  // Define the files that need to be downloaded
  const filesToDownload = [
    'meta.json',
    isWin ? 'start.bat' : 'start',
    isWin ? 'stop.bat' : 'stop',
    `${pluginFileName}`,
  ];

  try {
    // Download all necessary files
    for (const file of filesToDownload) {
      const downloadUrl = `${PLUGIN_CDN_URL}/${pluginName}/${file}`;
      const filePath = path.join(extractDir, file);
      logger.info(`Downloading from URL: ${downloadUrl} to path: ${filePath}`);

      logger.info(`Starting download: ${downloadUrl} to ${filePath}`);
      await downloadFile(downloadUrl, filePath);
      logger.info(`Downloaded: ${downloadUrl} to ${filePath}`);
    }

    // Unzip the plug-in package
    const zipFile = `${pluginFileName}`;
    const zipPath = path.join(extractDir, zipFile);

    logger.info(`Extracting: ${zipPath}`);
    await extractZip(zipPath, extractDir);
    logger.info(`Extracted: ${zipPath}`);

    // Delete ZIP file
    await fs.unlink(zipPath);

    return extractDir;
  } catch (error) {
    logger.error('Error in plugin download process:', error);
    // Clean any partially downloaded files
    await cleanupOnError(extractDir);
    throw error;
  }
}

/**
 * Unzip ZIP file
 */
async function extractZip(zipPath: string, extractDir: string): Promise<void> {
  return new Promise((resolve, reject) => {
    try {
      const zip = new AdmZip(zipPath);
      const zipEntries = zip.getEntries();

      zipEntries.forEach((entry) => {
        const { entryName } = entry;

        const topLevelName = entryName.split('/')[0];

        if (!entryName.includes('__MACOSX')) {
          const relativePath = entryName.replace(`${topLevelName}/`, '');
          const outputPath = path.join(extractDir, relativePath);

          // If it is a directory, create the directory
          if (entry.isDirectory) {
            if (!fsSync.existsSync(outputPath)) {
              fsSync.mkdirSync(outputPath, { recursive: true });
            }
          } else {
            // If it is a file, decompress the file
            logger.info(`Extracting: ${entryName} to ${extractDir}`);
            const targetPath = path.join(extractDir, relativePath.substring(0, relativePath.lastIndexOf('/')));
            zip.extractEntryTo(entry, targetPath, false, true);
          }
        }
      });

      resolve();
    } catch (error) {
      reject(error);
    }
  });
}

/**
 * Clean files when errors occur
 */
async function cleanupOnError(dir: string): Promise<void> {
  try {
    await fs.rm(dir, { recursive: true, force: true });
  } catch (error) {
    logger.error('Error during cleanup:', error);
  }
}

/**
 * Get the plug-in file name based on platform and architecture
 */
function getPluginFileName(pluginName: string, platform: string, arch: string): string {
  let osName = '';
  switch (platform) {
    case 'win32':
      osName = 'Windows';
      break;
    case 'darwin':
      osName = 'MacOS';
      break;
    case 'linux':
      osName = 'Linux';
      break;
    default:
      throw new Error(`Unsupported platform: ${platform}`);
  }

  let archName = '';
  switch (arch) {
    case 'x64':
      archName = 'x86_64';
      break;
    case 'arm64':
      archName = 'arm64';
      break;
    // Add support for other architectures
    default:
      throw new Error(`Unsupported architecture: ${arch}`);
  }

  return `${pluginName}_${osName}_${archName}.zip`;
}

/**
 * Auxiliary function for downloading files
 */
async function downloadFile(downloadUrl: string, destPath: string): Promise<void> {
  return new Promise((resolve, reject) => {
    https
      .get(downloadUrl, (response) => {
        if (response.statusCode !== 200) {
          reject(new Error(`Failed to download file, status code: ${response.statusCode}, URL: ${downloadUrl}`));
          return;
        }

        const fileStream = fsSync.createWriteStream(destPath);
        pipeline(response, fileStream)
          .then(() => {
            logger.info(`Downloaded file: ${destPath}`);
            resolve();
          })
          .catch((error) => {
            logger.error(`Error during download pipeline for URL: ${downloadUrl}`, error);
            reject(error);
          });
      })
      .on('error', (error) => {
        logger.error(`Error during HTTPS get request for URL: ${downloadUrl}`, error);
        reject(error);
      });
  });
}

/**
 * Start plugin
 */
async function launchPlugin({ pluginWindow, pluginName, token }: any) {
  // Start the frontend
  const pluginPath = await getPluginDirectory();
  pluginWindow.loadURL(
    url.format({
      pathname: path.join(pluginPath, pluginName, 'client', 'index.html'),
      protocol: 'file:',
      slashes: true,
    }),
  );

  // Start backend
  logger.info('launchPlugin Backend');
  let pluginStartFile = '';
  if (isWin) {
    pluginStartFile = path.join(pluginPath, pluginName, 'start.bat');
  } else {
    pluginStartFile = path.join(pluginPath, pluginName, 'start');
  }
  try {
    // read this file
    const data = fsSync.readFileSync(pluginStartFile, 'utf8');
    const command = replacePlaceholders({ command: data, token, pluginName });
    logger.info('launchPlugin Backend data:', command);

    exec(command, (error, stdout, stderr) => {
      if (error) {
        logger.log(`执行的错误: ${error}`);
        return;
      }
      if (stderr) {
        logger.log(`标准错误输出: ${stderr}`);
        return;
      }
      logger.log(`标准输出: ${stdout}`);
    });
  } catch (error) {}
}

// Close app
async function stopPlugin({ pluginWindow, pluginName }: any) {
  const pluginPath = await getPluginDirectory();
  let pluginStopFile = '';
  if (isWin) {
    pluginStopFile = path.join(pluginPath, pluginName, 'stop.bat');
  } else {
    pluginStopFile = path.join(pluginPath, pluginName, 'stop');
  }
  // read this file
  try {
    const data = fsSync.readFileSync(pluginStopFile, 'utf8');
    const command = replacePlaceholders({ command: data, pluginName });
    logger.info('stopPlugin Backend data:', command);
    exec(command, (error, stdout, stderr) => {
      if (error) {
        logger.log(`执行的错误: ${error}`);
        return;
      }
      if (stderr) {
        logger.log(`标准错误输出: ${stderr}`);
        return;
      }
      logger.log(`标准输出: ${stdout}`);
    });
  } catch (error) {
    logger.error('stopPlugin Error:', error);
  }
}

// Replace placeholders in command line with real values
function replacePlaceholders({ command, token, pluginName }: { command: string; pluginName: string; token?: string }) {
  const userHome = os.homedir();
  const PLUGIN_CURRENT_PATH = path.join(userHome, APP_CONSTANTS.CHAT2DB_DIRECTORY_NAME, PLUGIN_DIRECTORY_NAME, pluginName);

  // cd $PLUGIN_CURRENT_PATH/service &&  ./dbmotions --server-mode=DOCKER "--mysql-uri=$PLUGIN_CURRENT_PATH/dbmotion.db" "--default-work-dir=$PLUGIN_CURRENT_PATH/service" "--meta-db-type=sqlite" --member-id=$CHAT2DB_PLUGIN_TOKEN "--server-addr=127.0.0.1:11920"
  // Replace all command and all placeholders
  return command.replace(/\$CHAT2DB_PLUGIN_TOKEN/g, token || '').replace(/\$PLUGIN_CURRENT_PATH/g, PLUGIN_CURRENT_PATH);
}

export { getPluginDirectory, scanPlugins, downloadPlugin, launchPlugin, stopPlugin };
