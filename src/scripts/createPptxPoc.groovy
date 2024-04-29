// based on https://www.baeldung.com/apache-poi-slideshow

@Grab('org.apache.poi:poi:5.2.5')
@Grab('org.apache.poi:poi-ooxml:5.2.5')

import org.apache.poi.xslf.usermodel.SlideLayout
import org.apache.poi.xslf.usermodel.XMLSlideShow


def ppt = new XMLSlideShow()
ppt.createSlide()
def defaultMaster = ppt.slideMasters[0]
def layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT)
def slide = ppt.createSlide(layout)
def titleShape = slide.getPlaceholder(0)
titleShape.text = node.text
def contentShape = slide.getPlaceholder(1)
contentShape.text = node.children.collect { it.text }.join('\n')
def file = new File(node.mindMap.file.parent, 'powerpoint.pptx')
if (!file.exists()) {
    file.withDataOutputStream { outputStream ->
        ppt.write(outputStream)
    }
    ui.showMessage("$file has been created", 1)
} else {
    ui.showMessage("$file already exists", 0)
}
