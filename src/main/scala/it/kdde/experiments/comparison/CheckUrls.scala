package it.kdde.experiments.comparison

import scala.io.Source

/**
  * Created by fabiana on 2/11/16.
  */
object CheckUrls {
  /**
    * Check if there are urls in the sitemap file that are never elaborated during graph generation process
 *
    * @param urlsMapFile
    * @param sitemapFile
    */
  def run(urlsMapFile: String, sitemapFile:String)={
    val urlsMap = Source.fromFile(urlsMapFile).getLines().map(_.split(" , ")(0).trim).toList
    val urlsSitemap = Source.fromFile(sitemapFile)
      .getLines()
      .flatMap{l =>
        val tokens = l.split("\t")
        List(tokens(0).trim, tokens(2).trim).toList
      }.toList.distinct
    val diff = urlsSitemap.flatMap{url =>
     if(!urlsMap.contains(url))
       Some(url)
     else None
    }
    diff.foreach(println(_))
    println(s"${diff.size} ${urlsMap.size} ${urlsSitemap.size}")

  }

  def main(args: Array[String])= {
    var urlsMap: String = ""
    var sitemapFile = ""
    if (args.size == 0) {
//      urlsMap = "./experiments_Khachaturian/chemistry.princeton.edu.ListConstraint.words10000.depth7/urlsMap.txt"
//      sitemapFile = "./experiments_Khachaturian/chemistry.princeton.edu.ListConstraint.words10000.depth7/chemistry.princeton.edu.originalSitemap.txt"
      urlsMap = "/home/fabiana/git/khachaturian/experiments_Khachaturian/princeton/urlsMap.txt"
      sitemapFile = "/home/fabiana/git/khachaturian/experiments_Khachaturian/princeton/cs.princeton.edu.originalSitemap.txt"

    } else {
      urlsMap = args(0)
      sitemapFile = args(1)
    }
    run(urlsMap, sitemapFile)

  }

}
