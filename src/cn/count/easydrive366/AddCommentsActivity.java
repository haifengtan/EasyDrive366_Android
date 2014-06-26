package cn.count.easydrive366;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
public class AddCommentsActivity extends BaseHttpActivity {
	private EditText edtContent;
	
	private TextView txtLeft;
	private String _id;
	private String _title;
	private String _column_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_addcomments_activity);
		
		this.setupLeftButton();
		Intent intent = getIntent();
		_column_id = intent.getStringExtra("column_id");
		_id = intent.getStringExtra("id");
		_title = intent.getStringExtra("title");
		this.setBarTitle(_title);
		this.setRightButtonInVisible();
		
		edtContent = (EditText)findViewById(R.id.edt_content);
		
		
		
		txtLeft = (TextView)findViewById(R.id.txt_left);
		txtLeft.setText(String.format("还可以输入%d个字.",200-edtContent.getText().toString().length()));
		edtContent.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				txtLeft.setText(String.format("还可以输入%d个字.",200-arg0.toString().length()));
				
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}});
		findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				send_userfeedback();
				
			}
		});
	}
	private void send_userfeedback(){
		if (edtContent.getText().toString().trim().length()==0){
			this.showMessage("请输入您的评论", null);
			return;
		}
		/*
		String url =String.format("article/set_review?user_id=%d&column_id=%s&article_id=%s&content=%s", AppSettings.userid,
				_column_id,_id,edtContent.getText().toString());
		this.get(url, 1);
		*/
		String url ="article/set_review";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id",String.format("%d", AppSettings.userid)));
		params.add(new BasicNameValuePair("column_id",_column_id));
		params.add(new BasicNameValuePair("article_id",_id));
		params.add(new BasicNameValuePair("content",edtContent.getText().toString()));
		
		this.getHttpClient().postHttp(AppSettings.ServerUrl, url, params, 1);
	}
	@Override
	public void processMessage(int msgType, final Object result) {

		if (AppTools.isSuccess(result)){
			this.showMessage("您的评论已经提交。", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					
				}
			});
		}
		
	}
}
