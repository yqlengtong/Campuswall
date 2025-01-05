package com.lengtong.campuswall;
/**
 * 管理员查看待审帖子的界面。
 * 显示待审帖子列表，提供审核通过和不通过的操作。
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lengtong.campuswall.Adapter.PostImagesAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PendingPostsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_posts);

        ImageView backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_view_pending_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPendingPosts();
    }

    private void loadPendingPosts() {
        Api.ApiService apiService = Api.getApiService();
        Call<List<Post>> call = apiService.getPendingPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PendingPostsAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(PendingPostsActivity.this, "获取待审核帖子失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(PendingPostsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class PendingPostsAdapter extends RecyclerView.Adapter<PendingPostsAdapter.ViewHolder> {
        private List<Post> posts;

        public PendingPostsAdapter(List<Post> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_post, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post post = posts.get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView avatarImageView;
            TextView nicknameTextView;
            TextView qqTextView;
            TextView wechatTextView;
            TextView contentTextView;
            TextView dateTextView;
            TextView categoryTextView;
            RecyclerView recyclerViewImages;
            Button approveButton;
            Button rejectButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                avatarImageView = itemView.findViewById(R.id.image_avatar);
                nicknameTextView = itemView.findViewById(R.id.text_nickname);
                qqTextView = itemView.findViewById(R.id.text_qq);
                wechatTextView = itemView.findViewById(R.id.text_wechat);
                contentTextView = itemView.findViewById(R.id.text_content);
                dateTextView = itemView.findViewById(R.id.text_date);
                categoryTextView = itemView.findViewById(R.id.text_category);
                recyclerViewImages = itemView.findViewById(R.id.recycler_view_selected_images);
                approveButton = itemView.findViewById(R.id.button_approve);
                rejectButton = itemView.findViewById(R.id.button_reject);
            }

            public void bind(Post post) {
                contentTextView.setText(post.getContent());
                dateTextView.setText(post.getCreatedAt());
                nicknameTextView.setText(post.getNickname());
                qqTextView.setText("QQ: " + post.getQq());
                wechatTextView.setText("微信: " + post.getWechat());
                categoryTextView.setText("分类: " + post.getCategoryName());

                // 使用 Glide 加载用户头像
                Glide.with(itemView.getContext())
                        .load(post.getAvatar())
                        .placeholder(R.drawable.baseline_mood_bad_24)
                        .circleCrop()
                        .into(avatarImageView);

                // 解析图片链接并设置图片适配器
                String imageLinks = post.getImageLinks();
                if (imageLinks != null && !imageLinks.isEmpty()) {
                    try {
                        JSONArray imageLinksArray = new JSONArray(imageLinks);
                        List<String> imageUrls = new ArrayList<>();
                        for (int i = 0; i < imageLinksArray.length(); i++) {
                            imageUrls.add(imageLinksArray.getString(i));
                        }

                        PostImagesAdapter imagesAdapter = new PostImagesAdapter(itemView.getContext(), imageUrls);
                        recyclerViewImages.setLayoutManager(new GridLayoutManager(itemView.getContext(), 3)); // 设置为网格布局，每行三张图片
                        recyclerViewImages.setAdapter(imagesAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    recyclerViewImages.setAdapter(null);
                }

                approveButton.setOnClickListener(v -> changePostStatus(post.getId(), "approve"));
                rejectButton.setOnClickListener(v -> changePostStatus(post.getId(), "reject"));
            }
        }
    }

    private void changePostStatus(int postId, String action) {
        Api.ApiService apiService = Api.getApiService();
        Call<Void> call = apiService.changePostStatus(postId, action);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PendingPostsActivity.this, action.equals("approve") ? "审核通过" : "审核不通过", Toast.LENGTH_SHORT).show();
                    loadPendingPosts(); // 重新加载待审核帖子
                } else {
                    Toast.makeText(PendingPostsActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PendingPostsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}