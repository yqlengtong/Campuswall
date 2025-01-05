<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postId = $_POST['post_id'] ?? null;

    if ($postId) {
        $stmt = $conn->prepare("SELECT c.id, c.content, c.created_at, u.nickname, u.avatar FROM comments c JOIN users u ON c.user_id = u.id WHERE c.post_id = ? ORDER BY c.created_at DESC");
        $stmt->bind_param("i", $postId);
        $stmt->execute();
        $result = $stmt->get_result();
        $comments = [];
        while ($row = $result->fetch_assoc()) {
            $comments[] = $row;
        }
        jsonResponse($comments);
        $stmt->close();
    } else {
        jsonResponse(["status" => "error", "message" => "缺少参数"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>