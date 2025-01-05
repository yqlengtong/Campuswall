package com.lengtong.campuswall;
/**
 * 应用启动时的活动，检查用户登录状态。
 * 根据登录状态跳转到MainActivity或LoginActivity。
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        new Handler().postDelayed(() -> {
            if (checkLoginStatus()) {
                // 用户已登录，跳转到 MainActivity
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 结束当前活动，防止用户返回到启动页面
            } else {
                // 用户未登录，跳转到 LoginActivity
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 结束当前活动，防止用户返回到启动页面
            }
        }, 2000); // 延迟2秒检查登录状态，模拟启动过程
    }

    private boolean checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        return username != null;
    }
}