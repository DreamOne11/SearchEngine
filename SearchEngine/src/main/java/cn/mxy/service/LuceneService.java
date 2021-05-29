package cn.mxy.service;

import cn.mxy.dao.LuceneDao;
import cn.mxy.pojo.ResultBean;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneService {

    //定义停用词列表
    private static ArrayList<String> stopwords = new ArrayList<String>();
    //实例化Lucene Dao层对象
    private LuceneDao luceneDao = new LuceneDao();

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
     * 接收用户输入的查找词（从Lucene中检索）
     * 分词、过滤停用词、关键词标红处理 + 动态摘要
     * 返回结果：ResultBean列表
     * @param keywords
     * @throws IOException
     */
    public List<ResultBean> getResultFromLucene(String keywords) throws IOException, InvalidTokenOffsetsException, ParseException {
        //构建停用词表
        buildStopWordsList();
        //用于存放分词后的关键词String
        String keyWords = "";
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> words = segmenter.process(keywords, JiebaSegmenter.SegMode.SEARCH);

        for (SegToken token : words) {
            //分词的结果
            String word = token.word;
            //过滤停用词
            if(stopwords.contains(word)) {
                continue;
            } else {
                //存入关键词String
                keyWords = keyWords + word + " ";
            }
        }
        return luceneDao.searchIndex(keyWords);
    }

    /***
     * 接收用户输入的查找词（从Lucene中检索）
     * 分词、过滤停用词、关键词标红处理
     * 返回结果：relativeTitle列表
     * @param keywords
     * @throws IOException
     */
    public List<String> getRelativeTitle(String keywords) throws ParseException, InvalidTokenOffsetsException, IOException {
        //构建停用词表
        buildStopWordsList();
        //用于存放分词后的关键词String
        String keyWords = "";
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> words = segmenter.process(keywords, JiebaSegmenter.SegMode.SEARCH);

        for (SegToken token : words) {
            //分词的结果
            String word = token.word;
            //过滤停用词
            if(stopwords.contains(word)) {
                continue;
            } else {
                //存入关键词String
                keyWords = keyWords + word + " ";
            }
        }
        return luceneDao.searchTitle(keyWords);
    }
}
