# Campus Wall - 基于原生Android开发的校园墙应用

## 项目简介

Campus Wall是一个面向校园用户的社交分享平台，采用Android原生开发。该应用允许用户发布、浏览和互动各类校园信息，包括表白、吐槽、失物招领等多个分类板块。项目采用了现代Android开发实践，使用Retrofit进行网络请求，遵循Material Design设计规范。

## 应用演示截图

| 首页浏览 | 发布内容 | 个人中心 |
|:---:|:---:|:---:|
| ![首页浏览界面](https://github.com/user-attachments/assets/1d577249-a45c-4f18-bf82-d9c2f6d9e09c) | ![发布内容界面](https://github.com/user-attachments/assets/4a80cea0-9ebe-44f2-9d83-98b6da9a63bc) | ![个人中心界面](https://github.com/user-attachments/assets/ebd97699-4f5d-41a8-b3af-64dcbe74be47) |

## 主要功能

### 1. 用户系统
- 用户注册与登录
- 个人信息管理（修改头像、昵称、联系方式等）
- 密码修改
- 用户认证状态管理

### 2. 内容发布与浏览
- 多分类发帖（表白、交友、吐槽、日常分享、失物招领、学术交流）
- 支持图片上传（最多9张）
- 支持匿名发布
- 分类浏览与最新墙

### 3. 社交互动
- 帖子点赞功能
- 评论系统
- 查看用户主页
- 联系方式展示（QQ、微信）

### 4. 内容管理
- 管理员审核机制
- 用户删除自己的帖子
- 待审核帖子管理

## 技术架构

### 1. 网络层
```java
public class Api {
    private static final String BASE_URL = "https://API域名/";
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
}
```
使用Retrofit进行网络请求，实现了RESTful API的调用，包括用户认证、内容发布、社交互动等接口。

### 2. UI架构
- 采用Fragment + Activity的组合
- 使用BottomNavigationView实现主要导航
- RecyclerView展示列表内容
- 自定义适配器处理不同类型的内容展示

### 3. 数据存储
- SharedPreferences存储用户登录状态和基本信息
- 文件系统处理图片上传
- 远程服务器存储核心数据

### 4. 图片处理
```java
public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ImageViewHolder> {
    Glide.with(context)
        .load(imageUrl)
        .placeholder(R.drawable.baseline_mood_bad_24)
        .circleCrop()
        .into(holder.imageView);
}
```
使用Glide库处理图片加载、缓存和展示，支持圆形裁剪等特效。

## 项目特点

1. **模块化设计**：
   - 清晰的包结构
   - 独立的功能模块
   - 可复用的组件

2. **用户体验**：
   - 流畅的界面切换
   - 友好的错误提示
   - 图片预览和全屏查看

3. **安全性**：
   - 用户认证机制
   - 内容审核流程
   - 敏感信息保护

4. **可扩展性**：
   - 易于添加新的内容分类
   - 支持功能模块扩展
   - 灵活的API接口设计

## 技术栈

- Android SDK
- Retrofit2 (网络请求)
- Glide (图片加载)
- RecyclerView (列表展示)
- Material Design (UI设计)
- Apache Commons Net (FTP文件上传)

## 未来展望（ai画的大饼没打算优化）

1. **功能增强**：
   - 添加私信系统
   - 实现帖子分享功能
   - 增加更多互动方式

2. **性能优化**：
   - 引入本地缓存
   - 优化图片加载
   - 减少网络请求

3. **用户体验提升**：
   - 添加夜间模式
   - 支持主题定制
   - 优化交互动画

## 总结

Campus Wall项目展示了一个完整的Android应用开发实践，涵盖了用户系统、内容管理、社交互动等核心功能。项目采用现代Android开发技术栈，注重代码质量和用户体验，是一个很好的Android开发学习参考。

## 作者有话说

这个项目是本人的毕业设计，现在毕业了分享给大家借鉴一下，写的应该不是很好但是花了很多心思，因为全程使用AI编写的我自己都看不懂。希望这个项目的开源能对学弟学妹们有所帮助！

## 使用方法

需要自行修改ProfileFragment.java，PostFragment.java，Api.java中的参数配置。
将api目录下的全部文件上传到宝塔服务器并配置common.php中的数据库信息，将sql文件导入数据库。
