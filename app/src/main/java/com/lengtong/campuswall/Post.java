package com.lengtong.campuswall;

/**
 *这是一个数据模型类，用于表示一个帖子对象。它包含了与帖子相关的各种属性，并提供了相应的getter和setter方法。具体属性包括：
 * id: 帖子的唯一标识符。
 * userId: 发帖用户的ID。
 * categoryId: 帖子所属的分类ID。
 * content: 帖子的文本内容。
 * createdAt: 帖子的创建时间。
 * categoryName: 分类的名称。
 * imageLinks: 帖子中图片的链接，存储为JSON字符串。
 * nickname: 发帖用户的昵称。
 * avatar: 发帖用户的头像链接。
 * qq: 发帖用户的QQ号。
 * wechat: 发帖用户的微信号。
 * isApproved: 帖子的审核状态。
 * 这些属性使得Post类能够完整地描述一个帖子，并且通过getter和setter方法，其他类可以方便地访问和修改这些属性。
 */
public class Post {
    private int id;             // 帖子id
    private int userId;         // 用户名id
    private int categoryId;     // 分类id
    private String content;     // 帖子内容
    private String createdAt;   // 发布时间
    private String categoryName;// 分类名
    private String imageLinks;  // 图片链接的JSON字符串
    private String nickname;    // 昵称
    private String avatar;      // 头像链接
    private String qq;          // QQ号
    private String wechat;      // 微信号
    private int isApproved;     // 审核状态
    private int likesCount;     // 点赞数
    private int commentsCount;  // 评论数

    // Getter and Setter for likesCount
    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    // Getter and Setter for commentsCount
    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for userId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter and Setter for categoryId
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    // Getter and Setter for content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter and Setter for createdAt
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Getter and Setter for categoryName
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // Getter and Setter for imageLinks
    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    // Getter and Setter for nickname
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Getter and Setter for avatar
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // Getter and Setter for qq
    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    // Getter and Setter for wechat
    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }
}