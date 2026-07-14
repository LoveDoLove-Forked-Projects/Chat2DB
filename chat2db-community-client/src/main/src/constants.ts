const isOffline = process.env.DNETWORK_STATUS == 'OFFLINE';

/** Front-end resources loaded by Dev */
export const DEV_WEB_URL = 'http://localhost:8888/';

/** jar package name */
export const JAVA_APP_NAME = 'chat2db-enterprise.jar';
export const JAVA_PATH = 'jre/bin/java';


/** Plug-in folder name */
export const PLUGIN_DIRECTORY_NAME = "plugins";
/** Plug-in metadata file name */
export const PLUGIN_META_FILE_NAME = "meta.json";
/** Plug-in CDN path */
export const PLUGIN_CDN_URL = "https://cdn.chat2db-ai.com/plugins";

export const APP_CONSTANTS = {
  CHAT2DB_DIRECTORY_NAME : isOffline ? '.chat2db_local_edition' : '.chat2db',
}
