<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>SWPU-SearchEngine</title>
    <link rel="stylesheet" type="text/css" href="./static/css/search.css">
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="./static/js/search.js"></script>
</head>

<body onload="giveFocus()" align="center">
    <div class="signal">
        <div class="schoolBadge">
            <img class="schoolImage" src="${pageContext.request.contextPath}/static/image/swpu_badge.png">
        </div>
        <div class="schoolName">西南石油大学</div>
    </div>
    <div class="search">
        <form class="searchForm" name="search" id="Search" method="post" action="searchServlet">
            <div class="searchBody">
                <input class="inputBox" type="text" name="searchInfo" id="searchInput" placeholder="搜索校园网" maxlength="100" size="75">
                <input class="searchButton" id="searchBtn" type="button" value="搜索">
            </div>
        </form>
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
</body>
</html>




