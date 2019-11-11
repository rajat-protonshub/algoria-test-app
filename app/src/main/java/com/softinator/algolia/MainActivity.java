package com.softinator.algolia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.softinator.algolia.adapters.StoriesAdapter;
import com.softinator.algolia.models.Hits;
import com.softinator.algolia.network.AlgoliaApi;
import com.softinator.algolia.network.IAlogiaService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView selectedStories;
    private TextView retry;

    private IAlogiaService algoliaService;
    private StoriesAdapter viewBinderData;

    private int pageNum = 1;
    private boolean canLoadData = true;
    private boolean hardReset = false;
    private final String TAG = "MAINACTIVITY";
    private int storySelectCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.storiesContainerSRL);
        recyclerView = (RecyclerView) findViewById(R.id.storiesRV);
        progressBar = (ProgressBar) findViewById(R.id.loadingStoriesPB);
        selectedStories = (TextView) findViewById(R.id.selectedStoriesCountTV);
        retry = (TextView) findViewById(R.id.retryTV );
        retry.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                hardReset = true;
                pageNum  = 1;
                loadData();
            }

        });

        //init service and load data
        algoliaService = AlgoliaApi.getClient().create(IAlogiaService.class);
        viewBinderData = new StoriesAdapter(getApplicationContext(), new ArrayList<Hits.Story>()) {
            @Override
            public void onSwitchToggle(boolean state) {
                updateStoriesCounter(state);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(viewBinderData);

        initScrollListener();
        loadData();
    }


    /**
     *
     */
    private void loadData() {
        Log.i(TAG, "Loading Data...");
        Call<Hits> call = algoliaService.getStories("story", pageNum);
        call.enqueue(new Callback<Hits>() {

            @Override
            public void onResponse(Call<Hits> call, Response<Hits> response) {

                if(hardReset){
                    viewBinderData.resetStories();
                    hardReset = false;
                    swipeContainer.setRefreshing(false);
                }

                Hits hits = response.body();
                viewBinderData.addStories(hits.getStories());
                canLoadData = true;
                progressBar.setVisibility(View.GONE );
            }

            @Override
            public void onFailure(Call<Hits> call, Throwable t) {
                canLoadData = true;
                progressBar.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                swipeContainer.setVisibility(View.GONE);
            }
        });
    }


    /**
     *
     */
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if(canLoadData){
                    //bottom of list!
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == viewBinderData.getItemCount() - 1) {
                        Log.i(TAG, "Loading More Data");
                        pageNum++;
                        canLoadData = false;
                        progressBar.setVisibility(View.VISIBLE);
                        loadData();

                    }
                }
            }
        });
    }


    /**
     *
     */

    private void updateStoriesCounter(boolean state) {
        if(state){
            storySelectCount++;
        }else{
            storySelectCount--;
        }

        if(storySelectCount==0){
            selectedStories.setText("");
        }else{
            selectedStories.setText("("+storySelectCount+")");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.retryTV:
                progressBar.setVisibility(View.VISIBLE);
                retry.setVisibility(View.GONE);
                swipeContainer.setVisibility(View.VISIBLE);
                loadData();
                break;
        }
    }
}
