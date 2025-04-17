<?php
// 数据库连接和响应助手

function getDbConnection() {
    $servername = "localhost";
    $username = "数据库账号";
    $password = "数据库密码";
    $dbname = "数据库名";

    $conn = new mysqli($servername, $username, $password, $dbname);

    if ($conn->connect_error) {
        jsonResponse(["error" => "连接失败: " . $conn->connect_error], 500);
    }

    $conn->set_charset("utf8mb4");
    return $conn;
}

function jsonResponse($data, $statusCode = 200) {
    header('Content-Type: application/json');
    http_response_code($statusCode);
    echo json_encode($data);
    exit();
}
?>
