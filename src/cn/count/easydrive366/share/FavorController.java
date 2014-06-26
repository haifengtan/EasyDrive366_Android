package cn.count.easydrive366.share;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;

import cn.count.easydriver366.base.HttpExecuteGetTask;
import cn.count.easydriver366.base.IExecuteHttp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ImageView;

public class FavorController {
	private String target_id;
	private String type_name;
	private MenuItem menu_item;
	private ImageView image_view;
	private IExecuteHttp _context;
	private Boolean is_favor;
	public FavorController(IExecuteHttp context){
		_context = context;
	}
	
	public void init(final int favor,ImageView img,final String id,final String tname){
		is_favor = favor ==1;
		menu_item = null;
		image_view = img;
		target_id = id;
		type_name = tname;
		
	}
	public void init(final int favor,MenuItem menu,final String id,final String tname){
		is_favor = favor ==1;
		menu_item = menu;
		image_view = null;
		target_id = id;
		type_name = tname;
		menu_item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				click_menu();
				return false;
			}});
	}
	public void click_menu(){
		if (is_favor){
			this.unfavor();
		}else{
			this.favor();
		}
	}
	private void setupMenu(){
		if (is_favor){
			if (menu_item!=null)
				menu_item.setTitle("取消收藏");
			if (image_view!=null)
				image_view.setImageResource(R.drawable.favor);
		}else{
			if (menu_item!=null)
				menu_item.setTitle("收藏");
			if (image_view!=null)
				image_view.setImageResource(R.drawable.favorno);
		}
	}
	public void favor(){
		String url = String.format("favor/add?userid=%d&id=%s&type=%s", AppSettings.userid,target_id,type_name);
		if (_context!=null)
			_context.beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				if (_context!=null)
					_context.endHttp();
				Context c =null;
				if (_context instanceof Context)
					c = (Context) _context;
				else
					c = ((Fragment)_context).getActivity();
				if (AppSettings.isSuccessJSON(result,c)){
					is_favor = true;
					setupMenu();
				}
				
			}}.execute(url);
			
		
	}
	public void unfavor(){
		String url = String.format("favor/remove?userid=%d&id=%s&type=%s", AppSettings.userid,target_id,type_name);
		if (_context!=null)
			_context.beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				if (_context!=null)
					_context.endHttp();
				Context c =null;
				if (_context instanceof Context)
					c = (Context) _context;
				else
					c = ((Fragment)_context).getActivity();
				if (AppSettings.isSuccessJSON(result, c)){
					is_favor = false;
					setupMenu();
				}
				
			}}.execute(url);
			
		
	}
	

}
