package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 2/22/16.
  */
object TEMPLE extends SitemapCrawler {
  def main(args: Array[String]): Unit = {
    val url = "https://www.temple.edu/sitemap"
    val tagClass = "site-map-menus"
    val domain = "https://www.temple.edu"
    run(url, tagClass, domain)
  }

}
