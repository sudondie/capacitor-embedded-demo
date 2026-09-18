import { StrictMode, useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { CapacitorUpdater } from '@capgo/capacitor-updater'
import { createRemoteAppComponent } from '@module-federation/bridge-react/base'
import { Bell, ChevronRight, CreditCard, Heart, MapPin, UserRound, X } from 'lucide-react'
import { profilePlatform, type ShopperContext } from '@/profile-platform'
import './style.css'

type Tab = 'profile' | 'delivery'
const DeliveryRemote = createRemoteAppComponent({
  loader: () => import('delivery_mfe/DeliveryApp'),
  loading: <p className="profile-shell__loading">Загружаем Delivery MFE…</p>,
  fallback: ({ error }) => <section className="profile-shell__fallback"><MapPin size={28} /><h2>Адреса пока недоступны</h2><p>{error.message}</p></section>,
})

function DeliveryMfe() {
  return <section className="profile-shell__mfe-page"><DeliveryRemote platform={profilePlatform} /></section>
}

function ProfileHome({ shopper }: { shopper: ShopperContext | null }) {
  const rows = [
    { icon: Heart, label: 'Любимые снеки', value: shopper ? String(shopper.favoriteSnacks) : '…' },
    { icon: CreditCard, label: 'Способы оплаты', value: '•••• 4218' },
    { icon: Bell, label: 'Уведомления', value: 'Включены' },
  ]
  return <><section className="profile-shell__hero"><div className="profile-shell__avatar">{shopper?.name.split(' ').map((part) => part[0]).join('') ?? '…'}</div><div><h1>{shopper?.name ?? 'Загружаем профиль…'}</h1><p>{shopper?.email ?? ''}</p></div></section><section className="profile-shell__content"><div className="profile-shell__bonus">Бонусы JetSnack <strong>{shopper?.loyaltyPoints ?? '…'}</strong></div><div className="profile-shell__card">{rows.map(({ icon: Icon, label, value }) => <button className="profile-shell__row" key={label} type="button"><span className="profile-shell__row-icon"><Icon size={20} /></span><span><strong>{label}</strong><small>{value}</small></span><ChevronRight size={20} /></button>)}</div><p className="profile-shell__meta">Profile shell · Capacitor bundle · OTA подключены</p></section></>
}

function ProfileShell() {
  const [tab, setTab] = useState<Tab>('profile')
  const [shopper, setShopper] = useState<ShopperContext | null>(null)
  useEffect(() => {
    void CapacitorUpdater.notifyAppReady()
    void profilePlatform.shopper.getContext().then(setShopper)
    void profilePlatform.analytics.track('profile_shell_opened')
    const onPopState = (event: PopStateEvent) => {
      if (event.state?.profileTab) setTab(event.state.profileTab === 'delivery' ? 'delivery' : 'profile')
    }
    window.history.replaceState({ profileTab: 'profile' }, '')
    window.addEventListener('popstate', onPopState)
    return () => window.removeEventListener('popstate', onPopState)
  }, [])
  const selectTab = (nextTab: Tab) => {
    if (nextTab === tab) return
    window.history.pushState({ profileTab: nextTab }, '')
    setTab(nextTab)
  }
  return <main className="profile-shell"><header className="profile-shell__header"><p>Профиль JetSnack</p><button type="button" aria-label="Закрыть профиль" onClick={() => void profilePlatform.navigation.closeProfile()}><X size={22} /></button></header><section className="profile-shell__page" role="tabpanel">{tab === 'profile' ? <ProfileHome shopper={shopper} /> : <DeliveryMfe />}</section><div className="profile-shell__tabs" role="tablist" aria-label="Разделы профиля"><button type="button" role="tab" aria-selected={tab === 'profile'} className={tab === 'profile' ? 'is-active' : ''} onClick={() => selectTab('profile')}><UserRound size={18} />Профиль</button><button type="button" role="tab" aria-selected={tab === 'delivery'} className={tab === 'delivery' ? 'is-active' : ''} onClick={() => selectTab('delivery')}><MapPin size={18} />Адреса</button></div></main>
}

createRoot(document.getElementById('app')!).render(<StrictMode><ProfileShell /></StrictMode>)
