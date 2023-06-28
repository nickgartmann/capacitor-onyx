import { WebPlugin } from '@capacitor/core';

import type { OnyxPlugin } from './definitions';

export class OnyxWeb extends WebPlugin implements OnyxPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
