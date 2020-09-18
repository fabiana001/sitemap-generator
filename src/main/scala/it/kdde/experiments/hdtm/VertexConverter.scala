package it.kdde.experiments.hdtm

import java.io.{BufferedWriter, File, FileWriter}

import scala.io.Source

/**
  * Created by fabiana on 1/17/16.
  * Used to convert vertex file
  */
object VertexConverter {

  def run(pathFile: String, outputPath: String): Unit = {

    // val output = pathFile.replaceAll(".txt", "Converted.txt")
    val file = new File(outputPath)
    val bw = new BufferedWriter(new FileWriter(file))

    var map = Map.empty[String, Int]
    var lastId = 2

    val words = for (line <- Source.fromFile(pathFile).getLines()) yield (line.split("\t")(0), line.split("\t")(1).split(" ").filterNot(t => t=="").toList)
    words.foreach {
      case (id, tokens) =>
        val ids = tokens.map{ t =>
          map.isDefinedAt(t) match {
            case true => map.get(t).get
            case false =>
              lastId += 1
              map = map + (t -> lastId)
              lastId
          }

        }
        bw.write(id + "\t" + ids.mkString(" ") + "\n")
    }
    bw.close()

    //write map
    val output2 = pathFile.replaceAll(".txt", "TokensMap.txt")
    val fileMap = new File(output2)
    val bwMap = new BufferedWriter(new FileWriter(fileMap))
    map.keys.foreach( k => bwMap.write(k + "," + map.get(k).get + "\n")
    )
    bwMap.close()
  }


  def main(args: Array[String]): Unit = {
    args match {
      case Array(filePath: String) =>
        val output = filePath.replaceAll(".txt", "Converted.txt")
        run(filePath, output)
        println("File converted with success" )
//      case Array() =>
//        val filePath = "/home/fabiana/git/khachaturian/experiments_Khachaturian/illinois/vertex.txt"
//        val output = filePath.replaceAll(".txt", "Converted.txt")
//        run(filePath, output)
//        println("File converted with success" )
      case _ => println("Insert file to convert")
    }
  }
}
