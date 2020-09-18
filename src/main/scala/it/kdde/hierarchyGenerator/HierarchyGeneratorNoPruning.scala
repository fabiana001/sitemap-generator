package it.kdde.hierarchyGenerator

import java.io.{File, FileInputStream, ObjectInputStream, PrintWriter}

import it.kdde.sequentialpatterns.model.tree.{ClosedSequenceNode, ClosedSequenceTree}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * Created by fabiana on 1/9/16.
 */
class HierarchyGeneratorNoPruning(tree: ClosedSequenceTree,
                                  urlMapFile: String,
                                  outlinksMap: String) extends HierarchyGeneratorWithSupport(tree, urlMapFile, outlinksMap) {


//  def convert(): JNode = {
//    val newTree = tree
//      .getRoot
//      .getChildren
//      .reduceLeft((c1,c2) => if (c1.getAbsoluteSupport> c2.getAbsoluteSupport) c1 else c2)
//
//    val jRoot: JNode = toJNode(tree.getRoot, Root())
//    val jRootNewTree = toJNode(newTree, jRoot)
//    val root = (newTree, jRootNewTree)
//    val results = visit(root)
//    results
//  }


  /**
    * Equals to implementation of omonim method of superclass but without pruning method
    * (i.e. from clofast tree are removed only web page not consective wrt web graph)
    *
    *  Iterative version of Okasaki's Breadth-First Numbering (see http://debasishg.blogspot.com/2008/09/breadth-first-numbering-okasakis.html)
    *
    * @param root
    * @return
    */
  override protected def visit(root: (ClosedSequenceNode, JNode)) = {

    val partialQueue = mutable.Queue[(ClosedSequenceNode, JNode)](root)
    val auxList = mutable.ListBuffer[JNode](root._2)

    while(partialQueue.nonEmpty){

      val (currentNode, parentJNode) = partialQueue.dequeue()
      val currentJNode = toJNode(currentNode, parentJNode)

      val children = currentNode.getChildren
        .map { c => (toJNode(c, currentJNode), c)}

      val filteredChildren = children.filter(c=> isOutLink(currentJNode.urlCode, c._1.urlCode))
      val noFilteredChildren = children.filterNot(c=> isOutLink(currentJNode.urlCode, c._1.urlCode))


      val (childrenJNode, childrenSeqNode) = filteredChildren.unzip
      currentJNode.children = childrenJNode.toList
      childrenSeqNode.foreach(c => partialQueue.enqueue((c, currentJNode)))
      auxList.add(currentJNode)

    }

    var supportList = mutable.ListBuffer.empty[JNode]
    val auxList2 = auxList.toList.drop(1).reverse.to[mutable.Queue]

    while (auxList2.nonEmpty){
      val currentNode = auxList2.dequeue()
      val (newChildren, newSupportList) = supportList.splitAt(currentNode.children.size)
      supportList = newSupportList
      currentNode.children = newChildren.toList.reverse

      val weight = currentNode.children.map(c => c.weightSubTree).sum
      currentNode.weightSubTree += weight
      supportList += currentNode
    }

    supportList.head
  }




}


object HierarchyGeneratorNoPruningMain extends App {
  args match {

    case Array(serializedTreePath, urlMapPath, outlinksMapPath) =>
      //try to write and read a serialized tree
      println("Reading File")
      val fin = new FileInputStream(serializedTreePath)
      val ois = new ObjectInputStream(fin)
      val tree = ois.readObject.asInstanceOf[ClosedSequenceTree]
      ois.close()
      println("End Reading File")

      val outputFile = serializedTreePath.replace(".ser", "")
      val hg = new HierarchyGeneratorNoPruning(tree, urlMapPath, outlinksMapPath)
        val result = hg.convert()
      implicit val formats = Serialization.formats(NoTypeHints)
      val jsonFile = outputFile + "NoPruning.json"
      val linksFile = outputFile + "NoPruning.out"
      val out = new PrintWriter(new File(jsonFile))
      out.write(write(result))
      out.close()
      hg.printTree(result, linksFile)
      println("process terminated with success")
      println("Json printed in file " + jsonFile)
      println("Output file  printed in file " + linksFile)




    case _ =>
      val message =
        """ Insert:
          | <treePath>, the path of serialized closed tree, e.g., /path/to/sequencesIDs-0.001.clofastTree.ser
          | <Url-CodeMap> the path of the file containing the Map Url-Code, e.g. /path/to/sequencesMapUrl.txt
          | <outlinks Map> the path of the file containing for each url the list of its outlinks, e.g. /path/to/outlinksMap.txt
        """.stripMargin
      println(message)

  }
}

