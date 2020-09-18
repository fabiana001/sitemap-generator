package it.kdde.sequencemining.cloFast

import java.nio.file.Paths

import it.kdde.sequentialpatterns.model.fast.{CloFast, FastDataset}
import it.kdde.sequentialpatterns.model.tree.{ClosedSequenceNode, ClosedSequenceTree, NodeType}

import scala.collection.JavaConversions._
import scala.collection.immutable.Queue

/**
 * Created by fabiana on 25/09/14.
 *
 * Given a sequence DB extract frequent sequential patterns using consecutive support
 */
class FrequentSequencesExtractor {
  var cloFast: CloFast = _
  var absMinSupp: Int = _

  def sequenceMining(minSup: Float, inputFile: String): (Int, Int) = {

    val lastPointIndex = inputFile.lastIndexOf(".")

    val filename = inputFile.substring(0, lastPointIndex)
    val outFile = filename + "-" + minSup + ".txt"
    val statFile = filename + "-" + minSup + ".stat"
    println("start load dataset")
    val ds = FastDataset.fromPrefixspanSource(Paths.get(inputFile),minSup, FastDataset.Type.SPARSE);
    println("end load dataset")
    println("start frequent sequential pattern mining")

    val cloFast = new CloFast(ds)
    cloFast.run()
    this.absMinSupp = ds.getAbsMinSup
    this.cloFast = cloFast
    this.cloFast.saveOnFile(Paths.get(outFile),inputFile,minSup,statFile)
    println("end frequent sequential pattern mining")
    (cloFast.statistics.getNumClosedFrequentSequences, cloFast.statistics.getNumFrequentSequenceGenerated)

  }

  def getJsonTree(): Unit = {
    val tree: ClosedSequenceTree = cloFast.getOutputTree
  }


   def removePrunedNodes(): Unit  = {
   var queue =  Queue[ClosedSequenceNode](cloFast.getOutputTree.getRoot)
   while(!queue.isEmpty){
     val (node, tailQueue) = queue.dequeue
     val filteredNodes = node.getChildren.filterNot(n => n.getType.equals(NodeType.pruned))
     node.setChildren(filteredNodes)
     queue = tailQueue ++ filteredNodes
   }

  }


}

object FrequentSequencesExtractorMain extends App {

  args match {
    case  Array(inputFile, minSupp) =>
      val fse = new FrequentSequencesExtractor()
      val lastPointIndex = inputFile.lastIndexOf(".")
      val filename = inputFile.substring(0, lastPointIndex)
      val outFile = filename + "-" + minSupp + ".clofastTree.ser"
      fse.sequenceMining(minSupp.toFloat, inputFile)

      println("Start pruning useless nodes")
      fse.removePrunedNodes()

      println("Start serialization tree")
      fse.cloFast.serializeTree(outFile)
      println("Tree serialized in "+ outFile)
    case _ =>
      println("setup input file and support")
  }
}
