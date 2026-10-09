# Aerix Liquid Glass — full launcher concepts

A complete launcher identity and layout study: ten distinct 1600 × 900 screens, rendered individually with repository Minecraft wallpapers, translucent glass panes, refractive rims, aurora light, and legible UI copy. These are concept screens, not stitched variants.

| # | Screen | Mock image |
|---:|---|---|
| 01 | Home launch desk | [`01-home.png`](mockups/01-home.png) |
| 02 | Worlds and instances | [`02-library.png`](mockups/02-library.png) |
| 03 | New-instance creation | [`03-create-instance.png`](mockups/03-create-instance.png) |
| 04 | Discover catalogue | [`04-discover.png`](mockups/04-discover.png) |
| 05 | Modpack details and install | [`05-mod-details.png`](mockups/05-mod-details.png) |
| 06 | Multiplayer / server list | [`06-multiplayer.png`](mockups/06-multiplayer.png) |
| 07 | Settings / appearance | [`07-settings.png`](mockups/07-settings.png) |
| 08 | Wallpaper and atmosphere studio | [`08-wallpapers.png`](mockups/08-wallpapers.png) |
| 09 | Account and skin atelier | [`09-account-skin.png`](mockups/09-account-skin.png) |
| 10 | Instance control center | [`10-instance-overview.png`](mockups/10-instance-overview.png) |

Regenerate the set from the repository root:

```sh
python3 docs/aerix-liquid-glass/generate_mockups.py
```

The implementation keeps the existing navigation destinations and launcher flows, then reworks the shell, home dashboard, glass material, palette, and shared component proportions around the new identity.
