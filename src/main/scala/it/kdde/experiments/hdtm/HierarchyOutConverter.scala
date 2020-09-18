package it.kdde.experiments.hdtm

import java.io.{File, BufferedWriter, FileWriter}

import scala.io.Source

/**
  * Created by fabiana on 3/1/16.
  */
object HierarchyOutConverter {
  def main(args: Array[String]): Unit = {
    args match {
      case Array(hierarchyOut: String, urlsMap: String) =>

        val map = Source.fromFile(urlsMap).getLines().map{l =>
          val tokens = l.split(" , ")
          (tokens(1), tokens(0) )
        }.toMap

        val newLines = Source.fromFile(hierarchyOut).getLines().flatMap{ l =>
          val tokens = l.split("\t")

          val newTokens =  (map.isDefinedAt(tokens(0)), map.isDefinedAt(tokens(2))) match {
            case (true, true) =>
              Some((map.get(tokens(0)).get, tokens(1), map.get(tokens(2)).get))
            case _ => throw new RuntimeException(s"url ${tokens(0)} or ${tokens(2)} is not present")

          }
          newTokens match {
            case Some((src, depth ,dest)) => Some(s"$src\t$depth\t$dest\n")
            //case None => None
          }

        }
        val output = hierarchyOut.replace(".out","") + "Converted.out"
        val file = new File(output)
        val bw = new BufferedWriter(new FileWriter(file))

        newLines.foreach(bw.write(_))
        bw.close()


      case _ => println("Insert file path of HDTM hierarchy.out and urlsMap.txt files")
    }
  }
}
