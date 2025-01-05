<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postId = $_POST['post_id'] ?? null;
    $userId = $_POST['user_id'] ?? null;

    if ($postId && $userId) {
        // 检查用户是否已经点赞
        $checkStmt = $conn->prepare("SELECT * FROM likes WHERE post_id = ? AND user_id = ?");
        $checkStmt->bind_param("ii", $postId, $userId);
        $checkStmt->execute();
        $result = $checkStmt->get_result();

        if ($result->num_rows === 0) {
            // 如果没有点赞，则插入点赞记录
            $stmt = $conn->prepare("INSERT INTO likes (post_id, user_id) VALUES (?, ?)");
            $stmt->bind_param("ii", $postId, $userId);
            if ($stmt->execute()) {
                jsonResponse(["status" => "success", "action" => "liked"]);
            } else {
                jsonResponse(["status" => "error", "message" => "点赞失败"], 500);
            }
            $stmt->close();
        } else {
            // 如果已经点赞，则删除点赞记录
            $stmt = $conn->prepare("DELETE FROM likes WHERE post_id = ? AND user_id = ?");
            $stmt->bind_param("ii", $postId, $userId);
            if ($stmt->execute()) {
                jsonResponse(["status" => "success", "action" => "unliked"]);
            } else {
                jsonResponse(["status" => "error", "message" => "取消点赞失败"], 500);
            }
            $stmt->close();
        }
        $checkStmt->close();
    } else {
        jsonResponse(["status" => "error", "message" => "缺少参数"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>