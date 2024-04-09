<map version="freeplane 1.9.13">
<!--To view this file, download free mind mapping software Freeplane from https://www.freeplane.org -->
<node TEXT="macmarrum-dark" LOCALIZED_STYLE_REF="AutomaticLayout.level.root" FOLDED="false" ID="ID_1939172785" VGAP_QUANTITY="20 pt" CHILD_NODES_ALIGNMENT="BY_FIRST_NODE" NodeVisibilityConfiguration="SHOW_HIDDEN_NODES"><hook NAME="MapStyle" background="#2b2b2b">
    <conditional_styles>
        <conditional_style ACTIVE="false" STYLE_REF="?transBg" LAST="false"/>
        <conditional_style ACTIVE="true" STYLE_REF="?bigChildGap" LAST="false">
            <script_condition>
                <script>def hasMoreThanOneChild(n) {
 def ch = n.children
 return ch.size() &gt; 1 || ch.any { hasMoreThanOneChild(it) }
}
node.children.any { hasMoreThanOneChild(it) }</script>
            </script_condition>
        </conditional_style>
        <conditional_style ACTIVE="false" STYLE_REF="?alignChildren:center" LAST="false">
            <script_condition>
                <script>import static org.freeplane.features.map.SummaryNode.isSummaryNode

!node.root &amp;&amp; isSummaryNode(node.parent.delegate)</script>
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
    <properties show_icon_for_attributes="true" edgeColorConfiguration="#a9b7c6ff,#4cc46bff,#e95065ff,#d7b84dff,#41b1d1ff,#c54499ff,#d76b4fff" show_note_icons="true" fit_to_viewport="false"/>

<map_styles>
<stylenode LOCALIZED_TEXT="styles.root_node" ID="ID_680156716" STYLE="oval" UNIFORM_SHAPE="true" VGAP_QUANTITY="24 pt">
<font SIZE="24"/>
<stylenode LOCALIZED_TEXT="styles.predefined" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="default" ID="ID_602083445" ICON_SIZE="16 px" FORMAT_AS_HYPERLINK="false" COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="6 pt" SHAPE_VERTICAL_MARGIN="4 pt" NUMBERED="false" FORMAT="STANDARD_FORMAT" TEXT_ALIGN="DEFAULT" BORDER_WIDTH_LIKE_EDGE="false" BORDER_WIDTH="0 px" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#808080" BORDER_COLOR_ALPHA="128" BORDER_DASH_LIKE_EDGE="true" BORDER_DASH="SOLID" VGAP_QUANTITY="3 pt" CHILD_NODES_ALIGNMENT="AS_PARENT" MAX_WIDTH="10 cm" MIN_WIDTH="0 cm" VERTICAL_ALIGNMENT="AS_PARENT">
<arrowlink SHAPE="CUBIC_CURVE" COLOR="#a89984" WIDTH="2" TRANSPARENCY="255" DASH="" FONT_SIZE="9" FONT_FAMILY="SansSerif" DESTINATION="ID_602083445" STARTINCLINATION="102.77419 pt;0 pt;" ENDINCLINATION="102.77419 pt;2.90323 pt;" STARTARROW="NONE" ENDARROW="DEFAULT"/>
<font NAME="Lato" SIZE="12" BOLD="false" STRIKETHROUGH="false" ITALIC="false"/>
<edge COLOR="#808080"/>
<richcontent CONTENT-TYPE="xml/auto" TYPE="DETAILS">
<html>
  <head>
    
  </head>
  <body>
    <p>
      #a9b7c6ff
    </p>
  </body>
</html></richcontent>
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
<stylenode TEXT="?transBg" ID="ID_1783059692" BACKGROUND_COLOR="#333333" BACKGROUND_ALPHA="0" BORDER_DASH="SOLID"/>
<stylenode TEXT="?bigChildGap" ID="ID_21091341" VGAP_QUANTITY="20 pt"/>
<stylenode TEXT="?alignChildren:center" ID="ID_645894707" VGAP_QUANTITY="20 pt" CHILD_NODES_ALIGNMENT="BY_CENTER"/>
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
</stylenode>
<stylenode TEXT="=Warn" COLOR="#e5c453" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#9f893a">
<icon BUILTIN="emoji-26A0"/>
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
<stylenode TEXT="=Pin" ID="ID_225003551" COLOR="#e5c453" BORDER_COLOR_LIKE_EDGE="false">
<icon BUILTIN="emoji-1F4CC"/>
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
<stylenode TEXT="^cloud.bright" ID="ID_1738989435">
<cloud COLOR="#c7c7c7" SHAPE="ROUND_RECT"/>
</stylenode>
<stylenode TEXT="^cloud.dark" ID="ID_1275308483">
<cloud COLOR="#152242" SHAPE="ROUND_RECT"/>
</stylenode>
<stylenode TEXT="cStorageMarkupRoot" BACKGROUND_COLOR="#003e9b" BORDER_WIDTH="4 px" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#ffab00" VGAP_QUANTITY="15 pt">
<icon BUILTIN="emoji-1F3AF"/>
<font NAME="JetBrains Mono" SIZE="11"/>
</stylenode>
<stylenode TEXT="cStorageMarkupMaker" BACKGROUND_COLOR="#003e9b" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#ffab00">
<icon BUILTIN="emoji-1F335"/>
<font NAME="JetBrains Mono" SIZE="11"/>
</stylenode>
<stylenode TEXT="+max20cm" MAX_WIDTH="20 cm"/>
<stylenode TEXT="MaterializedSymlink">
<icon BUILTIN="emoji-2744"/>
</stylenode>
<stylenode TEXT="=Code" MAX_WIDTH="25 cm">
<font NAME="JetBrains Mono" SIZE="11"/>
</stylenode>
<stylenode TEXT="=DateISO" ID="ID_951700093" FORMAT="yyyy-MM-dd"/>
<stylenode TEXT="=TranspEdge" ID="ID_829143478" BORDER_COLOR_LIKE_EDGE="false">
<arrowlink DESTINATION="ID_829143478"/>
<edge COLOR="#808080" ALPHA="0"/>
</stylenode>
<stylenode TEXT="=SimuEdge" ID="ID_966983516" BORDER_COLOR_LIKE_EDGE="false">
<arrowlink SHAPE="EDGE_LIKE" COLOR="#808080" WIDTH="1" TRANSPARENCY="255" FONT_SIZE="-1" DESTINATION="ID_966983516"/>
<edge COLOR="#808080" ALPHA="0"/>
</stylenode>
<stylenode TEXT="=Seamless" ID="ID_218573881" BACKGROUND_COLOR="#2b2b2b" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#808080" BORDER_COLOR_ALPHA="0">
<arrowlink DESTINATION="ID_218573881"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" ID="ID_1659178249" COLOR="#a9b7c6" BACKGROUND_COLOR="#282828" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="10 pt" SHAPE_VERTICAL_MARGIN="10 pt" VGAP_QUANTITY="20 pt">
<font SIZE="20"/>
<richcontent TYPE="NOTE" CONTENT-TYPE="xml/">
<html>
  <head>
    
  </head>
  <body>
    <p>
      {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 1&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#52d273b6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 18,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#2d3830&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#2d3830ff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 2&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#f9556bb6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 16,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#3e2b2d&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#3e2b2dff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 3&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#e5c452b6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 14,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#3c382b&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#3c382bff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 4&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#46bddfb6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 14,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#2b363a&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#2b363aff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 5&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#d349a4b6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 14,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#372c33&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#372c33ff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 6&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#e57154b6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#3d2f2b&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#3d2f2bff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 7&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#52d273b6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#2d3830&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#2d3830ff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 8&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#f9556bb6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#3e2b2d&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#3e2b2dff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 9&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#e5c452b6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#3c382b&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#3c382bff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 10&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#46bddfb6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#2b363a&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#2b363aff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 11&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#d349a4b6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#372c33&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#372c33ff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;},
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&quot;Level 12&quot;: {
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;textColor&quot;: &quot;#e57154b6&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColorBlended&quot;: &quot;#3d2f2b&quot;,
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;backgroundColor&quot;: &quot;#3d2f2bff&quot;
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;}
    </p>
    <p>
      }
    </p>
  </body>
</html></richcontent>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" ID="ID_771207535" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#52d273" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,2" ID="ID_814211067" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#f9556b" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,3" ID="ID_352058479" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e5c452" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,4" ID="ID_119767224" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#46bddf" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,5" ID="ID_294563152" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#d349a4" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,6" ID="ID_1794312820" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e57154" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,7" ID="ID_759282133" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#52d273" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,8" ID="ID_94638384" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#f9556b" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,9" ID="ID_885575204" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e5c452" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,10" ID="ID_1580706732" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#46bddf" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,11" ID="ID_703695337" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#d349a4" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,12" ID="ID_1764911274" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e57154" BORDER_COLOR_ALPHA="182"/>
</stylenode>
</stylenode>
</map_styles>
</hook>
<attribute_layout VALUE_WIDTH="107.47826 pt"/>
<attribute NAME="scriptOnMapOpen" VALUE="if (!node.mindMap.file)&#xa;    io.github.macmarrum.freeplane.NodeIdRefresher.refreshAll(node)&#xa;// i.e. only for mind maps created from the template"/>
<hook NAME="accessories/plugins/AutomaticLayout.properties" VALUE="ALL"/>
<hook NAME="AutomaticEdgeColor" COUNTER="0" RULE="FOR_LEVELS"/>
<node TEXT="Showcase" FOLDED="true" POSITION="left" ID="ID_1068881056">
<node TEXT="1" OBJECT="java.lang.Long|1" ID="ID_1176543057">
<node TEXT="2" OBJECT="java.lang.Long|2" STYLE_REF="Projects" ID="ID_1355144522">
<node TEXT="3" OBJECT="java.lang.Long|3" STYLE_REF="Project" ID="ID_1122066066">
<node TEXT="4" OBJECT="java.lang.Long|4" STYLE_REF="Next Action" ID="ID_179614780">
<node TEXT="5" OBJECT="java.lang.Long|5" ID="ID_1453584683">
<node TEXT="6" OBJECT="java.lang.Long|6" ID="ID_644941512">
<node TEXT="7" OBJECT="java.lang.Long|7" ID="ID_1885017827">
<node TEXT="8" OBJECT="java.lang.Long|8" ID="ID_1812426112">
<node TEXT="9" OBJECT="java.lang.Long|9" ID="ID_1636773977">
<node TEXT="10" OBJECT="java.lang.Long|10" ID="ID_24534069">
<node TEXT="11" OBJECT="java.lang.Long|11" ID="ID_1598297337">
<node TEXT="12" OBJECT="java.lang.Long|12" ID="ID_745101133"/>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
<node TEXT="# Heading 1&#xa;Regular text. **Bold text.** *Italic text.* ***Both: bold and italic.***&#xa;&#xa;A line (horizontal ruler) using underscores:&#xa;___&#xa;&#xa;&lt;s&gt;Strike through&lt;/s&gt; NB. using `~~deleted text~~` doesn&apos;t work in Freeplane (Java html/css renderer)&#xa;&#xa;E.g. ~~deleted text~~&#xa;&#xa;&lt;u&gt;Underlined text&lt;/u&gt;&#xa;&#xa;## Heading 2&#xa;&gt; A quote, line 1.&#xa;&gt; Line 2 of the quote (will be joined with line 1).&#xa;&gt;&gt; A quote within a quote&#xa;&#xa;### Heading 3&#xa;Sample text with `an in-line piece of code`.&#xa;&#xa;```groovy&#xa;// a Groovy code example - as a block of code&#xa;def name = &apos;Freeplane User&apos;&#xa;&quot;Hello, ${name}!&quot;&#xa;```&#xa;&#xa;    Another example of a block of code&#xa;    introduced as indented Markdown (with a tab or 4 spaces)&#xa;&#xa;&gt;     An example of a quote&#xa;&gt;     containing a block of code&#xa;&gt;     At least 5 spaces need to be used&#xa;&gt; And it continues as a regular quote&#xa;&#xa;#### Heading 4&#xa;A table&#xa;&#xa;| # | Language | [Pangram](https://en.wikipedia.org/wiki/Pangram) |&#xa;|--|--|--|&#xa;| 1 | English | The quick brown fox jumps over the lazy dog |&#xa;| 2 | French | Portez ce vieux whisky au juge blond qui fume |&#xa;| 3 | German | Victor jagt zwölf Boxkämpfer quer über den großen Sylter Deich |&#xa;| 4 | Italian | Pranzo d&apos;acqua fa volti sghembi |&#xa;| 5 | Spanish | Benjamín pidió una bebida de kiwi y fresa. Noé, sin vergüenza, la más exquisita champaña del menú |&#xa;&#xa;##### Heading 5&#xa;A list of items&#xa;&#xa;* Item 1&#xa;* Item 2&#xa;* Item 3&#xa;&#xa;###### Heading 6&#xa;A numbered list&#xa;&#xa;1. Item A&#xa;2. Item B&#xa;3. Item C" STYLE_REF="=Markdown" ID="ID_325512923">
<node TEXT="a note; can be free; can have connectors" STYLE_REF="=FreeNote" ID="ID_1890635325"/>
</node>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" STYLE_REF="?=Table" ID="ID_1844478971">
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_918200734" MAX_WIDTH="157 px" MIN_WIDTH="157 px">
<attribute NAME="condiStyle" VALUE="?=Table.row.accent"/>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_1019798519" MAX_WIDTH="162 px" MIN_WIDTH="162 px">
<attribute NAME="condiStyle" VALUE="?=Table.cell.accent"/>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_296051241" MAX_WIDTH="162 px" MIN_WIDTH="162 px">
<attribute NAME="condiStyle" VALUE="?=Table.cell.accent"/>
</node>
</node>
</node>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_453441993" MAX_WIDTH="157 px" MIN_WIDTH="157 px">
<attribute NAME="condiStyle" VALUE="?=Table.row"/>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_556233187" MAX_WIDTH="162 px" MIN_WIDTH="162 px">
<attribute NAME="condiStyle" VALUE="?=Table.cell"/>
<node TEXT="=org.freeplane.features.styles.LogicalStyleController.controller.getFirstStyle(node.delegate)" ID="ID_1067949464" MAX_WIDTH="162 px" MIN_WIDTH="162 px">
<attribute NAME="condiStyle" VALUE="?=Table.cell"/>
</node>
</node>
</node>
</node>
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table" ID="ID_878695655">
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table.row.accent" ID="ID_707372107">
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table.cell.accent" ID="ID_1691723926"/>
</node>
</node>
<node TEXT="Style" GLOBALLY_VISIBLE="true" ID="ID_797952343">
<node TEXT="GTD (with scripts)" ID="ID_613028970">
<node TEXT="=style.name" STYLE_REF="!WaitingFor" ID="ID_1702177824"/>
<node TEXT="=style.name" STYLE_REF="!WaitingFor.Closed" ID="ID_602189075"><richcontent CONTENT-TYPE="xml/" TYPE="DETAILS">
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
<node TEXT="=style.name" STYLE_REF="!NextAction" ID="ID_917195021"/>
<node TEXT="=style.name" STYLE_REF="!NextAction.Closed" ID="ID_133806230"><richcontent CONTENT-TYPE="xml/" TYPE="DETAILS">
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
<node TEXT="cStorage" ID="ID_1657057230">
<node TEXT="=style.name" STYLE_REF="cStorageMarkupRoot" ID="ID_1859853408"/>
<node TEXT="=style.name" STYLE_REF="cStorageMarkupMaker" ID="ID_842423210"/>
<node TEXT="Icons" ID="ID_1675435887">
<node TEXT="eol (mkNode)" ID="ID_1502962718">
<icon BUILTIN="emoji-1F3C1"/>
<node TEXT="=parent.delegate.icons*.translatedDescription.join(&apos; | &apos;)" STYLE_REF="=Table.cell" ID="ID_645148925"/>
</node>
<node TEXT="new line within" ID="ID_89612255">
<icon BUILTIN="emoji-2935"/>
<node ID="ID_718602692" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="&lt;h1&gt; (mkNode)" ID="ID_159697610">
<icon BUILTIN="full-1"/>
<node ID="ID_1382123106" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="&lt;p&gt; + pReplacements (mkNode)" ID="ID_762113408">
<icon BUILTIN="emoji-1F17F"/>
<node ID="ID_1713063795" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="pReplacements (mkNode, mkTableCell, _mkHeading, mkZipList)" ID="ID_1810137487">
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
<node ID="ID_1934564504" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="s/ /&amp;nbsp;/g (mkNode, mkTableCell)" ID="ID_1497001735">
<icon BUILTIN="emoji-264A"/>
<node ID="ID_1464219629" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="escapeXml (mkNode, mkTableCell)" ID="ID_1750579653">
<icon BUILTIN="emoji-1F9F9"/>
<node ID="ID_1911847089" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="no trailing sep (mkNode)" ID="ID_123883854">
<icon BUILTIN="emoji-1F317"/>
<icon BUILTIN="emoji-264B"/>
<node ID="ID_125883854" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="no entry (mkNode, mkTableCell, mkTable)" ID="ID_1314478090">
<icon BUILTIN="emoji-26D4"/>
<node ID="ID_1771847933" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="stop at this (mkTable, also: mkZipList, mkCsv)" ID="ID_1804959483">
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
<node ID="ID_867479546" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="&lt;ol&gt; (mkList)" ID="ID_1553618132">
<icon BUILTIN="emoji-0023-20E3"/>
<node ID="ID_1034103639" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="collapse (mkCode)" ID="ID_273599376">
<icon BUILTIN="emoji-23EB"/>
<node ID="ID_527472432" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="line numbers (mkCode)" ID="ID_329929366">
<icon BUILTIN="emoji-1F522"/>
<node ID="ID_1720876546" CONTENT_ID="ID_645148925"/>
</node>
<node TEXT="border (mkImage, mkSection)" ID="ID_552910097">
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
<node ID="ID_22989845" CONTENT_ID="ID_645148925"/>
</node>
</node>
</node>
</node>
<node TEXT="macmarrum NodeChangeListener" GLOBALLY_VISIBLE="true" ALIAS="macmarrum_NodeChangeListener" ID="ID_1966479509" VGAP_QUANTITY="3 pt">
<attribute_layout NAME_WIDTH="33.75 pt" VALUE_WIDTH="166.5 pt"/>
<attribute NAME="runMe" VALUE="menuitem:_MinimizeNodesIfTextIsLongerNCL_on_single_node" OBJECT="java.net.URI|menuitem:_MinimizeNodesIfTextIsLongerNCL_on_single_node"/>
</node>
<node TEXT="minimize all nodes if text is longer" ID="ID_950285179">
<attribute_layout NAME_WIDTH="33 pt" VALUE_WIDTH="167.99999 pt"/>
<attribute NAME="runMe" VALUE="menuitem:_MinimizeAllNodesIfTextIsLonger_on_single_node" OBJECT="java.net.URI|menuitem:_MinimizeAllNodesIfTextIsLonger_on_single_node"/>
</node>
</node>
<node TEXT="+" FOLDED="true" POSITION="left" ID="ID_686574384"><richcontent TYPE="NOTE" CONTENT-TYPE="xml/">
<html>
  <head>
    
  </head>
  <body>
    <pre>/*
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see &lt;https://www.gnu.org/licenses/&gt;.
 */
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

class Styles {
    static void eachALS(Closure closure) {
        eachALS(closure, false)
    }

    static void eachALS(Closure closure, boolean withRoot) {
        def als0 = ScriptUtils.node().mindMap.root.style.styleNode
        //def als0 = als.children[0]
        def jsonStr = als0.note?.text
        def j = jsonStr ? new JsonSlurper().parseText(jsonStr) : [:]
       
        def als = als0.parent
        def children = withRoot ? als.children : als.children.drop(1)
        children.each { n -&gt;
            def h = j.get(n.text, [:])
            closure(n, h)
        }

        als0.note = JsonOutput.prettyPrint(JsonOutput.toJson(j))

        MenuUtils.executeMenuItems([
                'AutomaticLayoutControllerAction.null',
                'AutomaticLayoutControllerAction.ALL'
        ])
    }
}</pre>
  </body>
</html>
</richcontent>
<node TEXT="" ID="ID_433164527">
<hook NAME="FirstGroupNode"/>
</node>
<node TEXT="ALS off" ID="ID_69599035" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute NAME="script1" VALUE="menuUtils.executeMenuItems([&apos;AutomaticLayoutControllerAction.null&apos;])"/>
</node>
<node TEXT="ALS on" ID="ID_261692170" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute NAME="script1" VALUE="menuUtils.executeMenuItems([&apos;AutomaticLayoutControllerAction.ALL&apos;])"/>
</node>
<node TEXT="ALS text color off" ID="ID_200422131" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="33.75 pt" VALUE_WIDTH="123.75 pt"/>
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    if (n.style.isTextColorSet()) {&#xa;        h[&apos;textColor&apos;] = colorToRGBAString(n.style.textColor) //[0..6] + &apos;c0&apos;&#xa;        n.style.textColor = null&#xa;    }&#xa;}&#xa;"/>
</node>
<node TEXT="ALS text color on" ID="ID_197571083" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="34.5 pt" VALUE_WIDTH="121.5 pt"/>
<attribute NAME="script1" VALUE="def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def tc = h[&apos;textColor&apos;]&#xa;    if (tc) {&#xa;        n.style.textColorCode = tc&#xa;    }&#xa;}"/>
</node>
<node TEXT="ALS font size off" ID="ID_1266522216" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    if (n.style.font.isSizeSet()) {&#xa;        h[&apos;fontSize&apos;] = n.style.font.size&#xa;        n.style.font.resetSize()&#xa;    }&#xa;}&#xa;"/>
</node>
<node TEXT="ALS font size on" ID="ID_179359070" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def fs = h[&apos;fontSize&apos;]&#xa;    if (fs) {&#xa;        n.style.font.size = fs&#xa;    }&#xa;}&#xa;"/>
</node>
<node TEXT="ALS border color off" ID="ID_1935280726" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="33.75 pt" VALUE_WIDTH="123.75 pt"/>
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    if (n.style.border.isColorSet()) {&#xa;        h[&apos;borderColor&apos;] = colorToRGBAString(n.style.border.color) //[0..6] + &apos;c0&apos;&#xa;        n.style.border.color = null&#xa;    }&#xa;}&#xa;"/>
</node>
<node TEXT="ALS border color on" ID="ID_562820212" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="34.5 pt" VALUE_WIDTH="121.5 pt"/>
<attribute NAME="script1" VALUE="def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def c = h[&apos;borderColor&apos;]&#xa;    if (c) {&#xa;        n.style.border.colorCode = c&#xa;    }&#xa;}"/>
</node>
<node TEXT="ALS border color transparent" ID="ID_1568973441" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="34.5 pt" VALUE_WIDTH="121.5 pt"/>
<attribute NAME="script1" VALUE="def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def c = h[&apos;borderColor&apos;]&#xa;    if (c) {&#xa;        n.style.border.colorCode = c[0..6] + &apos;00&apos;&#xa;    }&#xa;}"/>
</node>
<node TEXT="ALS background color off" ID="ID_1074159512" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="33.75 pt" VALUE_WIDTH="123.75 pt"/>
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    if (n.style.isBackgroundColorSet()) {&#xa;        h[&apos;backgroundColor&apos;] = colorToRGBAString(n.style.backgroundColor) //[0..6] + &apos;c0&apos;&#xa;        n.style.backgroundColor = null&#xa;    }&#xa;}&#xa;"/>
</node>
<node TEXT="ALS backgorund color on" ID="ID_1975190231" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="34.5 pt" VALUE_WIDTH="121.5 pt"/>
<attribute NAME="script1" VALUE="def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def bc = h[&apos;backgroundColor&apos;]&#xa;    if (bc) {&#xa;        n.style.backgroundColorCode = bc&#xa;    }&#xa;}"/>
</node>
<node TEXT="ALS restore blended background color" ID="ID_1837870854" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="34.5 pt" VALUE_WIDTH="121.5 pt"/>
<attribute NAME="script1" VALUE="def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def bc = h[&apos;backgroundColorBlended&apos;]&#xa;    if (bc) {&#xa;        n.style.backgroundColorCode = bc&#xa;    }&#xa;}"/>
</node>
<node TEXT="ALS bg color alpha set" ID="ID_1453019032" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="33.75 pt" VALUE_WIDTH="123.75 pt"/>
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def script = htmlUtils.htmlToPlain(parent.noteText, true, false)&#xa;def Styles = new GroovyClassLoader().parseClass(script)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def c = colorToRGBAString(n.style.backgroundColor)[0..6] + &apos;ff&apos;&#xa;    n.style.backgroundColorCode = c&#xa;}&#xa;"/>
</node>
<node TEXT="edge colors" FOLDED="true" ID="ID_1205335687" VGAP_QUANTITY="3 pt">
<attribute NAME="script1" VALUE="node.children.each {&#xa;    it.style.backgroundColorCode = &quot;#$it.text&quot;&#xa;}"/>
<attribute NAME="script2" VALUE="node.children.each { it.appendChild(it) }&#xa;"/>
<node TEXT="4cc46b" ID="ID_695516513" COLOR="#000000" BACKGROUND_COLOR="#4cc46b">
<node TEXT="4cc46b" ID="ID_377983408" COLOR="#cccccc" BACKGROUND_COLOR="#223928"/>
</node>
<node TEXT="e95065" ID="ID_1836862036" COLOR="#000000" BACKGROUND_COLOR="#e95065">
<node TEXT="e95065" ID="ID_64734782" COLOR="#cccccc" BACKGROUND_COLOR="#8b2c39"/>
</node>
<node TEXT="d7b84d" ID="ID_1007232477" COLOR="#000000" BACKGROUND_COLOR="#d7b84d">
<node TEXT="d7b84d" ID="ID_1384336038" COLOR="#cccccc" BACKGROUND_COLOR="#706232"/>
</node>
<node TEXT="41b1d1" ID="ID_1094864016" COLOR="#000000" BACKGROUND_COLOR="#41b1d1">
<node TEXT="41b1d1" ID="ID_883393229" COLOR="#cccccc" BACKGROUND_COLOR="#2d5561"/>
</node>
<node TEXT="c54499" ID="ID_146958877" COLOR="#000000" BACKGROUND_COLOR="#c54499">
<node TEXT="c54499" ID="ID_1198372385" COLOR="#cccccc" BACKGROUND_COLOR="#543047"/>
</node>
<node TEXT="d76b4f" ID="ID_910028178" COLOR="#000000" BACKGROUND_COLOR="#d76b4f">
<node TEXT="d76b4f" ID="ID_1699672161" COLOR="#cccccc" BACKGROUND_COLOR="#4c2b23"/>
</node>
</node>
<node TEXT="node colors" FOLDED="true" ID="ID_1842448486" VGAP_QUANTITY="3 pt">
<attribute NAME="script1" VALUE="node.children.each { n -&gt;&#xa;    def k = n.appendChild(n)&#xa;    k.style.backgroundColor = n.style.textColor&#xa;}"/>
<attribute NAME="script2" VALUE="node.children.each { n -&gt;&#xa;    def k = n.children[0]&#xa;    //def l = k.appendChild(k)&#xa;    def l = k.children[0]&#xa;    def bcc = k.style.backgroundColorCode + &apos;33&apos;&#xa;    k.style.backgroundColorCode = bcc&#xa;    l.style.backgroundColorCode = bcc&#xa;}&#xa;"/>
<attribute NAME="script3" VALUE="import static org.freeplane.core.util.ColorUtils.makeNonTransparent&#xa;&#xa;def mbc = node.mindMap.backgroundColor&#xa;node.findAll().each {&#xa;    if (it.isLeaf())&#xa;        it.style.backgroundColor = makeNonTransparent(it.style.backgroundColor, mbc)&#xa;}"/>
<attribute NAME="script4" VALUE="def f = &apos;&apos;&apos;=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)&apos;&apos;&apos;&#xa;&#xa;node.findAll().each {&#xa;    if (it.isLeaf()) {&#xa;        it.text = (f + &apos;[1..-3]&apos;)&#xa;        it.parent.text = (f + &apos;[1..-1]&apos;)&#xa;    }&#xa;}"/>
<node TEXT="=node.style.textColorCode[1..-1]" ID="ID_1801338853" COLOR="#52d273" BACKGROUND_COLOR="#366c44">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-1]" ID="ID_212199897" COLOR="#52d273" BACKGROUND_COLOR="#366c44" BACKGROUND_ALPHA="51">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-3]" ID="ID_284290409" COLOR="#52d273" BACKGROUND_COLOR="#2d3830">
<font ITALIC="true"/>
</node>
</node>
</node>
<node TEXT="=node.style.textColorCode[1..-1]" ID="ID_1601564508" COLOR="#e94f64" BACKGROUND_COLOR="#8b2c39">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-1]" ID="ID_934593830" COLOR="#e94f64" BACKGROUND_COLOR="#8b2c39" BACKGROUND_ALPHA="51">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-3]" ID="ID_716419414" COLOR="#e94f64" BACKGROUND_COLOR="#3e2b2d">
<font ITALIC="true"/>
</node>
</node>
</node>
<node TEXT="=node.style.textColorCode[1..-1]" ID="ID_504358096" COLOR="#e5c351" BACKGROUND_COLOR="#84702d">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-1]" ID="ID_8709981" COLOR="#e5c351" BACKGROUND_COLOR="#84702d" BACKGROUND_ALPHA="51">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-3]" ID="ID_274893773" COLOR="#e5c351" BACKGROUND_COLOR="#3c382b">
<font ITALIC="true"/>
</node>
</node>
</node>
<node TEXT="=node.style.textColorCode[1..-1]" ID="ID_1416396070" COLOR="#45bcdf" BACKGROUND_COLOR="#2c6576">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-1]" ID="ID_1216267398" COLOR="#45bcdf" BACKGROUND_COLOR="#2c6576" BACKGROUND_ALPHA="51">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-3]" ID="ID_846675839" COLOR="#45bcdf" BACKGROUND_COLOR="#2b363a">
<font ITALIC="true"/>
</node>
</node>
</node>
<node TEXT="=node.style.textColorCode[1..-1]" ID="ID_1397101237" COLOR="#d349a4" BACKGROUND_COLOR="#683055">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-1]" ID="ID_78707706" COLOR="#d349a4" BACKGROUND_COLOR="#683055" BACKGROUND_ALPHA="51">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-3]" ID="ID_1985857134" COLOR="#d349a4" BACKGROUND_COLOR="#372c33">
<font ITALIC="true"/>
</node>
</node>
</node>
<node TEXT="=node.style.textColorCode[1..-1]" ID="ID_1869253122" COLOR="#e57053" BACKGROUND_COLOR="#87412f">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-1]" ID="ID_925165623" COLOR="#e57053" BACKGROUND_COLOR="#87412f" BACKGROUND_ALPHA="51">
<font ITALIC="true"/>
<node TEXT="=import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;colorToRGBAString(node.style.backgroundColor)[1..-3]" ID="ID_436111382" COLOR="#e57053" BACKGROUND_COLOR="#3d2f2b">
<font ITALIC="true"/>
</node>
</node>
</node>
</node>
<node TEXT="" ID="ID_1088130982">
<hook NAME="SummaryNode"/>
<hook NAME="AlwaysUnfoldedNode"/>
<node TEXT="" ID="ID_56709256"/>
</node>
</node>
</node>
</map>
