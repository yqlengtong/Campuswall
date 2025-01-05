package com.lengtong.campuswall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lengtong.campuswall.Adapter.CommentAdapter;
import com.lengtong.campuswall.Adapter.PostImagesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {
    private ImageButton buttonLike;
    private ImageButton buttonComment;
    private TextView textLike;
    private TextView textComment;
    private EditText editComment;
    private Button btnComment;
    private RecyclerView recyclerComment;
    private int postId;
    private LinearLayout linComment;
    private LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 获取传递的数据
        postId = getIntent().getIntExtra("postId", -1);
        String content = getIntent().getStringExtra("content");
        String createdAt = getIntent().getStringExtra("createdAt");
        String nickname = getIntent().getStringExtra("nickname");
        String qq = getIntent().getStringExtra("qq");
        String wechat = getIntent().getStringExtra("wechat");
        String avatar = getIntent().getStringExtra("avatar");
        String categoryName = getIntent().getStringExtra("categoryName");
        String imageLinks = getIntent().getStringExtra("imageLinks");

        // 设置视图
        TextView contentTextView = findViewById(R.id.text_content);
        TextView dateTextView = findViewById(R.id.text_date);
        TextView nicknameTextView = findViewById(R.id.text_nickname);
        TextView qqTextView = findViewById(R.id.text_qq);
        TextView wechatTextView = findViewById(R.id.text_wechat);
        TextView categoryTextView = findViewById(R.id.text_category);
        ImageView avatarImageView = findViewById(R.id.image_avatar);
        RecyclerView recyclerViewImages = findViewById(R.id.recycler_view_selected_images);
        buttonLike = findViewById(R.id.button_like);
        buttonComment = findViewById(R.id.button_comment);
        textLike = findViewById(R.id.text_like);

        // 初始化评论相关的视图
        textComment = findViewById(R.id.text_comment);
        editComment = findViewById(R.id.edit_comment);
        btnComment = findViewById(R.id.btn_comment);
        recyclerComment = findViewById(R.id.recycler_comment);
        linComment = findViewById(R.id.lin_comment);
        emptyView = findViewById(R.id.empty_view);

        // 设置评论列表的布局管理器
        recyclerComment.setLayoutManager(new LinearLayoutManager(this));

        contentTextView.setText(content);
        dateTextView.setText(createdAt);
        nicknameTextView.setText(nickname);
        qqTextView.setText("QQ: " + qq);
        wechatTextView.setText("微信: " + wechat);
        categoryTextView.setText(categoryName);

        Glide.with(this)
                .load(avatar)
                .placeholder(R.mipmap.nmtx)
                .circleCrop()
                .into(avatarImageView);

        // 获取用户ID
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        // 获取最新的点赞数
        Api.ApiService apiService = Api.getApiService();
        Call<ResponseBody> call = apiService.getLikesCount(postId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        int likesCount = jsonResponse.getInt("likes_count");
                        textLike.setText(String.valueOf(likesCount));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "获取点赞数失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 获取用户对帖子的点赞状态
        if (userId != -1) {
            Call<ResponseBody> likeStatusCall = apiService.getLikeStatus(postId, userId);
            likeStatusCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            boolean isLiked = jsonResponse.getBoolean("is_liked");
                            if (isLiked) {
                                buttonLike.setImageResource(R.drawable.like1);
                            } else {
                                buttonLike.setImageResource(R.drawable.like);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(PostDetailActivity.this, "获取点赞状态失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 点赞按钮点击事件
        buttonLike.setOnClickListener(v -> {
            if (userId != -1) {
                Call<ResponseBody> likeCall = apiService.likePost(postId, userId);
                likeCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseBody = response.body().string();
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String action = jsonResponse.getString("action");

                                int currentLikes = Integer.parseInt(textLike.getText().toString());

                                if ("liked".equals(action)) {
                                    textLike.setText(String.valueOf(currentLikes + 1));
                                    buttonLike.setImageResource(R.drawable.like1);
                                } else if ("unliked".equals(action)) {
                                    textLike.setText(String.valueOf(currentLikes - 1));
                                    buttonLike.setImageResource(R.drawable.like);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(PostDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(PostDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        });

        // 评论按钮点击事件
        buttonComment.setOnClickListener(v -> {
            linComment.setVisibility(View.VISIBLE);
            editComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editComment, InputMethodManager.SHOW_IMPLICIT);
        });

        // 加载评论数据
        loadComments();

        // 设置发表评论按钮的点击事件
        btnComment.setOnClickListener(v -> {
            String commentContent = editComment.getText().toString().trim();
            if (!commentContent.isEmpty()) {
                if (userId != -1) {
                    submitComment(commentContent, userId);

                    // 隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editComment.getWindowToken(), 0);

                    // 隐藏评论输入框
                    linComment.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            }
        });

        // 处理图片链接
        if (imageLinks != null && !imageLinks.isEmpty()) {
            try {
                JSONArray imageLinksArray = new JSONArray(imageLinks);
                List<String> imageUrls = new ArrayList<>();
                for (int i = 0; i < imageLinksArray.length(); i++) {
                    imageUrls.add(imageLinksArray.getString(i));
                }

                PostImagesAdapter imagesAdapter = new PostImagesAdapter(this, imageUrls);
                recyclerViewImages.setLayoutManager(new GridLayoutManager(this, 3)); // 设置为网格布局，每行三张图片
                recyclerViewImages.setAdapter(imagesAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 加载评论列表
    private void loadComments() {
        Api.ApiService apiService = Api.getApiService();

        // 获取评论总数
        Call<ResponseBody> countCall = apiService.getCommentsCount(postId);
        countCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        int commentsCount = jsonResponse.getInt("comments_count");
                        textComment.setText(String.valueOf(commentsCount));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "获取评论数失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 获取评论列表
        Call<List<Api.Comment>> listCall = apiService.getComments(postId);
        listCall.enqueue(new Callback<List<Api.Comment>>() {
            @Override
            public void onResponse(Call<List<Api.Comment>> call, Response<List<Api.Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Api.Comment> comments = response.body();
                    if (comments.isEmpty()) {
                        recyclerComment.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerComment.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                        CommentAdapter adapter = new CommentAdapter(comments);
                        recyclerComment.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Api.Comment>> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "加载评论失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 提交评论
    private void submitComment(String content, int userId) {
        Api.ApiService apiService = Api.getApiService();
        Call<ResponseBody> call = apiService.addComment(postId, userId, content);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PostDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    editComment.setText(""); // 清空输入框
                    loadComments(); // 重新加载评论列表
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}