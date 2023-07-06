import { WebPlugin } from '@capacitor/core';

import type { DrawAreaOptions, OnyxPlugin, StrokeOptions } from './definitions';

export class OnyxWeb extends WebPlugin implements OnyxPlugin {
  async start(_options: DrawAreaOptions): Promise<void> {
    throw new Error('Method not implemented.');
  }
  async stop(): Promise<void> {
    throw new Error('Method not implemented.');
  }
  async configureStroke(_options: StrokeOptions): Promise<void> {
    throw new Error('Method not implemented.');
  }
}
