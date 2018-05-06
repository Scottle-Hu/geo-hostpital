<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <title>模拟推荐界面</title>
</head>
<style>
    .input {
        background-color: #effff7;
        width: 500px;
        height: 400px;
        border: solid 1px black;
        padding: 5px;
    }

    #text {
        width: 490px;
        height: 300px;
    }

</style>
<body>

<div class="input">
    请输入文本
    <form action="main.geo" method="post" target="_blank">
        <textarea id="text" name="text"></textarea>
        <input type="submit" value="submit"/>
    </form>
</div>

</div>
</body>
</html>