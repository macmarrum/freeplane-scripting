// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.features.attribute.AttributeTableLayoutModel
import org.freeplane.features.attribute.ModelessAttributeController
import org.freeplane.features.map.MapModel
import org.freeplane.features.mode.Controller

// methods copied from AttributeViewTypeAction.java
def getAttributeViewType() {
    final MapModel map = Controller.getCurrentController().getMap()
    return ModelessAttributeController.getController().getAttributeViewType(map)
}

def setAttributeViewType(final String type) {
    final MapModel map = Controller.getCurrentController().getMap()
    ModelessAttributeController.getController().setAttributeViewType(map, type)
}

def attributeViewType = getAttributeViewType() == AttributeTableLayoutModel.HIDE_ALL ? AttributeTableLayoutModel.SHOW_ALL : AttributeTableLayoutModel.HIDE_ALL
setAttributeViewType(attributeViewType)
