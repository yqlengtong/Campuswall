<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postId = $_POST['post_id'] ?? null;

    if ($postId) {
        $stmt = $conn->prepare("SELECT COUNT(*) as likes_count FROM likes WHERE post_id = ?");
        $stmt->bind_param("i", $postId);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        jsonResponse(["likes_count" => $row['likes_count']]);
        $stmt->close();
    } else {
        jsonResponse(["status" => "error", "message" => "缺少参数"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>