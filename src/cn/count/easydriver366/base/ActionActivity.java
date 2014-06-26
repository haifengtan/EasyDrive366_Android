package cn.count.easydriver366.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ActionActivity extends Activity implements OnClickListener{
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    protected GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* ... */

        

    }
    protected void addSwipeToView(View v){
    	// Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        v.setOnClickListener(this);
        v.setOnTouchListener(gestureListener);
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	onLeftSwipe();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	onRightSwipe();
                    
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }
    public void onLeftSwipe(){
    	Toast.makeText(this, "Left Swipe", Toast.LENGTH_SHORT).show();
    }
    public void onRightSwipe(){
    	Toast.makeText(this, "Right Swipe", Toast.LENGTH_SHORT).show();
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
