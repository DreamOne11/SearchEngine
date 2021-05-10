package cn.mxy;

import cn.mxy.dao.HbaseDao;
import cn.mxy.service.HbaseService;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class test {
    public static void main(String[] args) throws IOException {


        //实例化Dao层对象
        HbaseDao hbaseDao = new HbaseDao();
        //定义停用词列表
        ArrayList<String> stopwords = new ArrayList<String>();
        //实例化Service层对象
        HbaseService hbaseService = new HbaseService();

        String keywords = "西南石油大学";

        //构建停用词表
        String src = "D:\\stopwords.txt";
        File file = new File(src);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String readline = " ";
        while ((readline=bufferedReader.readLine()) != null) {
            stopwords.add(readline);
        }
        bufferedReader.close();

        ArrayList<String> finishWords = new ArrayList<>();
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> words = segmenter.process(keywords, JiebaSegmenter.SegMode.SEARCH);

        for (SegToken token : words) {
            //分词的结果
            String word = token.word;
            //过滤停用词
            if(stopwords.contains(word)) {
                continue;
            } else {
                //存入
                finishWords.add(word);
            }
        }

        //将分词过滤后的关键词表传入TFIDFValue，获取排序后的url表
        List<Map.Entry<String, Double>> urlList = hbaseService.TFIDFValue(finishWords);

    }
}
