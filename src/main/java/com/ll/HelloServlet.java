package com.ll;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;

@WebServlet("/usr/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html; charset=UTF-8");

        try {
            resp.getWriter()
                    .append("Hello, <span style='color:red;'>Servlet</span>!!??")
                    .append("<br>")
                    .append(new Person(1, "Paul").toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doGet(req, resp);
    }
}

@AllArgsConstructor
@Getter
@ToString
class Person {
    private int id;
    private String name;
}