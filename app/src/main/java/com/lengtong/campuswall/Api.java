package com.lengtong.campuswall;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class Api {
    private static final String BASE_URL = "https://wall.kak1.cn/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public interface ApiService {
        // 注册
        @FormUrlEncoded
        @POST("register.php")
        Call<ResponseBody> registerUser(@Field("username") String username, @Field("password") String password);

        // 登录
        @FormUrlEncoded
        @POST("login.php")
        Call<ResponseBody> loginUser(@Field("username") String username, @Field("password") String password);

        // 获取帖子
        @FormUrlEncoded
        @POST("getPosts.php")
        Call<List<Post>> getPosts(@Field("category_id") Integer categoryId);

        // 发布
        @FormUrlEncoded
        @POST("submitPost.php")
        Call<Void> submitPost(
                @Field("user_id") int userId,
                @Field("category_id") int categoryId,
                @Field("content") String content,
                @Field("image_links") String imageLinksJson,
                @Field("is_anonymous") boolean isAnonymous);

        // 获取待审帖子
        @POST("getPendingPosts.php")
        Call<List<Post>> getPendingPosts();

        // 处理待审帖子
        @FormUrlEncoded
        @POST("approvePost.php")
        Call<Void> changePostStatus(@Field("post_id") int postId, @Field("action") String action);

        // 修改密码
        @FormUrlEncoded
        @POST("changePassword.php")
        Call<Void> changePassword(@Field("user_id") int userId, @Field("old_password") String oldPassword, @Field("new_password") String newPassword);

        // 获取用户信息
        @FormUrlEncoded
        @POST("getUserInfo.php")
        Call<UserInfo> getUserInfo(@Field("username") String username);

        // 获取用户帖子
        @FormUrlEncoded
        @POST("getUserPosts.php")
        Call<List<Post>> getUserPosts(@Field("username") String username);

        // 删除帖子
        @FormUrlEncoded
        @POST("deletePost.php")
        Call<ResponseBody> deletePost(@Field("post_id") int postId);

        // 更新头像
        @FormUrlEncoded
        @POST("updateAvatar.php")
        Call<Void> updateAvatar(@Field("user_id") int userId, @Field("avatar") String avatarUrl);

        // 修改用户信息
        @FormUrlEncoded
        @POST("updateUserInfo.php")
        Call<Void> updateUserInfo(@Field("username") String username, @Field("nickname") String nickname, @Field("qq") String qq, @Field("wechat") String wechat);

        // 获取点赞数
        @FormUrlEncoded
        @POST("getLikesCount.php")
        Call<ResponseBody> getLikesCount(@Field("post_id") int postId);

        // 获取点赞状态
        @FormUrlEncoded
        @POST("getLikeStatus.php")
        Call<ResponseBody> getLikeStatus(@Field("post_id") int postId, @Field("user_id") int userId);

        // 点赞
        @FormUrlEncoded
        @POST("likePost.php")
        Call<ResponseBody> likePost(@Field("post_id") int postId, @Field("user_id") int userId);

        // 获取评论数
        @FormUrlEncoded
        @POST("getCommentsCount.php")
        Call<ResponseBody> getCommentsCount(@Field("post_id") int postId);

        // 获取评论
        @FormUrlEncoded
        @POST("getComments.php")
        Call<List<Comment>> getComments(@Field("post_id") int postId);

        // 添加评论
        @FormUrlEncoded
        @POST("addComment.php")
        Call<ResponseBody> addComment(@Field("post_id") int postId, @Field("user_id") int userId, @Field("content") String content);
    }

    // 在 Api 类中添加 UserInfo 类
    public static class UserInfo {
        public String username;
        public String nickname;
        public String avatar;
        public String qq;
        public String wechat;
        public String role; // 用户身份
    }

    // 在 Api 类中添加 Comment 类
    public static class Comment {
        public int id;
        public String content;
        public String created_at; // 确保字段名与服务器返回的JSON字段名一致
        public String nickname;
        public String avatar;
    }
}