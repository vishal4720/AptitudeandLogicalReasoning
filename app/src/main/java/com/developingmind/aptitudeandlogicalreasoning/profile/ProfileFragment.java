package com.developingmind.aptitudeandlogicalreasoning.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.HomeActivity;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ShapeableImageView profileIcon;
    private TextInputLayout fname,lname,email,dob,gender;
    private Button update;

    private DialogMaker dialogMaker;

    private FirebaseFirestore firebaseFirestore;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileIcon = view.findViewById(R.id.profile_icon);
        fname = view.findViewById(R.id.fname_layout_profile);
        lname = view.findViewById(R.id.lname_layout_profile);
        email = view.findViewById(R.id.email_layout_profile);
        dob = view.findViewById(R.id.date_layout_profile);
        gender = view.findViewById(R.id.gender_profile);
        update = view.findViewById(R.id.profile_btn);
        dialogMaker = new DialogMaker(getContext());
        firebaseFirestore = FirebaseFirestore.getInstance();

        showDialog();
        firebaseFirestore.collection(DatabaseEnum.users.toString())
                .document(((HomeActivity)getActivity()).getFirebaseUser().getUid().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String,Object> map = documentSnapshot.getData();
                            setText(fname,map.get(ProfileEnum.fname.toString()).toString());
                            setText(lname,map.get(ProfileEnum.lname.toString()).toString());
                            setText(email,getEmail());
                            setText(dob,map.get(ProfileEnum.dob.toString()).toString());
                            setText(gender,map.get(ProfileEnum.gender.toString()).toString());
                            if(map.get(ProfileEnum.gender.toString()).toString().equals(Gender.Male.toString())){
                                profileIcon.setImageDrawable(getContext().getDrawable(R.drawable.male_avatar));
                            }else if(map.get(ProfileEnum.gender.toString()).toString().equals(Gender.Female.toString())){
                                profileIcon.setImageDrawable(getContext().getDrawable(R.drawable.female_avatar));
                            }else{

                            }
                            Log.d("Gender",map.get(ProfileEnum.dob.toString()).toString());
                            hideDialog();
                        }else{
                            backToHome();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        backToHome();
                    }
                });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                HashMap<String,Object> map = new HashMap<>();
                map.put(ProfileEnum.fname.toString(),fname.getEditText().getText().toString());
                map.put(ProfileEnum.lname.toString(),lname.getEditText().getText().toString());
                map.put(ProfileEnum.dob.toString(),dob.getEditText().getText().toString());
                map.put(ProfileEnum.gender.toString(),gender.getEditText().getText().toString());
                firebaseFirestore.collection(DatabaseEnum.users.toString())
                        .document(getFirebaseUser().getUid().toString())
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    ((HomeActivity)getActivity()).setHeaderTitle(fname.getEditText().getText().toString() + " " + lname.getEditText().getText().toString());
                                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                }
                                hideDialog();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Profile Exception",e.getMessage());
                                Toast.makeText(getContext(), "Something went wrong !!", Toast.LENGTH_SHORT).show();
                                hideDialog();
                            }
                        });
            }
        });


        return view;
    }

    private FirebaseUser getFirebaseUser(){
        return ((HomeActivity)getActivity()).getFirebaseUser();
    }

    private String getEmail(){
        return getFirebaseUser().getEmail().toString();
    }

    private void showDialog(){
        if(!dialogMaker.getDialog().isShowing()){
            dialogMaker.getDialog().show();
        }
    }
    private void hideDialog(){
        if(dialogMaker.getDialog().isShowing()){
            dialogMaker.getDialog().hide();
        }
    }

    private void setText(TextInputLayout text,String s){
        text.getEditText().setText(s);
    }

    private void backToHome(){
        Toast.makeText(getContext(), "Something went wrong !!", Toast.LENGTH_SHORT).show();
        hideDialog();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, new HomeFragment()); // replace a Fragment with Frame Layout
        transaction.commit();
    }
}