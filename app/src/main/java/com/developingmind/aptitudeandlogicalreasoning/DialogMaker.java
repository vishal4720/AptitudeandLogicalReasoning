package com.developingmind.aptitudeandlogicalreasoning;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

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

    public Dialog getDialog() {
        return dialog;
    }
}
