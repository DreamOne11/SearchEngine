package cn.mxy.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

public class HighLightService {

    //创建分词器
    private Analyzer analyzer = new IKAnalyzer();

    public void addIndex(String[] info) throws IOException {
        //指定索引库目录
        Directory directory = FSDirectory.open(Paths.get("D:\\Lucene_db\\article_tb"));

        //实例化索引配置
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //采用append模式（存在索引就追加，不存在就创建）
        config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);

        //Lucene通过IndexWriter实现索引的增、删、改
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //在Lucene中一个Document实例代表一条记录
        Document document = new Document();
        document.add(new StringField("url", info[0], Field.Store.YES));
        document.add(new StringField("time", info[1], Field.Store.YES));
        document.add(new TextField("title", info[2], Field.Store.NO));
        document.add(new TextField("content", info[3], Field.Store.NO));
        //将索引写至索引库
        indexWriter.addDocument(document);
        indexWriter.commit();//提交事务
        indexWriter.close();//关闭Lucene流
    }

    //更新索引
    public /*synchronized*/ void upDateIndex(String[] info) throws IOException {

        //实例化锁对象
        //ReentrantLock lock = new ReentrantLock();
        //加锁
        //lock.lock();
        //指定索引库目录
        Directory directory = FSDirectory.open(Paths.get("D:\\Lucene_db\\article_tb"));
        //实例化索引配置
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        //采用append模式（存在索引就追加，不存在就创建）
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        //Lucene通过IndexWriter实现索引的增、删、改
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //在Lucene中一个Document实例代表一条记录
        Document document = new Document();
        document.add(new StringField("url", info[0], Field.Store.YES));
        document.add(new StringField("time", info[1], Field.Store.NO));
        document.add(new TextField("title", info[2], Field.Store.NO));
        document.add(new TextField("content", info[3], Field.Store.NO));
        //将索根据url来更新（要是之前有，则先删除再添加）
        indexWriter.updateDocument(new Term("url", info[0]), document);
        indexWriter.commit();//提交事务
        indexWriter.close();//关闭Lucene流
        //lock.unlock();//解锁
    }

    public String[] highLighter(String keyWords, String[] info) throws ParseException, IOException, InvalidTokenOffsetsException {
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "content"}, analyzer);
        Query query = queryParser.parse(keyWords);
        //创建格式化信息
        Formatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        //通过Scorer包装query
        Scorer scorer = new QueryScorer(query);
        //创建高亮器
        Highlighter highlighter = new Highlighter(formatter, scorer);
        //创建格式化片段
        Fragmenter fragmenter = new SimpleFragmenter(140);
        //将格式化片段与高亮器关联
        highlighter.setTextFragmenter(fragmenter);
        String title = highlighter.getBestFragment(analyzer, "title", info[2]);
        String content = highlighter.getBestFragment(analyzer, "content", info[3]);
        return new String[]{title, content};
    }

    /*public String searchIndex(String keyWords, String[] info) throws ParseException, IOException, InvalidTokenOffsetsException {
        //指定索引库目录
        Directory directory = FSDirectory.open(Paths.get("D:\\Lucene_db\\article_tb"));

        //open方法指定需要读取的索引库信息
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建全文检索实例
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "content"}, analyzer);
        Query query = queryParser.parse(keyWords);

        highLighter(query).getBestFragment(analyzer, "title", info[2]);
    }*/
}
