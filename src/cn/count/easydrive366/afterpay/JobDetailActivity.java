package cn.count.easydrive366.afterpay;

import android.os.Bundle;
import android.view.WindowManager;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.BaseHttpActivity;

public class JobDetailActivity extends BaseHttpActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_afterpay_accident);
		this.setupLeftButton();
		getActionBar().setTitle("职业明细");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
	}
}
