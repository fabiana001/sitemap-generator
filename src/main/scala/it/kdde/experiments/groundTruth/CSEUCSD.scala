package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 2/22/16.
  */
object CSEUCSD extends SitemapCrawler {

  def main(args: Array[String]): Unit = {
    val url = "http://www.cse.ucsd.edu/sitemap"
    val tagClass = "site-map-menus"
    val domain = "http://www.cse.ucsd.edu"
    run(url, tagClass, domain)
  }

}
