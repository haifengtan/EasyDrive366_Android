package cn.count.easydriver366.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;

import cn.count.easydrive366.BusinessInsuranceActivity;
import cn.count.easydrive366.CarRegistrationActivity;
import cn.count.easydrive366.CarServiceActivity;
import cn.count.easydrive366.CompulsoryInsuranceActivity;
import cn.count.easydrive366.DriverLicenseActivity;
import cn.count.easydrive366.HelpCallActivity;
import cn.count.easydrive366.InformationActivity;
import cn.count.easydrive366.InsuranceProcessListActivity;
import cn.count.easydrive366.MaintainActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.RescueActivity;
import cn.count.easydrive366.TaxForCarShipActivity;
import cn.count.easydrive366.card.AddCardStep1Activity;
import cn.count.easydrive366.insurance.BuyInsuranceStep1;
import cn.count.easydrive366.insurance.BuyInsuranceStep4;
import cn.count.easydrive366.insurance.BuyInsuranceStep7;
import cn.count.easydrive366.insurance.InsuranceList;
import cn.count.easydrive366.insurance.InsuranceList2;
import cn.count.easydrive366.insurance.InsuranceMenuActivity;
import cn.count.easydrive366.order.NeedPayListActivity;
import cn.count.easydrive366.user.UesrProfileActivity;

public class Menus {
	private Activity _activity;
	private List<HomeMenu> menus;
	private List<HomeMenu> insurance_menus;
	public Menus(Activity activity){
		_activity = activity;
		initMenuItems();
	}
	private void initMenuItems(){
		menus = new ArrayList<HomeMenu>();
		insurance_menus = new ArrayList<HomeMenu>();
		HomeMenu mo =  new HomeMenu(_activity.getResources().getString(R.string.key_13),"13",NeedPayListActivity.class,R.drawable.dingdan);
		mo.params = new HashMap<String,String>();
		
		mo.params.put("status", "finished");
		menus.add(mo);
		//menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_14),"14",InformationActivity.class,R.drawable.dingdan));
		menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_01),"01",InformationActivity.class,R.drawable.xinxi));
		//menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_02),"02",HelpCallActivity.class,R.drawable.n));
		
		menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_04),"04",MaintainActivity.class,R.drawable.baoyang));	
		//menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_12),"12",CarServiceActivity.class,R.drawable.o));
		menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_06),"06",CarRegistrationActivity.class,R.drawable.cheliang));
		menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_05),"05",DriverLicenseActivity.class,R.drawable.jiashizheng));
		menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_03),"03",InsuranceMenuActivity.class,R.drawable.p));
		menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_15),"15",UesrProfileActivity.class,R.drawable.ziliao));
		
		
		insurance_menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_07),"07",TaxForCarShipActivity.class,R.drawable.q));
		insurance_menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_08),"08",CompulsoryInsuranceActivity.class,R.drawable.s));
		insurance_menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_09),"09",BusinessInsuranceActivity.class,R.drawable.t));	
		//insurance_menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_11),"11",BuyInsuranceStep1.class,R.drawable.r));
		//insurance_menus.add(new HomeMenu(_activity.getResources().getString(R.string.key_10),"10",AddCardStep1Activity.class,R.drawable.n));
		HomeMenu mh =  new HomeMenu(_activity.getResources().getString(R.string.key_13),"13",NeedPayListActivity.class,R.drawable.p);
		mh.params = new HashMap<String,String>();
		
		mh.params.put("status", "finished");
		mh.params.put("type", "ins");
		insurance_menus.add(mh);
		//
	}
	public List<HomeMenu> getMenus(){
		return menus;
	}
	public List<HomeMenu> getInsuranceMenus(){
		return insurance_menus;
	}
	public void updateHome(boolean fromCache){
		for(HomeMenu item:menus){
			if (item.menuItem!=null){
				item.menuItem.getLatest(fromCache);
			}
		}
	}
	public void updateInsurance(boolean fromCache){
		for(HomeMenu item:insurance_menus){
			if (item.menuItem!=null){
				item.menuItem.getLatest(fromCache);
			}
		}
	}
	public HomeMenu findMenuByKey(final String key){
		for(HomeMenu item : menus){
			if (item.key.equals(key)){
				return item;
			}
		}
		for(HomeMenu item : insurance_menus){
			if (item.key.equals(key)){
				return item;
			}
		}
		return null;
	}
	public Class<?> findMenuItemClassByKey(final String key){
		HomeMenu menu= findMenuByKey(key);
		if (menu!=null){
			return menu.activityClass;
		}else{
			return null;
		}
	}
	
	
}
