package com.handscape.nativereflect.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.handscape.nativereflect.R;


public class SimpleDialogFragment extends DialogFragment implements OnClickListener {
    private String mMessage;
    private String mTitle;
    private int mReqCode;
    private Callback mCallback;
    private boolean needBtnCancel;
    private boolean needChb;

    public interface Callback {
        void onClickOK(int reqCode);

        void onCheckedChanged(boolean isChecked);

        void onClickCancel(int reqCode);
    }

    public static SimpleDialogFragment get(int reqCode, String title, String msg, boolean needCancel) {
        return get(reqCode, title, msg, needCancel, false);
    }

    public static SimpleDialogFragment get(int reqCode, String title, String msg, boolean needCancel, boolean needChb) {
        SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("KEY_REQUEST_CODE", reqCode);
        bundle.putString("key_title", title);
        bundle.putString("KEY_MESSAGE", msg);
        bundle.putBoolean("key_dialog_cancel_btn", needCancel);
        bundle.putBoolean("key_dialog_checkbox", needChb);
        simpleDialogFragment.setArguments(bundle);
        return simpleDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mReqCode = arguments.getInt("KEY_REQUEST_CODE");
            this.mMessage = arguments.getString("KEY_MESSAGE");
            this.mTitle = arguments.getString("key_title");
            this.needBtnCancel = arguments.getBoolean("key_dialog_cancel_btn");
            this.needChb = arguments.getBoolean("key_dialog_checkbox");
        }
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FirmAlertDialogBuilder firmAlertDialogBuilder = new FirmAlertDialogBuilder(getContext());
        if (this.needChb) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.simple_checkbox_dialog, null);
            firmAlertDialogBuilder.setView(inflate);
            inflate.findViewById(R.id.sc_dialog_positive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onClickOK(mReqCode);
                    }
                }
            });
            inflate.findViewById(R.id.sc_dialog_negative).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onClickCancel(mReqCode);
                    }
                }
            });
            ((CheckBox) inflate.findViewById(R.id.sc_dialog_checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mCallback != null) {
                        mCallback.onCheckedChanged(isChecked);
                    }
                }
            });
            ((Button) inflate.findViewById(R.id.sc_dialog_negative)).setVisibility(this.needBtnCancel ? View.VISIBLE : View.GONE);
            TextView textView = (TextView) inflate.findViewById(R.id.sc_dialog_message);
            ((TextView) inflate.findViewById(R.id.sc_dialog_title)).setText(this.mTitle);
            textView.setText(this.mMessage);
        } else {
            if (!TextUtils.isEmpty(this.mTitle)) {
                firmAlertDialogBuilder.setTitle(this.mTitle);
            }
            firmAlertDialogBuilder.setMessage(this.mMessage).setPositiveButton((int) R.string.confirm, (OnClickListener) this);
            if (this.needBtnCancel) {
                firmAlertDialogBuilder.setNegativeButton((int) R.string.cancel, (OnClickListener) this);
            }
        }
        return firmAlertDialogBuilder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Fragment parentFragment = getParentFragment();
        if (which == -1) {
            if (parentFragment != null && (parentFragment instanceof Callback)) {
                ((Callback) getParentFragment()).onClickOK(this.mReqCode);
            } else if (getActivity() instanceof Callback) {
                ((Callback) getActivity()).onClickOK(this.mReqCode);
            } else if (this.mCallback != null) {
                this.mCallback.onClickOK(this.mReqCode);
            }
        } else if (which == -2) {
            if (parentFragment != null && (parentFragment instanceof Callback)) {
                ((Callback) getParentFragment()).onClickCancel(this.mReqCode);
            } else if (getActivity() instanceof Callback) {
                ((Callback) getActivity()).onClickCancel(this.mReqCode);
            } else if (this.mCallback != null) {
                this.mCallback.onClickCancel(this.mReqCode);
            }
        }
    }
}
