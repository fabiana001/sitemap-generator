package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 2/22/16.
  */
object GSAScolumbia extends SitemapCrawler {
  def main(args: Array[String]): Unit = {
    //    val url = "https://www.cs.princeton.edu/sitemap"
    //    val tagClass = "site-map-menus"
    //    val domain = "http://www.cs.princeton.edu"
    val url = "http://gsas.columbia.edu/sitemap"
    val tagClass = "site-map-menus"
    val domain = "http://gsas.columbia.edu"
    run(url, tagClass, domain)
  }
}
