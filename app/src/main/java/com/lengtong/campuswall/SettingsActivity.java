package com.lengtong.campuswall;
/**
 * 设置界面，提供修改个人信息、修改密码和退出登录的功能。
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button buttonChangePassword = findViewById(R.id.button_change_password);
        Button buttonLogout = findViewById(R.id.button_logout);
        Button buttonEditProfile = findViewById(R.id.button_editProfile);
        //修改个人信息
        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        // 修改密码
        buttonChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // 退出登录
        buttonLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.apply();
            Toast.makeText(SettingsActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();

            // 清除返回栈并启动登录界面,防止返回到我的界面
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}