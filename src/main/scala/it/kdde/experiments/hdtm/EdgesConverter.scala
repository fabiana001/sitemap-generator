package it.kdde.experiments.hdtm

import java.io.{File, PrintWriter}

import scala.io.Source

/**
  * Created by fabiana on 3/1/16.
  */
object EdgesConverter {
  def main(args: Array[String]): Unit = {
    args match {
      case Array(edgesFile: String) =>
        val set_e: Set[String] = Source.fromFile(edgesFile).getLines().toSet
        val output = edgesFile.replace(".txt","") + "Set.txt"
        val pw = new PrintWriter(new File(output))
        set_e.foreach(e => pw.println(e))
        pw.close()
        println(s"file $output generated with success")

      case _ => println("insert edges file to convert")
    }
  }
}
