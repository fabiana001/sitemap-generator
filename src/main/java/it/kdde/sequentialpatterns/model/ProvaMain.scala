package it.kdde.sequentialpatterns.model

import java.util

import it.kdde.sequentialpatterns.model.vil.VerticalIdListHashMap

import scala.collection.JavaConversions._

/**
 * Created by fabiana on 12/20/15.
 */
object ProvaMain extends App {
  val listNode1 = new ListNode (3, null)
  val listNode2= new ListNode(1, null, listNode1)
  val vil1 = new util.HashMap[Integer, ListNode]()
    vil1.put(1, listNode2)
  //val vil = mapAsJavaMap(Map(1 -> listNode2)).asInstanceOf[java.util.HashMap[java.lang.Integer, ListNode]]
  val verticalIdList = new VerticalIdListHashMap(vil1, 1)

  val validRows = verticalIdList.getValidRows
  val newMap = validRows.map{row =>
    val list = scala.collection.mutable.MutableList(verticalIdList.getElement(row).getColumn)
    var iterator = verticalIdList.getElement(row).next()
    while (iterator != null) {
      list += iterator.getColumn
      iterator = iterator.next()
    }
    (row,list)

  }.toMap

  print(newMap)

}
