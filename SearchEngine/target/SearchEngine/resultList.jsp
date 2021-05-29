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
    <link rel="stylesheet" type="text/css" href="./static/css/result.css">
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="./static/js/search.js"></script>
</head>
<body>

    <%--添加一个回跳--%>
    <div id="head">
    <div id="logo">
        <img class="schoolLogo" src="./static/image/swpu_badge.png">
        <span class="school">西南石油大学</span>
    </div>
    <div class="search">
        <form class="searchForm" name="search" id="Search" method="post" action="searchServlet">
            <div class="searchBody">
                <input class="inputBox" type="text" name="searchInfo" id="searchInput" value="${title}" maxlength="100" size="75">
                <input class="searchButton" id="searchBtn" type="button" value="搜索">
            </div>
        </form>
    </div>
</div>

    <div id="container">
    <div id="searchNums">
        <span class="numsText">为您找到相关结果约${sumUrlCount}个</span>
    </div>
    <div id="content">
        <c:if test="${!empty subPagesList}">
            <c:forEach items="${subPagesList}" var="page">
                <div class="result">
                    <div class="title">
                        <h3>
                            <a href="${page.url}">
                                    ${page.title}
                            </a>
                        </h3>
                    </div>
                    <div class="abstract">
                        <span class="url">${page.url}</span><br>
                        <span class="time">${page.time} · ${page.mainContent}</span>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>

    <div id="relative">
    <h3 class="relative-head"><span style='color:red'>${title}</span>的相关搜索</h3>
    <c:if test="${!empty relativeTitle}">
        <c:forEach items="${relativeTitle}" var="title">
            <p class="relative-info">${title}</p>
        </c:forEach>
    </c:if>
</div>

    <div id="page">
    <div id="page-inner">
        <c:if test="${currentPage > 1}">
            <%--<a href="/resultListServlet?currentPage=1" class="indexBottom">首页</a>--%>
            <a href="/resultListServlet?currentPage=${currentPage - 1}" class="pageBottom" id="lastBottom">&lt上一页</a>
        </c:if>
        <c:if test="${currentPage <= 6 }">
            <c:forEach var="i"  begin="1"  end="10"  step="1">
                <a href="/resultListServlet?currentPage=${i}" class="indexBottom" id="${i}" onclick="more(this);">${i}</a>
            </c:forEach>
        </c:if>
        <c:if test="${currentPage > 6 && currentPage < (pageCount-4)}">
            <c:forEach var="i"  begin="${currentPage - 5}"  end="${currentPage + 4}"  step="1">
                <a href="/resultListServlet?currentPage=${i}" class="indexBottom" id="${i}" onclick="more(this);">${i}</a>
            </c:forEach>
        </c:if>
        <c:if test="${currentPage >= (pageCount-4) }">
            <c:forEach var="i"  begin="${pageCount - 9}"  end="${pageCount}"  step="1">
                <a href="/resultListServlet?currentPage=${i}" class="indexBottom" id="${i}" onclick="more(this);">${i}</a>
            </c:forEach>
        </c:if>
        <c:if test="${currentPage < pageCount}">
            <a href="/resultListServlet?currentPage=${currentPage + 1}" class="pageBottom" id="nextBottom">下一页&gt</a>
            <%--<a href="/resultListServlet?currentPage=${pageCount}" class="indexBottom">尾页</a>--%>
        </c:if>
    </div>
</div>

    <div id="foot">
    <div id="copycontent">
        <p>
            <img src="${pageContext.request.contextPath }/static/image/local.png" width="19" height="17" style="width: 19px; height: 17px;" />
            四川省成都市新都区新都大道8号-成都校区|
            <img src="${pageContext.request.contextPath }/static/image/letter.png" width="18" height="16" style="width: 18px; height: 16px;"  alt=""/>&nbsp;610500|&nbsp;
            <img src="${pageContext.request.contextPath }/static/image/phone.png" width="20" height="20" style="width: 20px; height: 20px;" />&nbsp;028-83032308|&nbsp;招生咨询：028-83032224｜&nbsp;教务咨询：028- 83032113&nbsp;|&nbsp;
            <img src="${pageContext.request.contextPath }/static/image/local.png" width="19" height="17" style="white-space: normal; width: 19px; height: 17px;" />&nbsp;四川省南充市顺庆区油院路二段1号（原油院路30号）-南充校区|&nbsp;
            <img src="${pageContext.request.contextPath }/static/image/letter.png" width="18" height="16" style="white-space: normal; width: 18px; height: 16px;" />&nbsp;637001|&nbsp;
            <img src="${pageContext.request.contextPath }/static/image/phone.png" width="16" height="16" style="white-space: normal; width: 16px; height: 16px;" />&nbsp;0817-2641016
        </p>
    </div>
</div>

<script type="text/javascript">
    $(".indexBottom, .pageBottom").each(function(){
        $(this).mouseover(function () {
            $(this).css({'background-color' : '#3951B3'});
            $(this).css({'color' : '#ffffff'});
        })
        $(this).mouseout(function () {
            $(this).css({'background-color' : '#ffffff'});
            $(this).css({'color' : '#3951B3'});
            $("#"+${currentPage}).css({'background-color' : '#3951B3'});
            $("#"+${currentPage}).css({'color' : '#ffffff'});
        })
        $(this).click(function(){
            $("#"+${currentPage}).removeAttr('href');
            $("#"+${currentPage}).removeAttr('onclick');
        });

    });
    $(document).ready(function() {
        $("#"+${currentPage}).css({'background-color' : '#3951B3'});
        $("#"+${currentPage}).css({'color' : '#ffffff'});
    });

    $(function(){
        $("p.relative-info").click(function(){
            var title = $(this).text();
            $("#searchInput").val(title);
            $("#searchBtn").click();
        });
    });

    $(function(){
        $("#logo").click(function () {
            $(window).attr('location',"${pageContext.request.contextPath }/index.jsp");
        })
    });
</script>
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
