package cn.mxy.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mxy.dao.HbaseDao;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.apache.hadoop.io.IntWritable;

public class HbaseService {
    //定义停用词列表
    private static ArrayList<String> stopwords = new ArrayList<String>();
    //实例化Hbase Dao层对象
    private HbaseDao hbaseDao = new HbaseDao();

    //从本地读取通用词文件，构建停用词表
    private void buildStopWordsList() throws IOException {
        String src = "D:\\stopwords.txt";
        File file = new File(src);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String readline = " ";
        while ((readline=bufferedReader.readLine()) != null) {
            stopwords.add(readline);
        }
        bufferedReader.close();
    }

    /***
     * 接收用户输入的查找词
     * 分词、过滤停用词
     * 返回结果
     * @param keywords
     * @throws IOException
     */
    public void getKeyWords(String keywords) throws IOException {
        //构建停用词表
        buildStopWordsList();
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
        //将分词过滤后的关键词表传入TFIDFValue()

    }

    /***
     * 获取WordPagesCount，并计算每个关键词的IDF值
     * @param finishWords
     * @return
     * @throws IOException
     */
    private Map<String, Double> IDFValue(ArrayList<String> finishWords) throws IOException {
        //获取全部页面数
        Double wholePagesCount = hbaseDao.getWholePagesCount();
        //按关键词表获取每个关键词的IDF值
        Map<String, Integer> pagesCountMap = hbaseDao.getWordPagesCount(finishWords);
        Map<String, Double> IDFValue = new HashMap<>();

        for (Map.Entry<String, Integer> entry : pagesCountMap.entrySet()) {
            Double IDF = Math.log(wholePagesCount / entry.getValue());
            IDFValue.put(entry.getKey(), IDF);
        }
        return IDFValue;
    }

    /***
     * 计算TF-IDF值，返回排序后的url表
     * @param finishWords
     * @return
     * @throws IOException
     */
    private ArrayList<String> TFIDFValue(ArrayList<String> finishWords) throws IOException {
        //获取关键词IDF值表
        Map<String, Double> IDFMap= IDFValue(finishWords);


    }



}
