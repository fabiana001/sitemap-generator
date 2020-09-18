package it.kdde.experiments.groundTruth

import java.io.{File, PrintWriter}
import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Node}
import org.jsoup.select.Elements

import scala.collection.JavaConversions._

/**
  * Created by fabiana on 2/1/16.
  */
trait SitemapCrawler {
  case class Href(url:String, depth :Int)

  class AnchorsWriter(filePath: String) extends Function1[String, Unit] {
    val file = new File(filePath)
    val out = new PrintWriter(file, "UTF-8")
    def apply(string: String): Unit = {
      out.println(string)
      out.flush()
    }
    def close(): Unit =
      out.close()
  }

    private def normalizeUrl(url: URL, relativeUrl: URL): String = {
      val string = relativeUrl.getPath match {
        case empty: String if empty.equals("") =>
          url.toExternalForm
        case slash: String if slash.equals("/")   =>
          url.toExternalForm
        case string:String if string.endsWith("/") =>
          url.getProtocol + "://" + relativeUrl.getHost + relativeUrl.getPath.dropRight(1)
        case _   =>
          url.getProtocol + "://" + relativeUrl.getHost + relativeUrl.getPath

      }
      string
    }




  def run (url: String, classTag: String, homepage:String): Unit ={
    val doc = Jsoup.connect(url).get()
    //val elements: Elements = doc.select("div[id=block-menu_block-1]")
    val elements: Elements = doc.getElementsByClass(classTag)
    val output = new URL(url).getHost.replace("www.", "")+ ".originalSitemap.txt"
    convert(List((elements.first(), None)), new AnchorsWriter(output), homepage)
  }



  protected def convert(elementsList: List[(Node, Option[Href])], anchorsWriter: AnchorsWriter, homepage: String): Unit = {

    if (!elementsList.isEmpty) {
      val headElement = elementsList.head
      val hrefChildren = headElement._1.childNodes().filter {
        case n: Element =>
          if (n.tagName == "a")
            true
          else false

        case _ => false
      }

      if (!hrefChildren.isEmpty) {
        val href = hrefChildren.head
        headElement._2 match {
          case Some(h) =>
            val url1 = new URL(h.url)
            //val url2 = normalizeUrl(url1,new URL(href.attr("abs:href")))
            val url2 = normalizeUrl(url1,new URL(href.absUrl("href")))
            val line =url1 + "\t" + h.depth + "\t" + url2
            anchorsWriter(line)
            println(line)
            println("\t"+url1 + "\t" + h.depth + "\t" + href.absUrl("href"))
            //val siblings = headElement._1.childNodes().filter(c => c != href).map(sibling => (sibling, Option(Href(url = href.attr("abs:href"), depth = h.depth + 1)))).toList
            val siblings = headElement._1.childNodes().filter(c => c != href).map(sibling => (sibling, Option(Href(url = url2, depth = h.depth + 1)))).toList

            convert(siblings ++ elementsList.tail, anchorsWriter, homepage)
          case None =>
            val url1 = new URL(homepage)
            //val url2 = normalizeUrl(url1 ,new URL(href.attr("abs:href")))
            val url2 = normalizeUrl(url1,new URL(href.absUrl("href")))
            val line = url1 + "\t" + 1 + "\t" + url2
            //val line = homepage + "\t" + 1 + "\t" + href.attr("abs:href")
            anchorsWriter(line)
            println(line)
            println("\t"+url1 + "\t" + 1 + "\t" + href.absUrl("href"))
            //val siblings = headElement._1.childNodes().filter(c => c != href).map(sibling => (sibling, Option(Href(url = href.attr("abs:href"), depth = 2)))).toList
            val siblings = headElement._1.childNodes().filter(c => c != href).map(sibling => (sibling, Option(Href(url = url2, depth = 2)))).toList
            convert(siblings ++ elementsList.tail, anchorsWriter, homepage)
        }


      }
      else
        convert(headElement._1.childNodes().map(c => (c, headElement._2)).toList ++ elementsList.tail, anchorsWriter, homepage)
    }

  }

  }
