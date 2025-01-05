package com.lengtong.campuswall.Fragment;
/**
 * 用户信息显示:
 * 显示用户的头像、用户名、昵称、QQ号和微信号。
 * 使用SharedPreferences获取当前登录用户的信息，并通过API加载用户详细信息。
 * 2. 头像上传:
 * 用户可以点击头像来选择新的图片进行上传。
 * 使用FTP上传新头像到服务器，并更新数据库中的头像链接。
 * 3. 用户帖子管理:
 * 显示用户发布的帖子列表，使用RecyclerView和自定义的PostAdapter。
 * 帖子项显示内容、发布时间和审核状态（待审核、审核通过、审核不通过）。
 * 提供删除帖子功能，用户可以删除自己发布的帖子。
 * 4. 设置和管理功能:
 * 提供进入设置界面的按钮，用户可以修改个人信息或密码。
 * 如果用户是管理员，显示管理待审帖子的按钮，点击后进入PendingPostsActivity。
 * 5. 网络请求:
 * 使用Retrofit进行网络请求，获取用户信息、用户帖子，并更新头像。
 * 处理网络请求的成功和失败情况，显示相应的Toast提示。
 * 6. UI更新:
 * 根据获取的用户信息更新UI组件。
 * 使用Glide加载用户头像，并处理占位图和加载失败的情况。
 * 7. 内部类 PostAdapter:
 * 自定义适配器，用于在RecyclerView中显示用户的帖子。
 * 处理帖子项的显示逻辑，包括内容、发布时间、审核状态和删除按钮。
 * 接口 OnDeleteClickListener:
 * 定义了一个接口，用于处理帖子删除事件的回调。
 * 通过这些功能，ProfileFragment 提供了一个完整的用户个人资料管理界面，用户可以查看和管理自己的信息和帖子。
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lengtong.campuswall.Api;
import com.lengtong.campuswall.PendingPostsActivity;
import com.lengtong.campuswall.Post;
import com.lengtong.campuswall.R;
import com.lengtong.campuswall.SettingsActivity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private ImageView imageAvatar;
    private TextView textUsername;
    private TextView textNickname;
    private TextView textQQ;
    private TextView textWechat;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> userPosts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageAvatar = view.findViewById(R.id.image_avatar);
        textUsername = view.findViewById(R.id.text_username);
        textNickname = view.findViewById(R.id.text_nickname);
        textQQ = view.findViewById(R.id.text_qq);
        textWechat = view.findViewById(R.id.text_wechat);
        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);

        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(userPosts, this::deletePost);
        recyclerViewPosts.setAdapter(postAdapter);

        // 添加分隔线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewPosts.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewPosts.addItemDecoration(dividerItemDecoration);

        // 获取用户信息
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        if (!username.isEmpty()) {
            loadUserInfo(username);
        } else {
            textUsername.setText("未登录");
            textNickname.setText("请先登录");
        }

        // 点击头像上传
        imageAvatar.setOnClickListener(v -> pickImageFromGallery());

        // 设置
        Button buttonSettings = view.findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        // 待审管理按钮
        Button buttonManage = view.findViewById(R.id.button_manage);
        buttonManage.setVisibility(View.GONE); // 默认隐藏

        return view;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        new Thread(() -> {
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect("v0.ftp.upyun.com", 21);
                ftpClient.login("lengtong/img-lengtong", "y1bjiyRh7s3B65yY9BSqxBteACnSQnrN");
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                String uploadDirectory = "/avatars";
                ftpClient.changeWorkingDirectory(uploadDirectory);

                String fileExtension = getFileExtension(imageUri);
                String randomFileName = UUID.randomUUID().toString() + fileExtension;

                try (InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri)) {
                    boolean done = ftpClient.storeFile(randomFileName, inputStream);
                    if (done) {
                        String imageUrl = "https://img.jmm0.cn/avatars/" + randomFileName;
                        updateAvatarInDatabase(imageUrl);
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "头像上传失败", Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void updateAvatarInDatabase(String imageUrl) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        Api.getApiService().updateAvatar(userId, imageUrl).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "头像更新成功", Toast.LENGTH_SHORT).show();
                    loadUserInfo(textUsername.getText().toString().replace("用户名: ", ""));
                } else {
                    Toast.makeText(getContext(), "头像更新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        String extension = "";
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getContext().getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        return extension != null ? "." + extension : "";
    }

    private void loadUserInfo(String username) {
        Api.getApiService().getUserInfo(username).enqueue(new Callback<Api.UserInfo>() {
            @Override
            public void onResponse(Call<Api.UserInfo> call, Response<Api.UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Api.UserInfo userInfo = response.body();
                    updateUI(userInfo);
                    loadUserPosts(username);

                    // 根据用户角色显示管理按钮
                    if ("admin".equals(userInfo.role)) {
                        Button buttonManage = getView().findViewById(R.id.button_manage);
                        buttonManage.setVisibility(View.VISIBLE);
                        buttonManage.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(), PendingPostsActivity.class);
                            startActivity(intent);
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Api.UserInfo> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Api.UserInfo userInfo) {
        textUsername.setText("用户名: " + userInfo.username);
        textNickname.setText("昵称: " + (userInfo.nickname != null ? userInfo.nickname : "未设置"));
        textQQ.setText("QQ: " + (userInfo.qq != null ? userInfo.qq : "未设置"));
        textWechat.setText("微信: " + (userInfo.wechat != null ? userInfo.wechat : "未设置"));

        if (userInfo.avatar != null && !userInfo.avatar.isEmpty()) {
            Glide.with(this)
                    .load(userInfo.avatar)
                    .placeholder(R.drawable.baseline_mood_bad_24)
                    .circleCrop() // 确保图片是圆形的
                    .into(imageAvatar);
        } else {
            imageAvatar.setImageResource(R.drawable.baseline_mood_bad_24);
        }
    }

    private void loadUserPosts(String username) {
        Api.getApiService().getUserPosts(username).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userPosts.clear();
                    userPosts.addAll(response.body());
                    postAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "获取用户帖子失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePost(int postId) {
        Api.getApiService().deletePost(postId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "帖子删除成功", Toast.LENGTH_SHORT).show();
                    loadUserPosts(textUsername.getText().toString().replace("用户名: ", ""));
                } else {
                    Toast.makeText(getContext(), "删除帖子失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
        private List<Post> posts;
        private OnDeleteClickListener deleteListener;

        PostAdapter(List<Post> posts, OnDeleteClickListener deleteListener) {
            this.posts = posts;
            this.deleteListener = deleteListener;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_post, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = posts.get(position);
            holder.textContent.setText(post.getContent());
            holder.textCreatedAt.setText(post.getCreatedAt());

            // 根据 is_approved 字段设置状态文本和颜色
            String statusText;
            int statusColor;
            switch (post.getIsApproved()) {
                case 0:
                    statusText = "待审核";
                    statusColor = 0xFFFFE066; // 浅金黄色
                    break;
                case 1:
                    statusText = "审核通过";
                    statusColor = 0xFF66C266; // 浅绿色
                    break;
                case 2:
                    statusText = "审核不通过";
                    statusColor = 0xFFFF6666; // 浅红色
                    break;
                default:
                    statusText = "未知状态";
                    statusColor = 0xFFB3B3B3; // 浅灰色
            }
            holder.textStatus.setText(statusText);
            holder.textStatus.setTextColor(statusColor);

            holder.buttonDelete.setOnClickListener(v -> deleteListener.onDeleteClick(post.getId()));
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        class PostViewHolder extends RecyclerView.ViewHolder {
            TextView textContent;
            TextView textCreatedAt;
            TextView textStatus; // 新增的 TextView
            Button buttonDelete;

            PostViewHolder(View itemView) {
                super(itemView);
                textContent = itemView.findViewById(R.id.text_content);
                textCreatedAt = itemView.findViewById(R.id.text_date);
                textStatus = itemView.findViewById(R.id.text_status); // 初始化
                buttonDelete = itemView.findViewById(R.id.button_delete);
            }
        }
    }

    interface OnDeleteClickListener {
        void onDeleteClick(int postId);
    }
}