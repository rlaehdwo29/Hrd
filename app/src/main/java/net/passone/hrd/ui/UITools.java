package net.passone.hrd.ui;

import net.passone.hrd.R;
import net.passone.hrd.common.DialogCancelAction;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.ImageButton;

public class UITools {

	//	public static void setProgress(View view, int max, int progress) {
	//		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
	//		progressBar.setMax(100);
	//		progressBar.setProgress(progress);
	//	}
	//	
	public static void setVisible(View view, boolean condition) {
		if (condition)
			view.setVisibility(View.VISIBLE);
		else
			view.setVisibility(View.GONE);
	}

	public static void alert(final Context context, int errorMessageId, Object... listeners) {
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setMessage(context.getResources().getString(errorMessageId));
		dialog.setButton("확인", new DialogCancelAction());
		for (Object listener : listeners) {
			if (listener instanceof OnDismissListener)
				dialog.setOnDismissListener((OnDismissListener) listener);
			else if (listener instanceof OnDismissListener)
				dialog.setOnCancelListener((OnCancelListener) listener);
		}
		dialog.show();
	}
	public static void makeImageStateful(ImageButton button) {
		Drawable drawable = button.getDrawable();
		Drawable cover = button.getContext().getResources().getDrawable(R.drawable.selected_button_cover);

		StateListDrawable stateList = new StateListDrawable();
		LayerDrawable layer = new LayerDrawable(new Drawable[] { drawable, cover });
		stateList.addState(new int[] { android.R.attr.state_pressed }, layer);
		stateList.addState(new int[] { -android.R.attr.state_pressed }, drawable);
		button.setImageDrawable(stateList);
	}
	
	public static void makeStateful(View view) {
		Drawable drawable = view.getBackground();
		Drawable cover = view.getContext().getResources().getDrawable(R.drawable.selected_button_cover);
		StateListDrawable stateList = new StateListDrawable();

		LayerDrawable layer = new LayerDrawable(new Drawable[] { drawable, cover });
		stateList.addState(new int[] { android.R.attr.state_pressed }, layer);
		stateList.addState(new int[] { -android.R.attr.state_pressed }, drawable);
		view.setBackgroundDrawable(stateList);
	}
}
