import { registerPlugin } from '@capacitor/core'

export type ShopperContext = {
  name: string
  email: string
  favoriteSnacks: number
  loyaltyPoints: number
}

type ProfileHostPlugin = {
  getShopperContext(): Promise<ShopperContext>
  getCurrentLocation(): Promise<DeliveryLocation>
  closeProfile(): Promise<void>
  feedbackSuccess(): Promise<void>
  track(options: { name: string; properties?: Record<string, string> }): Promise<void>
}

const ProfileHost = registerPlugin<ProfileHostPlugin>('ProfileHost')

export type DeliveryLocation = {
  latitude: number
  longitude: number
}

export type ProfilePlatform = {
  shopper: {
    getContext(): Promise<ShopperContext>
  }
  delivery: {
    getCurrentLocation(): Promise<DeliveryLocation>
  }
  feedback: {
    success(): Promise<void>
  }
  analytics: {
    track(name: string, properties?: Record<string, string>): Promise<void>
  }
  navigation: {
    closeProfile(): Promise<void>
  }
}

export const profilePlatform: ProfilePlatform = {
  shopper: {
    getContext: () => ProfileHost.getShopperContext(),
  },
  delivery: {
    getCurrentLocation: () => ProfileHost.getCurrentLocation(),
  },
  feedback: {
    success: () => ProfileHost.feedbackSuccess(),
  },
  analytics: {
    track: (name, properties) => ProfileHost.track({ name, properties }),
  },
  navigation: {
    closeProfile: () => ProfileHost.closeProfile(),
  },
}
