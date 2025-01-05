package com.lengtong.campuswall.Fragment;
/**
 * 显示首页内容的Fragment。
 * 包含分类按钮，点击后加载相应分类的帖子。
 * 使用RecyclerView显示帖子列表。
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lengtong.campuswall.Api;
import com.lengtong.campuswall.Post;
import com.lengtong.campuswall.Adapter.PostAdapter;
import com.lengtong.campuswall.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private Button currentSelectedButton;
    private List<Post> postList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*// 添加分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);*/

        // 初始化适配器并设置给RecyclerView
        postAdapter = new PostAdapter(getContext(), postList, true);
        recyclerView.setAdapter(postAdapter);

        // 设置分类按钮点击事件
        Button btnLatest = view.findViewById(R.id.btn_latest);
        Button btnConfession = view.findViewById(R.id.btn_confession);
        Button btnPurchase = view.findViewById(R.id.btn_purchase);
        Button btnInternship = view.findViewById(R.id.btn_internship);
        Button btnDailyShare = view.findViewById(R.id.btn_daily_share);
        Button btnFriend = view.findViewById(R.id.btn_friend);
        Button btnAcademic = view.findViewById(R.id.btn_academic);

        View.OnClickListener categoryClickListener = v -> {
            if (currentSelectedButton != null) {
                currentSelectedButton.setBackgroundColor(getResources().getColor(R.color.default_button_color));
            }
            Button selectedButton = (Button) v;
            selectedButton.setBackgroundColor(getResources().getColor(R.color.selected_button_color));
            currentSelectedButton = selectedButton;

            Integer categoryId = null;
            if (v == btnConfession) categoryId = 1;
            else if (v == btnPurchase) categoryId = 2;
            else if (v == btnInternship) categoryId = 3;
            else if (v == btnDailyShare) categoryId = 4;
            else if (v == btnFriend) categoryId = 5;
            else if (v == btnAcademic) categoryId = 6;

            loadPosts(categoryId);
        };

        btnLatest.setOnClickListener(categoryClickListener);
        btnConfession.setOnClickListener(categoryClickListener);
        btnPurchase.setOnClickListener(categoryClickListener);
        btnInternship.setOnClickListener(categoryClickListener);
        btnDailyShare.setOnClickListener(categoryClickListener);
        btnFriend.setOnClickListener(categoryClickListener);
        btnAcademic.setOnClickListener(categoryClickListener);

        // 默认加载“最新墙”
        btnLatest.performClick();

        return view;
    }

    private void loadPosts(Integer categoryId) {
        Api.ApiService apiService = Api.getApiService();
        Call<List<Post>> call = apiService.getPosts(categoryId);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());

                    // 判断是否是“最新墙”
                    boolean isLatestWall = (categoryId == null);

                    postAdapter = new PostAdapter(getContext(), postList, isLatestWall);
                    recyclerView.setAdapter(postAdapter);
                    postAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "获取帖子失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("HomeFragment", "Context is null, cannot show Toast");
                }
            }
        });
    }
}