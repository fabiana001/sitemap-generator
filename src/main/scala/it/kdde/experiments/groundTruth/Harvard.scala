package it.kdde.experiments.groundTruth

import java.io.{PrintWriter, File}
import java.net.{HttpURLConnection, URL}

import scala.io.Source

/**
  * Created by fabiana on 2/22/16.
  */
object Harvard extends SitemapCrawler {
  def main(args: Array[String]): Unit = {
    val url = "http://www.hbs.edu/siteindex"
    val tagClass = "tablet-row"
    val domain = "http://www.hbs.edu"
    run(url, tagClass, domain)
    val file = new URL(url).getHost.replace("www.", "")+ ".originalSitemap.txt"
    convertUrls(url, file)

  }
  protected def normalizeUrl(url: String): String = {
    val string = url match {
      case string:String if string.endsWith("aspx") =>
        url
      case string:String if string.endsWith("/") =>
       url
      case _   =>
        url + "/"
    }
    string
  }

  def getLocation(url: String) = {
   val nUrl = normalizeUrl(url)
   // println("a")
    val u = new URL(nUrl)
    val con = u.openConnection()
      .asInstanceOf[HttpURLConnection]
    con.setInstanceFollowRedirects(false)
    con.connect()
    val location = con.getHeaderField("Location")
    if(location == null){
      //con.disconnect()
      url
    }
    else{
      //con.disconnect()
      location
    }
  }

  def convertUrls(url:String, file: String): Unit = {
    var map = Map.empty[String, String]

    val lines = Source.fromFile(file)
      .getLines()
      .map{l =>
        val tokens = l.split("\t")
        val list = List(getLocation(tokens(0)), tokens(1), getLocation(tokens(2))).mkString("\t")
        list
      }.toList
    val out = file.replace(".txt","") + "2.txt"
    val nFile = new File(out)
    val pw = new PrintWriter(nFile)
    lines.foreach(l => pw.println(l))
    pw.close
    println("Sitemap in file "+ nFile.getAbsolutePath)

  }


}
