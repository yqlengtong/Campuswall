<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';

    if (!empty($username) && !empty($password)) {
        $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?, ?)");
        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
        $stmt->bind_param("ss", $username, $hashedPassword);

        if ($stmt->execute()) {
            jsonResponse(["message" => "注册成功"]);
        } else {
            jsonResponse(["error" => "注册失败: " . $stmt->error], 500);
        }

        $stmt->close();
    } else {
        jsonResponse(["error" => "用户名和密码不能为空"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>
