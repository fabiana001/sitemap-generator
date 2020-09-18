package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 2/1/16.
  */
object Princeton extends SitemapCrawler{
  def main(args: Array[String]): Unit = {
    val url = "https://www.cs.princeton.edu/sitemap"
    val tagClass = "site-map-menus"
    val domain = "http://www.cs.princeton.edu"
//    val url = "http://chemistry.princeton.edu/sitemap"
//    val tagClass = "site-map-menus"
//    val domain = "http://chemistry.princeton.edu"
    run(url, tagClass, domain)
  }
}
