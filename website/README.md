# Aerix Launcher website

A static site for the launcher. No build step, no dependencies, no framework — plain HTML, one
stylesheet and one small script. It is published to GitHub Pages by
[`.github/workflows/deploy_pages.yml`](../.github/workflows/deploy_pages.yml) on every push to
`Aerix-launcher` that touches `website/`.

## Pages

| File | Purpose |
| --- | --- |
| `index.html` | Home — hero, why Aerix, screenshots, renderer teaser, privacy, download |
| `features.html` | The full feature list, grouped |
| `renderers.html` | The renderer lineup, the two-LTW split, how selection works |
| `guide.html` | Install and first-launch walkthrough, tuning, troubleshooting |
| `faq.html` | 14 common questions |
| `changelog.html` | Release notes, currently 1.6 |

## Theming

Visitors pick their own theme, stored in `localStorage` and applied before first paint so there
is no flash of the wrong colours.

- **Mode:** Light · Dark · System (System follows `prefers-color-scheme` and keeps following it
  if the OS changes while the page is open)
- **Accent:** Violet (default, matching the launcher) · Amber · Forest · Ocean · Rose

Both live in `assets/js/site.js`. The palettes are CSS custom properties in
`assets/css/style.css` — `[data-theme="light"]`, `[data-theme="dark"]` and `[data-accent="…"]`.
Adding an accent means one token block plus one `.swatch` button in each page's theme panel.

Motion is deliberately small: a float on the logo, a fade-up on scroll, and hover lifts. All of
it is disabled under `prefers-reduced-motion: reduce`.

## Deploying

1. **Settings → Pages → Build and deployment → Source → GitHub Actions.**
2. In **Settings → Environments → `github-pages`**, allow the `Aerix-launcher` branch to deploy.
3. Push a change under `website/` to `Aerix-launcher`, or run **Deploy website** from the Actions
   tab with `Aerix-launcher` selected. Dispatching from other branches will not publish.

## The address

The site’s custom domain is:

**<https://aerix-launcher.gt.tc/>**

The GitHub Pages fallback remains **<https://entitybrian69-bit.github.io/Aerix-launcher/>**.
All six pages use the custom domain in their canonical and Open Graph metadata; local navigation,
styles, scripts, and images stay relative so the complete site works at either Pages address.

## Custom domain setup

`website/CNAME` contains `aerix-launcher.gt.tc` and is included in every GitHub Pages artifact.
For the subdomain to resolve, its DNS CNAME should point to `entitybrian69-bit.github.io`, and
**Settings → Pages** should show `aerix-launcher.gt.tc` as the custom domain. Once GitHub issues
the TLS certificate, enable **Enforce HTTPS**. The HTML canonical and Open Graph URLs already use
`https://aerix-launcher.gt.tc/`.

## Community

### Discord

Invite: **<https://discord.gg/RS7q9KaCm6>** (server: **AERIX LAUNCHER**)
Appears in the README and in the header, footer and CTAs of all six pages.

> **Set this invite to never expire.** Discord's default is 7 days, and two earlier invites
> died that way — one of them after it was already baked into every page. In Discord:
> *Invite People → Edit invite link → Expire After: **Never***.

### Comments (giscus)

`index.html` and `faq.html` embed [giscus](https://giscus.app), which stores comments as GitHub
Discussions. Threads are mapped by `pathname`, so each page gets its own conversation, and
reactions are enabled.

Current configuration:

| Setting | Value |
| --- | --- |
| `data-repo` | `entitybrian69-bit/Aerix-launcher` |
| `data-repo-id` | `R_kgDOU01PCQ` |
| `data-category-id` | `DIC_kwDOU01PCc4DHAoA` (`Announcements`) |
| `data-mapping` | `pathname` |
| `data-reactions-enabled` | `1` |
| `data-theme` | `preferred_color_scheme` — **overridden at runtime**, see below |

**Requirements for it to work:** Discussions must be enabled in repository settings, the
[giscus GitHub App](https://github.com/apps/giscus) must be installed on the repository, and
the category ID above must exist. If any of those is missing, giscus renders a setup prompt
instead of a comment box.

**Theming.** giscus runs in its own iframe and cannot see this site's theme control, so
`site.js` posts the resolved theme into it — whenever the theme changes, and once on load via a
`MutationObserver` watching for the iframe. Without that the comment box would sit on the OS
preference and visibly disagree with the rest of the page.

The category is referenced **by ID rather than by name** deliberately: renaming the
"Announcements" category in GitHub would silently break a name-based reference.

## Local preview

Any static server works:

```sh
cd website
python3 -m http.server 8000
```

Then open <http://localhost:8000>. Opening `index.html` directly from the filesystem also works,
though the Google Fonts request needs a network connection.

## Editing

The header and footer are duplicated across the six pages on purpose — there is no templating
engine and no build step to hide it behind. If you change a nav item, change it in all six files'
header, mobile menu and footer. `grep -c 'aria-current' *.html` is a quick sanity check that
every page still marks itself as current exactly once.

Images in `assets/img/` are optimized copies of the originals in the repository's top-level
`assets/` directory. Screenshots are 1200px wide, the logo is 512px, and all of them are
metadata-stripped. Re-optimize after replacing a screenshot:

```sh
convert ../assets/screenshots/shot-home.jpg -resize 1200x -strip -interlace Plane -quality 82 \
  assets/img/shot-home.jpg
```

## Download links

Every download button points at
<https://github.com/entitybrian69-bit/Aerix-launcher/releases/latest> rather than at a specific
APK filename, so the site keeps working across releases without edits.

## Accessibility

Semantic landmarks, a skip link, visible focus rings, `aria-current` on the active nav item, and
`aria-pressed` on the theme controls. The theme popover closes on `Escape` and on an outside
click.
