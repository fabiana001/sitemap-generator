package it.kdde.sequencemining.cloFast

import java.nio.file.Paths


import it.kdde.sequentialpatterns.model.fast.{FastDataset, Fast}
import it.kdde.sequentialpatterns.model.tree.SequenceTree

/**
 * Created by fabiana on 25/09/14.
 *
 * Given a sequence DB extract frequent sequential patterns
 */
@deprecated
class FrequentSequencesExtractorFast {
  var fast: Fast = _
  var absMinSupp: Int = _

  def sequenceMining(minSup: Float, inputFile: String): Int = {

    val lastPointIndex = inputFile.lastIndexOf(".");

    val filename = inputFile.substring(0, lastPointIndex);
    val outFile = filename + "-" + minSup + ".txt";
    val statFile = filename + "-" + minSup + ".stat";
    println("start load dataset")
    val ds = FastDataset.fromPrefixspanSource(Paths.get(inputFile),minSup, FastDataset.Type.SPARSE);
    println("end load dataset")
    println("start frequent sequential pattern mining")

    val fast = new Fast(ds)
    fast.run()
    this.absMinSupp = ds.getAbsMinSup
    this.fast = fast
    this.fast.saveOnFile(Paths.get(outFile),inputFile,minSup,statFile)
    println("end frequent sequential pattern mining")
    fast.statistics.getNumFrequentSequenceGenerated

  }

  def getJsonTree(): Unit = {
    val tree: SequenceTree = fast.getTree
  }


}
@deprecated
object FrequentSequencesExtractorFast extends App {

  args match {
    case  Array(inputFile, minSupp) =>
      val lastPointIndex = inputFile.lastIndexOf(".")
      val filename = inputFile.substring(0, lastPointIndex)
      val outFile = filename + "-" + minSupp + ".fastTree.ser"
      val fse = new FrequentSequencesExtractorFast()
      fse.sequenceMining(minSupp.toFloat, inputFile)
      fse.fast.serializeTree(outFile)

    case _ =>
      println("setup support and inputfile")
  }
}
