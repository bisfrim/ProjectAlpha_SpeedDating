package com.bizzy.projectalpha.speeddating;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.adapter.UsernearmeParseAdapter;
import com.bizzy.projectalpha.speeddating.listeners.OnGridItemSelectedListener;
import com.bizzy.projectalpha.speeddating.loadmore.PersonAdapter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.rollviewpager.Util;

/**
 * Created by nubxf5 on 6/23/2016.
 */
public class MaleFragmentTab extends Fragment implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener,OnGridItemSelectedListener {

    private GridView mUserGrid;
    private UsernearmeParseAdapter maleAdapter, currentAdapter;
    private Boolean isInternetPresent = false;
    private ConnectionDetect connectionDetect;
    Snackbar snackbar;
    private RelativeLayout coordinatorLayout;

    private EasyRecyclerView recyclerView;
    private PersonAdapter adapter;
    private Handler handler = new Handler();

    private int page = 0;
    private boolean hasNetWork = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.activity_men_tab,container,false);

        mUserGrid = (GridView) v.findViewById(R.id.user_near_me_grid);
        recyclerView = (EasyRecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(getActivity(),0.5f), Util.dip2px(getActivity(),72),0);
        itemDecoration.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapterWithProgress(adapter = new PersonAdapter(getActivity()));
        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) { //check for the click position and open other users profile
                //adapter.remove(position);
                //return true;
                //currentAdapter = new UsernearmeParseAdapter(getActivity(), position);
                Intent profileIntent = new Intent(getActivity(), UsersProfileActivity.class);
                profileIntent.putExtra(UsersProfileActivity.EXTRA_USER_ID, adapter.getItem(position).getName());
                MaleFragmentTab.this.startActivity(profileIntent);

            }
        });
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });

        //mUserGrid.setOnItemClickListener((AdapterView.OnItemClickListener) new PeopleNearMeActivity());


        recyclerView.setRefreshListener(this);
        onRefresh();


        return v;
    }

    @Override
    public void onLoadMore() {
        Log.i("EasyRecyclerView","onLoadMore");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (!hasNetWork) {
                    adapter.pauseMore();
                    return;
                }
                adapter.addAll(DataProvider.getPersonList(page)); // this is fake 3rd party Data class, will call our method here
                //getMaleUsers();
                page++;
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {
        page = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                //刷新
                if (!hasNetWork) {
                    adapter.pauseMore();
                    return;
                }
                adapter.addAll(DataProvider.getPersonList(page));
                //getMaleUsers();
                page=1;
            }
        }, 1000);
    }


    protected void getMaleUsers(){
        if(maleAdapter == null){
            if(maleAdapter == null){
                maleAdapter = new UsernearmeParseAdapter(getActivity(), UsernearmeParseAdapter.TYPE_MALE);
            }
            currentAdapter = maleAdapter;
            mUserGrid.setAdapter(currentAdapter);
            currentAdapter.loadObjects();
            //mWaitForInternetConnectionView.close();

        }

    }


    @Override
    public void onGridItemClick(View v, int position) {
        Toast.makeText(getActivity(), "Grid item clicked position is " + position, Toast.LENGTH_SHORT).show();

    }

}
