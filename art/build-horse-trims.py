#!/usr/bin/env python3
"""Writes the horse armour trims into the module's pack: our own design, made from the game's files.

  python3 -I art/build-horse-trims.py ~/.minecraft/versions/26.3/26.3.jar

Every metal plate of the horse armour (iron's texture tells plate from saddle cloth) gets a shaded trim along its
edges, light on top and dark below as the game's own trims are shaded, in a style of its own per pattern (frames,
ribs, waves, zigzags, dots); larger plates also carry the pattern's lines, and the chest and rump plates the
pattern's own chestplate motif from the game. Written in the eight trim greys, each with a palette .mcmeta, so every
trim material tints it. Looked at in the real client: e2e ElytraScreenshotsTest.
"""
import io, sys, zipfile
from collections import deque
from PIL import Image

KEY = [224, 192, 160, 128, 96, 64, 32, 0]
from pathlib import Path
import json
game = zipfile.ZipFile(sys.argv[1])
OUT = (Path(__file__).resolve().parent.parent
       / "elytra-module/src/main/resources/de/raindancer/modules/elytra/pack/assets/minecraft/textures/trims/entity/horse_body")
OUT.mkdir(parents=True, exist_ok=True)
armour = Image.open(io.BytesIO(game.read('assets/minecraft/textures/entity/equipment/horse_body/iron.png'))).convert('RGBA')
lum = lambda p: 0.3 * p[0] + 0.59 * p[1] + 0.11 * p[2]
plate = {(x, y) for y in range(64) for x in range(64)
         if armour.getpixel((x, y))[3] and lum(armour.getpixel((x, y))) >= 100}

# the UV faces of the model's boxes (body 10x10x22 at 0,32): a plate never runs across a face edge
def face_of(x, y):
    if y >= 54:
        return 'L' if x < 22 else 'F' if x < 32 else 'R' if x < 54 else 'B'
    if 32 <= y < 54 and 22 <= x < 32:
        return 'T'
    return f'h{x // 8}{y // 8}'  # head, neck, legs: small boxes, an 8x8 cell is close enough

def plates():
    seen, found = set(), []
    for start in sorted(plate):
        if start in seen:
            continue
        q, comp = deque([start]), []
        seen.add(start)
        while q:
            x, y = q.popleft(); comp.append((x, y))
            for d in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                n = (x + d[0], y + d[1])
                if n in plate and n not in seen and face_of(*n) == face_of(x, y):
                    seen.add(n); q.append(n)
        if len(comp) >= 6:
            found.append(comp)
    return found

PLATES = plates()

def shade(level):
    v = KEY[max(0, min(7, level))]
    return (v, v, v, 255)

def edges(comp):
    s = set(comp)
    top, bottom, left, right = set(), set(), set(), set()
    for x, y in comp:
        if (x, y - 1) not in s: top.add((x, y))
        if (x, y + 1) not in s: bottom.add((x, y))
        if (x - 1, y) not in s: left.add((x, y))
        if (x + 1, y) not in s: right.add((x, y))
    return top, bottom, left, right

def put(c, xy, level):
    if xy in plate:
        c.putpixel(xy, shade(level))

def frame(c, comp, inset=0, dotted=False, sides='tblr'):
    s = set(comp)
    for _ in range(inset):
        t, b, l, r = edges(list(s)); s -= t | b | l | r
    if not s:
        return
    t, b, l, r = edges(list(s))
    parts = [('t', t, 1), ('l', l, 2), ('r', r, 4), ('b', b, 5)]
    for side, px, lv in parts:
        if side in sides:
            for x, y in px:
                if not dotted or (x + y) % 2 == 0:
                    put(c, (x, y), lv)

def lines(c, comp, step, vertical=True, offset=0, half=False):
    xs = [p[0] for p in comp]; ys = [p[1] for p in comp]
    y_mid = (min(ys) + max(ys)) / 2
    for x, y in comp:
        k = x if vertical else y
        if (k - offset) % step == 0 and (not half or y >= y_mid):
            put(c, (x, y), 3 if vertical else 2)

def waves(c, comp, level=3):
    ys = [p[1] for p in comp]
    for x, y in comp:
        if y == max(ys) - 1 - (1 if x % 4 in (1, 2) else 0):
            put(c, (x, y), level)

def zigzag(c, comp):
    ys = [p[1] for p in comp]; top = min(ys)
    for x, y in comp:
        if (y - top) == (x % 4 if x % 8 < 4 else 3 - x % 4):
            put(c, (x, y), 2)

def diagonals(c, comp, step=4):
    for x, y in comp:
        if (x + y) % step == 0:
            put(c, (x, y), 3)

def centre_dot(c, comp, size=1):
    xs = [p[0] for p in comp]; ys = [p[1] for p in comp]
    cx, cy = (min(xs) + max(xs)) // 2, (min(ys) + max(ys)) // 2
    for dx in range(-size, size + 1):
        for dy in range(-size, size + 1):
            if abs(dx) + abs(dy) <= size:
                put(c, (cx + dx, cy + dy), 1 if dx == dy == 0 else 3)

def motif(c, game_img, box, at, keep_plate=True):
    x0, y0, x1, y1 = box
    ax, ay = at
    for y in range(y0, y1):
        for x in range(x0, x1):
            p = game_img.getpixel((x, y))
            q = (ax + x - x0, ay + y - y0)
            if p[3] and (q in plate or not keep_plate):
                c.putpixel(q, p)

STYLES = {
    'coast':     lambda c, p: (frame(c, p, sides='b'), waves(c, p, 2)),
    'dune':      lambda c, p: (frame(c, p, sides='b'), frame(c, p, inset=1, dotted=True, sides='b')),
    'wild':      lambda c, p: (frame(c, p, sides='tb'), diagonals(c, p, 7)),
    'ward':      lambda c, p: (frame(c, p), frame(c, p, inset=1, dotted=True)),
    'eye':       lambda c, p: (frame(c, p, sides='tb'), centre_dot(c, p, 1)),
    'sentry':    lambda c, p: (frame(c, p, dotted=True), centre_dot(c, p, 0)),
    'vex':       lambda c, p: (frame(c, p, sides='b'), zigzag(c, p)),
    'tide':      lambda c, p: (waves(c, p, 2), frame(c, p, sides='t')),
    'snout':     lambda c, p: (frame(c, p, sides='b'), frame(c, p, inset=1, sides='b'), centre_dot(c, p, 0)),
    'rib':       lambda c, p: (frame(c, p, sides='tb'), lines(c, p, 3)),
    'spire':     lambda c, p: (frame(c, p, sides='b'), lines(c, p, 3, half=True)),
    'wayfinder': lambda c, p: (frame(c, p), diagonals(c, p, 8)),
    'shaper':    lambda c, p: (frame(c, p, sides='tblr'), lines(c, p, 4, vertical=False, offset=1)),
    'silence':   lambda c, p: (frame(c, p), frame(c, p, inset=1), centre_dot(c, p, 2)),
    'raiser':    lambda c, p: (frame(c, p, sides='tb'), lines(c, p, 4, half=True)),
    'host':      lambda c, p: (frame(c, p, dotted=True), centre_dot(c, p, 1)),
    'flow':      lambda c, p: (frame(c, p, sides='lr'), waves(c, p, 3), frame(c, p, sides='t', dotted=True)),
    'bolt':      lambda c, p: (frame(c, p, sides='tb'), zigzag(c, p)),
}

BIG = 24  # interior lines, dots and waves only where a plate is large enough to carry them

def gentle(style):
    def run(c, p):
        if len(p) >= BIG:
            style(c, p)
        else:
            frame(c, p, sides='tb')
    return run

for name, style in sorted(STYLES.items()):
    style = gentle(style)
    hum = Image.open(io.BytesIO(game.read(f'assets/minecraft/textures/trims/entity/humanoid/{name}.png'))).convert('RGBA')
    c = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    for p in PLATES:
        style(c, p)
    motif(c, hum, (20, 20, 28, 32), (23, 52))   # the pattern's own chest motif on the chest plate
    motif(c, hum, (32, 20, 40, 32), (55, 51))   # and its back motif on the rump
    c.save(OUT / f'{name}.png')
    (OUT / f'{name}.png.mcmeta').write_text(json.dumps({"palette": {"base_palette": "trim_base"}}, indent=2) + "\n")
print(len(STYLES), 'patterns,', len(PLATES), 'plates ->', OUT)
