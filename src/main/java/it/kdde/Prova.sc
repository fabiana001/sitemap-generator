import it.kdde.sequentialpatterns.model.ListNode
import it.kdde.sequentialpatterns.model.vil.VerticalIdListHashMap

import scala.collection.JavaConversions._

val listNode1 = new ListNode (3, null)
val listNode2= new ListNode(1, null, listNode1)
val vil = mapAsJavaMap(Map(1 -> listNode2)).asInstanceOf[java.util.HashMap[java.lang.Integer, ListNode]]
val verticalIdList = new VerticalIdListHashMap(vil, 1)

val validRows = verticalIdList.getValidRows
validRows.foreach{row =>
  val list = scala.collection.mutable.MutableList(verticalIdList.getElement(row).getColumn)
  var iterator = verticalIdList.getElement(row).next()
  while (iterator != null) {
    list += iterator.getColumn
    iterator = iterator.next()
  }
  print(list)

}