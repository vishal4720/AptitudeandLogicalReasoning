package com.developingmind.aptitudeandlogicalreasoning.quiz;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton back;
    TextView question,question_no;
    FloatingActionButton bookmark;
    LinearLayout options;
    Button share,next;
    int count =0;
    List<QuestionsModal> list = new ArrayList<>();
    int position = 0;
    private int score = 0;
    private String setId;
    Dialog sharedialog;
    DialogMaker progressdialog;
    int matchedQuestionPosition;

    List<QuestionsModal> bookmarklist;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public QuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizFragment newInstance(String param1, String param2) {
        QuizFragment fragment = new QuizFragment();
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
        sharedPreferences = getActivity().getSharedPreferences("MyPref",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
//        getBookmark();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_quiz, container, false);

        question = view.findViewById(R.id.question);
        question_no = view.findViewById(R.id.question_no);
        bookmark = view.findViewById(R.id.bookmark_btn);
        options = view.findViewById(R.id.options_container);
        share = view.findViewById(R.id.share_btn);
        next = view.findViewById(R.id.next_btn);
        sharedialog = new Dialog(getContext());
        sharedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch()){
                    bookmarklist.remove(matchedQuestionPosition);
                    bookmark.setImageDrawable(getActivity().getDrawable(R.drawable.bookmark_border));
                }else{
                    bookmarklist.add(list.get(position));
                    bookmark.setImageDrawable(getActivity().getDrawable(R.drawable.bookmark));
                }
            }
        });

        progressdialog = new DialogMaker(getContext());

        list = new ArrayList<>();
        showDialog();



        return view;
    }

    private void showDialog(){
        progressdialog.getDialog().show();
    }

    private void dismissLoader(){
        progressdialog.getDialog().hide();
    }

    private boolean modelMatch(){
        boolean matched = false;
        int i =0;
        for (QuestionsModal modal : bookmarklist){
            if (modal.getQuestion().equals(list.get(position).getQuestion())
                    && modal.getCorrectAns().equals(list.get(position).getCorrectAns())
                    && modal.getSetNo() == list.get(position).getSetNo()){
                matched = true;
                matchedQuestionPosition = i;
            }
            i++;
        }
        return matched;
    }
}