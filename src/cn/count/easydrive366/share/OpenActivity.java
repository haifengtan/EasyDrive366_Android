package cn.count.easydrive366.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydrive366.goods.GoodsDetailActivity;
import cn.count.easydrive366.provider.ProviderDetailActivity;
import cn.count.easydriver366.base.BaseHttpActivity;

public class OpenActivity extends BaseHttpActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_open_activity);
		this.setBarTitle("导航");
		this.setupLeftButton();
		Intent intent = this.getIntent();
		Uri uri = intent.getData();
		String type = uri.getQueryParameter("type").toLowerCase();
		String id = uri.getQueryParameter("id");
		String name = uri.getQueryParameter("name");
		String querystirng = intent.getData().getQuery();
		TextView txt = (TextView)findViewById(R.id.textView1);
		txt.setText(querystirng);
		if (type.equals("spv")){
			Intent i = new Intent(this,ProviderDetailActivity.class);
			i.putExtra("code", id);
			startActivity(i);
		}else if (type.equals("gds")){
			Intent i = new Intent(this,
					GoodsDetailActivity.class);
			i.putExtra("id",Integer.parseInt( id));
			startActivity(i);
		}
		this.finish();
		
		
	}
}
