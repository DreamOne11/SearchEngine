package cn.mxy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Arrays;

public class EachWordPages {
    public static void main(String[] args) throws Exception {
        String wordPagesCount = null;
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

        //列族
        byte[] cf = Bytes.toBytes("Urls");
        //列限定符
        byte[] qf = Bytes.toBytes("finalPagesCount");

        Get get = new Get(Bytes.toBytes("year"));//通过rowkey创建get对象，用于搜索
        get.addColumn(cf, qf);  //指定get搜索列
        get.setCheckExistenceOnly(true);
        Result result = table.get(get);//执行数据读取并返回结果对象

        Get get1 = new Get(Bytes.toBytes("year"));//通过rowkey创建get对象，用于写入
        Result result1 = table.get(get1);//执行数据读取并返回结果对象

        //判断数据是否存在
        if(result.getExists()){
            System.out.println("数据存在！ " + result.getExists());
            //若存在则获取，赋给返回值
            byte[] val = result1.getValue(cf, qf);
            wordPagesCount = Bytes.toString(val);
            System.out.println("数据结果为：" +  wordPagesCount);
        } else {
            System.out.println("数据不存在！ " + result.getExists());
            Cell[] cells  = result1.rawCells();   //从结果对象中获取所有cell值
            //word in pages计数
            int wordpagescount = 0;
            //遍历result中所有cell值
            for(Cell cell : cells) {
                //该词出现在多少个页面中计数
                wordpagescount++;
            }
            //根据rowkey创建一个put对象
            Put put=new Put(Bytes.toBytes("year"));
            //在put对象中的指定列中写入cell值
            put.addColumn(Bytes.toBytes("Urls"), Bytes.toBytes("finalPagesCount"), Bytes.toBytes(Integer.toString(wordpagescount)));
            //执行写入
            table.put(put);
            System.out.println("数据写入Hbase!");
        }
        //关闭HBase连接
        table.close();
    }
}
