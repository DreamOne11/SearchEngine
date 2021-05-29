package cn.mxy.dao;

import cn.mxy.pojo.ResultBean;
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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LuceneDao {
    //创建分词器
    private Analyzer analyzer = new IKAnalyzer();

    //添加索引
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
        Directory directory = FSDirectory.open(Paths.get("C:\\Lucene_db\\article_tb"));
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

    //高亮、动态摘要(HBase检索使用)
    public String[] highLighter(String keyWords, String[] info) throws ParseException, IOException, InvalidTokenOffsetsException {
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "content"}, analyzer);
        Query query = queryParser.parse(keyWords);
        //创建格式化信息
        Formatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        //通过Scorer包装query，计分器计分
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

    //Lucene检索，包含HighLighter
    public List<ResultBean> searchIndex(String keyWords) throws ParseException, IOException, InvalidTokenOffsetsException {
        //指定索引库目录
        Directory directory = FSDirectory.open(Paths.get("C:\\Lucene_db\\article_tb"));
        //实例化ResultBean，用于保存返回结果
        List<ResultBean> resultBeanList = new ArrayList<>();

        //open方法指定需要读取的索引库信息
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建全文检索实例
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "content"}, analyzer);
        Query query = queryParser.parse(keyWords);

        //创建格式化信息
        Formatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        //通过Scorer包装query，计分器计分
        Scorer scorer = new QueryScorer(query);
        //创建高亮器
        Highlighter highlighter = new Highlighter(formatter, scorer);
        //创建格式化片段
        Fragmenter fragmenter = new SimpleFragmenter(140);
        //将格式化片段与高亮器关联
        highlighter.setTextFragmenter(fragmenter);
        TopDocs topDocs = indexSearcher.search(query, 1000);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (int i = 0; i < scoreDocs.length ; i++) {
            //实例化resultBean对象
            ResultBean resultBean = new ResultBean();
            int id = scoreDocs[i].doc;
            //System.out.println("id:" + id);
            //float score = scoreDocs[i].score;
            //System.out.println("文章得分" + score);
            Document document = indexSearcher.doc(id);
            //将url注入resultBean
            resultBean.setUrl(document.get("url"));
            //将time注入resultBean
            resultBean.setTime(document.get("time"));
            String title = highlighter.getBestFragment(analyzer, "title", document.get("title"));//title高亮处理
            String content = highlighter.getBestFragment(analyzer, "content", document.get("content"));//content高亮处理+动态摘要
            if (title != null) {
                resultBean.setTitle(title);//将高亮后的title注入resultBean
            } else {
                resultBean.setTitle(document.get("title"));//将原title注入
            }
            if (content != null) {
                resultBean.setMainContent(content);//将高亮后的content注入resultBean
            } else {
                resultBean.setMainContent(document.get("content"));//将原content注入
            }
            resultBeanList.add(resultBean);
        }
        return resultBeanList;
    }

    //Lucene检索，检索相关词汇的title，包含Highter
    public List<String> searchTitle(String keyWords) throws IOException, ParseException, InvalidTokenOffsetsException {
        //指定索引库目录
        Directory directory = FSDirectory.open(Paths.get("C:\\Lucene_db\\article_tb"));
        //实例化ResultBean，用于保存返回结果
        List<String> titleList = new ArrayList<>();

        //open方法指定需要读取的索引库信息
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建全文检索实例
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser queryParser = new QueryParser("title", analyzer);
        Query query = queryParser.parse(keyWords);

        //创建格式化信息
        Formatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        //通过Scorer包装query，计分器计分
        Scorer scorer = new QueryScorer(query);
        //创建高亮器
        Highlighter highlighter = new Highlighter(formatter, scorer);
        //创建格式化片段
        Fragmenter fragmenter = new SimpleFragmenter(10);
        //将格式化片段与高亮器关联
        highlighter.setTextFragmenter(fragmenter);
        TopDocs topDocs = indexSearcher.search(query, 5);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (int i = 0; i < scoreDocs.length ; i++) {
            int id = scoreDocs[i].doc;
            Document document = indexSearcher.doc(id);
            String title = document.get("title");
            String hightLightTitle = highlighter.getBestFragment(analyzer, "title", title);//title高亮处理
            if (hightLightTitle != null) {
                titleList.add(hightLightTitle);
            } else {
                titleList.add(title);
            }
        }
        return titleList;
    }

}
