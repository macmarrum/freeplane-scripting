# Copyright (C) 2026  macmarrum (at) outlook (dot) ie
# SPDX-License-Identifier: GPL-3.0-or-later
from __future__ import annotations
from dataclasses import dataclass
from typing import Any

TAttributeElem = tuple[str, Any] | tuple[str, Any, Any] | tuple[str, Any, Any, Any]


@dataclass
class NodeData:
    key: str
    core: Any | None = None
    details: str | None = None
    note: str | None = None
    attributes: list[TAttributeElem] | dict[str, Any] | None = None
    icons: list[str] | None = None
    style: StyleData | None = None
    children: list[NodeData] | None = None

    def as_dict(self) -> dict:
        dct = {}
        if self.core is not None:
            dct['@core'] = self.core
        if self.details is not None:
            dct['@details'] = self.details
        if self.note is not None:
            dct['@note'] = self.note
        if self.attributes is not None:
            dct['@attributes'] = self.attributes
        if self.icons is not None:
            dct['@icons'] = self.icons
        if self.style is not None:
            dct['@style'] = self.style.as_dict()
        if self.children is not None:
            for child in self.children:
                dct.update(child.as_dict())
        if not dct:
            dct = None
        return {self.key: dct}


@dataclass
class StyleData:
    background_color_code: str | None = None
    text_color_code: str | None = None
    css: str | None = None
    max_node_width: float | None = None
    min_node_width: float | None = None
    numbering: bool | None = None
    name: str | None = None

    def as_dict(self):
        dct = {}
        if self.background_color_code is not None:
            dct['backgroundColorCode'] = self.background_color_code
        if self.text_color_code is not None:
            dct['textColorCode'] = self.text_color_code
        if self.css is not None:
            dct['css'] = self.css
        if self.max_node_width is not None:
            dct['maxNodeWidth'] = self.max_node_width
        if self.min_node_width is not None:
            dct['minNodeWidth'] = self.min_node_width
        if self.numbering is not None:
            dct['numbering'] = self.numbering
        if self.name is not None:
            dct['name'] = self.name
        return dct
