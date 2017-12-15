package be.kerckhove.linefollowerremote;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by Stijn on 11/12/2017.
 */

public final class DialogFactory {

    public static Dialog createOkDialog(Context context, String message, String title) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.ok, null);
        return alertDialog.create();
    }
}
