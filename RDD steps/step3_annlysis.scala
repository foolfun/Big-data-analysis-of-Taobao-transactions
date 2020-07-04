import org.apache.spark.{ SparkContext, SparkConf }
import java.io._
import org.apache.log4j.{ Level, Logger }
import au.com.bytecode.opencsv.CSVReader
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

object annlysis {
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mapDemo").setMaster("local[2]");
    val sc = new SparkContext(conf);
//    val inputFile = "file:///e:/homework/user_log.csv"
     val inputFile = "file:///e:/homework/sample_user_log.csv"
    val data = sc.textFile(inputFile).distinct()
    val map_data = data.map(x => x.split(","))

    //1、数据库双 11 所有买家消费行为比例
    val res1 = map_data.filter(x => x(5) == "11" & x(6) == "11").map(x => (x(7), 1)).reduceByKey(_ + _)
//    res1.foreach(println)
    var writer = new PrintWriter(new File("E:/homework/total_reslut.json"))
    var jsonarr1: JSONArray = new JSONArray(); //创建“数组”
    var jsonroot: JSONObject = new JSONObject();
    res1.collect().foreach(
      line => {
        //       print(line._1)
        var jsonobj1: JSONObject = new JSONObject()
        jsonobj1.put("action", line._1)
        jsonobj1.put("num", line._2)
        jsonarr1.add(jsonobj1)

      })
    jsonroot.put("value1", jsonarr1)
    

    //2、男女买家交易对比
    //   x(9): 0女,1男，{0,1,2,3},x(7):0表示点击，1表示加入购物车，2表示购买，3表示关注商品
    val res2_1 = map_data.filter(x => x(7) == "2").map(x => (x(9), 1)).reduceByKey(_ + _)
    //  扩展
    val res2_2 = map_data.map(x => ((x(9), x(7)), 1)).reduceByKey(_ + _).sortByKey()
    var jsonarr2: JSONArray = new JSONArray(); //创建“数组”
    res2_2.collect().foreach(
      line => {
        //       print(line._1)
        var jsonobj2: JSONObject = new JSONObject()
        jsonobj2.put("gender", line._1._1)
        jsonobj2.put("action", line._1._2)
        jsonobj2.put("num", line._2)
        jsonarr2.add(jsonobj2)

      })
    jsonroot.put("value2", jsonarr2)


    //3、x(9)男女买家各个x(8)年龄段交易对比
    val res3_1 = map_data.filter(x => x(7) == "2").map(x => ((x(9), x(8)), 1)).reduceByKey(_ + _).sortByKey()
    //  扩展
    val res3_2 = map_data.map(x => ((x(9), x(8), x(7)), 1)).reduceByKey(_ + _).sortByKey()
    var jsonarr3_2: JSONArray = new JSONArray(); //创建“数组”
    res3_2.collect().foreach(
      line => {
        //       print(line._1)
        var jsonobj3_2: JSONObject = new JSONObject()
        jsonobj3_2.put("gender", line._1._1)
        jsonobj3_2.put("age_range", line._1._2)
         jsonobj3_2.put("action", line._1._3)
        jsonobj3_2.put("num", line._2)
        jsonarr3_2.add(jsonobj3_2)

      })
    jsonroot.put("value32", jsonarr3_2)
    
        var jsonarr3: JSONArray = new JSONArray(); //创建“数组”
    res3_1.collect().foreach(
      line => {
        //       print(line._1)
        var jsonobj3: JSONObject = new JSONObject()
        jsonobj3.put("gender", line._1._1)
        jsonobj3.put("age_range", line._1._2)
        jsonobj3.put("num", line._2)
        jsonarr3.add(jsonobj3)

      })
    jsonroot.put("value3", jsonarr3)


    //4、获取销量前五的商品类别
    val res4 = map_data.filter(x => x(7) == "2").map(x => (x(2), 1)).reduceByKey(_ + _).sortBy(x => x._2, false).take(5)
    var jsonarr4: JSONArray = new JSONArray(); //创建“数组”
    res4.foreach(
      line => {
        //       print(line._1)
        var jsonobj4: JSONObject = new JSONObject()
        jsonobj4.put("cat_id", line._1)
        jsonobj4.put("sale_num", line._2)
        jsonarr4.add(jsonobj4)

      })
    jsonroot.put("value4", jsonarr4)
//print(jsonroot.toString)

    //5、各个省份的总成交量对比
    val res5 = map_data.filter(x => x(7) == "2").map(x => (x(10), 1)).reduceByKey(_ + _).sortBy(x => x._2)
    var jsonarr5: JSONArray = new JSONArray(); //创建“数组”
    res5.collect().foreach(
      line => {
        //       print(line._1)
        var jsonobj5: JSONObject = new JSONObject()
        jsonobj5.put("province", line._1)
        jsonobj5.put("sale_num", line._2)
        jsonarr5.add(jsonobj5)

      })
    jsonroot.put("value5", jsonarr5)
//    print(jsonroot.toString)


    //6、其他统计
//    //6.1 总销售额前3的卖家双十一前7日（包括双十一，即：11.5-11.11）销售额的变化情况[卖家，时间，销售额];(1751,35), (3073,15), (4255,11), (4444,10), (3828,9)
//    var top_mer = map_data.filter(x => x(7) == "2").map(x => (x(3), 1)).reduceByKey(_ + _).sortBy(x => x._2, false).take(3)
//   top_mer.foreach(it=>{
////      println(it)
//      var res6 = map_data.filter(x => x(5) == "11" & x(6) > "4" & x(3) == it._1).map(x => ((x(3), (x(5), x(6))), 1)).reduceByKey(_ + _).sortBy(x => x._1._2._2)
//      res6.foreach(println)
//    })
//       var jsonarr6: JSONArray = new JSONArray(); //创建“数组”
//    res6.collect().foreach(
//      line => {
//        //       print(line._1)
//        var jsonobj6: JSONObject = new JSONObject()
//        jsonobj6.put("province", line._1)
//        jsonobj6.put("sale_num", line._2)
//        jsonarr6.add(jsonobj6)
//
//      })
//    jsonroot.put("value6", jsonarr6)

    //6.2 对于买家：双十一的购买行为占消费行为的比例的分布情况。例如0-20%：42人； (42,7,6,3,4)
    var data_11 = map_data.filter(x => x(5) == "11" & x(6) == "11")
    var sum_act = sc.broadcast(data_11.map(x => (x(0), 1)).reduceByKey(_ + _).collect()) //每个买家的总消费行为数统计
    var cnt1, cnt2, cnt3, cnt4 = 0
    for (it <- sum_act.value) {
      var tmp = data_11.filter(x => x(0) == it._1 & x(7) == "2")
      var ra = tmp.count() * 100 / it._2
      if (ra <= 25) { cnt1 += 1 }
      if (25 < ra & ra <= 50) { cnt2 += 1 }
      if (50 < ra & ra <= 75) { cnt3 += 1 }
      if (75 < ra & ra <= 100) { cnt4 += 1 }
    }
    print("对于买家：双十一的购买行为占消费行为的比例的分布情况：")
    println("小于等于25%:" + cnt1, "25%~50%:" + cnt2, "50%~75%:" + cnt3, "75%~100%:" + cnt4)
     var jsonarr61: JSONArray = new JSONArray(); //创建“数组”
   
      var c1: JSONObject = new JSONObject()
      c1.put("num", cnt1)
      jsonarr61.add(c1)
      var c2: JSONObject = new JSONObject()
      c2.put("num", cnt2)
      jsonarr61.add(c2)
       var c3: JSONObject = new JSONObject()
      c3.put("num", cnt3)
      jsonarr61.add(c3)
       var c4: JSONObject = new JSONObject()
      c4.put("num", cnt4)
      jsonarr61.add(c4)

    jsonroot.put("value61", jsonarr61)

//    //6.3 双十一购买行为占比超过消费行为总数的50%的买家[买家id，总消费行为数]->[买家id,购买行为占比]
//    for (it <- sum_act.value) {
//      var tmp = data_11.filter(x => x(0) == it._1 & x(7) == "2")
//      var rate = tmp.count() * 100 / it._2
//      if (rate > 80) {
//        var res6_3 = tmp.map(x => (x(0), rate))
//        res6_3.foreach(println)
//      }
//    }

    //6.4 对于卖家：双十一销售量占总销售量比例的分布情况
    var sum_act_m = sc.broadcast(map_data.filter(x => x(7) == "2").map(x => (x(3), 1)).reduceByKey(_ + _).collect()) //每个店铺的总销量统计
    var mcnt1, mcnt2, mcnt3, mcnt4 = 0
    for (it <- sum_act_m.value) {
      var tmp = data_11.filter(x => x(3) == it._1 & x(7) == "2") //每个店铺的双十一销量统计
      var rat = tmp.count() * 100 / it._2
      //       println(it._1,tmp.count(),rat)
      if (rat <= 25) { mcnt1 += 1 }
      if (25 < rat & rat <= 50) { mcnt2 += 1 }
      if (50 < rat & rat <= 75) { mcnt3 += 1 }
      if (75 < rat & rat <= 100) { mcnt4 += 1 }

    }
    print("对于卖家：双十一销售量占总销售量比例的分布情况：")
    //    小于等于25%:338,25%~50%:0,50%~75%:0,75%~100%:0
    println("小于等于25%:" + mcnt1, "25%~50%:" + mcnt2, "50%~75%:" + mcnt3, "75%~100%:" + mcnt4)
         var jsonarr62: JSONArray = new JSONArray(); //创建“数组”
   
      var m1: JSONObject = new JSONObject()
      m1.put("num", mcnt1)
      jsonarr62.add(m1)
      var m2: JSONObject = new JSONObject()
      m2.put("num", mcnt2)
      jsonarr62.add(m2)
       var m3: JSONObject = new JSONObject()
      m3.put("num", mcnt3)
      jsonarr62.add(m3)
       var m4: JSONObject = new JSONObject()
      m4.put("num", mcnt4)
      jsonarr62.add(m4)

    jsonroot.put("value62", jsonarr62)

//    //6.5 各个省份的双十一销量前3的商品类别[省份,(商品类别1，商品类别2，商品类别3)]
//    var res6_5 = data_11.filter(x => x(7) == "2").map(x => ((x(10), x(2)), 1)).
//      reduceByKey(_ + _).map(x => (x._1._1, x._1._2, x._2)).groupBy(x => x._1).sortBy(x => for (it <- x._2) { it._3 }, false)
//    //    结果大部分都是1，就不做这个统计了
//    res6_5.foreach(println)
    writer.write(jsonroot.toString)
    writer.close()
  }

}

