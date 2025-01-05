<?php
include 'common.php';

$conn = getDbConnection();

$sql = "SELECT p.id, p.content, p.created_at, p.image_links, u.nickname, u.qq, u.wechat, u.avatar, c.name AS category_name
        FROM posts p
        JOIN users u ON p.user_id = u.id
        JOIN categories c ON p.category_id = c.id
        WHERE p.is_approved = FALSE";
$result = $conn->query($sql);

$posts = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $posts[] = array(
            'id' => $row['id'],
            'content' => $row['content'],
            'createdAt' => $row['created_at'],
            'imageLinks' => $row['image_links'],
            'nickname' => $row['nickname'],
            'qq' => $row['qq'],
            'wechat' => $row['wechat'],
            'avatar' => $row['avatar'],
            'categoryName' => $row['category_name']
        );
    }
}

jsonResponse($posts);

$conn->close();
?>
