package cn.mxy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class main {
    public static void main(String[] args) throws Exception {
        //创建Hadoop配置对象
        Configuration Hbaseconf = HBaseConfiguration.create();
        // 指定Zookeeper集群地址
        Hbaseconf.set("hbase.zookeeper.quorum", "192.168.0.22:2181,192.168.0.33:2181,192.168.0.44:2181");
        //创建Hbase连接对象Connection
        Connection conn = ConnectionFactory.createConnection(Hbaseconf);
        //创建表名对象
        TableName tableName = TableName.valueOf("TFValue");
        //打开HBase连接并创建表对象
        Table table = conn.getTable(tableName);

        //通过rowkey创建get对象，用于写入
        Get get = new Get(Bytes.toBytes("@"));
        get.setCheckExistenceOnly(true);
        Result result = table.get(get);//执行数据读取并返回结果对象

        System.out.println(result.getExists());;

    }
}
