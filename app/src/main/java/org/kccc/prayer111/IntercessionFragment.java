package org.kccc.prayer111;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ezekiel on 2017. 2. 2..
 */

public class IntercessionFragment extends Fragment {
    private static final int MAX_ITEMS_PER_REQUEST = 30;
    private static final int NUMBER_OF_ITEMS = 100;
    private static final int SIMULATED_LOADING_TIME_IN_MS = 1500;

    final int ITEM_SIZE = 5;
    List<ListData> listData;
    ListData[] data;
    private int page = 0;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    public ProgressBar progressBar;

    String userId = "";

    private static int displayedposition = 0;

    private String TAG = IntercessionFragment.class.getSimpleName();
    private static String url = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getTogether";
    private String postUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";
    private static String getUrl;

    ListDataAdapter listDataAdapter;

    ArrayList<HashMap<String, String>> IntercessionPraysList;

    ProgressDialog progressDialog;

    boolean loadingCheck = false;

    Handler handler = new Handler();

    public IntercessionFragment() {

    }

    public static IntercessionFragment newInstance() {
        IntercessionFragment fragment = new IntercessionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intercession, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(listDataAdapter);

        Log.d("하이", "호출 : " +getUrl);

        userId = ((MainActivity)getActivity()).userId;

        if (!TextUtils.isEmpty(userId)) {


            getUrl = url +"&userId=" + userId;

        } else {

            getUrl = url;

        }

//            listData = new ArrayList<>();
//            listDataAdapter = new ListDataAdapter(getActivity().getApplicationContext(), listData, R.layout.fragment_intercession);
//            new GetIntercessionPrays().execute();

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//
//                View child = rv.findChildViewUnder(e.getX(), e.getY());
//
//                ImageView card_delete = (ImageView) rv.getChildViewHolder(child).itemView.findViewById(R.id.pray_delete);
//
//                card_delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Log.d("하이", "listdata.get :" + listData.get(rv.getChildAdapterPosition(child)+1));
//
//
//                        if (userId.equals(listData.get(rv.getChildAdapterPosition(child)).getEmail())) {
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//
//                            builder.setTitle("중보기도 삭제")
//                                    .setMessage("중보기도를 삭제하시겠습니까?")
//                                    .setCancelable(false)
//                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            new Thread() {
//                                                @Override
//                                                public void run() {
//
//                                                    HttpHandler sh = new HttpHandler();
//
//                                                    String url = postUrl + "?mode=removeTogetherPray" + "&userId=" + userId + "&prayNo=" + listData.get(rv.getChildAdapterPosition(child)).getNumber();
//
//                                                    Log.d("하이", "url : " + url);
//
//                                                    String jsonStr = sh.makeServiceCall(url);
//
//                                                    if (jsonStr != null) {
//
//                                                        try {
//
//                                                            JSONObject jsonObject = new JSONObject(jsonStr);
//                                                            JSONObject object = jsonObject.getJSONObject("result");
//
//                                                            String result = object.getString("msg");
//
//                                                            Log.d("하이", "삭제 결과 : " + result);
//
//                                                        } catch (JSONException e) {
//                                                            Log.e(TAG, "Json parsing error:" + e.getMessage());
//                                                        } catch (Exception e) {
//                                                            e.printStackTrace();
//                                                        }
//
//                                                    }
//
//
//                                                }
//                                            }.start();
//
//                                            listDataAdapter.remove(new ListData(listData.get(rv.getChildAdapterPosition(child)).getNumber(),
//                                                    listData.get(rv.getChildAdapterPosition(child)).getProfileImage(), listData.get(rv.getChildAdapterPosition(child)).getEmail(), listData.get(rv.getChildAdapterPosition(child)).getName(),
//                                                    listData.get(rv.getChildAdapterPosition(child)).getDate(), listData.get(rv.getChildAdapterPosition(child)).getContent(),
//                                                    listData.get(rv.getChildAdapterPosition(child)).getImageInput(), listData.get(rv.getChildAdapterPosition(child)).getWarn(),
//                                                    listData.get(rv.getChildAdapterPosition(child)).getPrayerNumber(), listData.get(rv.getChildAdapterPosition(child)).getCommentNumber(),
//                                                    listData.get(rv.getChildAdapterPosition(child)).getChkHeart()), rv.getChildAdapterPosition(child));
//                                        }
//                                    })
//                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//
//                        }
//
//                        Log.d(TAG, "getChildAdapterPosition=>" + rv.getChildAdapterPosition(child));
//                        Log.d(TAG, "getChildLayoutPosition=>" + rv.getChildLayoutPosition(child));
//                        Log.d(TAG, "getChildViewHolder=>" + rv.getChildViewHolder(child).itemView);
//
//                    }
//                });
//
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });


        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d("하이", "페이지 3번 보인다.");

            listData = new ArrayList<>();
            listDataAdapter = new ListDataAdapter(getContext(), listData, R.layout.fragment_intercession);
            new GetIntercessionPrays().execute();

        } else {
            Log.d("하이", "페이지 3번 안보인다.");
        }


    }

    private class GetIntercessionPrays extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(getContext(), null, "로딩중입니다.", true, false);
        }



        @Override
        protected Void doInBackground(Void... params) {

            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(getUrl);

            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                String dataJson = jsonObject.getString("result");

                JSONArray jsonArray = new JSONArray(dataJson);

                // 내림차순으로 정렬하기
//                jsonArray = soryJsonArray(jsonArray);

                for (int i = 0; i < jsonArray.length() ; i++) {
                    JSONObject object = jsonArray.getJSONObject(i);



                    String number = object.getString("no");
                    String email = object.getString("id");
                    String name = object.getString("name");
                    String content = object.getString("pray");
                    String date = object.getString("indate");
                    String profile = object.getString("prayPhoto");
                    String imageInput = object.getString("photo");
                    int warn = object.getInt("warn");
                    int prayNumber = object.getInt("heart");
                    int commentNumber = object.getInt("comment");
                    int chkHeart = object.getInt("chkHeart");

                    data = new ListData[jsonArray.length()];

                    // 신고당한 리스트(warn = 1인경우)는 add 하지 않음
                    if (warn == 0) {

                        data[i] = new ListData(number, profile, email, name, date, content, imageInput, warn, prayNumber, commentNumber, chkHeart);
                        listData.add(data[i]);

                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listDataAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(listDataAdapter);
                    }
                });

            } catch (JSONException e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            recyclerView.setAdapter(listDataAdapter);
            listDataAdapter.notifyDataSetChanged();
            loadingCheck = true;
//            progressDialog.dismiss();

        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            listDataAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(listDataAdapter);

        }
    }

    // 내림차순으로 정렬하는 함수
    public static JSONArray soryJsonArray(JSONArray array) {
        List<JSONObject> objects = new ArrayList<JSONObject>();
        try {
            for (int i = 0 ; i < array.length() ; i++) {
                objects.add(array.getJSONObject(i));
            }
            Collections.sort(objects, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    String lid = "", rid = "";
                    try {
                        lid = o1.getString("no");
                        rid = o2.getString("no");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return  rid.compareTo(lid);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONArray(objects);
    }

    @Override
    public void onResume() {

        Log.d("하이", "fragment3  Resume");

        FloatingActionButton fab_pray = (FloatingActionButton) getActivity().findViewById(R.id.fab_check_today);
        fab_pray.setVisibility(View.GONE);

        super.onResume();
    }

    @Override
    public void onPause() {

        FloatingActionButton fab_pray = (FloatingActionButton) getActivity().findViewById(R.id.fab_check_today);
        fab_pray.setVisibility(View.VISIBLE);
        Log.d("하이", "fragment3  onPause");

        super.onPause();
    }


}
