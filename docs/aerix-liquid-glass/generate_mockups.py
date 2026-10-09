#!/usr/bin/env python3
"""Render ten crisp Aerix launcher concept screens with ImageMagick.

The mockups are intentionally composed from repository wallpapers, simple vector
shapes, and system fonts so UI copy stays legible and the output is reproducible.
"""
from __future__ import annotations

import shutil
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
WALLPAPER_DIR = ROOT / "MiraiLauncher/src/main/assets/wallpapers"
RESOURCE_DIR = ROOT / "MiraiLauncher/src/main/res/drawable-nodpi"
OUT = ROOT / "docs/aerix-liquid-glass/mockups"
W, H = 1600, 900
FONT = "DejaVu-Sans"
FONT_BOLD = "DejaVu-Sans-Bold"
WHITE = "#F4F8FC"
SECONDARY = "#C1D0DC"
MUTED = "#8EA3B2"
AQUA = "#9AF0E8"
CYAN = "#9AE9FF"
VIOLET = "#C2B4FF"
ROSE = "#F6B4DE"
GREEN = "#8AE5B3"
INK = "#091521"


def run(args: list[str]) -> None:
    subprocess.run(args, check=True, stdout=subprocess.DEVNULL)


def escaped(text: str) -> str:
    return text.replace("\\", "\\\\").replace("'", "\\'")


def rounded_crop(source: Path, width: int, height: int, radius: int, out: Path) -> None:
    mask = out.with_suffix(".mask.png")
    run([
        "convert", str(source), "-resize", f"{width}x{height}^", "-gravity", "center",
        "-extent", f"{width}x{height}",
        "(", "-size", f"{width}x{height}", "xc:none", "-fill", "white",
        "-draw", f"roundrectangle 0,0 {width - 1},{height - 1} {radius},{radius}", ")",
        "-compose", "CopyOpacity", "-composite", str(out)
    ])
    mask.unlink(missing_ok=True)


class Screen:
    def __init__(self, wallpaper: str, section: str, active_nav: str):
        self.temp = Path(tempfile.mkdtemp(prefix="aerix-mock-"))
        self.args: list[str] = [
            "convert", str(WALLPAPER_DIR / wallpaper),
            "-resize", f"{W}x{H}^", "-gravity", "center", "-extent", f"{W}x{H}",
            "-modulate", "84,92,100",
            "-fill", "rgba(4,10,22,0.54)", "-stroke", "none",
            "-draw", f"rectangle 0,0 {W},{H}", "-gravity", "NorthWest"
        ]
        self.section = section
        self.active_nav = active_nav
        self.add_aurora()
        self.chrome()

    def add_aurora(self) -> None:
        glow = self.temp / "aurora.png"
        run([
            "convert", "-size", f"{W}x{H}", "xc:none",
            "-fill", "rgba(53,221,242,0.20)", "-draw", "circle 1370,80 1370,400",
            "-fill", "rgba(152,112,255,0.14)", "-draw", "circle 200,790 200,1120",
            "-fill", "rgba(255,138,205,0.07)", "-draw", "circle 930,60 930,230",
            "-blur", "0x90", str(glow)
        ])
        self.args.extend([str(glow), "-compose", "over", "-composite"])

    def draw(self, fill: str, stroke: str, width: int, command: str) -> None:
        self.args.extend(["-fill", fill, "-stroke", stroke, "-strokewidth", str(width), "-draw", command])

    def panel(
        self,
        x: int,
        y: int,
        width: int,
        height: int,
        radius: int = 24,
        tint: str = "rgba(184,224,244,0.105)",
        edge: str = "rgba(244,252,255,0.34)",
        shine: bool = True,
    ) -> None:
        x2, y2 = x + width, y + height
        self.draw("rgba(0,0,0,0.18)", "none", 0, f"roundrectangle {x},{y + 5} {x2},{y2 + 5} {radius},{radius}")
        self.draw(tint, edge, 1, f"roundrectangle {x},{y} {x2},{y2} {radius},{radius}")
        if shine:
            self.draw("none", "rgba(255,255,255,0.39)", 1, f"line {x + radius + 10},{y + 1} {x2 - radius - 10},{y + 1}")

    def pill(self, x: int, y: int, width: int, label: str, tint: str = "rgba(216,242,255,0.11)", edge: str = "rgba(239,251,255,0.24)", color: str = SECONDARY, size: int = 12) -> None:
        self.draw(tint, edge, 1, f"roundrectangle {x},{y} {x + width},{y + 34} 17,17")
        self.text(x + 16, y + 22, label, size, color, bold=True)

    def button(self, x: int, y: int, width: int, label: str, fill: str = AQUA, color: str = INK, height: int = 44, size: int = 12) -> None:
        self.draw(fill, "rgba(255,255,255,0.72)", 1, f"roundrectangle {x},{y} {x + width},{y + height} {height // 2},{height // 2}")
        self.text(x + 18, y + height // 2 + size // 2, label, size, color, bold=True)

    def text(self, x: int, y: int, content: str, size: int, color: str = WHITE, bold: bool = False, letter_spacing: int | None = None) -> None:
        font = FONT_BOLD if bold else FONT
        self.args.extend(["-font", font, "-pointsize", str(size), "-fill", color, "-stroke", "none"])
        self.args.extend(["-kerning", str(letter_spacing if letter_spacing is not None else 0)])
        self.args.extend(["-draw", f"text {x},{y} '{escaped(content)}'"])

    def line(self, x1: int, y1: int, x2: int, y2: int, color: str = "rgba(228,246,255,0.22)", width: int = 1) -> None:
        self.draw("none", color, width, f"line {x1},{y1} {x2},{y2}")

    def circle(self, x: int, y: int, radius: int, fill: str, edge: str = "rgba(255,255,255,0.36)", width: int = 1) -> None:
        self.draw(fill, edge, width, f"circle {x},{y} {x + radius},{y}")

    def add_image(self, source: Path, x: int, y: int, width: int, height: int, radius: int = 18) -> None:
        crop = self.temp / f"crop-{len(list(self.temp.iterdir()))}.png"
        rounded_crop(source, width, height, radius, crop)
        self.args.extend(["-gravity", "NorthWest", str(crop), "-geometry", f"+{x}+{y}", "-compose", "over", "-composite", "-gravity", "NorthWest"])

    def chrome(self) -> None:
        # Narrow floating rail with a luminous selection capsule.
        self.panel(24, 24, 78, 852, radius=38, tint="rgba(188,223,241,0.105)", edge="rgba(238,250,255,0.36)")
        self.circle(63, 67, 21, "rgba(82,223,227,0.22)", "rgba(194,253,255,0.68)", 2)
        self.text(55, 76, "A", 25, AQUA, bold=True)
        nav = [
            ("H", "HOME"), ("L", "LIBRARY"), ("D", "DISCOVER"),
            ("M", "MULTIPLAYER"), ("W", "WALLPAPERS"), ("S", "SETTINGS")
        ]
        for index, (glyph, name) in enumerate(nav):
            cy = 160 + index * 86
            if name == self.active_nav:
                self.draw("rgba(130,236,246,0.20)", "rgba(183,250,255,0.56)", 1, f"roundrectangle 36,{cy - 26} 90,{cy + 27} 19,19")
            self.circle(63, cy, 16, "rgba(217,241,250,0.08)" if name != self.active_nav else "rgba(148,236,244,0.15)", "rgba(233,248,255,0.18)")
            self.text(57, cy + 5, glyph, 15, AQUA if name == self.active_nav else SECONDARY, bold=True)
        self.line(43, 695, 83, 695, "rgba(255,255,255,0.22)")
        self.circle(63, 744, 18, "rgba(150,116,255,0.20)", "rgba(235,223,255,0.55)")
        self.text(57, 750, "P", 15, WHITE, bold=True)
        self.circle(63, 818, 18, "rgba(236,247,255,0.08)", "rgba(238,251,255,0.30)")
        self.text(57, 824, "?", 14, SECONDARY, bold=True)

        # Floating global command bar.
        self.panel(124, 24, 1452, 80, radius=38, tint="rgba(194,229,246,0.115)", edge="rgba(239,251,255,0.42)")
        self.circle(162, 64, 18, "rgba(115,225,232,0.18)", "rgba(201,254,255,0.52)")
        self.text(156, 70, "A", 18, AQUA, bold=True)
        self.text(192, 56, "AERIX", 13, AQUA, bold=True, letter_spacing=2)
        self.text(192, 79, self.section.upper(), 10, SECONDARY, bold=True, letter_spacing=1)
        self.panel(762, 43, 426, 42, radius=21, tint="rgba(210,239,250,0.075)", edge="rgba(238,251,255,0.22)", shine=False)
        self.circle(787, 64, 7, "none", CYAN, 2)
        self.line(792, 69, 797, 74, CYAN, 2)
        self.text(810, 69, "Search mods, versions, worlds", 12, MUTED)
        self.pill(1206, 43, 126, "⇩  TASKS", tint="rgba(164,222,246,0.10)", size=11)
        self.pill(1343, 43, 92, "FILES", tint="rgba(164,222,246,0.08)", size=11)
        self.circle(1500, 64, 21, "rgba(177,153,255,0.22)", "rgba(242,230,255,0.62)", 2)
        self.text(1494, 69, "B", 15, WHITE, bold=True)

    def label(self, x: int, y: int, text: str, color: str = CYAN) -> None:
        self.text(x, y, text.upper(), 10, color, bold=True, letter_spacing=1)

    def title(self, x: int, y: int, text: str, size: int = 32) -> None:
        self.text(x, y, text, size, WHITE, bold=True)

    def footer(self) -> None:
        self.text(132, 868, "AERIX  /  LIQUID GLASS CONCEPT", 9, "rgba(221,239,247,0.55)", bold=True, letter_spacing=1)

    def save(self, filename: str) -> None:
        try:
            self.footer()
            self.args.extend(["-define", "png:compression-level=9", str(OUT / filename)])
            run(self.args)
        finally:
            shutil.rmtree(self.temp, ignore_errors=True)


def create_home() -> None:
    s = Screen("wp_07_ocean_arch.jpg", "Home", "HOME")
    s.label(132, 147, "YOUR LAUNCH DESK")
    s.text(132, 177, "A little space for big worlds.", 13, SECONDARY)
    s.panel(132, 198, 922, 420, radius=34, tint="rgba(179,226,246,0.105)")
    s.add_image(WALLPAPER_DIR / "wp_07_ocean_arch.jpg", 576, 214, 458, 388, 28)
    s.draw("rgba(7,17,29,0.80)", "none", 0, "roundrectangle 146,212 634,602 30,30")
    s.label(170, 250, "READY WHEN YOU ARE", GREEN)
    s.title(170, 330, "A world of", 42)
    s.title(170, 378, "your own.", 42)
    s.text(170, 413, "Your next chapter is only one touch away.", 13, SECONDARY)
    s.pill(170, 444, 130, "1.21.4  /  VANILLA", tint="rgba(122,219,220,0.16)", edge="rgba(160,251,248,0.42)", color=AQUA, size=10)
    s.button(170, 500, 176, "▶  PLAY NOW", fill=AQUA)
    s.text(372, 529, "OPEN LIBRARY  ↗", 10, WHITE, bold=True, letter_spacing=1)
    s.panel(1076, 198, 474, 420, radius=32, tint="rgba(182,218,241,0.10)")
    s.label(1110, 244, "PICK UP WHERE YOU LEFT OFF")
    s.title(1110, 279, "Recent worlds", 22)
    for i, (name, sub, icon, accent) in enumerate([
        ("Valley of Echoes", "Fabric 1.20.1  ·  Today", "V", AQUA),
        ("Copper & Clouds", "NeoForge 1.21  ·  Yesterday", "C", VIOLET),
        ("Creative Archive", "Vanilla 1.21.4  ·  Sunday", "A", ROSE),
    ]):
        y = 307 + i * 84
        s.panel(1098, y, 428, 70, radius=22, tint="rgba(216,239,251,0.075)", edge="rgba(237,250,255,0.20)", shine=False)
        s.circle(1135, y + 35, 20, "rgba(137,224,234,0.18)", "rgba(255,255,255,0.20)")
        s.text(1129, y + 41, icon, 14, accent, bold=True)
        s.text(1170, y + 30, name, 13, WHITE, bold=True)
        s.text(1170, y + 51, sub, 10, SECONDARY)
        s.circle(1490, y + 35, 11, "rgba(132,237,226,0.18)", "rgba(150,246,237,0.42)")
        s.text(1487, y + 39, "›", 13, AQUA, bold=True)
    actions = [
        ("＋", "CREATE INSTANCE", "A clean new profile", AQUA),
        ("✧", "EXPLORE CONTENT", "Modpacks, mods & more", CYAN),
        ("▣", "YOUR LIBRARY", "Profiles, versions, saves", VIOLET),
        ("⌂", "FILE SPACE", "Worlds, logs, exports", ROSE),
    ]
    for i, (glyph, name, sub, color) in enumerate(actions):
        x = 132 + i * 359
        s.panel(x, 644, 342, 156, radius=28, tint="rgba(194,228,244,0.105)")
        s.circle(x + 43, 690, 19, "rgba(151,226,238,0.13)", "rgba(241,253,255,0.25)")
        s.text(x + 36, 696, glyph, 18, color, bold=True)
        s.text(x + 26, 740, name, 11, WHITE, bold=True, letter_spacing=1)
        s.text(x + 26, 762, sub, 10, SECONDARY)

    s.save("01-home.png")


def create_library() -> None:
    s = Screen("wp_14_cozy_village.jpg", "Worlds & instances", "LIBRARY")
    s.label(132, 151, "WORLD VAULT")
    s.title(132, 194, "Worlds & instances", 32)
    s.text(132, 221, "A considered home for every profile you have built.", 12, SECONDARY)
    s.button(1370, 152, 178, "＋  NEW INSTANCE", fill=AQUA, size=11)
    for i, (label, active) in enumerate([("ALL  12", True), ("VANILLA  5", False), ("MODDED  7", False), ("FAVOURITES", False)]):
        s.pill(132 + i * 136, 249, 124, label,
               tint="rgba(124,235,231,0.19)" if active else "rgba(201,231,244,0.09)",
               edge="rgba(155,243,240,0.42)" if active else "rgba(242,250,255,0.18)",
               color=AQUA if active else SECONDARY, size=9)
    s.panel(132, 301, 940, 497, radius=30)
    cards = [
        ("wp_04_crystal_river.jpg", "Valley of Echoes", "Fabric 1.20.1", "LAST PLAYED  TODAY", CYAN),
        ("wp_02_cherry_blossom.jpg", "Sakura Harbor", "Vanilla 1.21.4", "LAST PLAYED  MON", ROSE),
        ("wp_09_flower_meadow.jpg", "Wildflower", "NeoForge 1.21", "MODDED  24", VIOLET),
        ("wp_14_cozy_village.jpg", "Copper & Clouds", "Quilt 1.20.4", "LAST PLAYED  SUN", AQUA),
    ]
    for i, (wall, name, meta, foot, accent) in enumerate(cards):
        x = 153 + (i % 2) * 452
        y = 322 + (i // 2) * 226
        s.panel(x, y, 428, 204, radius=24, tint="rgba(211,235,248,0.08)", edge="rgba(241,251,255,0.22)")
        s.add_image(WALLPAPER_DIR / wall, x + 10, y + 10, 148, 184, 18)
        s.label(x + 180, y + 42, foot, accent)
        s.text(x + 180, y + 82, name, 18, WHITE, bold=True)
        s.text(x + 180, y + 111, meta, 11, SECONDARY)
        s.pill(x + 180, y + 137, 102, "OPEN  ›", tint="rgba(127,228,227,0.15)", edge="rgba(144,239,239,0.3)", color=AQUA, size=9)
    s.panel(1095, 249, 455, 549, radius=30, tint="rgba(200,229,245,0.11)")
    s.add_image(WALLPAPER_DIR / "wp_07_ocean_arch.jpg", 1110, 263, 425, 195, 24)
    s.label(1128, 493, "SELECTED PROFILE", AQUA)
    s.title(1128, 530, "Valley of Echoes", 23)
    s.text(1128, 558, "Fabric 1.20.1  ·  18 mods", 12, SECONDARY)
    s.line(1128, 581, 1516, 581)
    s.text(1128, 615, "GAME VERSION", 9, MUTED, bold=True, letter_spacing=1)
    s.text(1128, 641, "Minecraft 1.20.1", 12, WHITE, bold=True)
    s.text(1128, 682, "LOADER", 9, MUTED, bold=True, letter_spacing=1)
    s.text(1128, 708, "Fabric 0.16.9", 12, WHITE, bold=True)
    s.button(1128, 737, 180, "▶  PLAY", fill=AQUA)
    s.text(1330, 764, "PROFILE SETTINGS", 9, SECONDARY, bold=True, letter_spacing=1)

    s.save("02-library.png")


def create_new_instance() -> None:
    s = Screen("wp_06_the_end.jpg", "Create an instance", "LIBRARY")
    s.label(132, 151, "CREATE WORKSPACE")
    s.title(132, 194, "A new world, your way.", 31)
    s.text(132, 221, "Choose a game build. Add a loader. We will take care of the rest.", 12, SECONDARY)
    s.panel(132, 247, 1418, 76, radius=28, tint="rgba(199,229,245,0.095)")
    steps = [("01", "GAME VERSION", True), ("02", "LOADER", False), ("03", "PROFILE DETAILS", False), ("04", "INSTALL", False)]
    for i, (num, label, active) in enumerate(steps):
        x = 166 + i * 344
        color = AQUA if active else MUTED
        s.circle(x + 18, 285, 14, "rgba(131,230,235,0.18)" if active else "rgba(217,234,244,0.08)", "rgba(175,249,252,0.4)" if active else "rgba(230,244,250,0.18)")
        s.text(x + 9, 289, num, 9, color, bold=True)
        s.text(x + 43, 289, label, 10, color, bold=True, letter_spacing=1)
        if i < 3:
            s.line(x + 190, 285, x + 314, 285, "rgba(229,246,255,0.18)")
    s.panel(132, 346, 900, 454, radius=30)
    s.label(166, 390, "01  /  SELECT THE GAME BUILD")
    s.text(166, 425, "Search releases", 12, SECONDARY)
    s.panel(164, 447, 820, 42, radius=21, tint="rgba(197,229,245,0.075)", edge="rgba(235,248,255,0.18)", shine=False)
    s.text(187, 474, "⌕    Search Minecraft versions...", 11, MUTED)
    versions = [("1.21.4", "LATEST RELEASE", True), ("1.21.3", "RELEASE", False), ("1.20.4", "POPULAR", False), ("1.20.1", "STABLE", False), ("1.19.4", "RELEASE", False), ("1.16.5", "CLASSIC", False)]
    for i, (ver, tag, active) in enumerate(versions):
        x = 164 + (i % 3) * 276
        y = 512 + (i // 3) * 105
        s.panel(x, y, 256, 86, radius=20,
                tint="rgba(118,227,227,0.16)" if active else "rgba(208,235,247,0.07)",
                edge="rgba(157,248,241,0.48)" if active else "rgba(237,249,255,0.19)", shine=False)
        s.text(x + 18, y + 35, ver, 20, WHITE, bold=True)
        s.text(x + 18, y + 61, tag, 9, AQUA if active else MUTED, bold=True, letter_spacing=1)
    s.panel(1054, 346, 496, 454, radius=30)
    s.label(1090, 392, "02  /  CHOOSE A LOADER")
    for i, (name, meta, active) in enumerate([("Vanilla", "Official game", True), ("Fabric", "Lightweight + mods", False), ("Forge", "A broad mod library", False), ("NeoForge", "Modern modpacks", False)]):
        y = 421 + i * 72
        s.panel(1080, y, 444, 58, radius=18,
                tint="rgba(125,232,226,0.17)" if active else "rgba(210,235,245,0.06)",
                edge="rgba(160,248,243,0.42)" if active else "rgba(238,249,255,0.18)", shine=False)
        s.circle(1109, y + 29, 9, "rgba(132,235,227,0.24)" if active else "rgba(241,251,255,0.05)", "rgba(208,250,248,0.5)" if active else "rgba(239,250,255,0.24)")
        s.text(1132, y + 25, name, 12, WHITE, bold=True)
        s.text(1132, y + 43, meta, 9, SECONDARY)
    s.line(1090, 733, 1514, 733)
    s.text(1090, 764, "ESTIMATED DOWNLOAD", 9, MUTED, bold=True, letter_spacing=1)
    s.text(1090, 786, "~ 180 MB", 12, WHITE, bold=True)
    s.button(1320, 740, 196, "CONTINUE  →", fill=AQUA, size=11)

    s.save("03-create-instance.png")


def create_discover() -> None:
    s = Screen("wp_09_flower_meadow.jpg", "Discover", "DISCOVER")
    s.label(132, 151, "THE CURATED FRONTIER")
    s.title(132, 194, "Find your next favourite.", 31)
    s.text(132, 221, "A living catalogue of modpacks, mods, resource packs, and worlds.", 12, SECONDARY)
    for i, item in enumerate(["FEATURED", "MODPACKS", "MODS", "RESOURCE PACKS", "SHADERS"]):
        s.pill(132 + i * 142, 244, 130, item, tint="rgba(129,230,229,0.16)" if i == 0 else "rgba(207,234,246,0.075)", color=AQUA if i == 0 else SECONDARY, size=9)
    s.panel(132, 300, 922, 356, radius=32)
    s.add_image(WALLPAPER_DIR / "wp_04_crystal_river.jpg", 548, 314, 491, 328, 26)
    s.draw("rgba(7,17,29,0.82)", "none", 0, "roundrectangle 145,313 650,643 26,26")
    s.label(174, 359, "EDITOR'S PICK  /  MODPACK", VIOLET)
    s.title(174, 405, "Into the", 34)
    s.title(174, 444, "Wilds", 34)
    s.text(174, 477, "A slower, softer survival journey.", 12, SECONDARY)
    s.text(174, 508, "Explore biomes, build a home, and find your rhythm.", 10, SECONDARY)
    s.pill(174, 536, 102, "1.21  /  FABRIC", tint="rgba(207,185,255,0.14)", edge="rgba(221,210,255,0.36)", color=VIOLET, size=9)
    s.button(174, 587, 150, "VIEW PACK  →", fill=VIOLET, color=INK, height=38, size=10)
    s.panel(1076, 300, 474, 356, radius=32)
    s.label(1110, 343, "TRENDING THIS WEEK")
    for i, (name, category, count, accent) in enumerate([
        ("Soft Horizons", "SHADERS", "42k installs", CYAN),
        ("Create: Reframed", "MODPACK", "31k installs", AQUA),
        ("Wilder World", "MOD", "19k installs", ROSE),
    ]):
        y = 367 + i * 86
        s.circle(1144, y + 22, 20, "rgba(150,224,238,0.16)", "rgba(239,250,255,0.25)")
        s.text(1138, y + 28, str(i + 1), 13, accent, bold=True)
        s.text(1180, y + 19, name, 13, WHITE, bold=True)
        s.text(1180, y + 39, f"{category}  ·  {count}", 9, SECONDARY)
        s.line(1110, y + 67, 1515, y + 67)
    s.label(132, 698, "BROWSE BY MOOD")
    cards = [("wp_02_cherry_blossom.jpg", "Calm & cozy", "COMFORT BUILDS"), ("wp_13_frozen_glacier.jpg", "Into the wild", "EXPLORATION"), ("wp_05_nether_fortress.jpg", "Hard mode", "CHALLENGE RUNS"), ("wp_20_sakura_sunbeams.jpg", "Make it yours", "CREATIVE TOOLS")]
    for i, (wall, name, sub) in enumerate(cards):
        x = 132 + i * 359
        s.panel(x, 718, 342, 134, radius=24, tint="rgba(207,231,244,0.09)")
        s.add_image(WALLPAPER_DIR / wall, x + 9, 727, 126, 116, 17)
        s.text(x + 154, 773, name, 15, WHITE, bold=True)
        s.text(x + 154, 798, sub, 9, CYAN, bold=True, letter_spacing=1)

    s.save("04-discover.png")


def create_pack_detail() -> None:
    s = Screen("wp_04_crystal_river.jpg", "Pack details", "DISCOVER")
    s.label(132, 151, "DISCOVER  /  MODPACKS  /  FEATURED")
    s.panel(132, 177, 900, 470, radius=32)
    s.add_image(WALLPAPER_DIR / "wp_07_ocean_arch.jpg", 146, 191, 872, 442, 26)
    s.draw("rgba(6,15,27,0.58)", "none", 0, "roundrectangle 146,470 1018,633 26,26")
    s.pill(176, 500, 120, "CURATED PICK", tint="rgba(171,156,255,0.18)", edge="rgba(220,207,255,0.44)", color=VIOLET, size=9)
    s.title(176, 556, "Better Adventures", 33)
    s.text(176, 590, "A deep, tactile journey through a reimagined Overworld.", 12, WHITE)
    s.panel(1054, 177, 496, 610, radius=32)
    s.label(1090, 223, "PACK OVERVIEW")
    s.title(1090, 270, "Better", 28)
    s.title(1090, 304, "Adventures", 28)
    s.text(1090, 340, "by the Aerix community", 11, SECONDARY)
    s.pill(1090, 365, 98, "FABRIC  1.21", tint="rgba(125,232,227,0.14)", color=AQUA, size=9)
    s.pill(1197, 365, 109, "64 MODS", tint="rgba(189,170,255,0.16)", color=VIOLET, size=9)
    s.text(1090, 430, "4.9", 28, WHITE, bold=True)
    s.text(1154, 430, "★★★★★   2,418 reviews", 11, AQUA, bold=True)
    s.text(1090, 463, "A balanced collection for long evenings: discovery,", 11, SECONDARY)
    s.text(1090, 483, "building, and small surprises around every corner.", 11, SECONDARY)
    s.line(1090, 510, 1512, 510)
    s.text(1090, 541, "LAST UPDATED", 9, MUTED, bold=True, letter_spacing=1)
    s.text(1090, 564, "October 02, 2026", 11, WHITE, bold=True)
    s.text(1090, 596, "DOWNLOAD SIZE", 9, MUTED, bold=True, letter_spacing=1)
    s.text(1090, 619, "~ 842 MB", 11, WHITE, bold=True)
    s.button(1090, 681, 218, "＋  INSTALL PACK", fill=AQUA, height=48, size=11)
    s.text(1330, 710, "♡  SAVE", 10, SECONDARY, bold=True, letter_spacing=1)
    s.panel(132, 675, 900, 151, radius=28, tint="rgba(197,228,243,0.09)")
    s.label(166, 714, "INCLUDED IN THIS PACK")
    for i, (name, val, color) in enumerate([("World generation", "18", AQUA), ("Building tools", "14", VIOLET), ("Quality of life", "22", CYAN), ("Visuals", "10", ROSE)]):
        x = 166 + i * 206
        s.text(x, 759, val, 20, color, bold=True)
        s.text(x + 32, 759, name, 10, WHITE, bold=True)
        s.text(x + 32, 782, "verified & compatible", 8, SECONDARY)

    s.save("05-mod-details.png")


def create_multiplayer() -> None:
    s = Screen("wp_18_crater_harbor.jpg", "Multiplayer", "MULTIPLAYER")
    s.label(132, 151, "PLAY TOGETHER")
    s.title(132, 194, "Your people are one click away.", 31)
    s.text(132, 221, "Keep your favourite servers close, clear, and ready to join.", 12, SECONDARY)
    s.panel(132, 253, 1418, 128, radius=30, tint="rgba(137,226,239,0.13)")
    s.circle(182, 316, 25, "rgba(108,237,227,0.2)", "rgba(188,254,249,0.56)", 2)
    s.text(174, 323, "↗", 21, AQUA, bold=True)
    s.label(230, 297, "QUICK CONNECT  /  JOIN A SERVER")
    s.panel(230, 311, 705, 40, radius=20, tint="rgba(212,238,249,0.08)", edge="rgba(239,250,255,0.23)", shine=False)
    s.text(250, 337, "play.example.net", 12, WHITE)
    s.button(961, 302, 170, "JOIN SERVER  →", fill=AQUA, height=46, size=11)
    s.text(1171, 321, "LAST CONNECTION", 9, MUTED, bold=True, letter_spacing=1)
    s.text(1171, 346, "2 hours ago", 12, WHITE, bold=True)
    s.panel(132, 408, 916, 420, radius=30)
    s.label(168, 450, "SAVED SERVERS")
    s.text(168, 485, "Your server list", 21, WHITE, bold=True)
    s.button(852, 430, 158, "＋  ADD SERVER", fill=VIOLET, color=INK, height=38, size=10)
    rows = [("Cedar Valley", "play.cedarvalley.net", "18 / 40", "32 ms", GREEN), ("Build & Bloom", "mc.buildbloom.org", "7 / 24", "58 ms", AQUA), ("The Long Night", "nightfall.example", "Offline", "—", ROSE)]
    for i, (name, address, players, ping, accent) in enumerate(rows):
        y = 512 + i * 88
        s.panel(157, y, 864, 72, radius=19, tint="rgba(207,235,248,0.075)", edge="rgba(237,250,255,0.19)", shine=False)
        s.circle(195, y + 36, 19, "rgba(132,221,235,0.14)", "rgba(244,252,255,0.2)")
        s.text(189, y + 42, str(i + 1), 12, accent, bold=True)
        s.text(231, y + 30, name, 13, WHITE, bold=True)
        s.text(231, y + 51, address, 10, SECONDARY)
        s.text(711, y + 42, players, 10, SECONDARY)
        s.circle(872, y + 36, 4, accent if ping != "—" else ROSE, "none", 0)
        s.text(886, y + 42, ping, 10, accent, bold=True)
        s.text(967, y + 43, "JOIN  ›", 9, AQUA, bold=True, letter_spacing=1)
    s.panel(1072, 408, 478, 420, radius=30, tint="rgba(183,219,241,0.10)")
    s.label(1110, 451, "SERVER DETAILS")
    s.title(1110, 490, "Cedar Valley", 22)
    s.text(1110, 520, "A friendly survival world with room to grow.", 10, SECONDARY)
    s.add_image(WALLPAPER_DIR / "wp_14_cozy_village.jpg", 1100, 542, 422, 126, 20)
    s.pill(1110, 693, 100, "ONLINE", tint="rgba(99,221,165,0.14)", edge="rgba(130,236,183,0.36)", color=GREEN, size=9)
    s.pill(1220, 693, 146, "JAVA  1.21.4", tint="rgba(190,227,245,0.10)", color=CYAN, size=9)
    s.text(1110, 758, "18 players online  ·  32 ms ping", 10, SECONDARY)

    s.save("06-multiplayer.png")


def create_settings() -> None:
    s = Screen("wp_08_moonlit_lake.jpg", "Settings", "SETTINGS")
    s.label(132, 151, "CONTROL ROOM")
    s.title(132, 194, "Settings", 31)
    s.text(132, 221, "The details that make every session feel like yours.", 12, SECONDARY)
    s.panel(132, 253, 328, 566, radius=30)
    s.label(166, 297, "PREFERENCES")
    for i, (name, glyph) in enumerate([("General", "G"), ("Appearance", "A"), ("Java & memory", "J"), ("Game renderer", "R"), ("Controls", "C"), ("Storage", "S"), ("About Aerix", "i")]):
        y = 333 + i * 62
        if name == "Appearance":
            s.draw("rgba(139,231,231,0.16)", "rgba(161,247,245,0.39)", 1, f"roundrectangle 150,{y - 24} 440,{y + 26} 18,18")
        s.circle(180, y, 13, "rgba(169,225,245,0.09)", "rgba(239,251,255,0.17)")
        s.text(175, y + 5, glyph, 10, AQUA if name == "Appearance" else SECONDARY, bold=True)
        s.text(208, y + 5, name, 12, WHITE if name == "Appearance" else SECONDARY, bold=name == "Appearance")
    s.panel(485, 253, 1065, 566, radius=30, tint="rgba(192,225,243,0.105)")
    s.label(525, 299, "APPEARANCE")
    s.title(525, 340, "A softer kind of light.", 25)
    s.text(525, 367, "Fine-tune the atmosphere without hiding the world behind it.", 11, SECONDARY)
    s.panel(525, 395, 986, 111, radius=24, tint="rgba(211,236,247,0.08)", edge="rgba(239,250,255,0.2)")
    s.circle(571, 450, 18, "rgba(108,227,225,0.16)", "rgba(165,245,241,0.45)")
    s.text(565, 456, "✦", 16, AQUA, bold=True)
    s.text(611, 444, "Liquid Glass", 13, WHITE, bold=True)
    s.text(611, 467, "Translucent panels with a soft refractive edge", 10, SECONDARY)
    s.pill(1370, 429, 104, "ENABLED  ●", tint="rgba(104,224,195,0.16)", edge="rgba(140,248,214,0.42)", color=GREEN, size=9)
    s.text(525, 554, "WALLPAPER DIM", 10, WHITE, bold=True, letter_spacing=1)
    s.text(525, 577, "Keep the art visible through the glass surfaces.", 10, SECONDARY)
    s.line(525, 600, 1498, 600, "rgba(255,255,255,0.12)", 2)
    s.line(525, 600, 1188, 600, AQUA, 4)
    s.circle(1188, 600, 10, AQUA, "rgba(255,255,255,0.8)", 2)
    s.text(1470, 581, "68%", 11, AQUA, bold=True)
    s.text(525, 648, "BACKGROUND BLUR", 10, WHITE, bold=True, letter_spacing=1)
    s.text(525, 671, "A light, cached blur for a calm sense of depth.", 10, SECONDARY)
    s.pill(525, 692, 120, "OFF  /  SHARP", tint="rgba(144,226,246,0.13)", edge="rgba(172,239,250,0.35)", color=CYAN, size=9)
    s.pill(654, 692, 110, "SUBTLE", tint="rgba(201,231,245,0.075)", size=9)
    s.pill(773, 692, 110, "BALANCED", tint="rgba(201,231,245,0.075)", size=9)
    s.panel(525, 751, 986, 45, radius=21, tint="rgba(171,219,241,0.07)", edge="rgba(237,250,255,0.16)", shine=False)
    s.text(548, 780, "PERFORMANCE NOTE", 9, AQUA, bold=True, letter_spacing=1)
    s.text(713, 780, "Reflections are cached; no live full-screen blur.", 10, SECONDARY)

    s.save("07-settings.png")


def create_wallpapers() -> None:
    s = Screen("wp_13_frozen_glacier.jpg", "Wallpaper studio", "WALLPAPERS")
    s.label(132, 151, "ATMOSPHERE STUDIO")
    s.title(132, 194, "Set the mood.", 31)
    s.text(132, 221, "Your world is the wallpaper. Let the glass do the framing.", 12, SECONDARY)
    s.panel(132, 253, 920, 500, radius=34)
    s.add_image(WALLPAPER_DIR / "wp_13_frozen_glacier.jpg", 146, 267, 892, 472, 28)
    s.draw("rgba(5,14,25,0.15)", "none", 0, "roundrectangle 146,267 1038,739 28,28")
    s.pill(172, 291, 154, "CURRENT WALLPAPER", tint="rgba(7,18,29,0.58)", edge="rgba(236,250,255,0.35)", color=WHITE, size=9)
    s.text(172, 710, "FROSTED BLUE  /  HIGH CONTRAST", 9, WHITE, bold=True, letter_spacing=1)
    s.panel(1075, 253, 475, 500, radius=32)
    s.label(1110, 298, "GLASS FINISH")
    s.title(1110, 338, "A clear view,", 21)
    s.title(1110, 365, "with a little glow.", 21)
    s.text(1110, 402, "Wallpaper visibility", 10, SECONDARY)
    s.line(1110, 432, 1512, 432, "rgba(255,255,255,0.14)", 3)
    s.line(1110, 432, 1364, 432, CYAN, 4)
    s.circle(1364, 432, 9, CYAN, "rgba(255,255,255,0.8)", 2)
    s.text(1471, 410, "63%", 10, CYAN, bold=True)
    s.text(1110, 474, "BACKDROP SOFTENING", 10, SECONDARY, bold=True, letter_spacing=1)
    s.pill(1110, 495, 114, "CRYSTAL CLEAR", tint="rgba(135,224,239,0.15)", edge="rgba(178,240,250,0.35)", color=CYAN, size=8)
    s.pill(1235, 495, 88, "SOFT", tint="rgba(211,236,247,0.08)", size=9)
    s.pill(1332, 495, 88, "DREAMY", tint="rgba(211,236,247,0.08)", size=9)
    s.text(1110, 565, "FAVOURITES", 10, WHITE, bold=True, letter_spacing=1)
    for i, wall in enumerate(["wp_04_crystal_river.jpg", "wp_02_cherry_blossom.jpg", "wp_14_cozy_village.jpg", "wp_17_night_clouds.jpg"]):
        s.add_image(WALLPAPER_DIR / wall, 1110 + i * 100, 583, 88, 72, 15)
    s.line(1110, 684, 1512, 684)
    s.button(1110, 706, 192, "APPLY WALLPAPER", fill=AQUA, height=38, size=10)
    s.text(1326, 730, "RESTORE DEFAULT", 9, SECONDARY, bold=True, letter_spacing=1)
    s.label(132, 797, "AERIX WALLPAPER COLLECTION")
    for i, (wall, name) in enumerate([("wp_02_cherry_blossom.jpg", "Cherry hush"), ("wp_07_ocean_arch.jpg", "Tidal glass"), ("wp_14_cozy_village.jpg", "Cedar glow"), ("wp_20_sakura_sunbeams.jpg", "Sakura light")]):
        x = 350 + i * 302
        s.add_image(WALLPAPER_DIR / wall, x, 768, 270, 92, 17)
        s.text(x + 10, 850, name, 10, WHITE, bold=True)

    s.save("08-wallpapers.png")


def create_account() -> None:
    s = Screen("wp_20_sakura_sunbeams.jpg", "Account & skin studio", "HOME")
    s.label(132, 151, "IDENTITY STUDIO")
    s.title(132, 194, "Your look. Your legend.", 31)
    s.text(132, 221, "Accounts, character style, and the small details that make the game yours.", 12, SECONDARY)
    s.panel(132, 253, 420, 571, radius=32)
    s.label(169, 298, "LINKED ACCOUNT", AQUA)
    avatar = RESOURCE_DIR / "img_avatar_entitybrian.png"
    s.add_image(avatar, 215, 335, 250, 250, 125)
    s.circle(434, 546, 12, GREEN, "rgba(233,255,246,0.8)", 2)
    s.text(169, 625, "EntityBrian69", 22, WHITE, bold=True)
    s.text(169, 653, "Microsoft account  ·  Connected", 10, SECONDARY)
    s.pill(169, 679, 108, "VERIFIED", tint="rgba(103,224,177,0.13)", edge="rgba(141,242,206,0.38)", color=GREEN, size=9)
    s.line(169, 729, 515, 729)
    s.text(169, 763, "SWITCH ACCOUNT", 9, AQUA, bold=True, letter_spacing=1)
    s.text(389, 763, "MANAGE  →", 9, SECONDARY, bold=True, letter_spacing=1)
    s.panel(577, 253, 973, 571, radius=32)
    s.add_image(WALLPAPER_DIR / "wp_07_ocean_arch.jpg", 591, 267, 945, 300, 25)
    s.draw("rgba(7,17,29,0.26)", "none", 0, "roundrectangle 591,267 1536,567 25,25")
    s.label(628, 312, "SKIN ATELIER")
    s.title(628, 356, "A new layer of you.", 28)
    s.text(628, 387, "Preview a skin before it joins your next adventure.", 11, SECONDARY)
    # Stylized, pixel-inspired character preview built from crisp shapes.
    s.panel(1190, 292, 264, 236, radius=24, tint="rgba(192,226,243,0.10)", edge="rgba(239,250,255,0.22)")
    s.draw("#B98261", "#F0C3A1", 2, "rectangle 1281,327 1365,411")
    s.draw("#382D46", "none", 0, "rectangle 1270,309 1377,342")
    s.draw("#3A7F79", "#A8ECE2", 2, "rectangle 1268,411 1378,499")
    s.draw("#34445C", "none", 0, "rectangle 1272,498 1317,527")
    s.draw("#34445C", "none", 0, "rectangle 1330,498 1375,527")
    s.text(628, 622, "CURRENT SKIN", 9, MUTED, bold=True, letter_spacing=1)
    s.text(628, 649, "Wanderer in Moss", 15, WHITE, bold=True)
    s.text(628, 676, "Classic model  ·  64 × 64", 10, SECONDARY)
    s.button(628, 720, 177, "CHANGE SKIN", fill=AQUA, height=40, size=10)
    s.text(832, 746, "OPEN WARDROBE  ↗", 9, SECONDARY, bold=True, letter_spacing=1)
    s.pill(1322, 720, 158, "PREVIEW ACTIVE", tint="rgba(110,229,192,0.14)", edge="rgba(144,245,214,0.38)", color=GREEN, size=9)

    s.save("09-account-skin.png")


def create_instance_control() -> None:
    s = Screen("wp_03_sunset_river.jpg", "Instance control center", "LIBRARY")
    s.label(132, 151, "LIBRARY  /  VALLEY OF ECHOES")
    s.title(132, 194, "Valley of Echoes", 31)
    s.text(132, 221, "One profile. Every file, mod, setting, and launch detail in one calm place.", 12, SECONDARY)
    s.panel(132, 253, 1418, 219, radius=32)
    s.add_image(WALLPAPER_DIR / "wp_03_sunset_river.jpg", 146, 267, 1390, 191, 25)
    s.draw("rgba(5,15,26,0.58)", "none", 0, "roundrectangle 146,267 1536,458 25,25")
    s.label(183, 310, "FABRIC  1.20.1  /  PROFILE ACTIVE", AQUA)
    s.title(183, 357, "A place to call home.", 29)
    s.text(183, 389, "Last played today  ·  18 mods  ·  2.4 GB", 11, SECONDARY)
    s.button(1275, 322, 210, "▶  LAUNCH PROFILE", fill=AQUA, height=48, size=11)
    s.text(1285, 399, "PROFILE SETTINGS  ⚙", 9, WHITE, bold=True, letter_spacing=1)
    lower = [
        (132, 500, 442, "MODS & CONTENT", "18 enabled", "Manage, update, or inspect dependencies", "M", CYAN),
        (597, 500, 442, "FILES & WORLDS", "6 worlds", "Open saves, exports, screenshots, and logs", "F", VIOLET),
        (1062, 500, 488, "RUNTIME & PERFORMANCE", "LTW  ·  1.17+", "Tune memory, renderer, and compatibility", "R", AQUA),
    ]
    for x, y, width, title, count, sub, icon, accent in lower:
        s.panel(x, y, width, 181, radius=27, tint="rgba(195,228,244,0.10)")
        s.circle(x + 46, y + 48, 20, "rgba(153,220,241,0.14)", "rgba(232,249,255,0.24)")
        s.text(x + 40, y + 54, icon, 15, accent, bold=True)
        s.text(x + 82, y + 44, title, 10, WHITE, bold=True, letter_spacing=1)
        s.text(x + 82, y + 71, count, 12, accent, bold=True)
        s.text(x + 28, y + 118, sub, 10, SECONDARY)
        s.text(x + width - 54, y + 156, "OPEN  ›", 9, AQUA, bold=True, letter_spacing=1)
    s.panel(132, 710, 1418, 114, radius=27, tint="rgba(188,222,240,0.08)")
    s.label(166, 750, "RECENT ACTIVITY")
    s.text(166, 781, "Updated Sodium  ·  Saved new world  ·  Crash report ready", 11, SECONDARY)
    s.pill(1236, 744, 136, "VIEW LOGS  ↗", tint="rgba(206,231,245,0.10)", color=WHITE, size=9)
    s.pill(1384, 744, 130, "EXPORT PROFILE", tint="rgba(185,162,255,0.16)", edge="rgba(220,204,255,0.35)", color=VIOLET, size=9)

    s.save("10-instance-overview.png")


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    create_home()
    create_library()
    create_new_instance()
    create_discover()
    create_pack_detail()
    create_multiplayer()
    create_settings()
    create_wallpapers()
    create_account()
    create_instance_control()
    print(f"Rendered 10 mockups to {OUT}")


if __name__ == "__main__":
    main()
