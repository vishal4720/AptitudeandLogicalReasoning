package com.developingmind.aptitudeandlogicalreasoning;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DialogMaker extends Dialog {
    private Dialog dialog;

    // Loading Dialog
    public DialogMaker(@NonNull Context context) {
        super(context);
        dialog  = new Dialog(context);
        dialog.setContentView(R.layout.progress_loader);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.ic_loader));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    // Explanation Dialog
    public DialogMaker(@NonNull Context context,String title, String des){
        super(context);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.information_dialog);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.ic_loader));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        ((TextView)dialog.findViewById(R.id.info_title)).setText(title);
        ((TextView)dialog.findViewById(R.id.info_description)).setText(des);
        ((Button)dialog.findViewById(R.id.info_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // Maintenance Dialog
    public DialogMaker(String message, String date,@NonNull Context context){
        super(context);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.maintenance);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        ((TextView) dialog.findViewById(R.id.maintenance_message)).setText(Html.fromHtml(message,Html.FROM_HTML_MODE_LEGACY));
        ((TextView) dialog.findViewById(R.id.maintenance_date)).setText("Till : "+date);

    }

    // Feedback Dialog
    public DialogMaker(@NonNull Context context, FirebaseFirestore firebaseFirestore, FirebaseUser firebaseUser){
        super(context);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.feedback_dialog);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.ic_loader));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        TextInputLayout subject,message;
        subject = ((TextInputLayout)dialog.findViewById(R.id.feedback_subject));
        message = ((TextInputLayout)dialog.findViewById(R.id.feedback_message));
        ((Button)dialog.findViewById(R.id.feedback_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sub = subject.getEditText().getText().toString();
                String msg = message.getEditText().getText().toString();
                if(!sub.isEmpty() && !msg.isEmpty()){
                    Map<String,String> m = new HashMap<>();
                    m.put("subject",sub);
                    m.put("message",msg);
                    m.put("email", firebaseUser.getEmail());
                    m.put("time", Calendar.getInstance().getTime().toString());
                    m.put("status", "unresolved");
                    Map<String,Object> map = new HashMap<>();
                    map.put(firebaseUser.getUid().toString(),m);
                    firebaseFirestore.collection(DatabaseEnum.feedback.toString())
                            .document(Calendar.getInstance().getTime().toString())
                            .set(m)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, "Feedback Received.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }


    public Dialog getDialog() {
        return dialog;
    }
}
