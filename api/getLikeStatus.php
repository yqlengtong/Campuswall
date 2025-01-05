<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postId = $_POST['post_id'] ?? null;
    $userId = $_POST['user_id'] ?? null;

    if ($postId && $userId) {
        // 检查用户是否已经点赞
        $stmt = $conn->prepare("SELECT COUNT(*) as like_count FROM likes WHERE post_id = ? AND user_id = ?");
        $stmt->bind_param("ii", $postId, $userId);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        
        // 如果like_count大于0，说明用户已经点赞
        $isLiked = $row['like_count'] > 0;
        jsonResponse(["is_liked" => $isLiked]);
        
        $stmt->close();
    } else {
        jsonResponse(["status" => "error", "message" => "缺少参数"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>