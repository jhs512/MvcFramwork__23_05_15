package com.ll.framework;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/usr/*")
public class DispatcherServlet extends HttpServlet {
    // 조회
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        ControllerManager.runAction(req, resp);
    }

    // 등록
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doGet(req, resp);
    }

    // 삭제
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        doGet(req, resp);
    }

    // 수정
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        doGet(req, resp);
    }
}
