import org.apache.spark.graphx.GraphLoader
import org.apache.spark.sql.SparkSession

object PageRank {
  def main(args: Array[String]): Unit = {
  val spark = SparkSession
      .builder
      .appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()
    val sc = spark.sparkContext
    
    //The previous java implementation of pagerank has been taken and a new file has been created which has the format of url neighbor-url
    val graph = GraphLoader.edgeListFile(sc, "/user/hadoopuser/newoutput")
    val ranks = graph.pageRank(0.0001).vertices
    println(ranks.collect().mkString("\n"))
  
  
    //Take only the first 100 pages
    val outArray = spark.sparkContext.parallelize(output).take(100)
    val outRDD = spark.sparkContext.parallelize(outArray)
    
    //Write them to output path
    outRDD.saveAsTextFile(outputFilePath)
  
    spark.stop()
  }
}
