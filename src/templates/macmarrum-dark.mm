<map version="freeplane 1.11.5">
<!--To view this file, download free mind mapping software Freeplane from https://www.freeplane.org -->
<attribute_registry SHOW_ATTRIBUTES="hide"/>
<node TEXT="macmarrum-dark" LOCALIZED_STYLE_REF="AutomaticLayout.level.root" FOLDED="false" ID="ID_1939172785" VGAP_QUANTITY="20 pt" CHILD_NODES_LAYOUT="AUTO_FIRST" NodeVisibilityConfiguration="SHOW_HIDDEN_NODES"><hook NAME="MapStyle" background="#2b2b2b">
    <conditional_styles>
        <conditional_style ACTIVE="true" STYLE_REF="?bigChildGap" LAST="false">
            <script_condition user_name="any child has more than one child">
                <script>def hasMoreThanOneChild(n) {
 def ch = n.children
 return ch.size() &gt; 1 || ch.any { hasMoreThanOneChild(it) }
}
node.children.any { hasMoreThanOneChild(it) }</script>
            </script_condition>
        </conditional_style>
        <conditional_style ACTIVE="true" STYLE_REF="?singleInTree" LAST="false">
            <script_condition user_name="single clone in a clone tree">
                <script>def t = node.countNodesSharingContentAndSubtree
t &amp;&amp; node.countNodesSharingContent &gt; t</script>
            </script_condition>
        </conditional_style>
    </conditional_styles>
    <properties show_icon_for_attributes="true" edgeColorConfiguration="#a9b7c6ff,#4cc46bff,#e95065ff,#d7b84dff,#41b1d1ff,#c54499ff,#d76b4fff" show_note_icons="true" fit_to_viewport="false"/>

<map_styles>
<stylenode LOCALIZED_TEXT="styles.root_node" ID="ID_680156716" STYLE="oval" UNIFORM_SHAPE="true" VGAP_QUANTITY="24 pt">
<font SIZE="24"/>
<stylenode LOCALIZED_TEXT="styles.predefined" POSITION="bottom_or_right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="default" ID="ID_602083445" ICON_SIZE="20 px" FORMAT_AS_HYPERLINK="false" COLOR="#bbbbbb" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="6 pt" SHAPE_VERTICAL_MARGIN="4 pt" NUMBERED="false" FORMAT="STANDARD_FORMAT" TEXT_ALIGN="DEFAULT" TEXT_WRITING_DIRECTION="LEFT_TO_RIGHT" BORDER_WIDTH_LIKE_EDGE="false" BORDER_WIDTH="0 px" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#808080" BORDER_COLOR_ALPHA="192" BORDER_DASH_LIKE_EDGE="true" BORDER_DASH="SOLID" VGAP_QUANTITY="3 pt" COMMON_HGAP_QUANTITY="14 pt" CHILD_NODES_LAYOUT="AUTO" MAX_WIDTH="10 cm" MIN_WIDTH="0 cm" VERTICAL_ALIGNMENT="AS_PARENT">
<arrowlink SHAPE="CUBIC_CURVE" COLOR="#a89984" WIDTH="2" TRANSPARENCY="255" DASH="" FONT_SIZE="9" FONT_FAMILY="SansSerif" DESTINATION="ID_602083445" STARTINCLINATION="102.77419 pt;0 pt;" ENDINCLINATION="102.77419 pt;2.90323 pt;" STARTARROW="NONE" ENDARROW="DEFAULT"/>
<font NAME="Lato" SIZE="12" BOLD="false" STRIKETHROUGH="false" ITALIC="false"/>
<edge COLOR="#808080"/>
<richcontent TYPE="DETAILS" CONTENT-TYPE="xml/auto">
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
<stylenode LOCALIZED_TEXT="styles.user-defined" POSITION="bottom_or_right" STYLE="bubble">
<stylenode TEXT="?bigChildGap" ID="ID_21091341" VGAP_QUANTITY="20 pt"/>
<stylenode TEXT="?singleInTree" ID="ID_78482464">
<icon BUILTIN="emoji-1F3B2"/>
</stylenode>
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
<richcontent TYPE="NOTE">
<html>
  <head>
    
  </head>
  <body>
    <p>
      Before 1.12.x (91b76179), to switch on Clone Marks, `transparent` for Edge Color must be off
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
<richcontent TYPE="DETAILS">
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
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="bottom_or_right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" ID="ID_1659178249" COLOR="#a9b7c6" BACKGROUND_COLOR="#282828" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="10 pt" SHAPE_VERTICAL_MARGIN="10 pt" VGAP_QUANTITY="20 pt">
<font SIZE="20"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#52d273" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,2" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#f9556b" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,3" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e5c452" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,4" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#46bddf" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,5" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#d349a4" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,6" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e57154" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,7" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#52d273" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,8" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#f9556b" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,9" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e5c452" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,10" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#46bddf" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,11" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#d349a4" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,12" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e57154" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,13" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#52d273" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,14" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#f9556b" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,15" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e5c452" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,16" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#46bddf" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,17" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#d349a4" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,18" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e57154" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,19" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#52d273" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,20" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#f9556b" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,21" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e5c452" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,22" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#46bddf" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,23" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#d349a4" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,24" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e57154" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,25" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#52d273" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,26" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#f9556b" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,27" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e5c452" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,28" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#46bddf" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,29" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#d349a4" BORDER_COLOR_ALPHA="182"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,30" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#e57154" BORDER_COLOR_ALPHA="182"/>
</stylenode>
</stylenode>
</map_styles>
</hook>
<attribute_layout NAME_WIDTH="127.16129 pt" VALUE_WIDTH="43.54839 pt"/>
<attribute NAME="scriptOnMapOpen_NodeIdRefresher" VALUE="if (!node.mindMap.file) {&#xa;    // i.e. only for mind maps created from the template&#xa;    io.github.macmarrum.freeplane.NodeIdRefresher.refreshAll(node)&#xa;    node.attributes.removeAll(&apos;scriptOnMapOpen_NodeIdRefresher&apos;)&#xa;}"/>
<hook NAME="accessories/plugins/AutomaticLayout.properties" VALUE="ALL"/>
<node TEXT="Showcase" FOLDED="true" POSITION="top_or_left" ID="ID_1068881056">
<attribute_layout NAME_WIDTH="100 pt" VALUE_WIDTH="210 pt"/>
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
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table" ID="ID_878695655">
<attribute_layout NAME_WIDTH="100 pt" VALUE_WIDTH="210 pt"/>
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table.row.accent" ID="ID_707372107">
<attribute_layout NAME_WIDTH="100 pt" VALUE_WIDTH="210 pt"/>
<node TEXT="=node.style.allActiveStyles[0]" STYLE_REF="=Table.cell.accent" ID="ID_1691723926">
<attribute_layout NAME_WIDTH="100 pt" VALUE_WIDTH="210 pt"/>
</node>
</node>
</node>
<node TEXT="Style" GLOBALLY_VISIBLE="true" ID="ID_797952343">
<node TEXT="GTD (with scripts)" ID="ID_613028970">
<node TEXT="=style.name" STYLE_REF="!WaitingFor" ID="ID_1702177824"/>
<node TEXT="=style.name" STYLE_REF="!WaitingFor.Closed" ID="ID_602189075"><richcontent TYPE="DETAILS">
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
<node TEXT="=style.name" STYLE_REF="!NextAction.Closed" ID="ID_133806230"><richcontent TYPE="DETAILS">
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
<richcontent TYPE="DETAILS">
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
<richcontent TYPE="NOTE">
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
<richcontent TYPE="DETAILS">
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
</node>
</map>
