package it.kdde.hierarchyGenerator

import java.io.{File, FileInputStream, ObjectInputStream, PrintWriter}

import it.kdde.hierarchyGenerator.Model.Url
import it.kdde.sequentialpatterns.model.tree.{ClosedSequenceNode, ClosedSequenceTree}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.Queue
import scala.util.matching.Regex




/**
 * Created by fabiana on 12/21/15.
 */
class HierarchyGeneratorWithSupport (val tree: ClosedSequenceTree, urlMapFile: String, edgesFile: String) extends HierarchyGenerator{

  //contain a map with key = urlCode (e.g., u_10) and value the url
  val urlMap = readMap(urlMapFile)
  //map where key is the url and value is the list of JNode having same url
  var duplicateUrl = Map.empty[Url, List[JNode]]
  //contain queue of duplicate urls
  val duplicateQueue = Queue.empty[String]
  //map where for each url are stored its outlinks
  var mapConsecutiveUrls = readOutlinksMap(edgesFile)


  /**
   *
   * For each Jnode update the Map duplicateUrl and if its label have been analyzed add its label in duplicateQueue
    *
    * @param node to analize
   */
  protected def addNodeInDuplicateUrl(node: JNode) = {
    duplicateUrl.isDefinedAt(node.url) match {
      case true =>
        val duplicateNodes = duplicateUrl.get(node.url).get :+ node
        //at the first duplication of the url add the url in duplicateQueue
        if(duplicateNodes.size.equals(2))
          duplicateQueue.enqueue(node.url)
        duplicateUrl += (node.url -> duplicateNodes)

      case false =>
        duplicateUrl += (node.url -> List(node))
    }
  }


  /**
   * Remove all Jnode that are not the bests
    *
    * @param root
   */
  private def pruneTree(root: JNode) : Unit = {

    /**
     * For each duplicate url returns the best JNode (i.e., with max )
      *
      * @param queue
     * @param accMap
     * @return
     */
    @tailrec
    def getBestNodes(queue: scala.collection.immutable.Queue[Url], accMap: Map[Url, JNode]): Map[Url, JNode] = {
      if(queue.isEmpty){
        accMap
      }else {
        val (currentNode, newQueue) = queue.dequeue
        val duplicateList = duplicateUrl.get(currentNode).get
        val bestNode = getMax(duplicateList)
        val newAcc = accMap +  (currentNode -> bestNode)
        getBestNodes(newQueue, newAcc)
      }
    }

    /**
     * Return the best node, i.e., node having best weight
      *
      * @param list
     * @return
     */
    @tailrec
    def getMax(list: List[JNode]): JNode = {
      list match {
        case List(x:JNode) => x
        case x :: y :: rest => getMax((if (x.weightSubTree > y.weightSubTree) x else y) :: rest)
      }
    }

    @tailrec
    def pruneTreeRec(queue: scala.collection.immutable.Queue[JNode], bestMap: Map[String, JNode]): Unit = {

      if(queue.nonEmpty) {
        val (node, newQueue)= queue.dequeue
        val bestNodes = node.children.filter{c =>
          bestMap.get(c.url) match {
            case None => true
            case Some(n) =>
              c.sequence == n.sequence
          }
        }
        node.children = bestNodes
        pruneTreeRec(newQueue ++ bestNodes, bestMap)
      }

    }

    val immutableQueue = scala.collection.immutable.Queue(duplicateQueue: _*)
    val bestMap = getBestNodes(immutableQueue, Map.empty[Url,JNode])
    val queue = scala.collection.immutable.Queue(root)
    pruneTreeRec(queue, bestMap)
  }

  /**
   *  Iterative version of Okasaki's Breadth-First Numbering (see http://debasishg.blogspot.com/2008/09/breadth-first-numbering-okasakis.html)
    *
    * @param root
   * @return
   */
  protected def visit(root: (ClosedSequenceNode, JNode)): JNode = {

    val partialQueue = mutable.Queue[(ClosedSequenceNode, JNode)](root)
    val auxList = mutable.ListBuffer[JNode](root._2)

    while(partialQueue.nonEmpty){

      val (currentNode, parentJNode) = partialQueue.dequeue()
      val currentJNode = toJNode(currentNode, parentJNode)

      addNodeInDuplicateUrl(currentJNode)

      val children = currentNode.getChildren
        .map { c => (toJNode(c, currentJNode), c)}

      val filteredChildren = children
        //filter urls that are outlinks of currentJnode
        .filter(c=> isOutLink(currentJNode.urlCode, c._1.urlCode))
        //the sequence c (i.e. c.sequence) doesn't contain multiple occurrences of currentJNode.label
        //if c._1.urlCode == a, then the sequence a -1 b -1 a -1 -2 has numberOccurrences>1
        .filter{ c =>
        val pattern = new Regex(c._1.urlCode)
        val numberOccurrences = (pattern findAllIn c._1.sequence toList).size
        numberOccurrences == 1
      }

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

    pruneTree(supportList.head)

    supportList.head
  }

  /**
   *
   * @param node to convert
   * @param parentJNode JNode parent of node
   * @return JNode of input node
   */
  protected def toJNode(node: ClosedSequenceNode, parentJNode: Node): JNode = {

    val sequenceList = node.getSequence
    Option(node.getParent) match {
      case None =>
        val name = node.getSequence.toString
        val sequence =  "-2"
        val url = "-2"
        val urlCode = "-2"
        val children = List()
        val weightSubTree = 0L
        //println(node.getAbsoluteSupport())
        new JNode(name, sequence, children, Map.empty[Int, List[Int]], url, urlCode, node.getAbsoluteSupport(), weightSubTree, 1)

      case _ =>

        val code = node.getSequence.getLastItemset.last

        val name = urlMap.getOrElse(code,code) +" : "+ node.getAbsoluteSupport
        val sequence =  node.getSequence.toString
        val url = urlMap.getOrElse(code,code)
        val urlCode = code
        val children = List()
        val weightSubTree = node.getAbsoluteSupport
        val depth = sequence.split("-1").size -2
        //println(node.getAbsoluteSupport())
        new JNode(name, sequence, children, Map.empty[Int, List[Int]], url, urlCode, node.getAbsoluteSupport, weightSubTree, depth)
    }

  }


  def convert(): JNode = {
    val newTree = tree
      .getRoot
      .getChildren
      .reduceLeft((c1,c2) => if (c1.getAbsoluteSupport> c2.getAbsoluteSupport) c1 else c2)

    val jRoot: JNode = toJNode(tree.getRoot, Root())
    val jRootNewTree = toJNode(newTree, jRoot)
    val root = (newTree, jRootNewTree)
    val results = visit(root)
    results
  }


  private def readOutlinksMap(inputFile: String) = {
    val lines = scala.io.Source.fromFile(inputFile).getLines().map { line =>
      val tokens = line.split("\t")
      (tokens(0), tokens(1))
    }.toList

    lines.groupBy(_._1).map{
      case (key, list) =>
        val set = list.map(l => l._2).toSet
        (key, set)
    }
  }

  private def getOutlinks(url: Model.Url): Set[Model.Url] = {
    mapConsecutiveUrls.getOrElse(url, List()).toSet
  }

  /**
   *
   * @param urlCode
   * @param outLinkCode
   * @return true if the web page having code "urlCode" has an outlink having code outLinkCode
   */
  protected def isOutLink(urlCode: Model.Url, outLinkCode: Model.Url): Boolean = {
   val outlinks = getOutlinks(urlCode)
    outlinks.contains(outLinkCode)
  }

}


object HierarchyGeneratorWithSupportMain extends App {
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
      val hierarchyGenerator  = new HierarchyGeneratorWithSupport(tree, urlMapPath, outlinksMapPath)
        val result = hierarchyGenerator.convert()
      implicit val formats = Serialization.formats(NoTypeHints)
      val jsonFile = outputFile + ".json"
      val linksFile = outputFile+ ".out"
      val out = new PrintWriter(new File(jsonFile))
      out.write(write(result))
      out.close()
      hierarchyGenerator.printTree(result, linksFile)

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

