import { defineConfig } from 'vitepress'
import { withMermaid } from 'vitepress-plugin-mermaid'

const github = 'https://github.com/kaleidot725/PulsePlatform'

export default withMermaid(defineConfig({
  title: 'PulsePlatform',
  base: '/PulsePlatform/',

  head: [
    ['link', { rel: 'icon', type: 'image/svg+xml', href: '/PulsePlatform/favicon.svg' }],
  ],

  locales: {
    root: {
      label: 'English',
      lang: 'en',
      description: 'Rules and building blocks for Compose UIs that AI agents build, on the desktop, Android and iOS',
      themeConfig: {
        nav: [
          { text: 'Guide', link: '/guide/', activeMatch: '/guide/' },
          { text: 'API', link: '/api/', activeMatch: '/api/' },
          { text: 'Changelog', link: `${github}/releases` },
        ],
        sidebar: {
          '/guide/': [
            {
              text: 'Introduction',
              items: [
                { text: 'What is PulsePlatform?', link: '/guide/' },
                { text: 'Desktop quick start', link: '/guide/getting-started' },
                { text: 'Mobile quick start', link: '/guide/getting-started-mobile' },
                { text: 'Building with AI agents', link: '/guide/ai-agents' },
              ],
            },
            {
              text: 'Rules',
              items: [
                { text: 'Overview', link: '/guide/rules/' },
                { text: 'UI structure', link: '/guide/rules/ui' },
                { text: 'Domain and data', link: '/guide/rules/domain' },
                { text: 'Modules and Koin', link: '/guide/rules/modules' },
                { text: 'Text', link: '/guide/rules/text' },
              ],
            },
            {
              text: 'Library',
              items: [
                { text: 'MVI', link: '/guide/mvi' },
                { text: 'ViewModel', link: '/guide/viewmodel' },
                { text: 'Navigation 3', link: '/guide/navigation3' },
              ],
            },
            {
              text: 'Desktop',
              items: [
                { text: 'Host and Container', link: '/guide/container' },
                { text: 'Broadcast', link: '/guide/broadcast' },
                { text: 'Unicast', link: '/guide/unicast' },
              ],
            },
            {
              text: 'Mobile',
              items: [
                { text: 'Screens', link: '/guide/mobile' },
              ],
            },
          ],
          '/api/': [
            {
              text: 'API Reference',
              items: [
                { text: 'Overview', link: '/api/' },
                { text: 'Rules', link: '/api/rules' },
              ],
            },
            {
              text: 'Library',
              items: [
                { text: 'PulseViewModel', link: '/api/pulse-viewmodel' },
                { text: 'Composables', link: '/api/composables' },
                { text: 'Marker Interfaces', link: '/api/interfaces' },
                { text: 'Navigation 3', link: '/api/navigation3' },
              ],
            },
            {
              text: 'Desktop',
              items: [
                { text: 'PulseContainer', link: '/api/pulse-container' },
              ],
            },
          ],
        },
        editLink: {
          pattern: `${github}/edit/main/docs/:path`,
          text: 'Edit this page on GitHub',
        },
        footer: {
          message: 'Released under the Apache 2.0 License.',
          copyright: 'Copyright © 2026 kaleidot725',
        },
      },
    },
    ja: {
      label: '日本語',
      lang: 'ja',
      link: '/ja/',
      description: 'AI エージェントが作る Compose UI のためのルールと部品。デスクトップ、Android、iOS に対応',
      themeConfig: {
        nav: [
          { text: 'ガイド', link: '/ja/guide/', activeMatch: '/ja/guide/' },
          { text: 'API', link: '/ja/api/', activeMatch: '/ja/api/' },
          { text: '変更履歴', link: `${github}/releases` },
        ],
        sidebar: {
          '/ja/guide/': [
            {
              text: 'はじめに',
              items: [
                { text: 'PulsePlatform とは', link: '/ja/guide/' },
                { text: 'デスクトップのクイックスタート', link: '/ja/guide/getting-started' },
                { text: 'モバイルのクイックスタート', link: '/ja/guide/getting-started-mobile' },
                { text: 'AI エージェントと作る', link: '/ja/guide/ai-agents' },
              ],
            },
            {
              text: 'ルール',
              items: [
                { text: '概要', link: '/ja/guide/rules/' },
                { text: 'UI の構成', link: '/ja/guide/rules/ui' },
                { text: 'ドメインとデータ', link: '/ja/guide/rules/domain' },
                { text: 'モジュールと Koin', link: '/ja/guide/rules/modules' },
                { text: '文言', link: '/ja/guide/rules/text' },
              ],
            },
            {
              text: 'ライブラリ',
              items: [
                { text: 'MVI', link: '/ja/guide/mvi' },
                { text: 'ViewModel', link: '/ja/guide/viewmodel' },
                { text: 'Navigation 3', link: '/ja/guide/navigation3' },
              ],
            },
            {
              text: 'デスクトップ',
              items: [
                { text: 'Host と Container', link: '/ja/guide/container' },
                { text: 'Broadcast', link: '/ja/guide/broadcast' },
                { text: 'Unicast', link: '/ja/guide/unicast' },
              ],
            },
            {
              text: 'モバイル',
              items: [
                { text: 'Screen', link: '/ja/guide/mobile' },
              ],
            },
          ],
          '/ja/api/': [
            {
              text: 'API リファレンス',
              items: [
                { text: '概要', link: '/ja/api/' },
                { text: 'ルール', link: '/ja/api/rules' },
              ],
            },
            {
              text: 'ライブラリ',
              items: [
                { text: 'PulseViewModel', link: '/ja/api/pulse-viewmodel' },
                { text: 'Composable', link: '/ja/api/composables' },
                { text: 'マーカーインターフェース', link: '/ja/api/interfaces' },
                { text: 'Navigation 3', link: '/ja/api/navigation3' },
              ],
            },
            {
              text: 'デスクトップ',
              items: [
                { text: 'PulseContainer', link: '/ja/api/pulse-container' },
              ],
            },
          ],
        },
        editLink: {
          pattern: `${github}/edit/main/docs/:path`,
          text: 'GitHub でこのページを編集',
        },
        footer: {
          message: 'Apache 2.0 License の下で公開されています。',
          copyright: 'Copyright © 2026 kaleidot725',
        },
        outline: { label: 'このページの内容' },
        docFooter: { prev: '前のページ', next: '次のページ' },
        lastUpdated: { text: '最終更新' },
        returnToTopLabel: 'トップへ戻る',
        sidebarMenuLabel: 'メニュー',
        darkModeSwitchLabel: 'テーマ',
        langMenuLabel: '言語を変更',
      },
    },
  },

  mermaid: {
    theme: 'base',
    themeVariables: {
      fontFamily: 'Inter, ui-sans-serif, system-ui, sans-serif',
      fontSize: '14px',
    },
  },

  themeConfig: {
    logo: '/logo.svg',

    socialLinks: [
      { icon: 'github', link: github },
    ],

    search: {
      provider: 'local',
      options: {
        locales: {
          ja: {
            translations: {
              button: { buttonText: '検索', buttonAriaLabel: '検索' },
              modal: {
                noResultsText: '見つかりませんでした',
                resetButtonTitle: 'クリア',
                footer: { selectText: '選択', navigateText: '移動', closeText: '閉じる' },
              },
            },
          },
        },
      },
    },
  },
}))
