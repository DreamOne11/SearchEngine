package cn.mxy.dao;

import cn.mxy.pojo.ResultBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HbaseDao {

    /**
     * Hbase连接方法：Hbaseconnect()
     * 返回Connection对象
     */
    private Connection Hbaseconnect() throws IOException {
        //创建Hadoop配置对象
        Configuration Hbaseconf = HBaseConfiguration.create();
        // 指定Zookeeper集群地址
        Hbaseconf.set("hbase.zookeeper.quorum", "192.168.0.22:2181,192.168.0.33:2181,192.168.0.44:2181");
        //创建Hbase连接对象Connection
        return ConnectionFactory.createConnection(Hbaseconf);
    }

    /**
     * 获取一共有多少网页：getWholePagesCount()
     * 连接Hbase表：TFValue
     * 返回总页面数
     * */
    public Double getWholePagesCount() throws IOException {
        Double wholepagescount = null;
        //创建表名对象
        TableName tableName = TableName.valueOf("TFValue");
        //打开HBase连接并创建表对象
        Table table = Hbaseconnect().getTable(tableName);

        //定义rowkey
        byte[] rowkey = Bytes.toBytes("finalCount");
        //定义列族
        byte[] cf = Bytes.toBytes("Urls");
        //定义列限定符
        byte[] qf = Bytes.toBytes("finalPagesCount");

        //通过rowkey创建get对象，用于获取总页面数
        Get get = new Get(rowkey);
        Result result = table.get(get);//执行数据读取并返回结果对象
        byte[] val = result.getValue(cf, qf);
        wholepagescount = Bytes.toDouble(val);

        //关闭HBase连接
        table.close();
        return wholepagescount;
    }

    /**
     * 获取关键词被多少页面包含：getwordPagesCount
     * 连接Hbase表：TFValue
     * param:分词后的关键词表
     * 返回<关键词，包含该词的页面数>的Map
     * */
    public Map<String, Integer> getWordPagesCount(ArrayList<String> keywords) throws IOException {
        //实例化Map对象，用于保存每个keyword的返回值PagesCount
        Map<String, Integer> wordPagesCount = new HashMap<>();
        //创建表名对象
        TableName tableName = TableName.valueOf("TFValue");
        //打开HBase连接并创建表对象
        Table table = Hbaseconnect().getTable(tableName);
        //定义列族
        byte[] cf = Bytes.toBytes("Urls");
        //定义列限定符
        byte[] qf = Bytes.toBytes("finalPagesCount");

        for(String rowkey:keywords){
            //通过rowkey创建get对象，用于搜索
            Get get = new Get(Bytes.toBytes(rowkey));
            get.addColumn(Bytes.toBytes("Urls"), Bytes.toBytes("finalPagesCount"));  //指定get搜索列
            get.setCheckExistenceOnly(true);
            Result result = table.get(get);//执行数据读取并返回结果对象

            //通过rowkey创建get对象，用于写入
            Get get1 = new Get(Bytes.toBytes(rowkey));
            get1.setCheckExistenceOnly(true);
            Result result1 = table.get(get1);//执行数据读取并返回结果对象

            //判断关键词是否存在于TFValue表中
            if(result1.getExists()) {
                //判断该词的包含页数是否存在
                if(result.getExists()){
                    System.out.println("数据存在！ " + result.getExists());
                    //若存在则获取，赋给返回值(键值对)
                    byte[] val = result1.getValue(cf, qf);
                    wordPagesCount.put(rowkey, Bytes.toInt(val));
                    //System.out.println("数据结果为：" +  wordPagesCount);
                } else {
                    //若不存在，计算wordPagesCount并直接将其结果赋值给返回值
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
                    //赋给返回值(键值对)
                    wordPagesCount.put(rowkey, wordpagescount);
                }
            } else {
                //如果关键词不在倒排索引表中，则设为包含页面数为1，避免分母为0
                wordPagesCount.put(rowkey, 1);
            }
        }
        //关闭HBase连接
        table.close();
        return wordPagesCount;
    }

    /**
     * 按关键词获取对应所有的页面的TFValue
     * 连接Hbase表：TFValue
     * param:分词后的单个关键词
     * 返回<url，TFValue>的Map
     * */
    public Map<String, Double> getTFValue(String rowKey) throws IOException {
        //实例化Map对象，用于保存keyword在每个Url中的TFValue
        Map<String, Double> TFvalue = new HashMap<>();
        //定义rowkey
        byte[] rowkey = Bytes.toBytes(rowKey);

        //创建表名对象
        TableName tableName = TableName.valueOf("TFValue");
        //打开HBase连接并创建表对象
        Table table = Hbaseconnect().getTable(tableName);

        //通过rowkey创建get对象，用于获取总页面数
        Get get = new Get(rowkey);
        get.setCheckExistenceOnly(true);
        Result result = table.get(get);//执行数据读取并返回结果对象
        if (result.getExists()) {
            Cell[] cells  = result.rawCells();   //从结果对象中获取所有cell值
            //遍历result中所有cell值
            for(Cell cell:cells) {
                //获取cell的键值
                byte[] cellValue = cell.getValueArray();
                //获取cell键中的url值、TFValue
                String column = Bytes.toString(cellValue, cell.getQualifierOffset(), cell.getQualifierLength());
                //获取cell中的cell值
                String value = Bytes.toString(cellValue, cell.getValueOffset(), cell.getValueLength());
                TFvalue.put(column, Double.parseDouble(value));
            }
        } else {
            //要是关键词(rowkey)不在表中，则返回空表
            return TFvalue;
        }

        table.close();   //关闭Table对象并断开Hbase连接
        return TFvalue;
    }

    /**
     * 按url获取该网页的ResultBean
     * 连接Hbase表：Pages
     * param:按TFValue从大到小的顺序排列的url列表
     * 返回List<ResultBean>
     * */
    //todo 测试List<Bean> 能否实现一个实例化的多个循环添加
    public List<ResultBean> getPagesData(ArrayList<String> urls) throws IOException {
        List<ResultBean> resultBeanList = new ArrayList<>();
        //创建表名对象
        TableName tableName = TableName.valueOf("Pages");
        //打开HBase连接并创建表对象
        Table table = Hbaseconnect().getTable(tableName);
        for(String rowkey:urls) {
            ResultBean resultBean = new ResultBean();
            resultBean.setUrl(rowkey);
            //通过rowkey创建get对象，用于搜索
            Get get = new Get(Bytes.toBytes(rowkey));
            Result result = table.get(get);
            //如何取？
            Cell[] cells  = result.rawCells();   //从结果对象中获取所有cell值
            //遍历result中所有cell值
            for(Cell cell : cells) {
                //获取cell的键值
                byte[] cellValue = cell.getValueArray();
                //获取cell键中的rowkey对应的列限定符和值
                String column = Bytes.toString(cellValue, cell.getQualifierOffset(), cell.getQualifierLength());

                if(column.equals("title")) {
                    String title = Bytes.toString(cellValue, cell.getValueOffset(), cell.getValueLength());
                    resultBean.setTitle(title);
                } else if(column.equals("time")) {
                    String time = Bytes.toString(cellValue, cell.getValueOffset(), cell.getValueLength());
                    resultBean.setTime(time);
                } else {
                    //获取content
                    String content = Bytes.toString(cellValue, cell.getValueOffset(), cell.getValueLength());
                    resultBean.setMainContent(content);
                }
            }

            resultBeanList.add(resultBean);
            table.close();   //关闭Table对象并断开Hbase连接
        }
        return resultBeanList;
    }
}
