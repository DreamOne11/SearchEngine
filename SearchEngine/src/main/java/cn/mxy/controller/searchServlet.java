package cn.mxy.controller;

import cn.mxy.pojo.ResultBean;
import cn.mxy.service.HbaseService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@WebServlet(name = "searchServlet", urlPatterns = "/searchServlet")
public class searchServlet extends HttpServlet {
    //实例化HbaseService对象
    private HbaseService hbaseService = new HbaseService();
    //Object转List<T>
    private static <T> List<T> castList(Object source, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if(source instanceof List<?>) {
            for (Object o : (List<?>) source) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //将关键词传递到service层进行逻辑处理
        String input = request.getParameter("searchInfo");
        String str = new String(input.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        //pagesDataList获取完整结果列表
        try {
            List<ResultBean> pagesDataList = hbaseService.getKeyWords(str);
            request.getSession().setAttribute("pagesDataList", pagesDataList);
            request.getSession().setAttribute("str", str);
            //转发到resultListServlet中
            request.getRequestDispatcher("resultListServlet").forward(request,response);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }
    }
}
