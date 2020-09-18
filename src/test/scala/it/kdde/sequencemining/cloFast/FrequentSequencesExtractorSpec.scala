package it.kdde.sequencemining.cloFast

import java.io.File
import java.nio.file.Paths

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import scala.io.Source

/**
 * Created by fabiana on 1/8/16.
 */
class FrequentSequencesExtractorSpec  extends FlatSpec with ShouldMatchers {

  val seqExtr = new FrequentSequencesExtractor()
  val file = getClass.getResource("/cs.illinois.edu.ListConstraint.words1000.depth7/1000sequenceIDsFromHomepage.txt").getPath
  seqExtr.sequenceMining(0.005f, file)
  val outputFile = getClass.getResource("/cs.illinois.edu.ListConstraint.words1000.depth7/1000sequenceIDsFromHomepage-0.05.txt").getPath
  val numLines = Source.fromFile(outputFile).getLines().length

  "FrequentSequencesExtractorSpec" should "extract frequent sequences" in {
    numLines should be === 194
  }

  "removePrunedNodes" should "remove all pruned nodes" in {
    val output2 =  getClass.getResource("/cs.illinois.edu.ListConstraint.words1000.depth7/").getPath

    seqExtr.cloFast.writePatterns(Paths.get(output2+ "/sequencesIDs-0.005bis.txt"))
    seqExtr.cloFast.serializeTree(output2+ "/tree1.ser")
    val sizeFile1 = new File(output2+ "/tree1.ser").length()

    seqExtr.removePrunedNodes()
    //after pruning
    seqExtr.cloFast.serializeTree(output2+ "/tree2.ser")
    val sizeFile2 = new File(output2+ "/tree2.ser").length()
    val numLines2 = Source.fromFile(outputFile).getLines().length

    numLines should be === numLines2
    sizeFile1 should be > sizeFile2

  }
}
