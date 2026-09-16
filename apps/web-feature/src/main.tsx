import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { Bell, ChevronRight, CreditCard, Heart, MapPin, Settings } from 'lucide-react'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import './style.css'

const profileRows = [
  { icon: Heart, label: 'Любимые снеки', value: '12' },
  { icon: MapPin, label: 'Адреса доставки', value: '2' },
  { icon: CreditCard, label: 'Способы оплаты', value: '•••• 4218' },
  { icon: Bell, label: 'Уведомления', value: 'Включены' },
]

function ProfileApp() {
  return (
    <main className="min-h-dvh overflow-hidden bg-background text-foreground">
      <header className="profile-hero px-6 pb-7 pt-12 text-white">
        <p className="text-sm font-medium text-white/75">Аккаунт</p>
        <div className="mt-5 flex items-center gap-4">
          <Avatar className="size-16 border-2 border-white/40 bg-white/20">
            <AvatarFallback className="bg-transparent text-lg font-bold text-white">АК</AvatarFallback>
          </Avatar>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">Анна Крылова</h1>
            <p className="mt-1 text-sm text-white/75">anna@jetsnack.app</p>
          </div>
        </div>
      </header>

      <section className="px-4 pb-8 pt-5">
        <Card className="border-0 bg-white py-0 shadow-[0_10px_28px_rgba(75,48,237,0.12)]">
          <CardContent className="p-0">
            {profileRows.map(({ icon: Icon, label, value }, index) => (
              <div key={label}>
                <button className="flex w-full items-center gap-4 px-5 py-4 text-left active:bg-[#f4f2ff]">
                  <span className="grid size-10 place-items-center rounded-full bg-[#f4f2ff] text-[#4b30ed]">
                    <Icon className="size-5" strokeWidth={2.25} />
                  </span>
                  <span className="min-w-0 flex-1">
                    <span className="block font-semibold">{label}</span>
                    <span className="mt-0.5 block text-sm text-[#6b6375]">{value}</span>
                  </span>
                  <ChevronRight className="size-5 text-[#9b86fa]" />
                </button>
                {index < profileRows.length - 1 && <Separator className="ml-[76px] w-auto bg-black/10" />}
              </div>
            ))}
          </CardContent>
        </Card>

        <Card className="mt-5 border-0 bg-[#f4f2ff] py-0 shadow-none">
          <CardContent className="flex items-center justify-between gap-4 p-5">
            <div>
              <h2 className="font-bold">Настройте профиль</h2>
              <p className="mt-1 text-sm leading-5 text-[#6b6375]">Добавьте номер телефона, чтобы быстрее оформлять заказы.</p>
            </div>
            <Button className="shrink-0 rounded-full bg-[#4b30ed] px-4 text-white hover:bg-[#3420c8]">Добавить</Button>
          </CardContent>
        </Card>

        <button className="mt-6 flex w-full items-center gap-4 px-2 py-3 text-left text-[#6b6375]">
          <Settings className="size-5" />
          <span className="flex-1 font-medium">Настройки приложения</span>
          <ChevronRight className="size-5" />
        </button>
      </section>
    </main>
  )
}

createRoot(document.getElementById('app')!).render(
  <StrictMode>
    <ProfileApp />
  </StrictMode>,
)
