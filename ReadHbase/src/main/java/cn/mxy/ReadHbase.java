package cn.mxy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class ReadHbase extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new ReadHbase(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////
        // 创建作业，配置作业所需参数
        Configuration conf = HBaseConfiguration.create();
        // 程序中必须指定jar包的本地路径
        //conf.set("mapreduce.job.jar", "D:\\专业课程\\毕业设计\\ReadHbase\\out\\artifacts\\ReadHbase\\ReadHbase.jar");
        // 指定Zookeeper集群地址
        conf.set("hbase.zookeeper.quorum", "192.168.0.22:2181,192.168.0.33:2181,192.168.0.44:2181");
        System.setProperty("HADOOP_USER_NAME", "root");

        // 创建作业
        Job job = Job.getInstance(conf, "ReadHbase");
        String outPath = "/SearchEngine/extractedHtml";
        System.out.println("outPath=====" + outPath);

        // 注入作业的主类，远程提交不需要
        job.setJarByClass(ReadHbase.class);

        //注入Map类
        Scan scan = new Scan();
        scan.setCacheBlocks(false);
        TableMapReduceUtil.initTableMapperJob(TableName.valueOf("Pages"), scan,
                SearchMap.class, Text.class, Text.class,
                job);

        //注入Reduce类
        job.setReducerClass(WriteReduce.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(outPath)); //HDFS路径


        ////////////////////////////////////////////////////////////
        // 作业的执行流程
        // 执行MapReduce
        boolean res = job.waitForCompletion(true);
        if(res)
            return 0;
        else
            return -1;
    }

    public static class SearchMap extends TableMapper<Text, Text> {
        private Text url = new Text();
        private Text information = new Text();
        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context)
                throws IOException, InterruptedException {

            String title ="" , time = "", summaryRaw = "";
            //获取网页url
            String rowkey = Bytes.toString(key.get());
            url.set(rowkey);
            //System.out.println(rowkey);
            //获取标题、时间、正文
            for (Cell cell : value.rawCells()) {
                if ("Data".equals(Bytes.toString(CellUtil.cloneFamily(cell))))
                {
                    if ("title".equals(Bytes.toString(CellUtil     //column titel
                            .cloneQualifier(cell)))) {
                        title = Bytes.toString(CellUtil.cloneValue(cell));
                    }

                    if ("time".equals(Bytes.toString(CellUtil       //column time
                            .cloneQualifier(cell)))) {
                        time = Bytes.toString(CellUtil.cloneValue(cell));
                    }
                    if ("summary".equals(Bytes.toString(CellUtil      //column summary
                            .cloneQualifier(cell)))) {
                        summaryRaw = Bytes.toString(CellUtil.cloneValue(cell));
                    }
                }

            }

            //合成一条 url+time+title+summary
            String info = time + "\t" + title + "\t" + summaryRaw;
            System.out.println("Map的info"+info);
            information.set(info);
            context.write(url, information);
        }
    }

    public static class WriteReduce extends Reducer<Text, Text, NullWritable,Text> {
        private Text information = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text value : values) {
                String info = key.toString() + "\t" + value.toString();
                System.out.println("Reduce的value："+value.toString());
                System.out.println("Reduce的info："+info);
                information.set(info);
                context.write(NullWritable.get(), information);
            }
        }
    }

}
