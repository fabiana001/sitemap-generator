package it.kdde.hierarchyGenerator

/**
 * Created by fabiana on 1/8/16.
 */
import scala.annotation.tailrec
import scala.collection._
import scala.io.Source

sealed trait Trie
case object EmptyTrie extends Trie
case class TrieNode(label: String, children: mutable.Map[String, TrieNode] = mutable.Map.empty, support: Int = 0) extends Trie

object Trie {

  val SUPPORT_SEPARATOR = "#SUP:"
  val ITEM_SEPARATOR = " -1 "

  def load(path: String): Trie = {
    val lines = Source.fromFile(path).getLines()

    lines.foldLeft[Trie](EmptyTrie) {
      case (t, line) =>
        val sequenceSupport = line.split(SUPPORT_SEPARATOR)
        val (sequence, support) = (sequenceSupport.head.split(ITEM_SEPARATOR), sequenceSupport.tail.head)
        add(t, sequence.toList, support.trim.toInt)
    }
  }

  def add(t: Trie, path: List[String], support: Int): Trie = {
    @tailrec
    def add0(parent: Trie, path: List[String], acc: Trie): Trie = (parent, path) match {
      //case empty root
      case (EmptyTrie, h :: Nil) =>
        val root = TrieNode(label = h, support = support)
        add0(root, Nil, root)

      //case leaf
      case (p: TrieNode, h :: Nil) =>
        val node = p.children.getOrElseUpdate(h, TrieNode(label = h, support = support))
        add0(node, Nil, acc)

      //case inner node
      case (p: TrieNode, h :: tail) =>
        val node = if (h == p.label)
          p
        else p.children.getOrElseUpdate(h, TrieNode(h))
        add0(node, tail, acc)

      case (n, Nil) => acc
    }

    add0(t, path, t)
  }

  def find(trie: Trie, sequence: List[String]): Option[Int] = {

    @tailrec
    def find0(node: TrieNode, seq: List[String]): Option[Int] = seq match {

      case head :: Nil =>
        if (head == node.label)
          Some(node.support)
        else None

      case first :: second :: tail if first == node.label =>
        node.children.get(second) match {
          case Some(n) => find0(n, second :: tail)
          case None => None
        }

      case first :: second :: tail if first != node.label =>
        None
    }

    trie match {
      case n: TrieNode => find0(n, sequence)
      case EmptyTrie => None
    }
  }

  def print(trie: Trie): Unit = {
    def print0(node: TrieNode, level: Int): Unit = {
      //define the indentation in terms of tabs
      val tabs = (for (i <- 0 to level) yield "\t").mkString("")
      println(s"$tabs TrieNode(label=${node.label}, children=${node.children.keySet}, support=${node.support}")
      node.children.values.foreach(print0(_, level + 1))
    }

    trie match {
      case EmptyTrie => println(EmptyTrie)
      case n: TrieNode => print0(n, 0)
    }
  }
}

object Main extends App {

  val path = Main.getClass.getResource("/test.txt").getPath
  println(s"build a tree from $path")
  val trie = Trie.load(path)

  Trie.print(trie)

  val seq = List("0", "53", "1")
  val support = Trie.find(trie, seq)
  println(s"the support for the sequence $seq is $support")

}
