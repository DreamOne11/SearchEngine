package cn.mxy.controller;


import cn.mxy.pojo.ResultBean;
import cn.mxy.service.HbaseService;

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

@WebServlet(name = "resultListServlet")
public class resultListServlet extends HttpServlet {

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
        EnumMap<HbaseService.KeyWords, Object> map = hbaseService.getKeyWords(str);
        //pagesDataList获取完整结果列表
        List<ResultBean> pagesDataList = castList(map.get(HbaseService.KeyWords.resultBeanList), ResultBean.class);
        //获取分词后的keywords String
        String keywords = String.valueOf(map.get(HbaseService.KeyWords.breakKeyWords));

        int pageSize=10;//每页显示条数
        int pageCount = pagesDataList.size() % pageSize == 0 ? pagesDataList.size()/pageSize : pagesDataList.size()/pageSize + 1;//总分页页数

        //从前端页面获取当前页码
        String currentPage = request.getParameter("currentPage");
        int currentpage = 1; //当前页码，默认为第一页
        if(currentPage!=null){
            currentpage = Integer.parseInt(currentPage);
        }
        //如过当前页码超出最尾页页码，则返回尾页页码，防止fromIndex超过url总数
        if(currentpage > pageCount) {
            currentpage = pageCount;
        }
        //防止在首页点击上一页时溢出
        if(currentpage <= 0) {
            currentpage =1;
        }

        //从完整List中获取子List，实现假分页
        int fromIndex = (currentpage - 1) * pageSize;
        int toIndex = currentpage * pageSize;
        //防止toIndex超过url总数导致越界
        if (toIndex >= pagesDataList.size()) {
            toIndex = pagesDataList.size() + 1;//防止最后一页的subList获取不到最后一个List
        }
        //用于保存10个页面的数据，假分页
        List<ResultBean> subPagesList = pagesDataList.subList(fromIndex, toIndex);

        request.setAttribute("title", str);
        request.setAttribute("subPagesList", subPagesList);
        request.setAttribute("currentpage", currentpage);
        request.setAttribute("pageCount", pageCount);
        request.setAttribute("sumUrlCount", pagesDataList.size());
        request.setAttribute("keywords", keywords);
        //转发到list.jsp中
        request.getRequestDispatcher("resultList.jsp").forward(request,response);

    }

}
