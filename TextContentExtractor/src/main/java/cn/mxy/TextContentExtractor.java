package cn.mxy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.io.IOException;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;

public class TextContentExtractor extends Configured implements Tool {

    public static void main( String[] args ) throws Exception {
        int res = ToolRunner.run(new TextContentExtractor(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////
        // 创建作业，配置作业所需参数
        Configuration conf = new Configuration();
        // 创建作业
        Job job = Job.getInstance(conf, "TextContentExtractor");
        String inPath= args[0], outPath = args[1];
        System.out.println("args1=====" + inPath);
        System.out.println("args2=====" + outPath);

        // 注入作业的主类
        job.setJarByClass(TextContentExtractor.class);

        // 为作业注入Map类
        job.setMapperClass(Map.class);

        // 指定输入类型为：文本格式文件；注入文本输入格式类
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path(inPath));     //HDFS路径

        // 指定输出格式为：文本格式文件；注入文本输入格式类
        job.setOutputFormatClass(TextOutputFormat.class);
        // 指定Key为文本格式；注入文本类
        job.setOutputKeyClass(Text.class);
        // 执行Value为整型格式；注入文本类
        job.setOutputValueClass(Text.class);
        // 指定作业的输出目录
        TextOutputFormat.setOutputPath(job, new Path(outPath));     //HDFS路径


        ////////////////////////////////////////////////////////////
        // 作业的执行流程
        // 执行MapReduce
        boolean res = job.waitForCompletion(true);
        if(res)
            return 0;
        else
            return -1;
    }

    // Map过程
    public static class Map extends Mapper<LongWritable, Text, Text, Text> {
        private final static Text htmlContent = new Text();
        private Text url= new Text();
        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            ////将输入纯文本转为String
            String line = value.toString();

            String html = line;//按整行进行正文提取
            String urlTemp ="";

            //标题、时间、正文提取
            News news = null;
            try {
                news = ContentExtractor.getNewsByHtml(html);
                //合成total字符串
                String totalInfo = news.getTime() +"\t"+ news.getTitle() +"\t"+ news.getContent();
                htmlContent.set(totalInfo);//注入值
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            //对每一行按“\t”进行切分
            String[] splits = line.split("\t");
            urlTemp = splits[1];
            news.setUrl(urlTemp);

            url.set(news.getUrl());//注入键


            System.out.print(url.toString() + "\t" + htmlContent.toString()+ "\n");
            context.write(url,htmlContent);

        }
    }


}
