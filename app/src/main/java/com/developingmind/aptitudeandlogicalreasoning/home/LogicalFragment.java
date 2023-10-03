package com.developingmind.aptitudeandlogicalreasoning.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.HomeActivity;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.bookmark.BookmarkActivity;
import com.developingmind.aptitudeandlogicalreasoning.home.advertisment.AdvertismentAdapter;
import com.developingmind.aptitudeandlogicalreasoning.home.advertisment.AdvertismentModal;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuizCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LogicalFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayoutCompat study,solvedProblems,practice,test,tip,bookmark;
    private RecyclerView recyclerView;
    AdvertismentAdapter adapter;
    private List<AdvertismentModal> advertismentModalList = new ArrayList<>();

    public LogicalFragment() {
        // Required empty public constructor
    }

    public static LogicalFragment newInstance(String param1, String param2) {
        LogicalFragment fragment = new LogicalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        practice = view.findViewById(R.id.practice);
        study = view.findViewById(R.id.study);
        solvedProblems = view.findViewById(R.id.solved_problems);
        test = view.findViewById(R.id.test);
        tip = view.findViewById(R.id.tips);
        bookmark = view.findViewById(R.id.bookmark);
        recyclerView = view.findViewById(R.id.recycler_view);

        setHorizontalAdvertisment();

        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookmarkActivity.class);
                intent.putExtra(Constants.isAptitude,true);
                startActivity(intent);
            }
        });

        solvedProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),QuizCategoryActivity.class);
                intent.putExtra(Constants.isAptitude,false);
                intent.putExtra(Constants.isPractice,false);
                startActivity(intent);
            }
        });

        practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),QuizCategoryActivity.class);
                intent.putExtra(Constants.isAptitude,false);
                intent.putExtra(Constants.isPractice,true);
                startActivity(intent);
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogMaker dialogMaker = new DialogMaker(getContext(),true);
                dialogMaker.getDialog().show();
            }
        });

        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }



    private void setHorizontalAdvertisment(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdvertismentAdapter(getContext(),advertismentModalList);
        recyclerView.setAdapter(adapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(layoutManager.findLastCompletelyVisibleItemPosition() < (adapter.getItemCount()-1)){
                    layoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(),layoutManager.findLastCompletelyVisibleItemPosition()+1);
                }else{
                    layoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(),0);
                }
            }
        },0,3000);

        getAdvertismentData();
    }

    private void getAdvertismentData(){
        ((HomeActivity)getActivity()).getFirebaseFirestore().collection(DatabaseEnum.system.toString())
                .document(DatabaseEnum.advertisment.toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String, Object> map = documentSnapshot.getData();
                            if(map!=null){
                                Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Map.Entry<String, Object> x = iterator.next();
                                    JSONObject object = new JSONObject((Map) x.getValue());
                                    try {
                                        advertismentModalList.add(new AdvertismentModal(object.getString("image"),object.getString("redirect")));
                                    } catch (JSONException e) {
                                        Log.d("", e.getMessage());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }else{
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }
}