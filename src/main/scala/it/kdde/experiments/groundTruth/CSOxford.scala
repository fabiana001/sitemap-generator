package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 2/1/16.
  */
object CSOxford extends SitemapCrawler{

  def main(args: Array[String]): Unit = {
    val url = "http://www.cs.ox.ac.uk/sitemap.html"
    val tagClass = "sitemap"
    val domain = "http://www.cs.ox.ac.uk"
    CSOxford.run(url, tagClass, domain)
  }
}
