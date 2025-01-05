<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $post_id = $_POST['post_id'] ?? null;
    $action = $_POST['action'] ?? null;

    if ($post_id && $action) {
        $sql = $action == 'approve' ? 
            "UPDATE posts SET is_approved = 1 WHERE id = ?" : 
            ($action == 'reject' ? "UPDATE posts SET is_approved = 2 WHERE id = ?" : null);

        if ($sql) {
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("i", $post_id);

            if ($stmt->execute()) {
                jsonResponse(["success" => true]);
            } else {
                jsonResponse(["error" => "操作失败: " . $conn->error], 500);
            }

            $stmt->close();
        } else {
            jsonResponse(["error" => "无效的操作"], 400);
        }
    } else {
        jsonResponse(["error" => "缺少必要的参数"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>
