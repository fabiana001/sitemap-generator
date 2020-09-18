package it.kdde.experiments.comparison

/**
  * Created by fabiana on 2/1/16.
  */
object HierarchiesComparator {


  def compare(file1: String, groundTruthFile: String): Unit ={

    var listEdges = List.empty[String]
    val algorithm1 = io.Source.fromFile(file1).getLines.toList
    //.map(l => l.split("\t").dropRight(1).mkString("\t"))
    val grandTruth = io.Source.fromFile(groundTruthFile).getLines.toList

    val maxDepth = grandTruth.map(l => l.split("\t")(1).toInt).max
    Range(1, maxDepth+1).foreach{ depth =>
      val grandTruth_inDepth = grandTruth.filter(l => l.split("\t")(1).toInt==depth)
      val algorithm1_inDepth = algorithm1.filter(l =>
        l.split("\t")(1).toInt==depth)

      listEdges = listEdges ++ algorithm1_inDepth

      val truePositive = algorithm1_inDepth.intersect(grandTruth_inDepth).size
     // val falseNegative = grandTruth_inDepth.diff(algorithm1_inDepth).size

      val precision = truePositive.toFloat/grandTruth_inDepth.size
      val recall = truePositive.toFloat/algorithm1_inDepth.size
      val fMeasure = 2*precision*recall/(precision+recall)
      val numPages = grandTruth_inDepth.size

      val string =
        s"""
           |ANALYSIS SITEMAP AT DEPTH : $depth
           |PRECISION : $precision
           |RECALL : $recall
           |F-MEASURE : $fMeasure
           |NUM WEB PAGES @ $depth : $numPages
      """.stripMargin
      println(string)
      //      algorithm1_inDepth.foreach(println(_))
      //      println("*****************************************************")
      grandTruth_inDepth.diff(algorithm1_inDepth).foreach(println(_))
      //      println("*****************************************************")
    }

    val nodesAlg1 = listEdges.flatMap{ l =>
      val tokens = l.split("\t")
      if(tokens.size<3)
        println("stop")
      List(tokens(0), tokens(2))
    }.distinct

    val nodesGTruth = grandTruth.flatMap{ l =>
      val tokens = l.split("\t")
      List(tokens(0), tokens(2))
    }.distinct

    val n_truePositive = nodesAlg1.intersect(nodesGTruth).size
    val n_precision =  n_truePositive.toFloat/nodesGTruth.size
    val n_recall =  n_truePositive.toFloat/nodesAlg1.size
    val  n_fMeasure = 2* n_precision* n_recall/( n_precision+ n_recall)
    val string =
          s"""
            |ANALYSIS NODES
            |PRECISION : $n_precision
            |RECALL : $n_recall
            |F-MEASURE : $n_fMeasure
          """.stripMargin


    val e_truePositive = listEdges.intersect(grandTruth).size
    val  e_precision =  e_truePositive.toFloat/grandTruth.size
    val  e_recall =  e_truePositive.toFloat/listEdges.size
    val  e_fMeasure = 2* e_precision* e_recall/( e_precision+ e_recall)
    val stringEdges =
      s"""
         |ANALYSIS EDGES
         |PRECISION : $e_precision
         |RECALL : $e_recall
         |F-MEASURE : $e_fMeasure
          """.stripMargin


    println("***************************************")
    println(string)
    println(stringEdges)


//    val eNoDepth_algorithm1 = algorithm1.map{
//      line =>
//        val tokens = line.split("\t")
//        List(tokens(0), tokens(2)).mkString("\t")
//    }
//    val eNoDepth_groundTruth = grandTruth.map{
//      line =>
//        val tokens = line.split("\t")
//        List(tokens(0), tokens(2)).mkString("\t")
//    }
//    val eNoDepth_truePositive = eNoDepth_algorithm1.intersect(eNoDepth_groundTruth).size
//    val eNoDepth_precision = eNoDepth_truePositive.toFloat/eNoDepth_groundTruth.size
//    val eNoDepth_recall = eNoDepth_truePositive.toFloat/eNoDepth_algorithm1.size
//    val eNoDepth_fMeasure = 2* eNoDepth_precision* eNoDepth_recall/( eNoDepth_precision+ eNoDepth_recall)
//
//    val stringNoDepthEdges =
//      s"""
//         |AVG EDGES without depth
//         |PRECISION : $eNoDepth_precision
//         |RECALL : $eNoDepth_recall
//         |F-MEASURE : $eNoDepth_fMeasure
//          """.stripMargin
//
//
//    println("***************************************")
//    //println(string)
//    println(stringNoDepthEdges)

    println("***************************************")
    val webPagesNoIncluded = nodesGTruth.diff(nodesAlg1)
    val string2 =
      s"""
         |#Web pages not extracted: ${webPagesNoIncluded.size}
         |List web pages:
         """.stripMargin
    println(string2)
    webPagesNoIncluded.foreach(println)


  }

  def main(args: Array[String]) = {
    args match {
      case Array() =>
        //ILLINOIS
//        val file1= "/home/fabiana/git/khachaturian/experiments_Khachaturian/illinois/HIERARCHY_OUTConverted.out"
//        val file1= "/home/fabiana/git/khachaturian/experiments_Khachaturian/illinois/sequenceIDsFromHomepage500k-0.005.clofastTreeCS.out"
//        val groundTruth = "/home/fabiana/git/khachaturian/experiments_Khachaturian/illinois/cs.illinois.edu.originalSitemap.txt"

        //CS.OX
//        val file1= "/home/fabiana/git/khachaturian/experiments_Khachaturian/oxford/HIERARCHY_OUTConverted.out"
//         val file1= "/home/fabiana/git/khachaturian/experiments_Khachaturian/oxford/sequenceIDsFromHomepage-0.0005.clofastTree.out"
//         val groundTruth = "/home/fabiana/git/khachaturian/experiments_Khachaturian/oxford/cs.ox.ac.uk.originalSitemapPruned.txt"

        //Princeton
//        val file1 = "/home/fabiana/git/khachaturian/experiments_Khachaturian/princeton/HIERARCHY_OUTConverted.out"
//        val file1= "/home/fabiana/git/khachaturian/experiments_Khachaturian/princeton/sequenceIDsFromHomepage-0.001.clofastTree.out"
//        val groundTruth = "/home/fabiana/git/khachaturian/experiments_Khachaturian/princeton/cs.princeton.edu.originalSitemap.txt"

        //GSA_COLUMBIA
        //val file1 = "/home/fabiana/git/khachaturian/experiments_Khachaturian/GSA_COLUMBIA/HIERARCHY_OUTConverted.out"
//        val file1= "/home/fabiana/git/khachaturian/experiments_Khachaturian/GSA_COLUMBIA/sequenceIDsFromHomepage-0.001.clofastTree.out"
//        val groundTruth = "/home/fabiana/git/khachaturian/experiments_Khachaturian/GSA_COLUMBIA/gsas.columbia.edu.originalSitemap.txt"

        //MITSLOAN
//        val file1 = "/home/fabiana/git/khachaturian/experiments_Khachaturian/mitsloan/HIERARCHY_OUTConverted.out"
        //val file1= "/home/fabiana/git/khachaturian/experiments_Khachaturian/mitsloan/sequenceIDsFromHomepage-0.001.clofastTreeCS.out"
//        val groundTruth = "/home/fabiana/git/khachaturian/experiments_Khachaturian/mitsloan/mitsloan.mit.edu.originalSitemap.txt"

        //CSE_UCSD
//        val file1 = "/home/fabiana/git/khachaturian/experiments_Khachaturian/CSE_UCSD/sequenceIDsFromHomepage.lenght.20-0.001.clofastTree.out"
//        val groundTruth = "/home/fabiana/git/khachaturian/experiments_Khachaturian/CSE_UCSD/cse.ucsd.edu.originalSitemap.txt"

        //ENEL
//        val file1= "/home/fabiana/git/khachaturian/experiments_Khachaturian/enel/sequenceIDsFromHomepage-0.0005.clofastTree.out"
        val file1 = "/home/fabiana/git/khachaturian/experiments_Khachaturian/enel/HIERARCHY_OUTConverted.out"
        val groundTruth = "/home/fabiana/git/khachaturian/experiments_Khachaturian/enel/enel.it.originalSitemap.txt"

        compare(file1, groundTruth)
      case Array(file1: String, groundTruth: String) =>
        compare(file1, groundTruth)
      case _ =>
        Console.err.println(s"wrong parameters for: ${args.mkString(" ")}")
        val string = """to run the jar do: java -cp khachaturian.jar experiments.comparison.HierarchiesComparator <file1> <groundTruth>
                       | where:
                       | <file1> : file path containing the extracted sitemap  (in the form of url \t url)
                       | <groundTruth> : file path containing the real sitemap  (in the form of url \t url)
                     """

        Console.err.println(string)
    }
  }


}
