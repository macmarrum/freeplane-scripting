<map version="freeplane 1.9.13">
<!--To view this file, download free mind mapping software Freeplane from https://www.freeplane.org -->
<attribute_registry SHOW_ATTRIBUTES="hide"/>
<node TEXT="Root" LOCALIZED_STYLE_REF="AutomaticLayout.level.root" FOLDED="false" ID="ID_1090958577" NodeVisibilityConfiguration="SHOW_HIDDEN_NODES"><hook NAME="MapStyle" background="#2b2b2b">
    <conditional_styles>
        <conditional_style ACTIVE="false" STYLE_REF="?bg" LAST="false"/>
        <conditional_style ACTIVE="false" STYLE_REF="?gap" LAST="false">
            <script_condition>
                <script>!node.leaf &amp;&amp; node.children.every{ it.leaf }
// https://issues.apache.org/jira/browse/GROOVY-7207
// every() returns true for empty iterator/list</script>
            </script_condition>
        </conditional_style>
        <conditional_style ACTIVE="false" STYLE_REF="?=Table.row.accent" LAST="false">
            <script_condition>
                <script>def cs=[&apos;?=Table&apos;,&apos;?=Table.row.accent&apos;]
def cond=style.name===null &amp;&amp; !root &amp;&amp; (parent[CS.attr]==cs[0] || parent.style.name==cs[0]) &amp;&amp; parent.getChildPosition(node)==0
return CS.canApply(node, cs[1], cond)</script>
            </script_condition>
        </conditional_style>
        <conditional_style ACTIVE="false" STYLE_REF="?=Table.cell.accent" LAST="false">
            <script_condition>
                <script>def cs=[&apos;?=Table.row.accent&apos;,&apos;?=Table.cell.accent&apos;]
def cond=style.name===null &amp;&amp; !root &amp;&amp; (parent[CS.attr] in cs || parent.style.name in cs)
return CS.canApply(node, cs[1], cond)</script>
            </script_condition>
        </conditional_style>
        <conditional_style ACTIVE="false" STYLE_REF="?=Table.row" LAST="false">
            <script_condition>
                <script>def cs=[&apos;?=Table&apos;,&apos;?=Table.row&apos;]
def cond=style.name===null &amp;&amp; !root &amp;&amp; (parent[CS.attr]==cs[0] || parent.style.name==cs[0]) &amp;&amp; parent.getChildPosition(node)&gt;0
return CS.canApply(node, cs[1], cond)</script>
            </script_condition>
        </conditional_style>
        <conditional_style ACTIVE="false" STYLE_REF="?=Table.cell" LAST="false">
            <script_condition>
                <script>def cs=[&apos;?=Table.row&apos;,&apos;?=Table.cell&apos;]
def cond=style.name===null &amp;&amp; !root &amp;&amp; (parent[CS.attr] in cs || parent.style.name in cs)
return CS.canApply(node, cs[1], cond)</script>
            </script_condition>
        </conditional_style>
    </conditional_styles>
    <properties show_icon_for_attributes="false" edgeColorConfiguration="#a9b7c6ff,#4cc46bff,#e95065ff,#d7b84dff,#c54499ff,#41b1d1ff,#d76b4fff,#4cc46bff,#e95065ff,#d7b84dff,#c54499ff,#41b1d1ff,#d76b4fff" show_note_icons="true" fit_to_viewport="false"/>

<map_styles>
<stylenode LOCALIZED_TEXT="styles.root_node" ID="ID_680156716" STYLE="oval" UNIFORM_SHAPE="true" VGAP_QUANTITY="24 pt">
<font SIZE="24"/>
<stylenode LOCALIZED_TEXT="styles.predefined" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="default" ID="ID_602083445" ICON_SIZE="16 px" FORMAT_AS_HYPERLINK="false" COLOR="#a9b7c6" BACKGROUND_COLOR="#333333" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="6 pt" SHAPE_VERTICAL_MARGIN="4 pt" NUMBERED="false" FORMAT="STANDARD_FORMAT" TEXT_ALIGN="DEFAULT" BORDER_WIDTH_LIKE_EDGE="false" BORDER_WIDTH="1 px" BORDER_COLOR_LIKE_EDGE="true" BORDER_COLOR="#808080" BORDER_DASH_LIKE_EDGE="true" BORDER_DASH="SOLID" VGAP_QUANTITY="3 pt" MAX_WIDTH="10 cm" MIN_WIDTH="0 cm">
<arrowlink SHAPE="CUBIC_CURVE" COLOR="#a89984" WIDTH="2" TRANSPARENCY="255" DASH="" FONT_SIZE="9" FONT_FAMILY="SansSerif" DESTINATION="ID_602083445" STARTINCLINATION="102.75 pt;0 pt;" ENDINCLINATION="102.75 pt;3 pt;" STARTARROW="NONE" ENDARROW="DEFAULT"/>
<font NAME="Lato" SIZE="12" BOLD="false" STRIKETHROUGH="false" ITALIC="false"/>
<edge STYLE="bezier" COLOR="#808080" WIDTH="1" DASH="SOLID"/>
<richcontent CONTENT-TYPE="plain/auto" TYPE="DETAILS"/>
<richcontent TYPE="NOTE" CONTENT-TYPE="plain/auto"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" LOCALIZED_STYLE_REF="default" LAST="false">
        <node_matches_regexp SEARCH_PATTERN=".+" MATCH_CASE="false" ITEM="filter_details"/>
    </conditional_style>
</hook>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.details" ID="ID_599277530" COLOR="#c62e2e" BACKGROUND_COLOR="#323232" BACKGROUND_ALPHA="0">
<icon BUILTIN="links/file/css"/>
<font ITALIC="true"/>
<hook NAME="NodeCss">body { color: #a9b7c6; background-color: #2B2B2B; }

h1, h2, h3, h4, h5, h6 {
  color: #b3b8bc;
  background: #3d3d3d;
  border-top: 1px solid gray;
  border-bottom: 1px solid gray;
  font-weight: normal;
  padding-left: 2px;
  padding-right: 2px;
}
blockquote {
  color: #bdc2c7;
  background: #455548;
  border-left: 5px solid #408040;
  font-style: italic;
  padding-left: 5px;
  margin-left: 2px;
  padding-right: 5px;
}
pre {
  color: #b2c1d1;
  background: #435371;
  border-left: 5px solid #4488cc;
  padding: 5px;
  margin-left: 2px;
}
code {
  color: #b2c1d1;
  background: #435371;
  font-family: JetBrains Mono; /*, Courier New, Monospaced;*/
  font-size: 0.9em;
}
table {
  border-spacing: 0px;
  border-right: 1px solid gray;
  border-bottom: 1px solid gray;
}
th, td {
  border-left: 1px solid gray;
  border-top: 1px solid gray;
}
th {
  color: #b3b8bc;
  background: #3d3d3d;
  font-weight: 600;
}
ul {
  margin-left-ltr: 10px;
  margin-right-rtl: 10px;
  /*list-style-image: url(&quot;https://resource-centre.net/wp-content/uploads/2015/11/custom-bullet.png&quot;);*/
  /*list-style-type: square;*/
}
ol {
  margin-left-ltr: 15px;
  margin-right-rtl: 15px;
}</hook>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.attributes" ID="ID_1169762759">
<font SIZE="9"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.note" ID="ID_1824315873"/>
<stylenode LOCALIZED_TEXT="defaultstyle.floating" ID="ID_273404251">
<edge STYLE="hide_edge"/>
<cloud COLOR="#7f7f7f" SHAPE="ROUND_RECT"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.selection" ID="ID_313273079" COLOR="#ffffff" BACKGROUND_COLOR="#009999">
<edge COLOR="#ff6600"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.user-defined" POSITION="right" STYLE="bubble">
<stylenode TEXT="?bg" ID="ID_1783059692" BACKGROUND_COLOR="#333333" BORDER_COLOR="#808080" BORDER_DASH="SOLID"/>
<stylenode TEXT="?gap" ID="ID_21091341" VGAP_QUANTITY="3 pt"/>
<stylenode LOCALIZED_TEXT="styles.important" ID="ID_749235638" BORDER_WIDTH="3 px" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#cc241d">
<icon BUILTIN="yes"/>
<arrowlink COLOR="#cc241d" TRANSPARENCY="255" DESTINATION="ID_749235638"/>
<font SIZE="12" ITALIC="false"/>
</stylenode>
<stylenode TEXT="^gtd" ID="ID_193327768" STYLE="rectangle" SHAPE_HORIZONTAL_MARGIN="6 pt" SHAPE_VERTICAL_MARGIN="4 pt" BORDER_WIDTH="3 px"/>
<stylenode TEXT="!WaitingFor" ID="ID_686710494" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#46bddf" COLOR="#46bddf">
<icon BUILTIN="emoji-23F3"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^gtd" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="!WaitingFor.Closed" ID="ID_1769848801" BACKGROUND_COLOR="#435357" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#3693ad">
<icon BUILTIN="emoji-2714"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^gtd" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="!NextAction" ID="ID_1127156037" COLOR="#e5c453" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e5c453">
<icon BUILTIN="emoji-25FB"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^gtd" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="!NextAction.Closed" ID="ID_311404969" BACKGROUND_COLOR="#59553f" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#b39a41">
<icon BUILTIN="emoji-2714"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^gtd" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="=Enum#" ID="ID_774400710">
<icon BUILTIN="emoji-0023-20E3"/>
</stylenode>
<stylenode TEXT="=Numbering#" ID="ID_953587793" NUMBERED="true">
<icon BUILTIN="emoji-002A-20E3"/>
</stylenode>
<stylenode TEXT="^CSS">
<hook NAME="NodeCss">h1, h2, h3, h4, h5, h6 {
  color: #b3b8bc;
  background: #3d3d3d;
  border-top: 1px solid gray;
  border-bottom: 1px solid gray;
  font-weight: normal;
  padding-left: 2px;
  padding-right: 2px;
}
blockquote {
  color: #bdc2c7;
  background: #455548;
  border-left: 5px solid #408040;
  font-style: italic;
  padding-left: 5px;
  margin-left: 2px;
  padding-right: 5px;
}
pre {
  color: #b2c1d1;
  background: #435371;
  border-left: 5px solid #4488cc;
  padding: 5px;
  margin-left: 2px;
}
code {
  color: #b2c1d1;
  background: #435371;
  font-family: JetBrains Mono; /*, Courier New, Monospaced;*/
  font-size: 0.9em;
}
table {
  border-spacing: 0px;
  border-right: 1px solid gray;
  border-bottom: 1px solid gray;
}
th, td {
  border-left: 1px solid gray;
  border-top: 1px solid gray;
}
th {
  color: #b3b8bc;
  background: #3d3d3d;
  font-weight: 600;
}
ul {
  margin-left-ltr: 10px;
  margin-right-rtl: 10px;
  /*list-style-image: url(&quot;https://resource-centre.net/wp-content/uploads/2015/11/custom-bullet.png&quot;);*/
  /*list-style-type: square;*/
}
ol {
  margin-left-ltr: 15px;
  margin-right-rtl: 15px;
}</hook>
</stylenode>
<stylenode TEXT="=Markdown" FORMAT="markdownPatternFormat" MAX_WIDTH="20 cm">
<icon BUILTIN="emoji-24C2"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^CSS" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="=FreeNote" ID="ID_477157958" BACKGROUND_COLOR="#484747" STYLE="rectangle" SHAPE_HORIZONTAL_MARGIN="8 pt" SHAPE_VERTICAL_MARGIN="5 pt" FORMAT="markdownPatternFormat" BORDER_WIDTH_LIKE_EDGE="true">
<arrowlink SHAPE="EDGE_LIKE" COLOR="#59657c" TRANSPARENCY="255" DASH="7 7" FONT_SIZE="0" DESTINATION="ID_477157958" STARTARROW="NONE" ENDARROW="NONE"/>
<edge STYLE="linear" COLOR="#59657c" WIDTH="2" DASH="DASHES"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^CSS" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="=New" COLOR="#52d273" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#368b4c">
<icon BUILTIN="emoji-1F331"/>
<font SIZE="12"/>
</stylenode>
<stylenode TEXT="=Warn" COLOR="#e5c453" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#9f893a">
<icon BUILTIN="emoji-26A0"/>
<font SIZE="12"/>
</stylenode>
<stylenode TEXT="=Question" COLOR="#e57255" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#b35242">
<icon BUILTIN="emoji-2754"/>
</stylenode>
<stylenode TEXT="=Info" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#3693ad">
<icon BUILTIN="emoji-2139"/>
</stylenode>
<stylenode TEXT="=Plus" BACKGROUND_COLOR="#878787">
<icon BUILTIN="emoji-2795"/>
</stylenode>
<stylenode TEXT="^Table" BACKGROUND_COLOR="#323232" BACKGROUND_ALPHA="0" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="0 pt" SHAPE_VERTICAL_MARGIN="0 pt" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#323232" BORDER_COLOR_ALPHA="0" VGAP_QUANTITY="0 pt">
<edge STYLE="hide_edge"/>
<richcontent TYPE="NOTE" CONTENT-TYPE="xml/">
<html>
  <head>

  </head>
  <body>
    <p>
      To switch on Clone Marks, transparent for Edge Color must be off
    </p>
    <p>
      =&gt; turn on `Use edge color`
    </p>
  </body>
</html></richcontent>
</stylenode>
<stylenode TEXT="=Table">
<cloud COLOR="#152242" SHAPE="RECT"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^Table" LAST="false"/>
</hook>
<font SIZE="14" BOLD="true"/>
</stylenode>
<stylenode TEXT="=Table.row">
<cloud COLOR="#333333" SHAPE="RECT"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^Table" LAST="false"/>
</hook>
<hook NAME="NodeCss">body { padding-left: 2px; }</hook>
</stylenode>
<stylenode TEXT="=Table.row.accent">
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="=Table.row" LAST="false"/>
    <conditional_style ACTIVE="true" STYLE_REF="=Table.cell.accent" LAST="false"/>
</hook>
<cloud COLOR="#282828" SHAPE="RECT"/>
</stylenode>
<stylenode TEXT="=Table.cell">
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="^Table" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="=Table.cell.accent" COLOR="#c62e2e">
<font SIZE="14" BOLD="true"/>
<richcontent CONTENT-TYPE="xml/" TYPE="DETAILS">
<html>
  <head>

  </head>
  <body>
    <p>
      Old: #57a4ebff
    </p>
  </body>
</html></richcontent>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="=Table.cell" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="?=Table">
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="=Table" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="?=Table.row">
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="=Table.row" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="?=Table.row.accent">
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="=Table.row.accent" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="?=Table.cell">
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="=Table.cell" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="?=Table.cell.accent">
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" STYLE_REF="=Table.cell.accent" LAST="false"/>
</hook>
</stylenode>
<stylenode TEXT="^cloud.bright">
<cloud COLOR="#c7c7c7" SHAPE="ROUND_RECT"/>
</stylenode>
<stylenode TEXT="^cloud.dark">
<cloud COLOR="#152242" SHAPE="ROUND_RECT"/>
</stylenode>
<stylenode TEXT="cStorageMarkupRoot" BACKGROUND_COLOR="#003e9b" BORDER_WIDTH="4 px" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#ffab00">
<icon BUILTIN="emoji-1F3AF"/>
<font NAME="JetBrains Mono" SIZE="11"/>
</stylenode>
<stylenode TEXT="cStorageMarkupMaker" BACKGROUND_COLOR="#003e9b" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#ffab00">
<icon BUILTIN="emoji-1F335"/>
<font NAME="JetBrains Mono" SIZE="11"/>
</stylenode>
<stylenode TEXT="+max20cm" MAX_WIDTH="20 cm"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" ID="ID_1659178249" COLOR="#a9b7c6" BACKGROUND_COLOR="#282828" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="10 pt" SHAPE_VERTICAL_MARGIN="10 pt" VGAP_QUANTITY="20 pt">
<font SIZE="20"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" ID="ID_771207535" COLOR="#52d273" BACKGROUND_COLOR="#455448" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="8 pt" SHAPE_VERTICAL_MARGIN="5 pt" VGAP_QUANTITY="18 pt">
<font SIZE="18"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,2" ID="ID_814211067" COLOR="#f9556b" BACKGROUND_COLOR="#574248" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="8 pt" SHAPE_VERTICAL_MARGIN="5 pt" VGAP_QUANTITY="16 pt">
<font SIZE="16"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,3" ID="ID_352058479" COLOR="#e5c453" BACKGROUND_COLOR="#59553f" VGAP_QUANTITY="14 pt">
<font SIZE="14"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,4" ID="ID_294563152" COLOR="#d349a4" BACKGROUND_COLOR="#49434f" VGAP_QUANTITY="14 pt">
<font SIZE="14"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,5" ID="ID_119767224" COLOR="#46bddf" BACKGROUND_COLOR="#435357" VGAP_QUANTITY="14 pt">
<font SIZE="14"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,6" ID="ID_1794312820" COLOR="#e57255" BACKGROUND_COLOR="#3d3d3d" VGAP_QUANTITY="14 pt">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,7" ID="ID_759282133" COLOR="#52d273" BACKGROUND_COLOR="#3d3d3d" VGAP_QUANTITY="14 pt">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,8" ID="ID_94638384" COLOR="#f9556b" BACKGROUND_COLOR="#3d3d3d" VGAP_QUANTITY="14 pt">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,9" ID="ID_885575204" COLOR="#e5c453" BACKGROUND_COLOR="#3d3d3d" VGAP_QUANTITY="14 pt">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,10" ID="ID_703695337" COLOR="#d349a4" BACKGROUND_COLOR="#3d3d3d" VGAP_QUANTITY="14 pt">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,11" COLOR="#46bddf" BACKGROUND_COLOR="#3d3d3d" VGAP_QUANTITY="14 pt">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,12" COLOR="#e57255" BACKGROUND_COLOR="#3d3d3d" VGAP_QUANTITY="14 pt">
<font SIZE="12"/>
</stylenode>
</stylenode>
</stylenode>
</map_styles>
</hook>
<node TEXT="Style" GLOBALLY_VISIBLE="true" POSITION="left" ID="ID_495636280">
<node TEXT="GTD (with scripts)" ID="ID_251954954">
<node TEXT="=style.name" STYLE_REF="!WaitingFor" ID="ID_333083603"/>
<node TEXT="=style.name" STYLE_REF="!WaitingFor.Closed" ID="ID_1670631305"><richcontent CONTENT-TYPE="xml/" TYPE="DETAILS">
<html>
  <head>
    
  </head>
  <body>
    <p>
      := comments
    </p>
  </body>
</html></richcontent>
</node>
<node TEXT="=style.name" STYLE_REF="!NextAction" ID="ID_604624365"/>
<node TEXT="=style.name" STYLE_REF="!NextAction.Closed" ID="ID_1039839128"><richcontent CONTENT-TYPE="xml/" TYPE="DETAILS">
<html>
  <head>
    
  </head>
  <body>
    <p>
      := comments
    </p>
  </body>
</html></richcontent>
</node>
</node>
<node TEXT="cStorage" ID="ID_951766552">
<node TEXT="=style.name" STYLE_REF="cStorageMarkupRoot" ID="ID_1544493752"/>
<node TEXT="=style.name" STYLE_REF="cStorageMarkupMaker" ID="ID_1910107600"/>
<node TEXT="Icons" ID="ID_849269891">
<node TEXT="new line within" ID="ID_1488552511">
<icon BUILTIN="emoji-2935"/>
<node TEXT="=parent.delegate.icons*.translatedDescription.join(&apos; | &apos;)" STYLE_REF="=Table.cell" ID="ID_602893462"/>
</node>
<node TEXT="&lt;h1&gt; (mkNode)" ID="ID_528647596">
<icon BUILTIN="full-1"/>
<node ID="ID_1980373475" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="&lt;p&gt; + pReplacements (mkNode)" ID="ID_1665667454">
<icon BUILTIN="emoji-1F17F"/>
<node ID="ID_1082561284" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="pReplacements (mkNode, mkTableCell, _mkHeading, mkZipList)" ID="ID_115525689">
<icon BUILTIN="emoji-27BF"/>
<richcontent CONTENT-TYPE="xml/" TYPE="DETAILS">
<html>
  <head>
    
  </head>
  <body>
    <p>
      usefull also for mkQuote to apply repl. without P
    </p>
  </body>
</html></richcontent>
<node ID="ID_1130543427" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="s/ /&amp;nbsp;/g (mkNode, mkTableCell)" ID="ID_1176339246">
<icon BUILTIN="emoji-264A"/>
<node ID="ID_1146814755" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="escapeXml (mkNode, mkTableCell)" ID="ID_1750916384">
<icon BUILTIN="emoji-1F9F9"/>
<node ID="ID_713564453" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="no trailing sep (mkNode)" ID="ID_624013854">
<icon BUILTIN="emoji-1F317"/>
<icon BUILTIN="emoji-264B"/>
<node ID="ID_1623346704" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="no entry (mkNode, mkTableCell, mkTable)" ID="ID_1617878429">
<icon BUILTIN="emoji-26D4"/>
<node ID="ID_837428798" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="stop at this (mkTable, also: mkZipList, mkCsv)" ID="ID_750477354">
<icon BUILTIN="emoji-1F6D1"/>
<richcontent TYPE="NOTE" CONTENT-TYPE="xml/">
<html>
  <head>
    
  </head>
  <body>
    <p>
      mkTable continues to the next column even if it encounters a WikiLeaf
    </p>
    <p>
      stopAtThis makes it work similar to mkNode, where children of a WikiLeaf are ignored
    </p>
  </body>
</html></richcontent>
<node ID="ID_1903483171" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="&lt;ol&gt; (mkList)" ID="ID_1762666438">
<icon BUILTIN="emoji-0023-20E3"/>
<node ID="ID_1044722284" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="collapse (mkCode)" ID="ID_1621722269">
<icon BUILTIN="emoji-23EB"/>
<node ID="ID_967168174" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="line numbers (mkCode)" ID="ID_192571165">
<icon BUILTIN="emoji-1F522"/>
<node ID="ID_1926239337" CONTENT_ID="ID_602893462"/>
</node>
<node TEXT="border (mkImage, mkSection)" ID="ID_1937131038">
<icon BUILTIN="unchecked"/>
<richcontent CONTENT-TYPE="xml/" TYPE="DETAILS">
<html>
  <head>
    
  </head>
  <body>
    <p>
      height (image) | width (column)
    </p>
  </body>
</html></richcontent>
<node ID="ID_1238023406" CONTENT_ID="ID_602893462"/>
</node>
</node>
</node>
</node>
<node TEXT="minimize nodes if text is longer NCL" GLOBALLY_VISIBLE="true" ALIAS="minimize_nodes_if_text_is_longer_NCL" POSITION="left" ID="ID_758845119" VGAP_QUANTITY="3 pt">
<attribute_layout NAME_WIDTH="33.75 pt" VALUE_WIDTH="166.5 pt"/>
<attribute NAME="runMe" VALUE="menuitem:_MinimizeNodesIfTextIsLongerNCL_on_single_node" OBJECT="java.net.URI|menuitem:_MinimizeNodesIfTextIsLongerNCL_on_single_node"/>
</node>
<node TEXT="minimize all nodes if text is longer" POSITION="left" ID="ID_811994631">
<attribute_layout NAME_WIDTH="33 pt" VALUE_WIDTH="167.99999 pt"/>
<attribute NAME="runMe" VALUE="menuitem:_MinimizeAllNodesIfTextIsLonger_on_single_node" OBJECT="java.net.URI|menuitem:_MinimizeAllNodesIfTextIsLonger_on_single_node"/>
</node>
<node TEXT="# Heading 1&#xa;Regular text. **Bold text.** *Italic text.* ***Both: bold and italic.***&#xa;&#xa;A line (horizontal ruler) using underscores:&#xa;___&#xa;&#xa;&lt;s&gt;Strike through&lt;/s&gt; NB. using `~~deleted text~~` doesn&apos;t work in Freeplane (Java html/css renderer)&#xa;&#xa;E.g. ~~deleted text~~&#xa;&#xa;&lt;u&gt;Underlined text&lt;/u&gt;&#xa;&#xa;## Heading 2&#xa;&gt; A quote, line 1.&#xa;&gt; Line 2 of the quote (will be joined with line 1).&#xa;&gt;&gt; A quote within a quote&#xa;&#xa;### Heading 3&#xa;Sample text with `an in-line piece of code`.&#xa;&#xa;```groovy&#xa;// a Groovy code example - as a block of code&#xa;def name = &apos;Freeplane User&apos;&#xa;&quot;Hello, ${name}!&quot;&#xa;```&#xa;&#xa;    Another example of a block of code&#xa;    introduced as indented Markdown (with a tab or 4 spaces)&#xa;&#xa;&gt;     An example of a quote&#xa;&gt;     containing a block of code&#xa;&gt;     At least 5 spaces need to be used&#xa;&gt; And it continues as a regular quote&#xa;&#xa;#### Heading 4&#xa;A table&#xa;&#xa;| # | Language | [Pangram](https://en.wikipedia.org/wiki/Pangram) |&#xa;|--|--|--|&#xa;| 1 | English | The quick brown fox jumps over the lazy dog |&#xa;| 2 | French | Portez ce vieux whisky au juge blond qui fume |&#xa;| 3 | German | Victor jagt zwölf Boxkämpfer quer über den großen Sylter Deich |&#xa;| 4 | Italian | Pranzo d&apos;acqua fa volti sghembi |&#xa;| 5 | Spanish | Benjamín pidió una bebida de kiwi y fresa. Noé, sin vergüenza, la más exquisita champaña del menú |&#xa;&#xa;##### Heading 5&#xa;A list of items&#xa;&#xa;* Item 1&#xa;* Item 2&#xa;* Item 3&#xa;&#xa;###### Heading 6&#xa;A numbered list&#xa;&#xa;1. Item A&#xa;2. Item B&#xa;3. Item C" STYLE_REF="=Markdown" POSITION="right" ID="ID_537463989">
<node TEXT="a note; can be free; can have connectors" STYLE_REF="=FreeNote" ID="ID_103004008"/>
</node>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" STYLE_REF="?=Table" POSITION="right" ID="ID_759853847">
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_1061321757" MAX_WIDTH="157 px" MIN_WIDTH="157 px">
<attribute NAME="condiStyle" VALUE="?=Table.row.accent"/>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_650307212" MAX_WIDTH="162 px" MIN_WIDTH="162 px">
<attribute NAME="condiStyle" VALUE="?=Table.cell.accent"/>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_494801256" MAX_WIDTH="162 px" MIN_WIDTH="162 px">
<attribute NAME="condiStyle" VALUE="?=Table.cell.accent"/>
</node>
</node>
</node>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_1054433569" MAX_WIDTH="157 px" MIN_WIDTH="157 px">
<attribute NAME="condiStyle" VALUE="?=Table.row"/>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_1232181415" MAX_WIDTH="162 px" MIN_WIDTH="162 px">
<attribute NAME="condiStyle" VALUE="?=Table.cell"/>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_1553818585" MAX_WIDTH="162 px" MIN_WIDTH="162 px">
<attribute NAME="condiStyle" VALUE="?=Table.cell"/>
</node>
</node>
</node>
</node>
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table" POSITION="right" ID="ID_1079790042">
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table.row.accent" ID="ID_1237473870">
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table.cell.accent" ID="ID_672303643"/>
</node>
</node>
</node>
</map>
