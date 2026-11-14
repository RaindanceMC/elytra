#!/usr/bin/env python3
"""Horse armour trims taken from the player trims: every face of the horse armour wears the matching piece of the
pattern's own player trim texture, so a horse looks like it wears the same trim as its rider.

  python3 -I art/build-horse-trims.py ~/.minecraft/versions/26.3/26.3.jar

Chest and rump take the chestplate's front and back with their side columns, the flanks the chestplate all the way
round, the head and muzzle the helmet, the neck the arm and the legs the leggings, all four faces at once. Where the
armour shows only part of a face, the window of the piece that lands the most trim on armour is taken; nothing is
scaled, so the pixels are the game's own. Only drawn where the armour is.
Looked at in the real client (art/horse-trims-ingame.png).
"""
import io, sys, zipfile, json
from pathlib import Path
from PIL import Image

HERE = Path(__file__).resolve().parent
OUT = HERE.parent / "elytra-module/src/main/resources/de/raindancer/modules/elytra/pack/assets/minecraft/textures/trims/entity/horse_body"
PATTERNS = ['bolt', 'coast', 'dune', 'eye', 'flow', 'host', 'raiser', 'rib', 'sentry', 'shaper',
            'silence', 'snout', 'spire', 'tide', 'vex', 'ward', 'wayfinder', 'wild']

game = zipfile.ZipFile(sys.argv[1])
tex = lambda p: Image.open(io.BytesIO(game.read('assets/minecraft/textures/' + p))).convert('RGBA')
armour = tex('entity/equipment/horse_body/iron.png')

# (horse face x, y, w, h), (player texture, x, y, w, h). The player textures keep each body part's faces side by side
# (right side, front, left side, back), as the horse's do, so long horse faces take a run across several of them.
H, L = 'humanoid', 'humanoid_leggings'
FACES = [
    ((22, 54, 10, 10), (H, 19, 20, 10, 12)),   # chest       <- chestplate front with a column of each side
    ((54, 54, 10, 10), (H, 31, 20, 10, 12)),   # rump        <- chestplate back with a column of the side
    ((0, 54, 22, 10), (H, 18, 20, 22, 12)),    # left flank  <- the chestplate all the way round
    ((32, 54, 22, 10), (H, 18, 20, 22, 12)),   # right flank
    ((22, 32, 10, 22), (H, 32, 20, 8, 12)),    # saddle top  <- chestplate back, repeated along it
    ((0, 42, 22, 12), (H, 40, 20, 16, 12)),    # neck, all four faces <- the arm all the way round
    ((7, 35, 4, 7), (H, 44, 16, 4, 4)),        # neck top    <- arm top
    ((7, 13, 6, 7), (H, 8, 0, 8, 8)),          # head top    <- helmet top
    ((0, 20, 7, 5), (H, 0, 8, 8, 8)),          # head right  <- helmet side
    ((7, 20, 6, 5), (H, 8, 8, 8, 8)),          # head front  <- helmet front
    ((13, 20, 7, 5), (H, 16, 8, 8, 8)),        # head left   <- helmet side
    ((20, 20, 6, 5), (H, 24, 8, 8, 8)),        # head back   <- helmet back
    ((5, 25, 4, 5), (H, 8, 0, 8, 8)),          # muzzle top  <- helmet top
    ((0, 30, 5, 5), (H, 0, 8, 8, 8)),          # muzzle right
    ((5, 30, 4, 5), (H, 8, 8, 8, 8)),          # muzzle front
    ((9, 30, 5, 5), (H, 16, 8, 8, 8)),         # muzzle left
    ((52, 21, 4, 4), (L, 4, 16, 4, 4)),        # leg tops    <- leggings top
    ((48, 25, 16, 11), (L, 0, 20, 16, 12)),    # legs, all four faces <- the leggings all the way round
]

def covered(q):
    return armour.getpixel(q)[3] > 0

def best_offsets(src, face, piece):
    """Where to put the piece on the face: centred where it fits, else the window that lands most trim on armour."""
    dx, dy, dw, dh = face
    sx, sy, sw, sh = piece
    xs = range(dw - sw, 1) if sw > dw else [(dw - sw) // 2]
    ys = range(dh - sh, 1) if sh > dh else [(dh - sh) // 2]
    best, best_count = None, -1
    centre = ((dw - sw) // 2, (dh - sh) // 2)
    for ox in xs:
        for oy in ys:
            n = sum(1 for y in range(sh) for x in range(sw)
                    if 0 <= ox + x < dw and 0 <= oy + y < dh and src.getpixel((sx + x, sy + y))[3]
                    and covered((dx + ox + x, dy + oy + y)))
            key = (n, -abs(ox - centre[0]) - abs(oy - centre[1]))
            if best is None or key > best_count:
                best, best_count = (ox, oy), key
    return best

def place(c, src, face, piece):
    dx, dy, dw, dh = face
    sx, sy, sw, sh = piece
    ox, oy = best_offsets(src, face, piece)
    rows = range(oy, dh, sh) if sh < dh and dh >= 2 * sh else [oy]   # repeat along a face twice as long
    for oy2 in rows:
        for y in range(sh):
            for x in range(sw):
                tx, ty = ox + x, oy2 + y
                if 0 <= tx < dw and 0 <= ty < dh:
                    p = src.getpixel((sx + x, sy + y))
                    q = (dx + tx, dy + ty)
                    if p[3] and covered(q):
                        c.putpixel(q, p)

for name in PATTERNS:
    pieces = {H: tex(f'trims/entity/humanoid/{name}.png'), L: tex(f'trims/entity/humanoid_leggings/{name}.png')}
    c = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    for face, (kind, *piece) in FACES:
        place(c, pieces[kind], face, piece)
    c.save(OUT / f'{name}.png')
    (OUT / f'{name}.png.mcmeta').write_text(json.dumps({"palette": {"base_palette": "trim_base"}}, indent=2) + "\n")
print(len(PATTERNS), 'patterns ->', OUT)
