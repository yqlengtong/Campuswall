package com.lengtong.campuswall;
/**
 * 用户编辑个人信息的界面。
 * 提供修改昵称、QQ、微信的功能，并调用API更新信息。
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editNickname;
    private EditText editQQ;
    private EditText editWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editNickname = findViewById(R.id.edit_nickname);
        editQQ = findViewById(R.id.edit_qq);
        editWechat = findViewById(R.id.edit_wechat);
        Button buttonSave = findViewById(R.id.button_save);

        // 从SharedPreferences获取当前用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // 加载当前用户信息
        loadUserInfo(username);

        // 保存修改
        buttonSave.setOnClickListener(v -> {
            String newNickname = editNickname.getText().toString();
            String newQQ = editQQ.getText().toString();
            String newWechat = editWechat.getText().toString();

            // 调用API更新用户信息
            Api.getApiService().updateUserInfo(username, newNickname, newQQ, newWechat).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "信息更新成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "信息更新失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadUserInfo(String username) {
        Api.getApiService().getUserInfo(username).enqueue(new Callback<Api.UserInfo>() {
            @Override
            public void onResponse(Call<Api.UserInfo> call, Response<Api.UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Api.UserInfo userInfo = response.body();
                    editNickname.setText(userInfo.nickname);
                    editQQ.setText(userInfo.qq);
                    editWechat.setText(userInfo.wechat);
                } else {
                    Toast.makeText(EditProfileActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Api.UserInfo> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}