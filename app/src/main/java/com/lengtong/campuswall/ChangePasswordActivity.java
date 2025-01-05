package com.lengtong.campuswall;
/**
 * 用户修改密码的界面。
 * 用户输入旧密码和新密码，调用API修改密码。
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

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextOldPassword = findViewById(R.id.edit_text_old_password);
        editTextNewPassword = findViewById(R.id.edit_text_new_password);
        buttonSubmit = findViewById(R.id.button_submit);

        buttonSubmit.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = editTextOldPassword.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = getCurrentUserId(); // 从SharedPreferences获取用户ID

        // 调用API修改密码
        Api.ApiService apiService = Api.getApiService();
        Call<Void> call = apiService.changePassword(userId, oldPassword, newPassword);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Toast.makeText(ChangePasswordActivity.this, "原密码错误", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ChangePasswordActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1); // 返回存储的用户ID，默认值为-1
    }
}