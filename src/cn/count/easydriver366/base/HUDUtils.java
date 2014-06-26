package cn.count.easydriver366.base;

import cn.count.easydrive366.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class HUDUtils {
	 public static View createLoadingView(Context context) {
	        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_empty_loading, null);
	        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        linearLayout.setGravity(Gravity.CENTER);
	        linearLayout.setVisibility(View.GONE);
	        return linearLayout;
	    }

	    public static View createFailView(Context context) {
	        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_empty_fail, null);
	        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        linearLayout.setGravity(Gravity.CENTER);
	        linearLayout.setVisibility(View.GONE);
	        return linearLayout;
	    }
}
