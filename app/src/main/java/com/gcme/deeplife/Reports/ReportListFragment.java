package com.gcme.deeplife.Reports;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcme.deeplife.R;

import java.util.ArrayList;

/**
 * Created by BENGEOS on 3/27/16.
 */
public class ReportListFragment extends Fragment {
    private static RecyclerView myRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static Context myContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_list_page,container,false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(mLayoutManager);
        ArrayList<ReportObject> items = new ArrayList<ReportObject>();
        items.add(new ReportObject());
        items.add(new ReportObject());
        items.add(new ReportObject());
        items.add(new ReportObject());
        mAdapter = new ReportListAdapter(getActivity(),items);
        myRecyclerView.setAdapter(mAdapter);
        myContext = getActivity();
        return view;
    }
    public static void update_view(){

    }
}
