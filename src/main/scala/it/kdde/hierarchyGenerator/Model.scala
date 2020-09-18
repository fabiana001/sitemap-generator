package it.kdde.hierarchyGenerator

/**
 * Created by fabiofumarola on 16/09/14.
 */
object Model {

  type Url = String
  type Outlinks = List[Item]

  /**
   * Per motivi di complessità conviene inserire sempre un elemento in testa, e quindi la sequenza corretta
   * è ottenibile con un reverse
   */
  case class Sequence(sequence: List[Item]) {

    def lastItem() = sequence.head

    def size(): Int = {
      sequence.size
    }

    def extend(i: Item): Sequence = {
      copy(sequence = i :: this.sequence)
    }

    //def toPrefixspanFormat() =
    //  sequence.reverse.mkString("\t-1\t") + "\t-1\t-2"

    def contains(item: Item): Boolean =
      sequence.exists(e => e.url.equals(item.url))


    def contains(item: String): Boolean =
      sequence.exists(e => e.url.equals(item))

    def toTabFormat() = {
      val string = sequence.map(i => i.toString()).reverse.mkString("\t")
      string
    }

    def toPrefixspanFormat() = {
      val string = sequence.map(i => i.toString()).reverse.mkString("\t-1\t") + "\t-1\t-2"
      string
    }




  }

  /**
   * Each item contains the {@code url} field and { @code structure} field that will be used by FAST to extract frequent patterns
   * @param url contains the canonical url of a Web Page
   * @param cssPath
   * @param domCssPath
   * @param domPath
   */
  case class Item(url: String, host: String, cssPath: String, domCssPath: String, domPath: String, anchorText: String) {



    override def toString() = s"${url}"




  }

}
