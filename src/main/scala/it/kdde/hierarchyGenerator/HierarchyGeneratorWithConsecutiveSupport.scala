package it.kdde.hierarchyGenerator

import java.io.{File, FileInputStream, ObjectInputStream, PrintWriter}

import it.kdde.hierarchyGenerator.Model.Url
import it.kdde.sequentialpatterns.model.ListNode
import it.kdde.sequentialpatterns.model.tree.{ClosedSequenceNode, ClosedSequenceTree}
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.writePretty

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.Queue
import scala.util.matching.Regex

/**
 * Created by fabiana on 12/16/15.
 */

class HierarchyGeneratorWithConsecutiveSupport (val tree: ClosedSequenceTree, urlMapFile: String) extends HierarchyGenerator{

  val urlMap = readMap(urlMapFile)
  var duplicateUrl = Map.empty[Url, List[JNode]]
  val duplicateQueue = Queue.empty[String]


  /**
   * Given two sequences s,s' such that s=parent(s') and an int I, we have 3 cases:
   * <ul>
   *   <li> CASE 1: s is not consecutive in the i-th sequence database, then s' is not consecutive
   *   <li> CASE 2: s is consecutive in the i-th sequence database, but s' not
   *   <li> CASE 3: s and s' are both consecutive in the i-th sequence database
   * </ul>
   * @param sequenceNode represents sequence s
   * @param parentJNode represents sequence s'
   * @return Map(Int, List(Int)) where the key is the row I-th and the value is the tid (i.e. transaction id) of the sequence s' in the I-th sequence database
   */
  private def getConsecutiveVil(sequenceNode: ClosedSequenceNode, parentJNode: JNode) : Map[Int, List[Int]] = {

    /**
     * iterates on a ListNode and returns a List of tid (i.e. transaction id)
     * @param firstElement
     * @return
     */
    def convertRowVerticalIdList(firstElement: ListNode): List[Int] = {
      val list = scala.collection.mutable.MutableList(firstElement.getColumn)
      var iterator = firstElement.next()
      while (iterator != null) {
        list += iterator.getColumn
        iterator = iterator.next()
      }
      list.toList
    }

    //For homepage
    if(sequenceNode.getParent.getVerticalIdList == null){
      val validRows = sequenceNode.getVerticalIdList.getValidRows
      val res = validRows.map{row =>
        val firstElement = sequenceNode.getVerticalIdList.getElement(row)
        val list = convertRowVerticalIdList(firstElement)
        (row.toInt,list)
      }.toMap
      res
    }

    //for all other web pages
    else {
      val validRows = sequenceNode.getVerticalIdList.getValidRows
      val res = validRows.flatMap{row =>

        parentJNode.consecutiveVilMap.get(row) match {
          //CASE 1: given s = parent(s'), s is not consecutive for that row
          case None => None

          case Some(validParentElements)  =>
            val firstElement = sequenceNode.getVerticalIdList.getElement(row)
            val allElements = convertRowVerticalIdList(firstElement)
            val consecutiveElements = allElements.filter(el => validParentElements.contains(el - 1))

            //CASE 2: s is consecutive but s' not
            //CASE 3: s and s' are consecutives
            consecutiveElements match {
              case List() => None
              case _ => Some((row.toInt, consecutiveElements))
            }
        }
      }.toMap
      res}
  }

  /**
   * Each time that an url is analized 2 time, it is put in "duplicateUrl"
   * @param node
   */
  private def addNodeInDuplicateUrl(node: JNode) = {
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


  def pruneTree(root: JNode) : Unit = {

    /**
     * For each duplicate url returns the best JNode (i.e., with max )
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

    @tailrec
    def getMax(list: List[JNode]): JNode = {
      list match {
        case List(x:JNode) => x
        case x :: y :: rest => getMax((if (x.weightSubTree > y.weightSubTree) x else y) :: rest)
      }
    }

    @tailrec
    def pruneTreeRec(queue: scala.collection.immutable.Queue[JNode], bestMap: Map[String, JNode]): Unit = {

      if(!queue.isEmpty) {
        val (node, newQueue)= queue.dequeue
        val bestNodes = node.children.filter{c =>
          bestMap.get(c.url) match {
            case None => true
            case Some(node) =>
              c.sequence == node.sequence
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
   * @param root
   * @return
   */
  private def visit(root: (ClosedSequenceNode, JNode)): JNode = {

    val partialQueue = mutable.Queue[(ClosedSequenceNode, JNode)](root)
    val auxList = mutable.ListBuffer[JNode](root._2)

    while(!partialQueue.isEmpty){

      val (currentNode, parentJNode) = partialQueue.dequeue()
      val currentJNode = toJNode(currentNode, parentJNode)

      addNodeInDuplicateUrl(currentJNode)

      val children = currentNode.getChildren
        .map { c => (toJNode(c, currentJNode), c)}

      //          //print infrequent urls
      //          children
      //            .filterNot(c=> c._1.consecutiveSupport >= tree.getAbsSupport)
      //            .foreach(c => println("Sequence " + c._2.getSequence + " removed because infrequent ("+ c._1.consecutiveSupport + ")"))

      val frequentChildren = children
        //filter frequent sequences wrt consecutiveSupport
        .filter(c=> c._1.consecutiveSupport >= tree.getAbsSupport)
        //the sequence c (i.e. c.sequence) doesn't contain multiple occurrences of currentJNode.label
        //if c._1.urlCode == a, then the sequence a -1 b -1 a -1 -2 has numberOccurrences>1
        .filter{ c =>
        val pattern = new Regex(c._1.urlCode)
        val numberOccurrences = (pattern findAllIn c._1.sequence toList).size
        numberOccurrences == 1
      }


      val (childrenJNode, childrenSeqNode) = frequentChildren.unzip
      val childrenWeight = childrenJNode.map(c => c.weightSubTree).sum
      currentJNode.children = childrenJNode.toList

      childrenSeqNode.foreach(c => partialQueue.enqueue((c, currentJNode)))

      auxList.add(currentJNode)


    }

    var supportList = mutable.ListBuffer.empty[JNode]
    val auxList2 = auxList.toList.drop(1).reverse.to[mutable.Queue]

    while (!auxList2.isEmpty){
      val currentNode = auxList2.dequeue
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
  private def toJNode(node: ClosedSequenceNode, parentJNode: Node): JNode = {

    val sequenceList = node.getSequence
    Option(node.getParent) match {
      case None =>
        val name = node.getSequence.toString
        val sequence =  "-2"
        val url = "-2"
        val urlCode = "-2"
        val consecutiveVilMap = Map[Int, List[Int]]()
        val consecutiveSupport = node.getAbsoluteSupport
        val children = List()
        val weightSubTree = 0L
        new JNode(name, sequence, children, consecutiveVilMap, url, urlCode,consecutiveSupport, weightSubTree, 1)

      case _ =>

        val code = node.getSequence.getLastItemset.last

        val consecutiveVilMap = getConsecutiveVil(node, parentJNode.asInstanceOf[JNode])
        val name = urlMap.getOrElse(code,code ) +" : "+ node.getAbsoluteSupport + "--" + consecutiveVilMap.keys.size
        val sequence =  node.getSequence.toString
        val url = urlMap.getOrElse(code,code )
        val urlCode = code
        val consecutiveSupport = consecutiveVilMap.keys.size
        val children = List()
        val weightSubTree = consecutiveSupport
        val depth = sequence.split("-1").size -2

        new JNode(name, sequence, children, consecutiveVilMap, url, urlCode, consecutiveSupport, weightSubTree, depth)
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

}


object HierarchyGeneratorWithConsecutiveSupportMain extends App {
  args match {

    case Array(serializedTreePath, urlMapPath) =>
      //try to write and read a serialized tree
      println("Reading File")

      val fin = new FileInputStream(serializedTreePath)
      val ois = new ObjectInputStream(fin)
      val tree = ois.readObject.asInstanceOf[ClosedSequenceTree]
      ois.close()
      println("End Reading File")
      val outputFile = serializedTreePath.replace(".ser","")
      val hg =  new HierarchyGeneratorWithConsecutiveSupport(tree, urlMapPath)
      val result = hg.convert()
      implicit val formats = Serialization.formats(NoTypeHints)
      val jsonFile = outputFile + "CS.json"
      val linksFile =  outputFile + "CS.out"
      val out = new PrintWriter(new File(jsonFile))
      out.write(writePretty(result))
      out.close()
      hg.printTree(result, linksFile)
      println("process terminated with success")
      println ("Json printed in file " + jsonFile)
      println("Output file  printed in file " + linksFile)

    case _ => println("Insert fileName of serialized closed tree and fileName of Map Url-Code")

  }

}
