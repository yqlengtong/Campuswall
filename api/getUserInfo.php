<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';

    if (!empty($username)) {
        $stmt = $conn->prepare("SELECT username, nickname, avatar, qq, wechat, role FROM users WHERE username = ?");
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $response = array(
                "username" => $row['username'],
                "nickname" => $row['nickname'],
                "avatar" => $row['avatar'],
                "qq" => $row['qq'],
                "wechat" => $row['wechat'],
                "role" => $row['role']
            );
            jsonResponse($response);
        } else {
            jsonResponse(["error" => "用户不存在"], 404);
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
