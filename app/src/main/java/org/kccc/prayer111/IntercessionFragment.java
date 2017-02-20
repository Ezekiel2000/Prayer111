package org.kccc.prayer111;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ezekiel on 2017. 2. 2..
 */

public class IntercessionFragment extends Fragment {

    final int ITEM_SIZE = 5;
    List<ListData> listData;
    ListData[] data;

    // DATA Parsing 관련
//    private static final String TAG_NAME = "name";
//    private static final String TAG_DATE = "date";
//    private static final String TAG_NAME = "name";
//    private static final String TAG_NAME = "name";


    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    public IntercessionFragment() {

    }

    public static IntercessionFragment newInstance() {
        IntercessionFragment fragment = new IntercessionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_intercession, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        listData = new ArrayList<>();
        data = new ListData[ITEM_SIZE];

        data[0] = new ListData(R.drawable.a, "김보름", "1월 14일", "텍스트에 따라 카드 높이 늘어납니다ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ", 6, 2);
        data[1] = new ListData(R.drawable.b, "PSY", "2월 1일", "텍스트에 따라 카드 높이 늘어납니다", 6, 2);
        data[2] = new ListData(R.drawable.c, "전형배", "2월 13일", "텍스트에 따라 카드 높이 늘어납니다", 6, 2);
        data[3] = new ListData(R.drawable.d, "PSY", "2월 14일", "텍스트에 따라 카드 높이 늘어납니다", 6, 2);
        data[4] = new ListData(R.drawable.e, "이지은", "2월 15일", "텍스트에 따라 카드 높이 늘어납니다", 6, 2);


        Log.d("하이", "리스트 추가");


        for (int i = 0; i < ITEM_SIZE; i++) {
            listData.add(data[i]);
        }

        Log.d("하이", "리스트 붙이기");

        recyclerView.setAdapter(new ListDataAdapter(getActivity().getApplicationContext(), listData, R.layout.fragment_intercession));

        Log.d("하이", "리사이클러뷰 붙이기");

        return view;

    }


    @Override
    public void onResume() {

//        FloatingActionButton fab_write = (FloatingActionButton) getActivity().findViewById(R.id.fab_write);
//        FloatingActionButton fab_share = (FloatingActionButton) getActivity().findViewById(R.id.fab_share);
//        fab_share.hide();
//        fab_write.show();

        Log.d("하이", "fragment3  Resume");


        super.onResume();
    }

    @Override
    public void onPause() {

        Log.d("하이", "fragment3  onPause");

        super.onPause();
    }
}
