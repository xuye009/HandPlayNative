package com.handscape.nativereflect.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;

public class FirmAlertDialogBuilder extends Builder {
    public FirmAlertDialogBuilder(@NonNull Context context) {
        this(context, 0);
    }

    public FirmAlertDialogBuilder(@NonNull Context context, int i) {
        super(context, i);
    }

    public AlertDialog create() {
        AlertDialog create = super.create();
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        return create;
    }
}
