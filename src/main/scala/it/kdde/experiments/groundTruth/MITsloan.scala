package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 2/22/16.
  */
object MITsloan  extends SitemapCrawler {
  def main(args: Array[String]): Unit = {
    val url = "http://mitsloan.mit.edu/sitemap"
    val tagClass = "widgetBody"
    val domain = "http://mitsloan.mit.edu"
    run(url, tagClass, domain)
  }

}
