package it.kdde.experiments.groundTruth

/**
  * Created by fabiana on 3/9/16.
  */
object ENEL extends SitemapCrawler{

  def main(args: Array[String]): Unit = {
    val url = "http://www.enel.it/it-it/info/sitemap"
    val sitemapTag = "post"
    val domain = "http://www.enel.it/it-it"
    CSIllinois.run(url, sitemapTag, domain)
  }
}
