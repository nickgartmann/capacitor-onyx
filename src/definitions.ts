export interface OnyxPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
