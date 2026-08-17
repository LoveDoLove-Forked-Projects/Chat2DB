import type { ClientExtension } from './types';

export const clientExtension: ClientExtension = {
  navigationItems: [],
  requestPolicy: {
    permissionDeniedInteraction: 'prompt-application',
  },
};

export default clientExtension;
