package cn.count.easydrive366.user;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import cn.count.easydrive366.BindCellphoneActivity;
import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.BusinessInsuranceActivity;
import cn.count.easydrive366.CarRegistrationActivity;
import cn.count.easydrive366.CarServiceActivity;
import cn.count.easydrive366.CompulsoryInsuranceActivity;
import cn.count.easydrive366.DriverLicenseActivity;
import cn.count.easydrive366.HelpCallActivity;
import cn.count.easydrive366.InformationActivity;
import cn.count.easydrive366.MaintainActivity;
import cn.count.easydrive366.PasswordResetActivity;
import cn.count.easydrive366.SettingsActivity;
import cn.count.easydrive366.TaxForCarShipActivity;
import cn.count.easydrive366.UserFeedbackActivity;
import cn.count.easydrive366.article.ArticleListActivity;
import cn.count.easydrive366.card.AddCardStep1Activity;
import cn.count.easydrive366.card.CardViewActivity;
import cn.count.easydrive366.goods.GoodsListActivity;
import cn.count.easydrive366.insurance.BuyInsuranceStep1;
import cn.count.easydrive366.insurance.InsuranceMenuActivity;
import cn.count.easydrive366.order.NeedPayListActivity;
import cn.count.easydrive366.provider.ProviderListActivity;
import cn.count.easydrive366.signup.Step1Activity;
import cn.count.easydrive366.user.Task;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.Menus;

public class TaskDispatch {
	private Task task;
	private Context context;
	private List<PageTo> list;
	public TaskDispatch(Context c,Task t){
		task  = t;
		context = c;
		init();
	}
	private void init(){
		list = new ArrayList<PageTo>();
		list.add(new PageTo("01",InformationActivity.class));
		list.add(new PageTo("02",HelpCallActivity.class));
		list.add(new PageTo("04",MaintainActivity.class));
		list.add(new PageTo("12",CarServiceActivity.class));
		list.add(new PageTo("06",CarRegistrationActivity.class));
		list.add(new PageTo("05",DriverLicenseActivity.class));
		list.add(new PageTo("03",InsuranceMenuActivity.class));
		list.add(new PageTo("07",TaxForCarShipActivity.class));
		list.add(new PageTo("08",CompulsoryInsuranceActivity.class));
		list.add(new PageTo("09",BusinessInsuranceActivity.class));
		list.add(new PageTo("10",AddCardStep1Activity.class));
		//list.add(new PageTo("",.class));
		list.add(new PageTo("600",InsuranceMenuActivity.class));
		list.add(new PageTo("601",BuyInsuranceStep1.class));
		list.add(new PageTo("700",GoodsListActivity.class));
		list.add(new PageTo("720",ProviderListActivity.class));
		list.add(new PageTo("740",ArticleListActivity.class));
		list.add(new PageTo("800",SettingsActivity.class));
		list.add(new PageTo("801",SetupUserActivity.class));
		list.add(new PageTo("805",BoundActivity.class));
		list.add(new PageTo("810",NeedPayListActivity.class,"status","notpay")); //
		list.add(new PageTo("815",InsuranceMenuActivity.class));
		list.add(new PageTo("820",TaskListActivity.class));
		
		list.add(new PageTo("825",FriendActivity.class));
		list.add(new PageTo("830",UserFeedbackActivity.class));
		list.add(new PageTo("835",NeedPayListActivity.class,"status","finished")); // status="finished"
		list.add(new PageTo("840",CardViewActivity.class));
		list.add(new PageTo("845",MyFavorActivity.class));
		list.add(new PageTo("850",MyHistroyActivity.class));
		
		
		list.add(new PageTo("855",TaskListActivity.class));
		list.add(new PageTo("860",TaskListActivity.class));
		list.add(new PageTo("865",TaskListActivity.class));
		list.add(new PageTo("870",PasswordResetActivity.class));
		list.add(new PageTo("875",BindCellphoneActivity.class));
		
		//list.add(new PageTo("880",FindPassword.class));
		list.add(new PageTo("885",Step1Activity.class));
		
		
		
	}
	public void execute(){
		if (task.ation_url!=null && !task.ation_url.isEmpty()){
			Intent intent = new Intent(context,BrowserActivity.class);
			intent.putExtra("url", task.ation_url);
			context.startActivity(intent);
			return;
		}else if (task.page_id!=null && !task.page_id.isEmpty()){
			for(PageTo p : list){
				if (p.page_id.equals(task.page_id)){
					AppSettings.task_id = task.id;
					Intent intent = new Intent(context,p.activity_class);
					intent.putExtra("task_id",task.id);
					if (p.has_1_parameter){
						intent.putExtra(p.key, p.value);
					}
					context.startActivity(intent);
				}
			}
		}else{
			Intent intent = new Intent(context,DoTaskActivity.class);
			intent.putExtra("task_id", task.id);
			context.startActivity(intent);
		}
		
		
		
	}
	private class PageTo{
		public String page_id;
		public Class<?> activity_class;
		public String key;
		public String value;
		public boolean has_1_parameter;
		public PageTo(final String p,Class<?> c){
			page_id = p;
			activity_class = c;
			has_1_parameter = false;
		}
		public PageTo(final String p,Class<?> c,final String k,final String v){
			page_id = p;
			activity_class = c;
			key = k;
			value = v;
			has_1_parameter = true;
		}
	}
	

}
