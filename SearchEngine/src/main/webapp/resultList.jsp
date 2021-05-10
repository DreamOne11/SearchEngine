<%--
  Created by IntelliJ IDEA.
  User: 15199
  Date: 2021/4/20
  Time: 16:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>${title} - SWPU搜索</title>
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js" type="text/javascript"></script>
</head>
<body>

<div id="head">
    <div id="logo">
        <img class="schoolImage" src="./static/image/swpu_badge.jfif">
    </div>
    <div class="search">
        <form class="searchForm" name="search" id="Search" method="post" action="resultListServlet">
            <div class="searchBody">
                <input class="inputBox" type="text" name="searchInfo" id="searchInput" placeholder="搜索校园网" maxlength="100" size="75">
                <input class="searchButton" id="searchBtn" type="button" value="搜索">
            </div>
        </form>
    </div>
</div>
<div id="container">
    <div id="searchNums">
        <span class="numsText">为您找到相关结果约${sumUrlCount}</span>
    </div>
    <div id="content">
        <c:if test="${!empty subPagesList}">
            <c:forEach items="${subPagesList}" var="page">
                <div class="result">
                    <h2>
                        <a href="${page.url}">
                            <%--<c:choose>
                                <c:when test="${fn:length(page.title) > 18}">
                                    <c:out value="${fn:substring(page.title, 0, 20)}..." />
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${page.title}" />
                                </c:otherwise>
                            </c:choose>--%>
                        </a>
                    </h2>
                    <div class="abstract">
                        <span class="url">${page.url}</span>
                        <span class="time">${page.time}</span>
                        <p>${page.mainContent}</p>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>
<div id="page">
    <c:if test="${currentPage > 1}">

    </c:if>
    <a href="/resultListServlet?currentPage=1">首页</a>
    <a href="/resultListServlet?currentPage=${currentPage - 1}">上一页</a>
    <a href="/resultListServlet?currentPage=${currentPage + 1}">下一页</a>
    <a href="/resultListServlet?currentPage=${pageCount}">尾页</a>
    当前第${currentPage}/${pageCount}页
    一共${sumUrlCount}条数据
</div>
<div id="foot">
</div>

</body>

<%--<script type="text/javascript">
    function renderMatchSubstring(str, substr)
    {
        var substrList = substr.split(/\s+/);
        var regStr = "\(" + substrList.join("\)|\(") + "\)";
        var reg = new RegExp(regStr,"g");
        var matchResult = str.match(reg);
        for(index in matchResult)
        {
            var regS = new RegExp("[^</]\(" + matchResult[index] + "\)","g");
            str = str.replace(regS,"<span style='color:red'>$1</span>");
        }
        return str;
    }
    document.getElementById("title").innerHTML = renderMatchSubstring(${page.title}, ${keywords});
</script>--%>

</html>
