<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = $_POST['user_id'] ?? null;
    $category_id = $_POST['category_id'] ?? null;
    $content = $_POST['content'] ?? null;
    $image_links = $_POST['image_links'] ?? null;
    $is_anonymous = isset($_POST['is_anonymous']) ? ($_POST['is_anonymous'] === 'true' ? 1 : 0) : 0;

    if ($user_id && $category_id && $content && $image_links !== null) {
        if (json_decode($image_links) === null && json_last_error() !== JSON_ERROR_NONE) {
            jsonResponse(['error' => '无效的图片链接格式'], 400);
        }

        $stmt = $conn->prepare("INSERT INTO posts (user_id, category_id, content, image_links, is_anonymous, is_approved) VALUES (?, ?, ?, ?, ?, 0)");
        $stmt->bind_param("iissi", $user_id, $category_id, $content, $image_links, $is_anonymous);

        if ($stmt->execute()) {
            jsonResponse(['success' => '帖子已提交，等待审核']);
        } else {
            jsonResponse(['error' => '帖子提交失败: ' . $conn->error], 500);
        }

        $stmt->close();
    } else {
        jsonResponse(['error' => '缺少必要的参数'], 400);
    }
} else {
    jsonResponse(['error' => '只支持 POST 请求'], 405);
}

$conn->close();
?>
