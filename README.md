# Capacitor embedded demo

Демонстрационный monorepo для статьи о встраивании Capacitor в существующее
native-приложение. Здесь Capacitor не создаёт второе Android-приложение:
React-фича живёт в `apps/web-feature`, а `apps/android-host` — существующий
Compose-хост, в который она встроена как один из экранов.

## Состав

- `apps/android-host` — адаптированный [Jetsnack](https://github.com/android/compose-samples/tree/main/Jetsnack)
  из Android Compose Samples. Экран Profile теперь содержит Capacitor bridge.
- `apps/web-feature` — отдельная React 19 + Vite + Tailwind + shadcn/ui
  фича профиля.

`EmbeddedCapacitorFragment` — небольшой адаптер вокруг Capacitor 8.5.2:
Compose размещает `FragmentContainerView`, фрагмент владеет `Bridge` и WebView,
а `MainActivity` передаёт ему результаты разрешений, activity results, intents и
изменения конфигурации. Это важно для будущих плагинов; обычного WebView для
этого недостаточно.

## Сборка

Предварительно нужны Node.js, JDK 17+ и Android SDK Platform 37 with extension
level 1. Capacitor 8 требует `minSdk 24`, поэтому demo-хост поднят с 23 до 24.

```powershell
cd apps/web-feature
npm install

cd ../android-host
.\gradlew.bat :app:assembleDebug
```

Задача Gradle сама запускает `npm run build`, затем кладёт Vite-бандл в
генерируемые Android assets (`public/`) вместе с `capacitor.config.json`.
Итоговый APK: `apps/android-host/app/build/outputs/apk/debug/app-debug.apk`.

## Проверено

Собран debug APK и проверен на Pixel 5a (Android API 33): нативная лента
Jetsnack запускается, а Profile отображает React-фичу в `CapacitorWebView`
вместе с нативным нижним баром.

## Attribution

Android-хост основан на Jetsnack из Android Compose Samples, распространяемом
по Apache License 2.0. Исходные copyright-заголовки и файл `ASSETS_LICENSE`
сохранены в `apps/android-host`.
