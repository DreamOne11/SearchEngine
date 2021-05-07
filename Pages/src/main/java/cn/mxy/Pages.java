package cn.mxy;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class Pages extends Configured implements Tool {
    public static void main(String[] args) throws Exception {

        int res = ToolRunner.run(new Pages(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////
        // 创建作业，配置作业所需参数
        Configuration conf = HBaseConfiguration.create();
        // 程序中必须指定jar包的本地路
        conf.set("mapreduce.job.jar", "D:\\专业课程\\毕业设计\\Pages\\out\\artifacts\\Pages\\Pages.jar");
        // 指定Zookeeper集群地址
        conf.set("hbase.zookeeper.quorum", "192.168.0.22:2181,192.168.0.33:2181,192.168.0.44:2181");
        System.setProperty("HADOOP_USER_NAME", "root");


        // 创建作业
        Job job = Job.getInstance(conf, "Pages");
        String inPath = "/SearchEngine/extractedpages/extractedHtml.txt", outPath = "/SearchEngine/output";
        System.out.println("args1=====" + inPath);
        System.out.println("args2=====" + outPath);


        // 注入作业的主类，远程提交不需要
        //job.setJarByClass(Pages.class);

        // 为作业注入Map类
        job.setMapperClass(ReadHDFSMap.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(Put.class);

        //其中自动注入Reduce类和Partition类
        TableMapReduceUtil.initTableReducerJob(
                "Pages",      // output table
                WriteToHbaseReduce.class,        // reducer class
                job,
                null,     // partition class
                null,
                null,
                null,
                false);

        //设置reduce任务数为1
        job.setNumReduceTasks(1);

        // 指定输入类型为：文本格式文件；注入文本输入格式类
        //job.setInputFormatClass(FileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(inPath));     //HDFS路径

        ////////////////////////////////////////////////////////////
        // 作业的执行流程
        // 执行MapReduce
        boolean res = job.waitForCompletion(true);
        if (res)
            return 0;
        else
            return -1;
    }

    //Map过程
    public static class ReadHDFSMap extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {
        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            ////将输入纯文本转为String
            String line = value.toString();
            //对每一行按“\t”进行切分
            String[] splits = line.split("\t");

            //获取url、时间、标题、摘要
            String url = splits[0].trim();
            String timeRaw = splits[1];
            String title = splits[2];
            String summaryRaw = splits[3].trim();
            //过滤《附件下载》页面
            if(title.equals("附件下载")){
                return;
            } else {
                //url作为rowkey
                ImmutableBytesWritable rowKey = new ImmutableBytesWritable(Bytes.toBytes(url));
                //初始化put对象
                Put put=new Put(Bytes.toBytes(url));
                //存入标题
                put.addColumn(Bytes.toBytes("Data"), Bytes.toBytes("title"), Bytes.toBytes(title));
                //存入正文
                put.addColumn(Bytes.toBytes("Data"), Bytes.toBytes("summary"), Bytes.toBytes(summaryRaw));

                //对原生时间进行处理，判断时间是否非null，并且在正常时间区间内，否则爬取其网页的Last-Modified
                try {
                    if(isRightTime(timeRaw)) {
                        put.addColumn(Bytes.toBytes("Data"), Bytes.toBytes("time"), Bytes.toBytes(timeRaw));
                        System.out.println("时间在范围区间内 " + timeRaw);
                    } else {
                        String gottenTime = "2020-11-15";
                        put.addColumn(Bytes.toBytes("Data"), Bytes.toBytes("time"), Bytes.toBytes(gottenTime));
                        System.out.println(url + " 的Last-Modified：" + gottenTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //map结果写入上下文
                context.write(rowKey, put);
            }
        }

        //判断时间是否正确，符合规则
        private static boolean isRightTime(String timeRaw) throws Exception {
            if(timeRaw.equals("null")) {
                //如果没有原本时间，则去爬取网页的last-modified时间
                try {
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String beginTime = "2005-01-01";
                String endTime = "2021-06-01";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date beginDate = sdf.parse(beginTime);
                Date endDate = sdf.parse(endTime);
                Date urlDate ;

                //如果时间格式中包含分秒
                if(timeRaw.contains(":")) {
                    String[] splits = timeRaw.split(" ");
                    //获取年月日
                    String[] ymd = splits[0].split("-");
                    //判断月份是否在1-12月之间
                    if (Integer.parseInt(ymd[1]) >= 1 && Integer.parseInt(ymd[1]) <= 12) {
                        //判断日期是否在1-31日之内
                        if(Integer.parseInt(ymd[2]) >= 1 && Integer.parseInt(ymd[2]) <= 31) {
                            urlDate = sdf.parse(timeRaw);
                            if(urlDate.after(beginDate)&&urlDate.before(endDate)) {
                                return true;
                            } else {
                                //规定时间范围外，爬取
                                try {
                                    return false;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            //规定时间范围外，爬取
                            try {
                                return false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //规定时间范围外，爬取
                        try {
                            return false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    String[] splits = timeRaw.split("-");
                    if (Integer.parseInt(splits[1]) >= 1 && Integer.parseInt(splits[1]) <= 12) {
                        //判断日期是否在1-31日之内
                        if(Integer.parseInt(splits[2]) >= 1 && Integer.parseInt(splits[2]) <= 31) {
                            urlDate = sdf.parse(timeRaw);
                            if(urlDate.after(beginDate)&&urlDate.before(endDate)) {
                                return true;
                            } else {
                                //规定时间范围外，爬取
                                try {
                                    return false;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            //规定时间范围外，爬取
                            try {
                                return false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //规定时间范围外，爬取
                        try {
                            return false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;
        }

    }

    public static class WriteToHbaseReduce extends TableReducer<ImmutableBytesWritable, Put, NullWritable> {
        @Override
        protected void reduce(ImmutableBytesWritable key,Iterable<Put> values,Context context) throws IOException, InterruptedException {
            //将读出来的数据写入data表中
            for(Put put:values){
                context.write(NullWritable.get(),put);
            }
        }
    }

}
