<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postId = $_POST['post_id'] ?? null;

    if ($postId) {
        // 开始事务
        $conn->begin_transaction();

        try {
            // 删除与帖子相关的评论
            $stmt = $conn->prepare("DELETE FROM comments WHERE post_id = ?");
            $stmt->bind_param("i", $postId);
            $stmt->execute();
            $stmt->close();

            // 删除与帖子相关的点赞
            $stmt = $conn->prepare("DELETE FROM likes WHERE post_id = ?");
            $stmt->bind_param("i", $postId);
            $stmt->execute();
            $stmt->close();

            // 删除帖子
            $stmt = $conn->prepare("DELETE FROM posts WHERE id = ?");
            $stmt->bind_param("i", $postId);
            $stmt->execute();
            $stmt->close();

            // 提交事务
            $conn->commit();

            jsonResponse(["success" => true, "message" => "帖子删除成功"]);
        } catch (Exception $e) {
            // 回滚事务
            $conn->rollback();
            jsonResponse(["success" => false, "message" => "帖子删除失败"], 500);
        }
    } else {
        jsonResponse(["error" => "缺少帖子ID"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>