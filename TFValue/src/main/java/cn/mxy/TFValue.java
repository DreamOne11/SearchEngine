package cn.mxy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;


public class TFValue extends Configured implements Tool {

    public static void main(String[] args) throws Exception {

        int res = ToolRunner.run(new TFValue(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////
        // 创建作业，配置作业所需参数
        Configuration conf = HBaseConfiguration.create();
        // 程序中必须指定jar包的本地路径
        conf.set("mapreduce.job.jar", "D:\\专业课程\\毕业设计\\TFValue\\out\\artifacts\\TFValue\\TFValue.jar");
        // 指定Zookeeper集群地址
        conf.set("hbase.zookeeper.quorum", "192.168.0.22:2181,192.168.0.33:2181,192.168.0.44:2181");
        System.setProperty("HADOOP_USER_NAME", "hadoop");

        //定义list，用于存放停用词
        List<String> list = new ArrayList<>();
        //从HDFS中读取stopwords.txt文件，构建停用词列表
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path("/SearchEngine/stopwords.txt");
        //判断路径是否存在
        if(fs.exists(path)) {
            FSDataInputStream hdfsInStream = fs.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(hdfsInStream));
            String readline;
            while ((readline=reader.readLine()) != null) {
                list.add(readline);
            }
            hdfsInStream.close();
            fs.close();
        }
        //设置stopWordslist参数
        conf.set("stopWordslist", String.valueOf(list));

        // 创建作业
        Job job = Job.getInstance(conf, "TFValue");
        String inPath = "/SearchEngine/extractedpages/extractedHtml11", outPath = "/SearchEngine/output";
        System.out.println("args1=====" + inPath);
        System.out.println("args2=====" + outPath);


        // 注入作业的主类
        //job.setJarByClass(TFValue.class);

        // 为作业注入Map类
        job.setMapperClass(TFMap.class);
        job.setMapOutputKeyClass(UrlWordBean.class);
        job.setMapOutputValueClass(IntWritable.class);

        //其中自动注入Reduce类和Partition类
        TableMapReduceUtil.initTableReducerJob(
                "TFValue",      // output table
                TFReduce.class,        // reducer class
                job,
                TFPartition.class,     // partition class
                null,
                null,
                null,
                false);

        // 指定Key为文本格式；注入文本类
        job.setOutputKeyClass(NullWritable.class);
        // 执行Value为整型格式；注入文本类
        job.setOutputValueClass(Put.class);
        //设置reduce任务数为2
        job.setNumReduceTasks(2);

        // 为作业注入Partition类
        //job.setPartitionerClass(TFPartition.class);
        // 为作业注入Reduce类
        //job.setReducerClass(TFReduce.class);

        // 指定输入类型为：文本格式文件；注入文本输入格式类
        //job.setInputFormatClass(FileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(inPath));     //HDFS路径

        // 指定输出格式为：文件格式文件；注入文本输出格式类
        //job.setOutputFormatClass(FileOutputFormat.class);
        // 指定作业的输出目录
        FileOutputFormat.setOutputPath(job, new Path(outPath));     //HDFS路径


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
    public static class TFMap extends Mapper<LongWritable, Text, UrlWordBean, IntWritable> {
        //定义停用词列表
        private List<String> stopWordslist;
        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            //从配置文件中获取上下文
            Configuration conf = context.getConfiguration();
            //将String进行拆分，去除"[" "]" 按", "划分String  然后划分结果放入list
            String pre_stopWords = conf.get("stopWordslist").substring(1);
            pre_stopWords = pre_stopWords.substring(0, pre_stopWords.length() - 1);
            String[] split = pre_stopWords.split(", ");
            stopWordslist = Arrays.asList(split);

            ////将输入纯文本转为String
            String line = value.toString();
            //对每一行按“\t”进行切分
            String[] splits = line.split("\t");
            //拆分数据
            if (splits.length >= 2) {
                //获取url
                String url = splits[0].trim();
                //获取标题和正文内容
                String content = splits[2].trim() + splits[3].trim();
                //创建Jieba对象，进行正文分词
                JiebaSegmenter segmenter = new JiebaSegmenter();
                List<SegToken> words = segmenter.process(content, JiebaSegmenter.SegMode.SEARCH);
                for (SegToken token : words) {
                    //分词的结果
                    String word = token.word;
                    //过滤停用词
                    if(stopWordslist.contains(word)) {
                        continue;
                    } else {
                        //注入键值对
                        context.write(new UrlWordBean(url, word), new IntWritable(1));
                    }
                }
                //pagescount 1 计数
                context.write(new UrlWordBean("pagesCount", ""), new IntWritable(1));
            }
        }
    }

    //Partition过程, 采用partition，让页面计数结果独占一个分区
    public static class TFPartition extends Partitioner<UrlWordBean, IntWritable> {
        @Override
        public int getPartition(UrlWordBean key, IntWritable value, int reduceCount) {
            if (key.getUrl().equals("pagesCount")) {
                return reduceCount-1;
            } else {
                return reduceCount-2;
            }
        }
    }

    //Reduce过程
    public static class TFReduce extends TableReducer<UrlWordBean, IntWritable, NullWritable> {
        @Override
        public void reduce(UrlWordBean key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //container0中统计一个网页中出现了多少个词，container1中统计一共多少个网页
            int wordsCount = 0;
            //统计每个词在当前网页中出现的次数
            Map<String, Integer> map =new HashMap<>();
            //迭代器迭代
            for(IntWritable i : values) {
                //因为在map过程中将每个键的值都设为1，所以相当于i++
                wordsCount = wordsCount + i.get();
                //构造word_url字符串
                String urlWordBean = key.getWord() + "_" + key.getUrl();
                //判断某词是否为第一次出现，若不是则累加，若是则设值为1
                if(map.containsKey(urlWordBean)) {
                   //System.out.println("词汇：" +  key.getWord() + "重复出现在：" + key.getUrl());
                    map.put(urlWordBean, map.get(urlWordBean) + 1);
                } else {
                    //System.out.println("给" + key.getUrl() + "的单词 " + key.getWord() + "设置初始值");
                    map.put(urlWordBean, 1);
                }
            }

            //处理数据，计算TF值，将TF值存入Hbase表中
            if(key.getUrl().equals("pagesCount")) {
                String finalPagesCount = Integer.toString(wordsCount);
                //将网页总数填入UrlCount列组，设置rowkey
                Put put = new Put(Bytes.toBytes("finalCount"));
                //填入信息，指定：列族、列名、值 ，将每个词在每页的TF值填入Urls列族
                put.addColumn(Bytes.toBytes("Urls"), Bytes.toBytes("finalPagesCount"), Bytes.toBytes("116920"));
                context.write(NullWritable.get(), put);
            } else {
                //循环遍历map
                for(Entry<String, Integer> entry : map.entrySet()) {
                    //拆分word、url
                    String[] splits = entry.getKey().split("_");
                    String word = splits[0];
                    String url = splits[1];
                    //计算TF值，并将其转化为String
                    String TFValue = Double.toString(entry.getValue() * 1.0 / wordsCount);
                        if(word != null && !word.equals("")) {
                            //设置rowkey
                            Put put = new Put(Bytes.toBytes(word));
                            //填入信息，指定：列族、列名、值 ，将每个词在每页的TF值填入Urls列族
                            put.addColumn(Bytes.toBytes("Urls"), Bytes.toBytes(url), Bytes.toBytes(TFValue));
                            context.write(NullWritable.get(), put);
                        }
                    }

            }

        }
    }

}