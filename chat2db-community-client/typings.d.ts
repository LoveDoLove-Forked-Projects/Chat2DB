import 'umi/typings';
import { IVersionResponse } from '@/typings';
import { Platform } from '@/constants/os';

declare module 'monaco-editor/esm/vs/basic-languages/sql/sql';
declare module 'monaco-editor/esm/vs/language/typescript/ts.worker.js';
declare module 'monaco-editor/esm/vs/editor/editor.worker.js';
declare namespace NodeJS {
  interface ProcessEnv {
    readonly NODE_ENV: 'development' | 'production';
    readonly UMI_ENV: string;
    readonly __ENV: string;
  }
}

declare global {
  interface Window {
    _Lang: string;
    _APP_PORT: string;
    _BUILD_TIME: string;
    _AppThemePack: { [key in string]: string };
    _appGatewayParams: IVersionResponse; // Gateway version response
    _notificationApi: any;
    _indexedDB: any;
    _appTitleBarHeight: number;
    _PRINT_LOGS: boolean; // Whether to print logs

    electronApi?: {
      // Already compatible APIs
      revealInExplorer: (url: string) => void;
      selectDirectory: () => string;
      maximizeWindow: () => void;
      minimizeWindow: () => void;
      isWindowMaximized: () => Promise<boolean>;
      closeWindow: () => void;
      appCheckUpdate: () => void;
      webFrameSetZoom: (data: { action: 'zoomIn' | 'zoomOut' | 'zoomReset' }) => void;
      openLog: () => void;
      openDevTools: () => void;
      saveFile: (data: { fileName: string; fileContent: string; fileType: string }) => void;
      updateFileContent: (data: { filePath: string; fileContent: string }) => void;
      getMacAddress: () => any;
      
      // Deprecated APIs
      on: (data: any, fun: IFunction) => boolean;
      off: (data: any, fun: IFunction) => void;

      // APIs pending migration
      updateAutoUpdaterConfig: (data: any) => void;
      ipcRenderer: {
        on: (data: string, fun: IFunction) => void; // Register a listener
        removeListener: (data: string, fun: any) => void;
        removeAllListeners: (data: string) => void;
      };
      scanPlugin: () => void;
      installPlugin: (pluginName: string) => void;
      openPlugin: (props: { pluginName: string; token: string }) => void;
    };

    // JCEF bridge declarations
    /**
     * JCEF: Call Java methods from JavaScript.
     * @param query Object containing request data and callbacks.
     * @returns queryId (numeric type) for possible cancellation.
     */
    javaQuery: (query: {
      request: any; // Typically a JSON string, possibly compressed/Base64 encoded
      persistent?: boolean; // Check whether the query is persistent (CefMessageRouter related)
      onSuccess: (response: string) => void; // The string returned by Java after successful processing (usually JSON)
      onFailure: (errorCode: number, errorMessage: string) => void; // Failure callback of the JCEF communication bridge itself
    }) => number; // Returns a queryId

    /**
     * JCEF: Cancel a query previously initiated via javaQuery.
     * @param queryId The ID returned by javaQuery.
     */
    javaCancelQuery?: (queryId: number) => void;

    handleJavaMessage: (data: string) => void;
  }

  interface Navigator {
    app_language?: string;
    os_type: Platform;
  }

  // Global build constants
  const __APP_VERSION__: string; // Application version
  const __BUILD_TIME__: string; // Build time
  const __ENV__: string; // environment variables
  const __RUNTIME_ENV__: string; // runtime environment
  const __APP_NAME__: string; // Application name
  const __GATEWAY_URL__: string; // Gateway URL
  const __PRINT_LOGS__: boolean; // Whether to print log
  const __WEBAPP__: boolean; // Is it a web application?
}

declare module '*.sql' {
  const content: string;
  export default content;
}
