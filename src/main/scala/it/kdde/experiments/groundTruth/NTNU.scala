package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 2/22/16.
  */
object NTNU extends SitemapCrawler {
  def main(args: Array[String]): Unit = {
    val url = "http://www.ntnu.edu/sitemap"
    val tagClass = "columns-2"
    val domain = "http://www.ntnu.edu"
    run(url, tagClass, domain)
  }

}
