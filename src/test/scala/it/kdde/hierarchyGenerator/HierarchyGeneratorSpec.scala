package it.kdde.hierarchyGenerator

import it.kdde.sequencemining.cloFast.FrequentSequencesExtractor
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import scala.collection.JavaConversions._

/**
  * Created by fabiana on 2/3/16.
  */
class HierarchyGeneratorSpec extends FlatSpec with ShouldMatchers {
  //generate tree
  val fileSequences = getClass.getResource("/cs.illinois.edu.ListConstraint.words1000.depth7/1000sequenceIDsFromHomepage.txt").getPath
  val fileUrlMap = getClass.getResource("/cs.illinois.edu.ListConstraint.words1000.depth7/urlsMap.txt").getPath
  val fileOutlinkMap = getClass.getResource("/cs.illinois.edu.ListConstraint.words1000.depth7/edges.txt").getPath

  val seqExtr = new FrequentSequencesExtractor()
  seqExtr.sequenceMining(0.05f, fileSequences)
  val tree = seqExtr.cloFast.getOutputTree

  val homepageTree = tree
    .getRoot
    .getChildren
    .reduceLeft((c1,c2) => if (c1.getAbsoluteSupport> c2.getAbsoluteSupport) c1 else c2)

  val u_10ClosedSequenceTree = homepageTree.getChildren.filter(c => c.getSequence.getLastItem=="10" ).head

  "HierarchyGeneratorWithSupport" should "prune sequences with redundant urls" in {

    val hierarchy = new HierarchyGeneratorWithSupport(tree, fileUrlMap, fileOutlinkMap).convert()
    val u_10Hierarchy= hierarchy.children.filter(c => c.urlCode=="10").head

    u_10Hierarchy.children.size should be === 0
    u_10ClosedSequenceTree.getChildren.size() should be > 1
  }

  "HierarchyGeneratorWithConsecutiveSupport" should "prune sequences with redundant urls" in {

    val hierarchy = new HierarchyGeneratorWithConsecutiveSupport(tree, fileUrlMap).convert()
    val u_10Hierarchy= hierarchy.children.filter(c => c.urlCode=="10").head

    u_10Hierarchy.children.size should be === 0
  }

  "HierarchyGeneratorNoPruning" should "no prune sequences with redundant urls" in {

    val hierarchy = new HierarchyGeneratorNoPruning(tree, fileUrlMap, fileOutlinkMap).convert()
    val u_10Hierarchy= hierarchy.children.filter(c => c.urlCode=="10").head

    u_10Hierarchy.children.size should be === 13
  }






}
