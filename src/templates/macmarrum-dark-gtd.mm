<map version="freeplane 1.9.13">
<!--To view this file, download free mind mapping software Freeplane from https://www.freeplane.org -->
<node TEXT="macmarrum-dark-gtd" LOCALIZED_STYLE_REF="AutomaticLayout.level.root" FOLDED="false" ID="ID_1939172785" NodeVisibilityConfiguration="SHOW_HIDDEN_NODES">
<font BOLD="false"/>
<hook NAME="AutomaticEdgeColor" COUNTER="0" RULE="FOR_LEVELS"/>
<attribute_layout NAME_WIDTH="86.25 pt" VALUE_WIDTH="194.24999 pt"/>
<attribute NAME="scriptOnMapOpen" VALUE="if (!node.mindMap.file) NodeIdRefresher.refreshAll(node)&#xa;// i.e. only for mind maps created from the template"/>
<hook NAME="MapStyle" background="#2b2b2b">
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
<stylenode LOCALIZED_TEXT="default" ID="ID_602083445" ICON_SIZE="20 px" FORMAT_AS_HYPERLINK="false" COLOR="#a9b7c6" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="10 pt" SHAPE_VERTICAL_MARGIN="4 pt" NUMBERED="false" FORMAT="STANDARD_FORMAT" TEXT_ALIGN="DEFAULT" BORDER_WIDTH_LIKE_EDGE="false" BORDER_WIDTH="0 px" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#2b2b2b" BORDER_DASH_LIKE_EDGE="false" BORDER_DASH="SOLID" VGAP_QUANTITY="3 pt" CHILD_NODES_ALIGNMENT="AS_PARENT" MAX_WIDTH="10 cm" MIN_WIDTH="0 cm" VERTICAL_ALIGNMENT="AS_PARENT">
<arrowlink SHAPE="CUBIC_CURVE" COLOR="#a89984" WIDTH="2" TRANSPARENCY="255" DASH="" FONT_SIZE="9" FONT_FAMILY="SansSerif" DESTINATION="ID_602083445" STARTINCLINATION="102.78261 pt;0 pt;" ENDINCLINATION="102.78261 pt;3.13043 pt;" STARTARROW="NONE" ENDARROW="DEFAULT"/>
<font NAME="Lato" SIZE="12" BOLD="false" STRIKETHROUGH="false" ITALIC="false"/>
<edge STYLE="bezier" COLOR="#808080" WIDTH="4" DASH="SOLID"/>
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
<stylenode TEXT="Inbox" ID="ID_1628185139" ICON_SIZE="30 px">
<icon BUILTIN="emoji-1F423"/>
<font SIZE="18"/>
</stylenode>
<stylenode TEXT="Projects" ID="ID_851594446" ICON_SIZE="30 px">
<icon BUILTIN="emoji-1F334"/>
<font SIZE="18"/>
</stylenode>
<stylenode TEXT="Somday/maybe" ID="ID_590906336" ICON_SIZE="30 px">
<icon BUILTIN="emoji-1F98B"/>
<font SIZE="18"/>
</stylenode>
<stylenode TEXT="Archive" ID="ID_803838958" ICON_SIZE="30 px">
<icon BUILTIN="emoji-1F342"/>
<font SIZE="18"/>
</stylenode>
<stylenode TEXT="Next Action" ID="ID_914962296">
<icon BUILTIN="emoji-1F426"/>
</stylenode>
<stylenode TEXT="Next Action Done" ID="ID_1431546927">
<icon BUILTIN="emoji-1F99C"/>
<font STRIKETHROUGH="true"/>
</stylenode>
<stylenode TEXT="Waiting For" ID="ID_832755575">
<icon BUILTIN="emoji-1F433"/>
</stylenode>
<stylenode TEXT="Waiting For Done" ID="ID_156169656">
<icon BUILTIN="emoji-1F422"/>
<font STRIKETHROUGH="true"/>
</stylenode>
<stylenode TEXT="Project" ID="ID_351748814">
<icon BUILTIN="emoji-1F419"/>
</stylenode>
<stylenode TEXT="?transBg" ID="ID_1783059692" BACKGROUND_COLOR="#000000" BACKGROUND_ALPHA="0" BORDER_DASH="SOLID"/>
<stylenode TEXT="?bigChildGap" ID="ID_21091341" VGAP_QUANTITY="20 pt"/>
<stylenode TEXT="?alignChildren:center" ID="ID_1690287595"/>
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
<stylenode TEXT="=Table.cell.accent" ID="ID_552523766" COLOR="#c62e2e">
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
<stylenode TEXT="=Code" ID="ID_1148084453" MAX_WIDTH="25 cm">
<font NAME="JetBrains Mono" SIZE="11"/>
</stylenode>
<stylenode TEXT="JumpIn" ID="ID_202746744">
<icon BUILTIN="emoji-1F4CD"/>
<hook NAME="NodeConditionalStyles">
    <conditional_style ACTIVE="true" LOCALIZED_STYLE_REF="AutomaticLayout.level.root" LAST="false"/>
</hook>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" ID="ID_1659178249" COLOR="#a9b7c6" BACKGROUND_COLOR="#282828" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="10 pt" SHAPE_VERTICAL_MARGIN="10 pt">
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 18
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 16
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 14
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 14
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 14
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12
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
      &#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&quot;fontSize&quot;: 12
    </p>
    <p>
      &#xa0;&#xa0;&#xa0;&#xa0;}
    </p>
    <p>
      }
    </p>
  </body>
</html>
</richcontent>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" ID="ID_771207535" BACKGROUND_COLOR="#455448" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,2" ID="ID_814211067" BACKGROUND_COLOR="#574248" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,3" ID="ID_352058479" BACKGROUND_COLOR="#59553f" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,4" ID="ID_119767224" BACKGROUND_COLOR="#435357" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,5" ID="ID_294563152" BACKGROUND_COLOR="#49434f" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,6" ID="ID_1794312820" BACKGROUND_COLOR="#3d3d3d" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,7" ID="ID_759282133" BACKGROUND_COLOR="#455448" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,8" ID="ID_94638384" BACKGROUND_COLOR="#574248" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,9" ID="ID_885575204" BACKGROUND_COLOR="#59553f" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,10" ID="ID_1798762136" BACKGROUND_COLOR="#435357" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,11" ID="ID_703695337" BACKGROUND_COLOR="#49434f" BACKGROUND_ALPHA="64"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,12" ID="ID_417359754" BACKGROUND_COLOR="#3d3d3d" BACKGROUND_ALPHA="64"/>
</stylenode>
</stylenode>
</map_styles>
</hook>
<hook NAME="accessories/plugins/AutomaticLayout.properties" VALUE="ALL"/>
<node TEXT="GTD" POSITION="right" ID="ID_1176543057">
<node TEXT="Inbox" STYLE_REF="Inbox" POSITION="right" ID="ID_253550823">
<node TEXT="Next Action 1" STYLE_REF="Next Action" ID="ID_1267688390"/>
<node TEXT="Next Action 2" STYLE_REF="Next Action Done" ID="ID_675320670"/>
<node TEXT="Waiting For 1" STYLE_REF="Waiting For" ID="ID_1031159864"/>
<node TEXT="Waiting For 2" STYLE_REF="Waiting For Done" ID="ID_751101877"/>
</node>
<node TEXT="Projects" STYLE_REF="Projects" POSITION="right" ID="ID_1355144522">
<node TEXT="Project 1" STYLE_REF="Project" ID="ID_1122066066">
<node ID="ID_179614780" CONTENT_ID="ID_1267688390"/>
<node ID="ID_1811605773" CONTENT_ID="ID_1031159864"/>
</node>
<node TEXT="Project 2" STYLE_REF="Project" ID="ID_279001961">
<node ID="ID_1660068250" CONTENT_ID="ID_675320670"/>
<node ID="ID_936471557" CONTENT_ID="ID_751101877"/>
</node>
</node>
<node TEXT="Somday/maybe" STYLE_REF="Somday/maybe" POSITION="left" ID="ID_1771227092"/>
<node TEXT="Archive" STYLE_REF="Archive" POSITION="left" ID="ID_1132191027"/>
</node>
<node TEXT="+" POSITION="left" ID="ID_720535384"><richcontent TYPE="NOTE" CONTENT-TYPE="xml/">
<html>
  <head>
    
  </head>
  <body>
    <pre>import groovy.json.JsonSlurper
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
}
    </pre>
  </body>
</html>
</richcontent>
<node TEXT="ALS off" ID="ID_1150973169" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute NAME="script1" VALUE="menuUtils.executeMenuItems([&apos;AutomaticLayoutControllerAction.null&apos;])"/>
</node>
<node TEXT="ALS on" ID="ID_1273700963" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute NAME="script1" VALUE="menuUtils.executeMenuItems([&apos;AutomaticLayoutControllerAction.ALL&apos;])"/>
</node>
<node TEXT="ALS text color off" ID="ID_1129408520" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="33.75 pt" VALUE_WIDTH="123.75 pt"/>
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def Styles = new GroovyClassLoader().parseClass(parent.note.text)&#xa;Styles.eachALS { n, h -&gt;&#xa;    if (n.style.isTextColorSet()) {&#xa;        h[&apos;textColor&apos;] = colorToRGBAString(n.style.textColor) //[0..6] + &apos;c0&apos;&#xa;        n.style.textColor = null&#xa;    }&#xa;}&#xa;"/>
</node>
<node TEXT="ALS text color on" ID="ID_718767722" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="34.5 pt" VALUE_WIDTH="121.5 pt"/>
<attribute NAME="script1" VALUE="def Styles = new GroovyClassLoader().parseClass(parent.note.text)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def tc = h[&apos;textColor&apos;]&#xa;    if (tc) {&#xa;        n.style.textColorCode = tc&#xa;    }&#xa;}"/>
</node>
<node TEXT="ALS font size off" ID="ID_1762413184" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def Styles = new GroovyClassLoader().parseClass(parent.note.text)&#xa;Styles.eachALS { n, h -&gt;&#xa;    if (n.style.font.isSizeSet()) {&#xa;        h[&apos;fontSize&apos;] = n.style.font.size&#xa;        n.style.font.resetSize()&#xa;    }&#xa;}&#xa;"/>
</node>
<node TEXT="ALS font size on" ID="ID_1055321516" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def Styles = new GroovyClassLoader().parseClass(parent.note.text)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def fs = h[&apos;fontSize&apos;]&#xa;    if (fs) {&#xa;        n.style.font.size = fs&#xa;    }&#xa;}&#xa;"/>
</node>
<node TEXT="ALS bg color alpha set" ID="ID_1434480384" LINK="menuitem:_ExecuteScriptForSelectionAction">
<attribute_layout NAME_WIDTH="33.75 pt" VALUE_WIDTH="123.75 pt"/>
<attribute NAME="script1" VALUE="import static org.freeplane.core.util.ColorUtils.colorToRGBAString&#xa;&#xa;def Styles = new GroovyClassLoader().parseClass(parent.note.text)&#xa;Styles.eachALS { n, h -&gt;&#xa;    def c = colorToRGBAString(n.style.backgroundColor)[0..6] + &apos;40&apos;&#xa;    n.style.backgroundColorCode = c&#xa;}&#xa;"/>
</node>
</node>
</node>
</map>
