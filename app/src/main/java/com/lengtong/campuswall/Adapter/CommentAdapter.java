package com.lengtong.campuswall.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lengtong.campuswall.Api;
import com.lengtong.campuswall.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Api.Comment> commentList;

    public CommentAdapter(List<Api.Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Api.Comment comment = commentList.get(position);
        holder.nicknameTextView.setText(comment.nickname);
        holder.contentTextView.setText(comment.content);
        holder.dateTextView.setText(comment.created_at);

        // 使用 Glide 加载头像
        Glide.with(holder.itemView.getContext())
                .load(comment.avatar)
                .placeholder(R.drawable.baseline_mood_bad_24) // 占位图
                .circleCrop() // 圆形裁剪
                .into(holder.avatarImageView);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView nicknameTextView;
        TextView contentTextView;
        TextView dateTextView;
        ImageView avatarImageView; // 新增的 ImageView

        CommentViewHolder(View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.text_comment_nickname);
            contentTextView = itemView.findViewById(R.id.text_comment_content);
            dateTextView = itemView.findViewById(R.id.text_comment_date);
            avatarImageView = itemView.findViewById(R.id.image_avatar); // 初始化
        }
    }
}