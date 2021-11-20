// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.NodeRO
import org.freeplane.api.Node as FPN
c.selecteds.each { NodeRO nodeToBeSplit ->
    FPN newlyCreatedChild = nodeToBeSplit  // the initial one is the original node
    nodeToBeSplit.text.tokenize('|').each {
        newlyCreatedChild = newlyCreatedChild.createChild()
        newlyCreatedChild.text = it
    }
}
