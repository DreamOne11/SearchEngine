import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.jsoup.nodes.Element;

import javax.jnlp.ExtensionInstallerService;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HtmlContentExtractor {
    //用于保存Html中第一行的Url
    private static String url = "";
    private static ArrayList<String> stopwords = new ArrayList<String>();

    /*文件读取并转为String*/
    public static String reader() throws Exception {
        String src = "D:\\totalText1.txt";
        File file = new File(src);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String temp = "";

        /*防止读取漏行*/
        while ((temp = bufferedReader.readLine()) != null) {
            String[] splits = temp.split("\t");
            url = splits[1];
            String html = temp;

            try{
                //标题、时间、正文提取
                News news = ContentExtractor.getNewsByHtml(html);
                news.setUrl(url);

                //创建Jieba对象，进行分词
                JiebaSegmenter segmenter = new JiebaSegmenter();
                String sentence = news.getContent();
                List<SegToken> words = segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH);


                for (SegToken token : words){
                    if(stopwords.contains(token.word)) {
                        continue;
                    } else {
                        //分词的结果
                        String fishInfo = token.word + "_" + news.getUrl();
                        stringBuilder.append(fishInfo + "\n");
                        //System.out.println(token.word);
                    }
                }

                //合成字符串
                //String fishInfo = news.getUrl() +"\t"+ news.getTime() +"\t"+ news.getTitle() +"\t"+ news.getContent();
            }catch (Exception e){
                //e.printStackTrace();
                continue;
            }
        }

        bufferedReader.close();
        String text = stringBuilder.toString();
        //System.out.println(text);
        return text;
    }


    /*输入HTML，输出制定网页结构化信息*/
    /*public static void demo1() throws Exception {

        String html = reader();
        News news = ContentExtractor.getNewsByHtml(html);
        news.setUrl(url);
        System.out.println(news);
    }*/


    /*输入HTML，输出制定网页的正文*/
    /*public static void demo3() throws Exception {

        String html = reader();
        String content = ContentExtractor.getContentByHtml(html);
        System.out.println(content);

        //创建Jieba对象
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String sentence = content;
        List<SegToken> words = segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH);
        for (SegToken token:words){
            //分词的结果
            System.out.println( token.word);
        }
        //System.out.println(segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX).toString());
        //System.out.println(segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH).toString());

        //也可抽取网页正文所在的Element
        //Element contentElement = ContentExtractor.getContentElementByHtml(html, url);
        //System.out.println(contentElement);
    }*/


    /*public static void testDemo() {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String[] sentences =
                new String[] {"这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。", "我不喜欢日本和服。", "雷猴回归人间。",
                        "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作", "结果婚的和尚未结过婚的"};
        for (String sentence : sentences) {
            System.out.println(segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX).toString());
        }
    }*/


    public static void main(String[] args) throws Exception {

        String src = "D:\\专业课程\\毕业设计\\代码资料\\stopwords.txt";
        File file = new File(src);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String readline = " ";
        while ((readline=bufferedReader.readLine()) != null) {
            stopwords.add(readline);
        }
        bufferedReader.close();

        String word = reader();

        String path = "D:\\finishTotalText.txt";
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path,true)));
        bufferedWriter.write(word);
        bufferedWriter.close();

    }
}
