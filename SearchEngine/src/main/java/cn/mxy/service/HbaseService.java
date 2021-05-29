package cn.mxy.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import cn.mxy.dao.HbaseDao;
import cn.mxy.dao.LuceneDao;
import cn.mxy.pojo.ResultBean;
import cn.mxy.utils.TimeRank;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.*;



public class HbaseService {
    //定义停用词列表
    private static ArrayList<String> stopwords = new ArrayList<String>();
    //实例化Hbase Dao层对象
    private HbaseDao hbaseDao = new HbaseDao();
    //实例化Lucene Dao层对象
    private LuceneDao luceneDao = new LuceneDao();
    //实例化TimeRank
    private TimeRank timeRank = new TimeRank();
    //获取全部页面数
    private Double wholePagesCount = hbaseDao.getWholePagesCount();
    public HbaseService() throws IOException {
    }

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
     * 接收用户输入的查找词（从HBase中检索）
     * 分词、过滤停用词、关键词标红处理 + 动态摘要
     * 返回结果：ResultBean列表
     * @param keywords
     * @throws IOException
     */
    public List<ResultBean> getKeyWords(String keywords) throws IOException, ParseException, InvalidTokenOffsetsException {
        //构建停用词表
        buildStopWordsList();
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
        //用于存放原始结果表
        List<ResultBean> resultBeanList = hbaseDao.getPagesData(urlList);
        //加入时间因素排序的url表
        List<Map.Entry<ResultBean, Double>> timeRankList = timeRankValue(resultBeanList);
        //用于存放高亮后的结果表
        List<ResultBean> highLightList = new ArrayList<>();

        //todo
        System.out.println("开始高亮！！！");
        //标红处理、动态摘要
        for (Map.Entry<ResultBean, Double> value : timeRankList) {
            if(!value.getKey().getUrl().equals("finalPagesCount")) { //排除finalPagesCount
                //按url、时间、标题、正文的顺序写入Lucene
                String[] info = {value.getKey().getUrl(), value.getKey().getTime(), value.getKey().getTitle(), value.getKey().getMainContent()};
                String[] afterInfo = luceneDao.highLighter(keyWords.trim(), info);//高亮，返回结果
               if(afterInfo[0] != null) {//若没有标红信息，则不不改动
                   value.getKey().setTitle(afterInfo[0]);
               }
               if(afterInfo[1] != null) {//若没有标红信息，则不不改动
                   value.getKey().setMainContent(afterInfo[1]);
               }
                highLightList.add(value.getKey());
            }
        }
        //todo
        System.out.println("高亮结束！！！");
        return highLightList;
    }


    /***
     * 获取WordPagesCount，并计算每个关键词的IDF值
     * @param finishWords
     * @return 返回IDF值
     * @throws IOException
     */
    private Map<String, Double> IDFValue(ArrayList<String> finishWords) throws IOException {
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
     * @return TF-IDF值
     * @throws IOException
     */
    private List<Map.Entry<String, Double>> TFIDFValue(ArrayList<String> finishWords) throws IOException {
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
                //todo
                System.out.println("TF排序完成！！！！");
            }
        } else if (finishWords.size() >= 2) {
            //多关键词，循环IDF表获取每个关键词的url-TF值和该词的IDF值
            for(Map.Entry<String, Double> entry : IDFMap.entrySet()) {
                //获取每个关键词的url-TF值表
                TFMap = hbaseDao.getTFValue(entry.getKey());
                Double IDFValue = entry.getValue();
                //第二层循环
                for(Map.Entry<String, Double> entry1 : TFMap.entrySet()) {
                    //TF-IDF值计算
                    double TFIDF = IDFValue * entry1.getValue();
                    //double TFIDF = 1/Math.log(1/entry1.getValue()) * IDFValue;
                    //TF-IDF值保留9位小数，返回Double值
                    BigDecimal b = new BigDecimal(TFIDF);
                    TFIDF = b.setScale(9, BigDecimal.ROUND_HALF_UP).doubleValue();

                    if(!TFIDFMap.containsKey(entry1.getKey())) {
                        //若该url在表中第一次出现，直接写入
                        TFIDFMap.put(entry1.getKey(), TFIDF);
                    } else {
                        //若该url已存在，则TF-IDF值累加
                        double TFIDF1 = TFIDFMap.get(entry1.getKey()) + TFIDF;
                        TFIDFMap.put(entry1.getKey(), TFIDF1);
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
            //todo
            System.out.println("IDF排序完成！！！！");
        }

        return list;
    }

    /***
     * 获取ResultBean中的time和TF-IDF
     * 调用TimeRank计算时间百分比，计算每个网页的最终Rank值
     * @param urls
     * @return 返回Rank顺序排序的list
     */
    private List<Map.Entry<ResultBean, Double>> timeRankValue(List<ResultBean> urls) {
        //用于url的排序
        List<Map.Entry<ResultBean, Double>> list = new ArrayList<>();
        //实例化Map对象，用于放resultbean和rank值
        Map<ResultBean, Double> timeRankMap = new HashMap<>();
        //循环处理每一个TF-IDF值，将其与时间因素结合
        for(ResultBean resultBean : urls) {
            Double timerank = timeRank.timeTFIDFValue(resultBean.getTime());//将网页年份转为时间百分比
            String TFIDFStr = String.format("%E", resultBean.getTfidf());//直接格式化浮点数为科学计数
            char N = TFIDFStr.charAt(TFIDFStr.length() - 1);//获取TFIDF值的最后一位，即数量级
            int n = Integer.parseInt(String.valueOf(N));//将数量级转为int型
            Double finalRank = resultBean.getTfidf() + timerank * Math.pow(10, -1*n);//计算最终的Rank值
            timeRankMap.put(resultBean, finalRank);
        }

        //进行Map的键排序，先将entrySet转换为List
        list = new ArrayList<>(timeRankMap.entrySet());
        //使用list.sort()降序排序
        list.sort(new Comparator<Map.Entry<ResultBean, Double>>() {
            @Override
            public int compare(Map.Entry<ResultBean, Double> o1, Map.Entry<ResultBean, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        return list;
    }

}
