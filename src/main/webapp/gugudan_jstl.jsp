<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="dan" value="${param.dan}"/>
<c:set var="limit" value="${param.limit}"/>

<h1>${dan}단</h1>

<c:forEach var="i" begin="1" end="${limit}">
    <div>
            ${dan} * ${i} = ${dan * i}
    </div>
</c:forEach>