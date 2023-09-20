package com.developingmind.aptitudeandlogicalreasoning;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.developingmind.aptitudeandlogicalreasoning.R;

import org.w3c.dom.Text;

public class DialogMaker extends Dialog {
    private Dialog dialog;
    public DialogMaker(@NonNull Context context) {
        super(context);
        dialog  = new Dialog(context);
        dialog.setContentView(R.layout.progress_loader);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.ic_loader));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

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

    public Dialog getDialog() {
        return dialog;
    }
}
