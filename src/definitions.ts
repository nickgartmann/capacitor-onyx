import { PluginListenerHandle } from "@capacitor/core";

export interface DrawAreaOptions {
  x: number
  y: number
  height: number
  width: number
}

export interface StrokeOptions {
  width: number;
  style: "pencil" | "fountain" | "marker" | "neo-brush" | "charcoal" | "dash" | "charcoal-v2";
}

export interface Point {
  x: number;
  y: number;
  size: number;
  pressure: number;
  timestamp: number;
}

export interface Stroke {
  points: Point[];
}

export interface OnyxPlugin {
  start(options: DrawAreaOptions) : Promise<void>;
  stop(): Promise<void>;
  configureStroke(options: StrokeOptions): Promise<void>;
  addListener(name: 'onDrawingStart', callback: (point: Point) => void): PluginListenerHandle;
  addListener(name: 'onDrawingEnd', callback: () => void): PluginListenerHandle;
  addListener(name: 'onStroke', callback: (stroke: Stroke) => void): PluginListenerHandle;
  addListener(name: 'onErasingStart', callback: (point: Point) => void): PluginListenerHandle;
  addListener(name: 'onErasingEnd', callback: () => void): PluginListenerHandle;
  addListener(name: 'onErase', callback: (stroke: Stroke) => void): PluginListenerHandle;
  removeAllListeners(): void;
}
