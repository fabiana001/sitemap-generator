package it.kdde.experiments

import java.io.{PrintWriter, File}

/**
  * Created by fabiana on 2/5/16.
  */
object RemoveSitemapUrl {

  val separator = " , "

  def run(urlsMap:String, edges:String, sitemapUrl: String) = {
    val line = io.Source.fromFile(urlsMap)
      .getLines()
      .map(l => l.split(separator))
      .toList
      .filter(l => l(0)==sitemapUrl)

    if(line.isEmpty) {
      Console.err.println(s"Url $sitemapUrl not present in file $urlsMap")
    }else {
     val sitemapCode = line.head(1)
     val lines = io.Source.fromFile(edges)
       .getLines()
       .map(l => l.split("\t"))
       .filterNot(l => l(0)==sitemapCode| l(1)==sitemapCode)
       .map(a => a.mkString("\t"))
       .toList

      val parent = new File(edges).getParent
      val output = new File(parent + "/edgesNOsitemap.txt")
      val out = new PrintWriter(output, "UTF-8")
      lines.foreach(out.println(_))
      out.close()
      println(output)

      val prunedLines = io.Source.fromFile(edges)
        .getLines()
        .map(l => l.split("\t"))
        .filter(l => l(0)==sitemapCode| l(1)==sitemapCode)
        .map(a => a.mkString("\t"))
        .toList

      prunedLines.foreach(println)
    }

  }

  def main(args: Array[String])= {
    args match {
      case Array(urlsMap, edges, sitemapUrl) =>
        run(urlsMap,edges,sitemapUrl)
    }
  }
}
