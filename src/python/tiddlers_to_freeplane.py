#!/usr/bin/python3
# Copyright (C) 2025  macmarrum (at) outlook (dot) ie
# SPDX-License-Identifier: GPL-3.0-or-later
import json
import sys
import os
import re
from pathlib import Path

me = Path(__file__)

# This regex captures [[tags with spaces]] and individual tags
rx_split_tags = re.compile(r'\[\[(.*?)]]|(\S+)')


def parse_tags(tag_string):
    tags = []
    for match in rx_split_tags.finditer(tag_string):
        if match.group(1):  # tag inside [[...]]
            tags.append(match.group(1))
        elif match.group(2):  # single-word tag
            tags.append(match.group(2))
    return tags


def convert_tiddlers_to_freeplane(input_file, output_file):
    with open(input_file, "r", encoding="utf-8") as f:
        tiddlers = json.load(f)

    freeplane_dict = {
        "0": {
            "@core": os.path.basename(input_file),
        }
    }

    for idx, item in enumerate(tiddlers, start=1):
        tags = parse_tags(item.get("tags", ""))
        freeplane_dict["0"][str(idx)] = {
            "@core": item.get("title", ""),
            "@note": item.get("text", ""),
            "@tags": tags
        }

    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(freeplane_dict, f, indent=2, ensure_ascii=False)

    print(f"✅ Conversion completed! Output saved to: {output_file}")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(f"Usage: python {me.name} <tiddlers.json>")
    else:
        convert_tiddlers_to_freeplane(sys.argv[1], sys.argv[2] if len(sys.argv) > 2 else "freeplane.json")
