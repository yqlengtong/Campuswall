<?php
// 数据库连接和响应助手

function getDbConnection() {
    $servername = "mysql";
    $username = "campuswall";
    $password = "lengtong608040";
    $dbname = "campuswall";

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
