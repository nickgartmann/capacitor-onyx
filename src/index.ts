import { registerPlugin } from '@capacitor/core';

import type { OnyxPlugin } from './definitions';

const Onyx = registerPlugin<OnyxPlugin>('Onyx', {
  web: () => import('./web').then(m => new m.OnyxWeb()),
});

export * from './definitions';
export { Onyx };
