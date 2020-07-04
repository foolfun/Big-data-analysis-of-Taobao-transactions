import java.sql.{ Connection, Driver, DriverManager, ResultSet, PreparedStatement }
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.log4j.{ Level, Logger }
import au.com.bytecode.opencsv.CSVReader

object add_to_mysql {
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  def main(args: Array[String]) {
    //定义SparkContext对象
    val conf = new SparkConf().setAppName("add_data_mysql").setMaster("local");
    //创建SparkContext对象
    val sc = new SparkContext(conf)
    //读取数据
    val inputFile = "file:///e:/homework/sample_user_log.csv"
    val data = sc.textFile(inputFile)
    var res = data.map(x => x.split(",")).map(x => (x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8), x(9), x(10)))
    
    res.foreachPartition(
      it => {
        //连接数据库
        val url = "jdbc:mysql://localhost:3306/dbtaobao?serverTimezone=UTC&characterEncoding=utf8"
        val conn = DriverManager.getConnection(url, "root", "zsl123")
        var InsertSql = "INSERT IGNORE INTO `user_log` (`user_id`, `item_id`, `cat_id`, `merchant_id`, `brand_id`, `month_`, `day_`, `action`, `age_range`, `gender`, `province`) VALUES (?, ?,?,?, ?,?,?, ?,?,?,?)"
        var pstat = conn.prepareStatement(InsertSql)
        //写入数据库
        for (obj <- it) {
          pstat.setString(1, obj._1)
          pstat.setString(2, obj._2)
          pstat.setString(3, obj._3)
          pstat.setString(4, obj._4)
          pstat.setString(5, obj._5)
          pstat.setString(6, obj._6)
          pstat.setString(7, obj._7)
          pstat.setString(8, obj._8)
          pstat.setString(9, obj._9)
          pstat.setString(10, obj._10)
          pstat.setString(11, obj._11)
          pstat.addBatch
        }
        try {
          pstat.executeBatch
        } finally {
          pstat.close
          conn.close
        }
      })
    sc.stop();
  }
}