<?php
include 'common.php';

$conn = getDbConnection();

$category_id = isset($_POST['category_id']) ? $conn->real_escape_string($_POST['category_id']) : null;

$sql = $category_id ? 
    "SELECT posts.id, posts.content, posts.created_at, posts.image_links, 
            posts.is_anonymous, users.username, users.nickname, users.avatar, users.qq, users.wechat, 
            categories.name AS category_name
     FROM posts 
     JOIN users ON posts.user_id = users.id 
     JOIN categories ON posts.category_id = categories.id
     WHERE posts.category_id = '$category_id' AND posts.is_approved = TRUE
     ORDER BY posts.created_at DESC" :
    "SELECT posts.id, posts.content, posts.created_at, posts.image_links, 
            posts.is_anonymous, users.username, users.nickname, users.avatar, users.qq, users.wechat, 
            categories.name AS category_name
     FROM posts 
     JOIN users ON posts.user_id = users.id 
     JOIN categories ON posts.category_id = categories.id
     WHERE posts.is_approved = TRUE
     ORDER BY posts.created_at DESC";

$result = $conn->query($sql);

$posts = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $isAnonymous = $row['is_anonymous'];
        $posts[] = array(
            'id' => $row['id'],
            'content' => $row['content'],
            'createdAt' => $row['created_at'],
            'imageLinks' => $row['image_links'],
            'username' => $isAnonymous ? '匿名' : $row['username'],
            'nickname' => $isAnonymous ? '匿名用户' : $row['nickname'],
            'avatar' => $isAnonymous ? 'default_avatar_url' : $row['avatar'],
            'qq' => $isAnonymous ? '未知' : $row['qq'],
            'wechat' => $isAnonymous ? '未知' : $row['wechat'],
            'categoryName' => $row['category_name']
        );
    }
}

jsonResponse($posts);

$conn->close();
?>
