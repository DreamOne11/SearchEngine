package cn.mxy;

import cn.mxy.dao.HbaseDao;

import java.io.IOException;
import java.util.*;

public class test {

    public static void main(String[] args) throws IOException {
        //实例化Hbase Dao层对象
        HbaseDao hbaseDao = new HbaseDao();
        //用于存放排序后的url以及其TF-IDF值
        Map<String, Double> TFIDFMap;

        //获取全部页面数
        Double wholePagesCount = hbaseDao.getWholePagesCount();
        System.out.println(wholePagesCount);

        ArrayList<String> finishWords = new ArrayList<>();
        String a = "1999";
        finishWords.add(a);

        if (finishWords.size() == 1) {
            //若只有一个关键词则直接按Map的TF值排序
            for(String keyword : finishWords) {
                TFIDFMap = hbaseDao.getTFValue(keyword);
                //进行Map的键排序，先将entrySet转换为List
                List<Map.Entry<String, Double>> list = new ArrayList<>(TFIDFMap.entrySet());
                //使用list.sort()排序
                list.sort(new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });

                for (Map.Entry<String, Double> stringDoubleEntry : list) {
                    System.out.println(stringDoubleEntry.getKey() + ": " + stringDoubleEntry.getValue());
                }
            }

        } else if (finishWords.size() >= 2) {
            System.out.println("有两个以上的keyword");
        }

    }
}
