package cn.count.easydrive366.comments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class ItemCommentsActivity extends BaseHttpActivity {
	
	private List<Comment> _comments;
	private MenuItem menuComment;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_itemcomments);
		this.setupLeftButton();
		this.setupRightButtonWithText("撰写评论");
		load_data();
		((TextView)findViewById(R.id.txt_avg_star)).setText("");
		((TextView)findViewById(R.id.txt_total_voter)).setText("");
	}
	private void load_data(){
		final String item_id = getIntent().getStringExtra("id");
		final String item_type = getIntent().getStringExtra("type");
		if (item_id!=null && item_type!=null){
			beginHttp();
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					endHttp();
					load_view(result);
					
				}}.execute(String.format("comment/get_comment?userid=%d&type=%s&id=%s", AppSettings.userid,item_type,item_id));
			
		}
	}
	private void load_view(final String result){
		JSONObject json = AppSettings.getSuccessJSON(result,this);
		if (json==null) return;
		try{
			/*
			_total_stars = json.getInt("total_star");
			_total_voters = json.getInt("total_voters");
			_avg_star = (float) json.getDouble("avg_star");
			*/
			((TextView)findViewById(R.id.txt_avg_star)).setText(json.getString("avg_star"));
			((TextView)findViewById(R.id.txt_total_voter)).setText(json.getString("total_voters"));
			RatingBar bar = (RatingBar)findViewById(R.id.rating_bar);
			bar.setRating(json.getInt("avg_star_num"));
			JSONArray list = json.getJSONArray("list");
			Intent intent = new Intent();
			intent.putExtra("star",json.getString("avg_star"));
			intent.putExtra("star_num",json.getInt("avg_star_num"));
			intent.putExtra("star_voternum",json.getString("total_voters"));
			this.setResult(RESULT_OK, intent);
			JSONObject stars_info = json.getJSONObject("stars_info");
			JSONObject star = stars_info.getJSONObject("1");
			ProgressBar pb  =(ProgressBar)findViewById(R.id.pb_star1);
			TextView txt = (TextView)findViewById(R.id.txt_star1_voter);
			pb.setProgress((int) (star.getDouble("percent")*100));
			txt.setText(star.getString("count"));
			// star 2
			star = stars_info.getJSONObject("2");
			pb  =(ProgressBar)findViewById(R.id.pb_star2);
			txt = (TextView)findViewById(R.id.txt_star2_voter);
			pb.setProgress((int) (star.getDouble("percent")*100));
			txt.setText(star.getString("count"));
			
			star = stars_info.getJSONObject("3");
			pb  =(ProgressBar)findViewById(R.id.pb_star3);
			txt = (TextView)findViewById(R.id.txt_star3_voter);
			pb.setProgress((int) (star.getDouble("percent")*100));
			txt.setText(star.getString("count"));
			
			star = stars_info.getJSONObject("4");
			pb  =(ProgressBar)findViewById(R.id.pb_star4);
			txt = (TextView)findViewById(R.id.txt_star4_voter);
			pb.setProgress((int) (star.getDouble("percent")*100));
			txt.setText(star.getString("count"));
			
			star = stars_info.getJSONObject("5");
			pb  =(ProgressBar)findViewById(R.id.pb_star5);
			txt = (TextView)findViewById(R.id.txt_star5_voter);
			pb.setProgress((int) (star.getDouble("percent")*100));
			txt.setText(star.getString("count"));
			_comments = new ArrayList<Comment>();
			
			for(int i=0;i<list.length();i++){
				JSONObject item= list.getJSONObject(i);
				Comment comment = new Comment();
				comment.comment = item.getString("comment");
				comment.user_id = item.getInt("user_id");
				comment.star = item.getInt("star");
				comment.submit_time = item.getString("submit_time");
				comment.username = item.getString("username");
				_comments.add(comment);
			}
			if (_comments.size()==0){
				Comment comment = new Comment();
				comment.comment = "目前还没有评论";
				comment.user_id = 0;
				comment.star = 5;
				comment.submit_time = "2013-10-1";
				comment.username = "管理员";
				_comments.add(comment);
			}
			ListView lv = (ListView)findViewById(R.id.lv_items);
			CommentAdapter adapter  =new CommentAdapter(this);
			lv.setAdapter(adapter);
		}catch(Exception e){
			log(e);
		}
		
	}
	
	
	
	@Override
	protected void onRightButtonPress() {
		Intent intent = new Intent(this,DoCommentActivity.class);
		intent.putExtra("id", getIntent().getStringExtra("id"));
		intent.putExtra("type", getIntent().getStringExtra("type"));
		startActivityForResult(intent,1);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		load_data();
	}
	
	private class Comment{
		public int user_id;
		public String comment;
		public String submit_time;
		public int star;
		public String username;
		
	}
	private class ViewHolder{
		public TextView txtUsername;
		public TextView txtDate;
		public TextView txtDescription;
		public RatingBar rbRating;
	}
	private class CommentAdapter extends BaseAdapter{
		private LayoutInflater mInflater = null;
		public CommentAdapter(Context context){
			mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			if (_comments==null)
				return 0;
			
			return _comments.size();
		}

		@Override
		public Object getItem(int position) {
			
			return position;
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView ==null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_comment, null);
				holder.rbRating = (RatingBar)convertView.findViewById(R.id.rating_bar);
				holder.txtDate = (TextView)convertView.findViewById(R.id.txt_date);
				holder.txtUsername = (TextView)convertView.findViewById(R.id.txt_username);
				holder.txtDescription = (TextView)convertView.findViewById(R.id.txt_comment);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			Comment comment = _comments.get(position);
			holder.txtUsername.setText(comment.username);
			holder.txtDate.setText(comment.submit_time);
			holder.txtDescription.setText(comment.comment);
			holder.rbRating.setRating(comment.star);
			return convertView;
		}}
}
