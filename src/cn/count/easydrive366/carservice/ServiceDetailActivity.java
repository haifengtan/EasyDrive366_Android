package cn.count.easydrive366.carservice;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.BaseHttpActivity;

public class ServiceDetailActivity extends BaseHttpActivity {
	private String code;
    private String pageId;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_carservice_main);
		
        Intent intent = this.getIntent();
        code = intent.getStringExtra("code");
        pageId = intent.getStringExtra("pageId");
        
		this.setupLeftButton();
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().addTab(getActionBar().newTab()
								.setText("section1")
								.setTabListener(new TabListener(){

									@Override
									public void onTabSelected(Tab tab,
											FragmentTransaction ft) {
										
										NoteFragment f = new NoteFragment();
										Bundle bundle = new Bundle();
										bundle.putString("code", code);
										f.setArguments(bundle);
										ft.replace(R.id.id_main, f);
									}

									@Override
									public void onTabUnselected(Tab tab,
											FragmentTransaction ft) {
										
										
									}

									@Override
									public void onTabReselected(Tab tab,
											FragmentTransaction ft) {
										
										
									}
									
								}));
		getActionBar().addTab(getActionBar().newTab()
				.setText("section2")
				.setTabListener(new TabListener(){

					@Override
					public void onTabSelected(Tab tab,
							FragmentTransaction ft) {
						
						VendorFragment f = new VendorFragment();
						Bundle bundle = new Bundle();
						bundle.putString("code", code);
						f.setArguments(bundle);
						ft.replace(R.id.id_main, f);
					}

					@Override
					public void onTabUnselected(Tab tab,
							FragmentTransaction ft) {
						
						
					}

					@Override
					public void onTabReselected(Tab tab,
							FragmentTransaction ft) {
						
						
					}
					
				}));
	}
}
