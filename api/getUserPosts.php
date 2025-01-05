<?php
include 'common.php';

$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';

    if (!empty($username)) {
        $stmt = $conn->prepare("SELECT p.id, p.user_id, p.category_id, p.content, p.created_at, p.is_approved 
                                FROM posts p 
                                JOIN users u ON p.user_id = u.id 
                                WHERE u.username = ? 
                                ORDER BY p.created_at DESC");
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();

        $posts = array();

        while ($row = $result->fetch_assoc()) {
            $posts[] = array(
                "id" => $row['id'],
                "userId" => $row['user_id'],
                "categoryId" => $row['category_id'],
                "content" => $row['content'],
                "createdAt" => $row['created_at'],
                "isApproved" => $row['is_approved']
            );
        }

        jsonResponse($posts);

        $stmt->close();
    } else {
        jsonResponse(["error" => "缺少用户名"], 400);
    }
} else {
    jsonResponse(["error" => "只支持 POST 请求"], 405);
}

$conn->close();
?>
