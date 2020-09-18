package it.kdde.hierarchyGenerator

import java.io.{File, PrintWriter}

import scala.io.Source

/**
 * Created by fabiana on 12/21/15.
 */
class Node() extends Serializable

case class JNode(name: String,
                 @transient sequence: String,
                 var children: List[JNode],
                 @transient consecutiveVilMap: Map[Int, List[Int]],
                 @transient url: String,
                 @transient urlCode: String,
                 var consecutiveSupport: Int,
                 @transient var weightSubTree: Long,
                 @transient depth: Int
                  ) extends Node

case class Root() extends Node
//TODO do normalization of urls
abstract class HierarchyGenerator() {


  /**
    *
    * @param inputFile file name of map serialized
    */
  protected def readMap(inputFile:String):Map[String, String] ={
    var resMap: Map[String, String]= Map()

    val map: Iterator[(String, String)] = Source.fromFile(inputFile).getLines().map { line =>
      val elements= line.split(" , ")
      elements match{
        case Array(value, key) => key.trim -> value.trim
        case Array(key) => key.trim -> "_"
      }
    }
    map.toMap
  }

  def printTree(node:JNode, output: String): Unit = {
    var depth = 0
    val file = new File(output)
    val out = new PrintWriter(file, "UTF-8")
    val stack = List(node)
    printTree0(stack, out)
    out.close()


    def printTree0(stack: List[JNode], out: PrintWriter): Unit = {
      if(!stack.isEmpty){
        val head: JNode = stack.head
        val inlink = head.url
        val children = head.children
        children.foreach{c =>
          val outlink = c.url
          out.println(s"$inlink\t${c.depth}\t$outlink\t${c.consecutiveSupport}")
        }
        printTree0(children++stack.tail, out)

      }


    }
  }

}
