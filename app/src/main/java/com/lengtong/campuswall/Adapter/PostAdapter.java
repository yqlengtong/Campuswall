package com.lengtong.campuswall.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lengtong.campuswall.Api;
import com.lengtong.campuswall.Post;
import com.lengtong.campuswall.PostDetailActivity;
import com.lengtong.campuswall.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private boolean isLatestWall;
    private Context context;

    public PostAdapter(Context context, List<Post> postList, boolean isLatestWall) {
        this.context = context;
        this.postList = postList;
        this.isLatestWall = isLatestWall;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.contentTextView.setText(post.getContent());
        holder.dateTextView.setText(post.getCreatedAt());

        // 设置用户信息
        holder.nicknameTextView.setText(post.getNickname());
        holder.qqTextView.setText("QQ: " + post.getQq());
        holder.wechatTextView.setText("微信: " + post.getWechat());

        // 仅在最新墙中显示所属分类
        if (isLatestWall) {
            holder.categoryTextView.setText("所属分类：" + post.getCategoryName());
            holder.categoryTextView.setVisibility(View.VISIBLE);
        } else {
            holder.categoryTextView.setVisibility(View.GONE);
        }

        // 使用Glide加载用户头像
        Glide.with(context)
                .load(post.getAvatar())
                .placeholder(R.mipmap.nmtx)
                .circleCrop()
                .into(holder.avatarImageView);

        // 获取用户ID
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        // 获取最新的点赞数
        Api.ApiService apiService = Api.getApiService();
        Call<ResponseBody> call = apiService.getLikesCount(post.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        int likesCount = jsonResponse.getInt("likes_count");
                        post.setLikesCount(likesCount);
                        holder.textLike.setText(String.valueOf(likesCount));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "获取点赞数失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 获取评论总数
        Call<ResponseBody> commentCountCall = apiService.getCommentsCount(post.getId());
        commentCountCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        int commentsCount = jsonResponse.getInt("comments_count");
                        holder.textComment.setText(String.valueOf(commentsCount));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "获取评论数失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 获取用户对帖子的点赞状态
        if (userId != -1) {
            Call<ResponseBody> likeStatusCall = apiService.getLikeStatus(post.getId(), userId);
            likeStatusCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            boolean isLiked = jsonResponse.getBoolean("is_liked");
                            if (isLiked) {
                                holder.buttonLike.setImageResource(R.drawable.like1); // 已点赞图标
                            } else {
                                holder.buttonLike.setImageResource(R.drawable.like); // 未点赞图标
                            }

                            // 点赞按钮点击事件
                            holder.buttonLike.setOnClickListener(v -> {
                                Call<ResponseBody> likeCall = apiService.likePost(post.getId(), userId);
                                likeCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            try {
                                                String responseBody = response.body().string();
                                                JSONObject jsonResponse = new JSONObject(responseBody);
                                                String action = jsonResponse.getString("action");
                                                if ("liked".equals(action)) {
                                                    // 更新点赞数
                                                    post.setLikesCount(post.getLikesCount() + 1);
                                                    holder.textLike.setText(String.valueOf(post.getLikesCount()));
                                                    // 更新UI
                                                    holder.buttonLike.setImageResource(R.drawable.like1); // 已点赞图标
                                                } else if ("unliked".equals(action)) {
                                                    // 更新点赞数
                                                    post.setLikesCount(post.getLikesCount() - 1);
                                                    holder.textLike.setText(String.valueOf(post.getLikesCount()));
                                                    // 更新UI
                                                    holder.buttonLike.setImageResource(R.drawable.like); // 未点赞图标
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "获取点赞状态失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 解析图片链接JSON
        String imageLinks = post.getImageLinks();
        if (imageLinks != null && !imageLinks.isEmpty()) {
            try {
                JSONArray imageLinksArray = new JSONArray(imageLinks);
                List<String> imageUrls = new ArrayList<>();
                for (int i = 0; i < imageLinksArray.length(); i++) {
                    imageUrls.add(imageLinksArray.getString(i));
                }

                // 设置图片适配器
                PostImagesAdapter imagesAdapter = new PostImagesAdapter(context, imageUrls);
                holder.recyclerViewImages.setLayoutManager(new GridLayoutManager(context, 3)); // 设置为网格布局，每行三张图片
                holder.recyclerViewImages.setAdapter(imagesAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // 设置点击事件
        View.OnClickListener detailClickListener = v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("content", post.getContent());
            intent.putExtra("createdAt", post.getCreatedAt());
            intent.putExtra("nickname", post.getNickname());
            intent.putExtra("qq", post.getQq());
            intent.putExtra("wechat", post.getWechat());
            intent.putExtra("avatar", post.getAvatar());
            intent.putExtra("categoryName", post.getCategoryName());
            intent.putExtra("imageLinks", post.getImageLinks());
            context.startActivity(intent);
        };

        holder.itemView.findViewById(R.id.PostDetails).setOnClickListener(detailClickListener);
        holder.itemView.findViewById(R.id.button_comment).setOnClickListener(detailClickListener);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView;
        TextView dateTextView;
        TextView nicknameTextView;
        TextView qqTextView;
        TextView wechatTextView;
        TextView categoryTextView;
        ImageView avatarImageView;
        RecyclerView recyclerViewImages;
        TextView textLike;
        TextView textComment;
        ImageButton buttonLike;

        PostViewHolder(View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.text_content);
            dateTextView = itemView.findViewById(R.id.text_date);
            nicknameTextView = itemView.findViewById(R.id.text_nickname);
            qqTextView = itemView.findViewById(R.id.text_qq);
            wechatTextView = itemView.findViewById(R.id.text_wechat);
            categoryTextView = itemView.findViewById(R.id.text_category);
            avatarImageView = itemView.findViewById(R.id.image_avatar);
            recyclerViewImages = itemView.findViewById(R.id.recycler_view_selected_images);
            textLike = itemView.findViewById(R.id.text_like);
            textComment = itemView.findViewById(R.id.text_comment);
            buttonLike = itemView.findViewById(R.id.button_like);
        }
    }
}