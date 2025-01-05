package com.lengtong.campuswall.Adapter;
/**
 * RecyclerView适配器，用于显示帖子中的图片组。
 * 点击图片->查看全屏(ImageDialogFragment)。
 */

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lengtong.campuswall.R;

import java.util.List;

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ImageViewHolder> {
    private Context context;
    private List<String> imageUrls;

    public PostImagesAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context).load(imageUrl).into(holder.imageView);

        holder.imageView.setOnClickListener(v -> showFullScreenImage(imageUrl));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    private void showFullScreenImage(String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.fragment_image_dialog);
        ImageView imageView = dialog.findViewById(R.id.fullscreen_image_view);
        Glide.with(context).load(imageUrl).into(imageView);

        // 添加点击事件监听器，点击图片关闭对话框
        imageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}