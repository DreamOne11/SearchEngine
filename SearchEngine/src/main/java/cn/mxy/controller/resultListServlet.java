package cn.mxy.controller;


import cn.mxy.pojo.ResultBean;
import cn.mxy.service.HbaseService;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "resultListServlet")
public class resultListServlet extends HttpServlet {

    //实例化HbaseService对象
    private HbaseService hbaseService = new HbaseService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //将关键词传递到service层进行逻辑处理
        String input = request.getParameter("searchInfo");
        String str = new String(input.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        //List<ResultBean> pagesDataList = hbaseService.getKeyWords(str);

        request.setAttribute("title", str);
        //request.setAttribute("pagesDataList", pagesDataList);
        //转发到list.jsp中
        request.getRequestDispatcher("resultList.jsp").forward(request,response);

    }

}
