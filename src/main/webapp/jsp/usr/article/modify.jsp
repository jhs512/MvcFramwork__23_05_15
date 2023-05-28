<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="../common/head.jspf" %>

<script>
    function ArticleSave__submitForm(form) {
        form.title.value = form.title.value.trim();
        if (form.title.value.length == 0) {
            alert('제목을 입력해주세요.');
            form.title.focus();
            return;
        }

        const editor = $(form).find(".toast-ui-editor").data("data-toast-editor");

        const markdown = editor.getMarkdown();
        console.log(markdown);
        form.body.value = markdown.trim();

        if (form.body.value.length == 0) {
            alert("내용을 입력해주세요");
            editor.focus();

            return;
        }

        form.submit();
    }
</script>

<section>
    <div class="container px-3 mx-auto">
        <h1 class="font-bold text-lg"><i class="fa-solid fa-pen"></i> 작성</h1>
        <form method="POST" onsubmit="ArticleSave__submitForm(this); return false;">
            <input type="hidden" name="_method" value="PUT"/>
            <input type="hidden" name="body"/>
            <div class="form-control w-full">
                <label class="label">
                    <span class="label-text">제목</span>
                </label>
                <input name="title" type="text" maxlength="50" placeholder="제목을 입력해주세요."
                       class="input input-bordered w-full max-w-xs" value="${article.title}"/>
            </div>

            <div class="form-control w-full">
                <label class="label">
                    <span class="label-text">내용</span>
                </label>
                <div class="toast-ui-editor" toast-ui-editor--height="calc(100vh - 300px)">
                    <script type="text/x-template">${articleBody}</script>
                </div>
            </div>

            <div class="mt-3">
                <div class="btns">
                    <input class="btn btn-primary btn-outline" type="submit" value="수정"/>
                </div>
            </div>
        </form>
    </div>
</section>

<%@ include file="../common/foot.jspf" %>