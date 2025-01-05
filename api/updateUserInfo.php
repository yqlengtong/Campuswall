<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';
    $nickname = $_POST['nickname'] ?? '';
    $qq = $_POST['qq'] ?? '';
    $wechat = $_POST['wechat'] ?? '';

    if (!empty($username)) {
        $stmt = $conn->prepare("UPDATE users SET nickname = ?, qq = ?, wechat = ? WHERE username = ?");
        $stmt->bind_param("ssss", $nickname, $qq, $wechat, $username);

        if ($stmt->execute()) {
            jsonResponse(["success" => true]);
        } else {
            jsonResponse(["success" => false, "error" => $stmt->error], 500);
        }

        $stmt->close();
    } else {
        jsonResponse(["error" => "缺少用户名"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>
