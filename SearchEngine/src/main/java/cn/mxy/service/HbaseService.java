package cn.mxy.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import cn.mxy.dao.HbaseDao;
import cn.mxy.pojo.ResultBean;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Formatter;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.sound.midi.Soundbank;


public class HbaseService {
    //定义停用词列表
    private static ArrayList<String> stopwords = new ArrayList<String>();
    //实例化Hbase Dao层对象
    private HbaseDao hbaseDao = new HbaseDao();
    //用于返回多个值
    public enum KeyWords{
        resultBeanList,
        breakKeyWords
    }
    //实例化Lucene操作对象
    private HighLightService highLightService = new HighLightService();

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
     * 分词、过滤停用词、关键词高亮处理 + 动态摘要
     * 返回结果：ResultBean列表
     * @param keywords
     * @throws IOException
     */
    public EnumMap<KeyWords, Object> getKeyWords(String keywords) throws IOException, ParseException, InvalidTokenOffsetsException {
        //构建停用词表
        buildStopWordsList();
        //构建返回值Map
        EnumMap<KeyWords, Object> map =new EnumMap<KeyWords, Object>(KeyWords.class);
        //用于存放分词后的关键词list
        ArrayList<String> finishWords = new ArrayList<>();
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
                //存入关键词list
                finishWords.add(word);
                //存入关键词String
                keyWords = keyWords + word + " ";
            }
        }
        //将分词过滤后的关键词表传入TFIDFValue，获取排序后的url表
        List<Map.Entry<String, Double>> urlList = TFIDFValue(finishWords);
        List<ResultBean> highLightResult = hbaseDao.getPagesData(urlList);

        for (ResultBean value : highLightResult) {
            //按url、时间、标题、正文的顺序写入Lucene
            String[] info = {value.getUrl(), value.getTime(), value.getTitle(), value.getMainContent()};
            highLightService.upDateIndex(info);
            String[] afterInfo = highLightService.highLighter(keyWords.trim(), info);
            value.setTitle(afterInfo[0]);
            value.setMainContent(afterInfo[1]);
        }

        //调用DAO层getPagesData，作为函数第一个返回值
        map.put(KeyWords.resultBeanList, hbaseDao.getPagesData(urlList));
        map.put(KeyWords.breakKeyWords, keyWords);
        return map;
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
     * 计算每个url的TF-IDF值，返回排序后的url表
     * @param finishWords
     * @return
     * @throws IOException
     */
    public List<Map.Entry<String, Double>> TFIDFValue(ArrayList<String> finishWords) throws IOException {
        //获取关键词IDF值表
        Map<String, Double> IDFMap = IDFValue(finishWords);
        //用于存放单个keyword获取到的TF值
        Map<String, Double> TFMap;
        //用于存放排序后的url以及其TF-IDF值
        Map<String, Double> TFIDFMap = new HashMap<>();
        //用于url的排序
        List<Map.Entry<String, Double>> list = new ArrayList<>();

        if (finishWords.size() == 1) {
            //若只有一个关键词则直接按Map的TF值排序，因为IDF都是一个值
            for(String keyword : finishWords) {
                //获取该词的TF值
                TFIDFMap = hbaseDao.getTFValue(keyword);
                //进行Map的键排序，先将entrySet转换为List
                list = new ArrayList<>(TFIDFMap.entrySet());
                //使用list.sort()降序排序
                list.sort(new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });
            }

        } else if (finishWords.size() >= 2) {
            //多关键词，循环IDF表获取每个关键词的url-TF值和该词的IDF值
            for(Map.Entry<String, Double> entry : IDFMap.entrySet()) {
                //获取每个关键词的url-TF值表
                TFMap = hbaseDao.getTFValue(entry.getKey());
                Double IDFValue = entry.getValue();
                //todo 第二层循环
                for(Map.Entry<String, Double> entry1 : TFMap.entrySet()) {
                    double TFIDF = IDFValue * entry1.getValue();
                    //TF-IDF值保留9位小数，返回Double值
                    BigDecimal b = new BigDecimal(TFIDF);
                    TFIDF = b.setScale(9, BigDecimal.ROUND_HALF_UP).doubleValue();

                    if(!TFIDFMap.containsKey(entry1.getKey())) {
                        //若该url在表中第一次出现，直接写入
                        TFIDFMap.put(entry1.getKey(), TFIDF);
                    } else {
                        //若该url已存在，则TF-IDF值累加 todo 测试是累加，还是存为新的键值对！
                        double TFIDF1 = TFIDFMap.get(entry1.getKey()) + TFIDF;
                        System.out.println(TFIDF1);
                        TFIDFMap.put(entry1.getKey(), TFIDF1);
                        System.out.println(TFIDFMap.get(entry1.getKey()));
                    }
                }
            }
            //进行Map的键排序，先将entrySet转换为List
            list = new ArrayList<>(TFIDFMap.entrySet());
            //使用list.sort()降序排序
            list.sort(new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
        }

        return list;
    }



}
