package net.passone.hrd.common;

import android.content.DialogInterface;

public class DialogCancelAction implements DialogInterface.OnClickListener {

	public void onClick(DialogInterface dialog, int which) {
		dialog.cancel();
	}
}