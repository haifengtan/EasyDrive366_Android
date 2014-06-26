package cn.count.easydrive366.insurance;

import java.util.Stack;

import android.app.Activity;

import cn.count.easydriver366.base.BaseHttpActivity;

public class BaseInsurance extends BaseHttpActivity {
	protected static Stack<Activity> stacks;
	protected void stack_init(){
		if (stacks!=null){
			stacks.clear();
		}else{
			stacks = new Stack<Activity>();
		}
	}
	protected void stack_push(){
		if (stacks.indexOf(this)<0)
			stacks.push(this);
	}
	protected void stack_pop(){
		stacks.pop();
	}

}
