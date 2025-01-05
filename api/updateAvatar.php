<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userId = $_POST['user_id'] ?? null;
    $avatarUrl = $_POST['avatar'] ?? null;

    if ($userId && $avatarUrl) {
        $stmt = $conn->prepare("UPDATE users SET avatar = ? WHERE id = ?");
        $stmt->bind_param("si", $avatarUrl, $userId);

        if ($stmt->execute()) {
            jsonResponse(["status" => "success", "url" => $avatarUrl]);
        } else {
            jsonResponse(["status" => "error", "message" => "数据库更新失败: " . $conn->error], 500);
        }

        $stmt->close();
    } else {
        jsonResponse(["status" => "error", "message" => "缺少参数"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>
