package cn.count.easydrive366.user;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class DoTaskActivity extends BaseHttpActivity {
	private TextView txt_title;
	private TextView txt_content;
	private TextView txt_remark;
	private Button btn_ok;
	private ImageView img_picture;
	private boolean is_finished;
	private boolean is_get;
	private int task_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_dotask);
		this.setupLeftButton();
		this.setBarTitle("");
		this.setupPhoneButtonInVisible();
		task_id = getIntent().getIntExtra("task_id", 0);
		init_view();
		load_data();
	}
	private void load_data(){
		String url = String.format("bound/get_task?userid=%d&taskid=%d", AppSettings.userid,task_id);
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				load_view(result);
				
			}}.execute(url);
	}
	private void load_view(final String result){
		JSONObject json = AppSettings.getSuccessJSON(result,this);
		try{
			txt_title.setText(json.getString("title"));
			txt_content.setText(json.getString("content"));
			txt_remark.setText(json.getString("remark"));
			is_get=true;
			is_finished = json.getBoolean("is_finished");
			if (is_finished){
				btn_ok.setText("更多惊喜去上商城看看");
			}else{
				btn_ok.setText(json.getString("title"));
			}
		}catch(Exception e){
			log(e);
		}
	}
	private void init_view(){
		is_get=false;
		is_finished = false;
		txt_title = (TextView)findViewById(R.id.txt_title);
		txt_content =(TextView)findViewById(R.id.txt_content);
		txt_remark = (TextView)findViewById(R.id.txt_remark);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		img_picture = (ImageView)findViewById(R.id.img_picture);
		txt_title.setText("");
		txt_content.setText("");
		txt_remark.setText("");
		btn_ok.setText("");
		btn_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				do_task();
				
			}});
	}
	private void do_task(){
		if (is_get && !is_finished){
			String url = String.format("bound/exec_task?userid=%d&taskid=%d", AppSettings.userid,task_id);
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					load_data();
					
				}}.execute(url);
			
		}else{
			//go to shopping
		}
	}

}
