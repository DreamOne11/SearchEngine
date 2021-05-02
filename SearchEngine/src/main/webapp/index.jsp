<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>SWPU-Search</title>
    <link rel="stylesheet" type="text/css" href="./static/css/search.css">
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="./static/js/search.js"></script>
</head>

<body onload="giveFocus()" align="center">
    <div class="signal">
        <div class="schoolBadge">
            <img class="schoolImage" src="./static/image/swpu_badge.jfif">
        </div>
        <div class="schoolName">西南石油大学</div>
    </div>

    <div class="search">
        <form class="searchForm" name="search" id="Search" method="post" action="resultListServlet">
            <div class="searchBody">
                <input class="inputBox" type="text" name="searchInfo" id="searchInput" placeholder="搜索校园网" maxlength="100" size="75">
                <input class="searchButton" id="searchBtn" type="button" value="搜索">
            </div>
        </form>
    </div>
</body>
</html>




