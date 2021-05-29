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

    <div class="qs-container">
        <p class="qs-text">快速链接</p>
        <div class="qs-bottom">
            <img class="qs-image" src="${pageContext.request.contextPath}/static/image/uparrow.png" alt="">
        </div>
    </div>

    <div id="quickSearch">
        <div class="item" onclick="window.open('https://www.swpu.edu.cn/','_self')" title="西南石油大学主站">
            <div class="image-border">
                <div class="image-container">
                    <div class="image">
                        <img src="${pageContext.request.contextPath}/static/image/school.png" alt="西南石油大学主站">
                    </div>
                </div>
            </div>
            <div class="title-container">
                <p class="title">西南石油大学主站</p>
            </div>
        </div>
        <div class="item" onclick="window.open('https://www.swpu.edu.cn/dean/','_self')" title="教务处">
            <div class="image-border">
                <div class="image-container">
                    <div class="image">
                        <img src="${pageContext.request.contextPath}/static/image/school.png" alt="教务处">
                    </div>
                </div>
            </div>
            <div class="title-container">
                <p class="title">教务处</p>
            </div>
        </div>
        <div class="item" onclick="window.open('https://lib.swpu.edu.cn/','_self')" title="西南石油图书馆">
            <div class="image-border">
                <div class="image-container">
                    <div class="image">
                        <img src="${pageContext.request.contextPath}/static/image/lib.png" alt="西南石油图书馆">
                    </div>
                </div>
            </div>
            <div class="title-container">
                <p class="title">西南石油图书馆</p>
            </div>
        </div>
        <div class="item" onclick="window.open('https://www.cnki.net/','_self')" title="中国知网">
            <div class="image-border">
                <div class="image-container">
                    <div class="image">
                        <img src="${pageContext.request.contextPath}/static/image/zw.png" alt="中国知网">
                    </div>
                </div>
            </div>
            <div class="title-container">
                <p class="title">中国知网</p>
            </div>
        </div>
        <div class="item" onclick="window.open('http://my.swpu.edu.cn/web/guest','_self')" title="西南石油服务门户">
            <div class="image-border">
                <div class="image-container">
                    <div class="image">
                        <img src="${pageContext.request.contextPath}/static/image/school.png" alt="西南石油服务门户">
                    </div>
                </div>
            </div>
            <div class="title-container">
                <p class="title">西南石油服务门户</p>
            </div>
        </div>
        <div class="item" onclick="window.open('http://xg.swpu.edu.cn/userhall/smart/student','_self')" title="学工一体化平台">
            <div class="image-border">
                <div class="image-container">
                    <div class="image">
                        <img src="${pageContext.request.contextPath}/static/image/xgpt.png" alt="学工一体化平台">
                    </div>
                </div>
            </div>
            <div class="title-container">
                <p class="title">学工一体化平台</p>
            </div>
        </div>
        <div class="item" onclick="window.open('http://swpu.co.cnki.net/','_self')" title="毕业设计管理网站">
            <div class="image-border">
                <div class="image-container">
                    <div class="image">
                        <img src="${pageContext.request.contextPath}/static/image/bysj.png" alt="毕业设计管理网站">
                    </div>
                </div>
            </div>
            <div class="title-container">
                <p class="title">毕业设计管理网站</p>
            </div>
        </div>
        <div class="item" onclick="" title="添加站点">
            <div class="image-border">
                <div class="image-container" style="background-color: transparent;">
                    <div class="image">
                        <img src="${pageContext.request.contextPath}/static/image/add.png" alt="添加站点">
                    </div>
                </div>
            </div>
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
        $(".item, .qs-bottom").each(function(){
            $(this).mouseover(function () {
                $(this).css({'background-color' : '#eeeeeeab'});
            })
            $(this).mouseout(function () {
                $(this).css({'background-color' : 'transparent'});
            })
        });

        $(".qs-bottom, .qs-text").click(function () {
            if($('.qs-text').is(':visible')) {
                $(".qs-text").hide();
                $("#quickSearch").show();
                $(".qs-image").attr("src", "${pageContext.request.contextPath}/static/image/downarrow.png");
            } else {
                $(".qs-text").show();
                $("#quickSearch").hide();
                $(".qs-image").attr("src", "${pageContext.request.contextPath}/static/image/uparrow.png");
            }
        });
    </script>
</body>

</html>




