import { useEffect, useState } from 'react'
import { Crosshair, MapPin, Plus } from 'lucide-react'
import type { ProfilePlatform } from './platform'
import './style.css'

export default function App({ platform }: { platform: ProfilePlatform }) {
  const [screen, setScreen] = useState<'list' | 'create'>('list')
  const [location, setLocation] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const findLocation = async () => {
    setLoading(true)
    try {
      const point = await platform.delivery.getCurrentLocation()
      setLocation(`Точка доставки: ${point.latitude.toFixed(4)}, ${point.longitude.toFixed(4)}`)
      await platform.analytics.track('delivery_location_received')
    } catch (error) { setLocation(error instanceof Error ? error.message : 'Не удалось определить местоположение') }
    finally { setLoading(false) }
  }
  useEffect(() => {
    const onPopState = (event: PopStateEvent) => {
      setScreen(event.state?.deliveryScreen === 'create' ? 'create' : 'list')
    }
    window.addEventListener('popstate', onPopState)
    return () => window.removeEventListener('popstate', onPopState)
  }, [])
  const openCreate = () => {
    window.history.pushState({ profileTab: 'delivery', deliveryScreen: 'create' }, '')
    setScreen('create')
    void platform.analytics.track('delivery_address_create_opened')
  }
  if (screen === 'create') return <section className="delivery"><button className="delivery__back" onClick={() => window.history.back()}>‹ Адреса доставки</button><p className="delivery__eyebrow">DELIVERY MFE · REACT 18</p><h2>Куда привезти снеки?</h2><p>Определите точку, чтобы курьер нашёл вас быстрее.</p><button className="delivery__locate" disabled={loading} onClick={() => void findLocation()}><Crosshair size={18} /> {loading ? 'Определяем…' : 'Определить моё местоположение'}</button><p className="delivery__result">{location ?? 'Геолокация пока не определена'}</p><button className="delivery__primary" disabled={!location} onClick={() => void platform.feedback.success()}>Сохранить адрес</button></section>
  return <section className="delivery"><p className="delivery__eyebrow">DELIVERY MFE · REACT 18</p><h2>Адреса доставки</h2><p>Выберите адрес, чтобы любимые снеки приехали вовремя.</p><article><strong>Дом</strong><span>ул. Примерная, 12</span><small>Доставить к двери</small></article><article><strong>Работа</strong><span>наб. Реки, 8</span><small>Позвонить при доставке</small></article><button className="delivery__primary" onClick={openCreate}><Plus size={18} /> Добавить адрес</button></section>
}
