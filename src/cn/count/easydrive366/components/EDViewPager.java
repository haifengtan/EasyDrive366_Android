package cn.count.easydrive366.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class EDViewPager extends ViewPager {
	private boolean enabled = true;
	public EDViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	
	}
	@Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(enabled)
            return super.onInterceptTouchEvent(arg0);

        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
