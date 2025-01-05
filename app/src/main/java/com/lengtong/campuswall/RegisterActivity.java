package com.lengtong.campuswall;
/**
 * 用户注册界面。
 * 处理用户输入的用户名和密码，调用API进行注册。
 */

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private EditText et_passwords; // 新增的确认密码输入框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // 初始化控件
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_passwords = findViewById(R.id.et_passwords); // 初始化确认密码输入框

        // 点击返回
        findViewById(R.id.toolbar).setOnClickListener(view -> finish());

        // 点击注册
        findViewById(R.id.bregister).setOnClickListener(view -> {
            String username = et_username.getText().toString();
            String password = et_password.getText().toString();
            String confirmPassword = et_passwords.getText().toString(); // 获取确认密码

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(username, password);
            }
        });
    }

    private void registerUser(String username, String password) {
        com.lengtong.campuswall.Api.ApiService apiService = Api.getApiService();
        Call<ResponseBody> call = apiService.registerUser(username, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Toast.makeText(RegisterActivity.this, "注册成功请登录", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (IOException e) {
                        Log.e("RegisterActivity", "解析错误: " + e.getMessage());
                        Toast.makeText(RegisterActivity.this, "解析错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "该用户名已被注册", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RegisterActivity", "网络错误: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}