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
        String str = String.format("%E", 0.005);//获取直接格式化结果
        System.out.println(str);
    }
}
