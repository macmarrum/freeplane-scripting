// @ExecutionModes({ON_SINGLE_NODE})
/*
 * Alternative "unclone" functionality
 *
 * To only remove the clone from the single node, not each in the branch
 * see org/freeplane/features/map/mindmapmode/ConvertCloneToIndependentNodeAction.java
 */
import org.freeplane.features.map.mindmapmode.MMapController
import org.freeplane.features.mode.Controller

final MMapController mapController = (MMapController) Controller.getCurrentModeController().getMapController()
def myChildren
def myPosition
def temp
def selecteds = c.selecteds.collect()
selecteds.each { self ->
    if (self.children.size() > 0) {
        // move children to temp
        myChildren = self.children.collect()
        myPosition = self.parent.getChildPosition(self)
        temp = self.parent.createChild(myPosition)
        temp.text = '_temp_unclone'
        myChildren.each { it.moveTo(temp) }
        // unclone myself
        mapController.convertClonesToIndependentNodes(self.delegate)
        // move children back
        myChildren.each { it.moveTo(self) }
        // clean up
        temp.delete()
    }
}
c.select(selecteds)
