package com.lengtong.campuswall.Fragment;
/**
 * 用户发布帖子的Fragment。
 * 处理媒体选择和上传，提交帖子内容到服务器。
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lengtong.campuswall.Adapter.SelectedImagesAdapter;
import com.lengtong.campuswall.Api;
import com.lengtong.campuswall.R;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_MEDIA = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private Spinner spinnerCategory;
    private EditText editTextContent;
    private Button buttonSubmit;
    private Button buttonSelectMedia;
    private CheckBox checkboxAnonymous;
    private RecyclerView recyclerViewSelectedImages;
    private SelectedImagesAdapter selectedImagesAdapter;
    private List<Uri> selectedMediaUris = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        spinnerCategory = view.findViewById(R.id.spinner_category);
        editTextContent = view.findViewById(R.id.edit_text_content);
        buttonSubmit = view.findViewById(R.id.button_submit);
        buttonSelectMedia = view.findViewById(R.id.btn_select_media);
        checkboxAnonymous = view.findViewById(R.id.checkbox_anonymous);
        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images);

        // 初始化时隐藏RecyclerView
        recyclerViewSelectedImages.setVisibility(View.GONE);

        // 检查并请求权限
        if (!hasPermissions()) {
            requestPermissions();
        }

        // 设置分类选择器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // 设置RecyclerView为网格布局，每行显示三张图片
        recyclerViewSelectedImages.setLayoutManager(new GridLayoutManager(getContext(), 3));
        selectedImagesAdapter = new SelectedImagesAdapter(getContext(), selectedMediaUris);
        recyclerViewSelectedImages.setAdapter(selectedImagesAdapter);

        // 设置提交按钮点击事件
        buttonSubmit.setOnClickListener(v -> submitPost());

        // 设置选择媒体按钮点击事件
        buttonSelectMedia.setOnClickListener(v -> openMediaSelector());

        return view;
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "需要存储权限以访问媒体文件", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openMediaSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_SELECT_MEDIA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_MEDIA && resultCode == Activity.RESULT_OK && data != null) {
            selectedMediaUris.clear();
            if (data.getClipData() != null) {
                // 多选
                int count = data.getClipData().getItemCount();
                if (count > 9) {
                    Toast.makeText(getContext(), "最多只能选择9张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedMediaUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                // 单选
                selectedMediaUris.add(data.getData());
            }

            // 更新适配器
            selectedImagesAdapter.notifyDataSetChanged();

            // 根据选择的图片数量设置RecyclerView的可见性
            if (selectedMediaUris.isEmpty()) {
                recyclerViewSelectedImages.setVisibility(View.GONE);
            } else {
                recyclerViewSelectedImages.setVisibility(View.VISIBLE);
            }
        }
    }

    private void submitPost() {
        String content = editTextContent.getText().toString();
        int categoryId = spinnerCategory.getSelectedItemPosition() + 1; // 假设分类ID从1开始
        boolean isAnonymous = checkboxAnonymous.isChecked();

        if (content.isEmpty()) {
            Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取用户ID
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "用户未登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 上传媒体文件（如果有）
        if (!selectedMediaUris.isEmpty()) {
            uploadMediaFiles(userId, categoryId, content, isAnonymous);
        } else {
            // 如果没有图片，传递一个空的JSON数组字符串
            String emptyImageLinksJson = "[]";
            submitPostToServer(userId, categoryId, content, emptyImageLinksJson, isAnonymous);
        }
    }

    private void uploadMediaFiles(int userId, int categoryId, String content, boolean isAnonymous) {
        new Thread(() -> {
            FTPClient ftpClient = new FTPClient();
            List<String> imageLinks = new ArrayList<>();
            try {
                ftpClient.connect("v0.ftp.upyun.com", 21);
                ftpClient.login("lengtong/img-lengtong", "y1bjiyRh7s3B65yY9BSqxBteACnSQnrN");
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                // 指定上传目录
                String uploadDirectory = "/campuswall";
                ftpClient.changeWorkingDirectory(uploadDirectory);

                for (Uri mediaUri : selectedMediaUris) {
                    try (InputStream inputStream = getActivity().getContentResolver().openInputStream(mediaUri)) {
                        String fileExtension = getFileExtension(mediaUri);
                        String randomFileName = UUID.randomUUID().toString() + fileExtension;

                        boolean done = ftpClient.storeFile(randomFileName, inputStream);
                        if (done) {
                            // 假设上传成功后可以生成图片链接
                            String imageUrl = "https://img.jmm0.cn/campuswall/" + randomFileName;
                            imageLinks.add(imageUrl);
                        } else {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "媒体上传失败", Toast.LENGTH_SHORT).show());
                            return;
                        }
                    }
                }

                // 将图片链接列表转换为JSON字符串
                String imageLinksJson = new JSONArray(imageLinks).toString();

                // 所有媒体文件上传成功后，提交帖子
                getActivity().runOnUiThread(() -> submitPostToServer(userId, categoryId, content, imageLinksJson, isAnonymous));

            } catch (Exception ex) {
                ex.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "媒体上传错误", Toast.LENGTH_SHORT).show());
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

    private void submitPostToServer(int userId, int categoryId, String content, String imageLinksJson, boolean isAnonymous) {
        Api.ApiService apiService = Api.getApiService();
        Call<Void> call = apiService.submitPost(userId, categoryId, content, imageLinksJson, isAnonymous);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "发布成功，请等待审核！", Toast.LENGTH_SHORT).show();
                    editTextContent.setText(""); // 清空输入框
                    selectedMediaUris.clear(); // 清空已选媒体
                    selectedImagesAdapter.notifyDataSetChanged(); // 更新适配器
                    recyclerViewSelectedImages.setVisibility(View.GONE); // 隐藏RecyclerView
                } else {
                    Toast.makeText(getContext(), "发布失败", Toast.LENGTH_SHORT).show();
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
            extension = mime.getExtensionFromMimeType(getActivity().getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        return extension != null ? "." + extension : "";
    }
}