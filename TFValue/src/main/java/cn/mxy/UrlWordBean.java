package cn.mxy;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
/**
* url和文本内容
* @author mengxiangyi
*/

public class UrlWordBean implements WritableComparable<UrlWordBean> {

    private String url;
    private String word;

    public UrlWordBean(){

    }

    public UrlWordBean(String url, String word){
        super();
        this.url = url;
        this.word = word;
    }

    //写入数据至流
    //用于框架对数据的处理
    //注意读readFields和写write的顺序一致
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(url);
        out.writeUTF(word);
    }

    //从流中读取数据
    //将框架返回的数据提取出到对应属性中来
    //注意读readFields和写write的顺序一致
    @Override
    public void readFields(DataInput in) throws IOException {
        this.url = in.readUTF();
        this.word = in.readUTF();
    }

    @Override
    public int compareTo(UrlWordBean o){
        int result = this.url.compareTo(o.getUrl());
        return result;
    }


    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public String getWord() { return word; }

    public void setWord(String word) { this.word = word; }

}
