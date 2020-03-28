package com.dtt.fingerprint.biomitric_auth;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dtt.fingerprint.R;

public class BiometricDialogV23 extends Dialog implements View.OnClickListener {
    public static final int DIALOG_TWO_BUTTON = 2;
    public static final int DIALOG_ONE_BUTTON = 1;
    private Context context;
    private Button btnCancel, btnHuy;
    private TextView itemTitle, itemDescription;

    private BiometricCallback biometricCallback;

    public BiometricDialogV23(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_Dialog_Alert);
        this.context = context.getApplicationContext();
        setDialogView();
    }

    private void setDialogView() {
        View bottomsheetView = getLayoutInflater().inflate(R.layout.dialog_fingerprint, null);
        setContentView(bottomsheetView);

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnHuy = findViewById(R.id.btn_huy);

        itemTitle = findViewById(R.id.item_title);
        itemDescription = findViewById(R.id.item_description);

    }

    public void setTvitle(String title) {
        itemTitle.setText(title);
    }

    public void setDescription(String description) {
        itemDescription.setText(description);
    }

    public BiometricDialogV23(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public BiometricDialogV23(@NonNull Context context, BiometricCallback callback) {
        super(context, R.style.Theme_AppCompat_Dialog_Alert);
        this.context = context.getApplicationContext();
        biometricCallback = callback;
        setDialogView();
    }




    public void setButtonText(int action, String strBtnOne, String strBtntwo) {
        btnCancel.setText(strBtnOne);
        setBackGroundDialog(action, strBtnOne, strBtntwo);
    }

    private void setBackGroundDialog(int action, String strBtnOne, String strBtnTwo) {
        if (action == BiometricDialogV23.DIALOG_ONE_BUTTON) {
            btnHuy.setVisibility(View.GONE);
            btnCancel.setTextColor(ContextCompat.getColor(context, R.color.dialog_fingerprint_btn));
            btnCancel.setText(strBtnTwo);
        } else {
            btnHuy.setText(strBtnOne);
            btnCancel.setText(strBtnTwo);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_huy:
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                if (biometricCallback != null) {
                    biometricCallback.onAuthenticationCancelled();
                }
                break;
            default:
                break;

        }
    }
}
