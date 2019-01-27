package com.homework.grop.group_homework;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.homework.grop.group_homework.network.FeedResponse;
import com.homework.grop.group_homework.network.IMiniDouyinService;
import com.homework.grop.group_homework.network.RetrofitManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MyAdapter.DetailInformation {
    private RecyclerView mNumbersListView;
    private MyAdapter myAdapter;
    private LinearLayoutManager layoutManager;
    private List<Feed>feeds;
    private  StandardGSYVideoPlayer standardGSYVideoPlayer;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                   // mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };



    public void fetchFeed() {
        RetrofitManager.get(IMiniDouyinService.HOST).create(IMiniDouyinService.class).fetchFeed().enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                //Log.d(TAG, "onResponse() called with: call = [" + call + "], response = [" + response.body() + "]");
                if (response.isSuccessful()) {
                    feeds.clear();
                    feeds.addAll(response.body().getFeeds());
                    mNumbersListView.getAdapter().notifyDataSetChanged();
                } else {

                    Toast.makeText(MainActivity.this, "fetch feed failure!", Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.d("aaaaa", "onResponse: "+MainActivity.this+t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }





    public void setfeeds(){
        feeds=new LinkedList<>();
        fetchFeed();
//        Feed feed=new Feed();
//        feed.setImageUrl("https://cdn2.thecatapi.com/images/2a1.jpg");
//        feed.setVideoUrl("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
//        feed.setStudentId("1120172710");
//        feed.setUserName("顾骁");
//        for(int i=0;i<20;i++)
//            feeds.add(feed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //获取信息列表
        setfeeds();

        //设置recyclerview
        mNumbersListView = findViewById(R.id.my_list);
        myAdapter=new MyAdapter(feeds,this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNumbersListView.setLayoutManager(layoutManager);
        mNumbersListView.setHasFixedSize(true);
        mNumbersListView.setAdapter(myAdapter);
        //自动播放
        mNumbersListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem   = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if(dy>=0) {//下滑
                    StandardGSYVideoPlayer mStandardGSYVideoPlayer = (StandardGSYVideoPlayer) recyclerView.getChildAt(lastVisibleItem - firstVisibleItem).findViewById(R.id.detail_player);
                    int[] screenPosition = new int[2];
                    mStandardGSYVideoPlayer.getLocationOnScreen(screenPosition);
                    //Log.d("ccc", "onScrolled: "+mStandardGSYVideoPlayer.getHeight());
                    //大概第二个视频居中时底下视频开始播放
                    if (screenPosition[1] <= 800 && !mStandardGSYVideoPlayer.isInPlayingState()) {
                        mStandardGSYVideoPlayer.startPlayLogic();
                        standardGSYVideoPlayer=mStandardGSYVideoPlayer;
                    }
                }else{//上划
                    if(lastVisibleItem==firstVisibleItem){
                        //上划刷新




                    }else{
                    StandardGSYVideoPlayer mStandardGSYVideoPlayer = (StandardGSYVideoPlayer) recyclerView.getChildAt(lastVisibleItem - firstVisibleItem-1).findViewById(R.id.detail_player);
                    int[] screenPosition = new int[2];
                    mStandardGSYVideoPlayer.getLocationOnScreen(screenPosition);
                    if (screenPosition[1] >= -800 && !mStandardGSYVideoPlayer.isInPlayingState())
                        mStandardGSYVideoPlayer.startPlayLogic();
                        standardGSYVideoPlayer=mStandardGSYVideoPlayer;
                    }
                }
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这里申请权限

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },0);
                //开启拍照模式
                startActivity(new Intent(MainActivity.this,TakeCamera.class));
            }
        });

    }


    @Override
    public void onStop()
    {super.onStop();
    if(standardGSYVideoPlayer!=null)
        standardGSYVideoPlayer.release();
    }
    @Override
    public void openDetailInformation(Feed feed) {
        Intent intent=new Intent(this,DetailVideoActivity.class);
        intent.putExtra("video_url",feed.getVideoUrl());
        intent.putExtra("image_url",feed.getImageUrl());
        intent.putExtra("username",feed.getUserName());
        intent.putExtra("student_id",feed.getStudentId());
        startActivity(intent);
    }
}
