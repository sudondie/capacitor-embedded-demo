export type ProfilePlatform = {
  delivery: { getCurrentLocation(): Promise<{ latitude: number; longitude: number }> }
  feedback: { success(): Promise<void> }
  analytics: { track(name: string, properties?: Record<string, string>): Promise<void> }
}
