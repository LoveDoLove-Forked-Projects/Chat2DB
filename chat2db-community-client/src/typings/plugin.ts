/**
 * Plug-in status
 */
export enum PluginStatus {
  // unknown
  NONE = 'NONE',
  // Installed
  INSTALLED = 'INSTALLED',
  // Installing
  INSTALLING = 'INSTALLING',
  // Not installed
  UNINSTALLED = 'UNINSTALLED',
  // To be updated
  UPDATE = 'UPDATE',
}
export interface IPluginDataPackageVO {
  size: number;
  usedSize: number;
  orderId: number;
  buyTime: number;
  runOutTime: number;
}

export type IPluginItem = IPluginVO & { pluginStatus: PluginStatus; token?: string };

/**
 * Backend VO object
 */
export interface IPluginVO {
  /**
   * Primary key
   */
  id: number;

  /**
   * name
   */
  name: string;

  /**
   * icon
   */
  icon: string;

  /**
   * Description
   */
  description: string;

  /**
   * size
   */
  size: number;

  /**
   * version
   */
  version: string;

  /**
   *Number of downloads
   */
  downCount: number;

  /**
   * User ratings
   */
  rating: number;

  /**
   *Download address
   */
  downloadUrl: string;

  /**
   *Details
   */
  detail: string;

  /**
   * url
   */
  url: string;

  /**
   * Create user ID
   */
  createUserId: number;

  /**
   * Creation time
   */
  createTime: number;

  /**
   * Update time
   */
  modifyTime: number;
}
