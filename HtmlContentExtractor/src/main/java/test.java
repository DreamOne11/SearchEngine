import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.List;

public class test {
    public static void main(String[] args) throws Exception {
        //创建Jieba对象，进行分词
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String sentence = "奇怪的校园恋情";
        List<SegToken> words = segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH);
        for (SegToken token : words){
            System.out.println(token.word);
        }
    }
}
