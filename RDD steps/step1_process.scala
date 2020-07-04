import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.log4j.{ Level, Logger }
import au.com.bytecode.opencsv.CSVReader

object process {
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mapDemo").setMaster("local[2]");
    val sc = new SparkContext(conf);
    val inputFile = "file:///e:/homework/sample_user_log.csv"
    val data = sc.textFile(inputFile)

    // 1、查看日志前 10 个交易日志的商品品牌
    var mapresult = data.map(line => (line, line.split(",")(5).toFloat))
    mapresult.collect
    mapresult.take(10).foreach(t => println(t._2))

    //2、查询前 20 个交易日志中购买商品时的时间和商品的种类
    var time_and_cat = data.map(line => (line.split(",")(5) + "," + line.split(",")(6), line.split(",")(2)))
    time_and_cat.collect
    time_and_cat.take(20).foreach(x => println(x._1.split(",")(0) + "月" + x._1.split(",")(1) + "日" + " 商品种类：" + x._2))

    // 3、查询双 11 那天有多少人购买了商品
    /// 筛选双十一数据  
    var data_11 = data.filter(x => x.split(",")(5) == "11" & x.split(",")(6) == "11")
    // 去重当天的买家id 并统计个数
    data_11.map(x => x.split(",")(0)).distinct.count

    //  4、取给定时间和给定品牌，求当天购买的此品牌商品的数量
    //这里指定的时间是：7月9日；品牌是：7622；2表示的是购买  
    data.filter(x => x.split(",")(4) == "7622" & x.split(",")(5) == "7" & x.split(",")(6) == "9" & x.split(",")(7) == "2").distinct.count

    //5、查询有多少用户在双 11 购买了商品 与 3是一样的

    //6、查询有多少用户在双 11 点击了该店
    //指定时间是双十一，指定action是购买即用2表示，指定性别是女性即用0表示
    data.filter(x => x.split(",")(5) == "11" & x.split(",")(6) == "11" & x.split(",")(7) == "2" & x.split(",")(9) == "0").count

    //7、查询双 11 那天女性购买商品的数量
    //指定时间是双十一，指定action是购买即用2表示，指定性别是女性即用0表示
    data.filter(x => x.split(",")(5) == "11" & x.split(",")(6) == "11" & x.split(",")(7) == "2" & x.split(",")(9) == "1").count

    //8、查询双 11 那天男性购买商品的数量
    //指定时间是双十一，指定action是购买即用2表示，指定性别是女性即用0表示
    data.filter(x => x.split(",")(5) == "11" & x.split(",")(6) == "11" & x.split(",")(7) == "2" & x.split(",")(9) == "1").count

    //9、查询某一天在该网站购买商品超过 5 次的用户 id
    //首先筛选出购买的数据，然后用map将"时间;user_id"的形式作为key，value是1；通过以wordcount的思想进行key的统计    
    var tmp = data.filter(x => x.split(",")(7) == "2").map(x => (x.split(",")(5) + "月" + x.split(",")(6) + "日;" + x.split(",")(0), 1)).reduceByKey(_ + _)
    //统计结果，从结果可以看出还需要筛选出购买超过5的数据    
    tmp.collect
    //筛选[K,V]中V超过5的数据    
    var res = tmp.filter(x => x._2 > 5)
    //最终结果 ：日期，user_id,购买次数  
    res.collect
    res.foreach(println)
    //仅打印用户id  
    res.foreach(x => println(x._1.split(";")(1)))
    sc.stop();
  }
}
