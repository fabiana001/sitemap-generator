package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 1/28/16.
  */
object CSIllinois extends SitemapCrawler{

  def main(args: Array[String]): Unit = {
    val url = "http://cs.illinois.edu/site-map"
    val sitemapTag = "menu-block-1"
    val domain = "http://cs.illinois.edu"
    CSIllinois.run(url, sitemapTag, domain)
  }

}
