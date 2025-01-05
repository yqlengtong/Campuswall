<?php
include 'common.php';

session_start(); // 确保会话已启动
$conn = getDbConnection();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $old_password = $_POST['old_password'] ?? null;
    $new_password = $_POST['new_password'] ?? null;
    $user_id = $_POST['user_id'] ?? null; // 从请求中获取用户ID

    if ($user_id && $old_password && $new_password) {
        $stmt = $conn->prepare("SELECT password FROM users WHERE id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            $stmt->bind_result($hashedPassword);
            $stmt->fetch();
            if (password_verify($old_password, $hashedPassword)) {
                $new_password_hashed = password_hash($new_password, PASSWORD_BCRYPT);
                $update_stmt = $conn->prepare("UPDATE users SET password = ? WHERE id = ?");
                $update_stmt->bind_param("si", $new_password_hashed, $user_id);

                if ($update_stmt->execute()) {
                    jsonResponse(['success' => '密码修改成功']);
                } else {
                    jsonResponse(['error' => '密码修改失败: ' . $conn->error], 500);
                }

                $update_stmt->close();
            } else {
                jsonResponse(['error' => '旧密码不正确'], 400);
            }
        } else {
            jsonResponse(['error' => '用户不存在'], 400);
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