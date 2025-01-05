<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postId = $_POST['post_id'] ?? null;
    $userId = $_POST['user_id'] ?? null;
    $content = $_POST['content'] ?? null;

    if ($postId && $userId && $content) {
        $stmt = $conn->prepare("INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)");
        $stmt->bind_param("iis", $postId, $userId, $content);
        if ($stmt->execute()) {
            jsonResponse(["status" => "success"]);
        } else {
            jsonResponse(["status" => "error", "message" => "评论失败"], 500);
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