package cn.mxy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
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
        TableName tableName = TableName.valueOf("TFValue1");
        //打开HBase连接并创建表对象
        Table table = conn.getTable(tableName);

        //列族
        byte[] cf = Bytes.toBytes("Urls");
        //列限定符
        byte[] qf = Bytes.toBytes("https://www.swpu.edu.cn/cly/kxyj/kycg/3.htm");

        //通过rowkey创建get对象，用于写入
        Get get = new Get(Bytes.toBytes("石油大学"));
        Result result = table.get(get);//执行数据读取并返回结果对象
        byte[] val = result.getValue(cf, qf);
        System.out.println(Bytes.toString(val));
        /*Cell[] cells  = result.rawCells();
        for (Cell cell : cells) {
            //获取cell的键值
            byte[] cellValue = cell.getValueArray();
            //获取cell键中的rowkey值
            String row = Bytes.toString(cellValue, cell.getRowOffset(), cell.getRowLength());
            System.out.println(row);
            //获取cell中的cell值
            int value = Bytes.toInt(cellValue, cell.getValueOffset(), cell.getValueLength());
            System.out.println(value);
        }*/

    }
}
