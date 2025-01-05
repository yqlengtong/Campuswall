<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';

    if (!empty($username) && !empty($password)) {
        $stmt = $conn->prepare("SELECT id, password FROM users WHERE username = ?");
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            $stmt->bind_result($userId, $hashedPassword);
            $stmt->fetch();
            if (password_verify($password, $hashedPassword)) {
                jsonResponse(["user_id" => $userId, "message" => "登录成功"]);
            } else {
                jsonResponse(["error" => "用户名或密码错误"], 401);
            }
        } else {
            jsonResponse(["error" => "用户名或密码错误"], 401);
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
