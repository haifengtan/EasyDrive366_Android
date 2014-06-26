package cn.count.easydrive366;



import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;



public class CarServiceDetailActivity extends TabActivity {
	 /**
     * TabHost控件
     */
    private TabHost mTabHost;

    /**
     * TabWidget控件
     */
    private TabWidget mTabWidget;

    /** Called when the activity is first created. */
    
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        String code;
        String pageId;
        Intent intent = this.getIntent();
        code = intent.getStringExtra("code");
        pageId = intent.getStringExtra("pageId");
        setContentView(R.layout.modules_carservice_detail_activity);
        mTabHost = this.getTabHost(); 
        mTabHost.setup();
        /* 去除标签下方的白线 */
        mTabHost.setPadding(mTabHost.getPaddingLeft(),
                mTabHost.getPaddingTop(), mTabHost.getPaddingRight(),
                mTabHost.getPaddingBottom() - 5);
        Resources rs = getResources();

        Intent layout1intent = new Intent();
        layout1intent.setClass(this, CarServiceVendorActivity.class);
        layout1intent.putExtra("code", code);
        TabHost.TabSpec layout1spec = mTabHost.newTabSpec("layout1");
        layout1spec.setIndicator("服务商",
                rs.getDrawable(R.drawable.icon0087));
        layout1spec.setContent(layout1intent);
        mTabHost.addTab(layout1spec);

        Intent layout2intent = new Intent();
        layout2intent.setClass(this, CarServiceNoteActivity.class);
        layout2intent.putExtra("code", code);
        TabHost.TabSpec layout2spec = mTabHost.newTabSpec("layout2");
        layout2spec.setIndicator("服务须知",
                rs.getDrawable(R.drawable.icon0052));
        layout2spec.setContent(layout2intent);
        mTabHost.addTab(layout2spec);

        Intent layout3intent = new Intent();
        layout3intent.setClass(this, CarServiceWikiActivity.class);
        layout3intent.putExtra("code", code);
        layout3intent.putExtra("pageId", pageId);
        layout3intent.putExtra("title", "易驾百科");
        TabHost.TabSpec layout3spec = mTabHost.newTabSpec("layout3");
        layout3spec.setIndicator("易驾百科",
                rs.getDrawable(R.drawable.icon0177));
        layout3spec.setContent(layout3intent);
        mTabHost.addTab(layout3spec);
        
        /* 对Tab标签的定制 */
        mTabWidget = mTabHost.getTabWidget();
        for (int i = 0; i < mTabWidget.getChildCount(); i++)
        {
            /* 得到每个标签的视图 */
            View view = mTabWidget.getChildAt(i);
            /* 设置每个标签的背景 */
            if (mTabHost.getCurrentTab() == i)
            {
                view.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.number_bg_pressed));
            }
            else
            {
                view.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.number_bg));
            }
            /* 设置Tab间分割竖线的颜色 */
            // tabWidget.setBackgroundColor(Color.WHITE);
            /* 设置Tab间分割竖线的背景图片 */
            // tabWidget.setBackgroundResource(R.drawable.icon);
            /* 设置tab的高度 */
            mTabWidget.getChildAt(i).getLayoutParams().height = 100;
            TextView tv = (TextView) mTabWidget.getChildAt(i).findViewById(
                    android.R.id.title);
            /* 设置tab内字体的颜色 */
            tv.setTextColor(Color.rgb(49, 116, 171));
        }

        /* 当点击Tab选项卡的时候，更改当前Tab标签的背景 */
        mTabHost.setOnTabChangedListener(new OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String tabId)
            {
                for (int i = 0; i < mTabWidget.getChildCount(); i++)
                {
                    View view = mTabWidget.getChildAt(i);
                    if (mTabHost.getCurrentTab() == i)
                    {
                        view.setBackgroundDrawable(getResources().getDrawable(
                                R.drawable.number_bg_pressed));
                    }
                    else
                    {
                        view.setBackgroundDrawable(getResources().getDrawable(
                                R.drawable.number_bg));
                    }
                }
            }
        });
    }

}
