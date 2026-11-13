#!/usr/bin/env python3
"""Writes the module's resource pack files from their two sources.

  python3 -I art/build-pack.py <BareBonesXElytraTrims.zip> <elytratrims-fabric.jar> <minecraft-26.3-client.jar>

- The wing trim textures come from the "Elytra Trims" mod by KikuGie (LGPL-3.0-or-later,
  https://modrinth.com/mod/elytra-trims, source https://codeberg.org/KikuGie/elytra-trims): one shaded pattern per
  trim, in the game's eight trim greys, so 26.3's palette .mcmeta tints them with depth. Unchanged; its licence and
  the GPL it builds on go into the pack beside them (art/licenses/).
- The item overlay comes from "Bare Bones X Elytra Trims" by rando3214 (BSD-2-Clause,
  https://modrinth.com/resourcepack/bare-bones-x-elytra-trims); its flat wing textures were the ones used up to 0.2.0.
- The chestplate looks reuse the game's own armour textures: each look is the vanilla chestplate's humanoid layers
  (with its trim overrides) plus the elytra's wings, so nothing of Mojang's is copied, only referred to.
"""
import json
import sys
import zipfile
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "elytra-module/src/main/resources/de/raindancer/modules/elytra/pack"
NS = "rainselytra"
LOOKS = ["leather", "chainmail", "copper", "iron", "gold", "diamond", "netherite"]
WINGS = "assets/minecraft/textures/trims/entity/wings/"
OVERLAY = "assets/minecraft/textures/trims/items/wings_trim.png"


def write(path, data):
    target = OUT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    if isinstance(data, (dict, list)):
        data = json.dumps(data, indent=2) + "\n"
    target.write_bytes(data.encode() if isinstance(data, str) else data)


def main(bare_bones, elytra_trims, client_jar):
    with zipfile.ZipFile(bare_bones) as pack, zipfile.ZipFile(elytra_trims) as kiku, zipfile.ZipFile(client_jar) as game:
        patterns = sorted(Path(n).stem for n in kiku.namelist() if n.startswith(WINGS) and n.endswith(".png"))
        vanilla = sorted(Path(n).stem for n in game.namelist()
                         if n.startswith("assets/minecraft/textures/trims/entity/humanoid/") and n.endswith(".png"))
        if patterns != vanilla:
            sys.exit(f"the pack's wing patterns {patterns} are not the game's {vanilla}")
        for pattern in patterns:
            write(WINGS + pattern + ".png", kiku.read(WINGS + pattern + ".png"))
            write(WINGS + pattern + ".png.mcmeta", {"palette": {"base_palette": "trim_base"}})
        write(OVERLAY, pack.read(OVERLAY))

        atlas = json.loads(game.read("assets/minecraft/atlases/items.json"))
        trims = next(s for s in atlas["sources"] if s["type"].endswith("paletted_permutations")
                     and "minecraft:trims/items/chestplate_trim" in s["textures"])
        write("assets/minecraft/atlases/items.json", {"sources": [{
            "type": "minecraft:paletted_permutations",
            "palette_key": trims["palette_key"],
            "permutations": trims["permutations"],
            "textures": ["minecraft:trims/items/wings_trim"]}]})

        chest = json.loads(game.read("assets/minecraft/items/iron_chestplate.json"))["model"]
        trim_materials = [case["when"] for case in chest["cases"]]
        for state in ("elytra", "elytra_broken"):
            for when in trim_materials:
                material = when.split(":")[1]
                write(f"assets/{NS}/models/item/{state}_{material}_trim.json", {
                    "parent": "minecraft:item/generated",
                    "textures": {"layer0": f"minecraft:item/{state}",
                                 "layer1": f"minecraft:trims/items/wings_trim_{material}"}})

        def by_trim(state):
            return {"type": "minecraft:select", "property": "minecraft:trim_material",
                    "cases": [{"when": when, "model": {"type": "minecraft:model",
                               "model": f"{NS}:item/{state}_{when.split(':')[1]}_trim"}} for when in trim_materials],
                    "fallback": {"type": "minecraft:model", "model": f"minecraft:item/{state}"}}

        vanilla_elytra = json.loads(game.read("assets/minecraft/items/elytra.json"))["model"]
        assert vanilla_elytra["property"] == "minecraft:broken", vanilla_elytra
        write("assets/minecraft/items/elytra.json", {"model": {
            "type": "minecraft:condition", "property": "minecraft:broken",
            "on_false": by_trim("elytra"), "on_true": by_trim("elytra_broken")}})

        wings = json.loads(game.read("assets/minecraft/equipment/elytra.json"))["layers"]["wings"]
        for look in LOOKS:
            armour = json.loads(game.read(f"assets/minecraft/equipment/{look}.json"))
            equipment = {"layers": {"humanoid": armour["layers"]["humanoid"],
                                    "humanoid_baby": armour["layers"]["humanoid_baby"],
                                    "wings": wings}}
            if "trim_overrides" in armour:
                equipment["trim_overrides"] = armour["trim_overrides"]
            write(f"assets/{NS}/equipment/{look}_chestplate.json", equipment)

    write("pack.mcmeta", {"pack": {"description": "Rain's Elytra — trims and chestplate looks on elytra",
                                   "min_format": 97, "max_format": 97}})
    for licence in ("elytra-trims-LGPL-3.0.txt", "GPL-3.0.txt"):
        write("licenses/" + licence, (Path(__file__).resolve().parent / "licenses" / licence).read_text())
    write("CREDITS.txt", (
        "Wing trim textures (textures/trims/entity/wings/*.png): from the \"Elytra Trims\" mod by KikuGie,\n"
        "https://modrinth.com/mod/elytra-trims, source https://codeberg.org/KikuGie/elytra-trims, unchanged.\n"
        "Licensed under the GNU Lesser General Public License v3.0 or later: licenses/elytra-trims-LGPL-3.0.txt,\n"
        "with the GNU General Public License v3.0 it extends: licenses/GPL-3.0.txt.\n\n"
        "The elytra trim overlay (textures/trims/items/wings_trim.png): \"Bare Bones X Elytra Trims\" by rando3214,\n"
        "https://modrinth.com/resourcepack/bare-bones-x-elytra-trims — BSD 2-Clause License:\n\n"
        "Redistribution and use in source and binary forms, with or without modification, are permitted provided\n"
        "that the following conditions are met:\n"
        "1. Redistributions of source code must retain the above copyright notice, this list of conditions and the\n"
        "   following disclaimer.\n"
        "2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and\n"
        "   the following disclaimer in the documentation and/or other materials provided with the distribution.\n\n"
        "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY EXPRESS OR IMPLIED\n"
        "WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A\n"
        "PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY\n"
        "DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,\n"
        "PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)\n"
        "HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING\n"
        "NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE\n"
        "POSSIBILITY OF SUCH DAMAGE.\n"))
    print(f"{len(patterns)} wing patterns, {len(trim_materials)} trim materials, {len(LOOKS)} looks -> {OUT}")


if __name__ == "__main__":
    if len(sys.argv) != 4:
        sys.exit(__doc__)
    main(sys.argv[1], sys.argv[2], sys.argv[3])
