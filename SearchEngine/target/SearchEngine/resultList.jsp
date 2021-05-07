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
</head>
<body>
<c:if test="${!empty pagesDataList}">
    <c:forEach items="${pagesDataList}" var="page">
        <div class="page">
            ${page.url}<br>
            ${page.title}<br>
            ${page.time}<br>
        </div>
    </c:forEach>
</c:if>
</body>
</html>
