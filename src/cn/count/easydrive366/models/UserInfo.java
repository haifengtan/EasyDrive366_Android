package cn.count.easydrive366.models;

/**
 * 用户信息实体类 使用单例模式
 * 
 * @author 谭海峰
 * 
 */
public class UserInfo {
	
	private UserInfo(){}
	
	private static UserInfo userInfo =new UserInfo();
	
	public static UserInfo getUserInfo(){
		return userInfo;
	}
	
	/**百度推送使用的userId*/
	private String pushUserID;
	
	/**百度推送使用的channelID*/
	private String pushChannelID;
	
	public String getPushUserID() {
		return pushUserID;
	}
	public void setPushUserID(String pushUserID) {
		this.pushUserID = pushUserID;
	}
	public String getPushChannelID() {
		return pushChannelID;
	}
	public void setPushChannelID(String pushChannelID) {
		this.pushChannelID = pushChannelID;
	}

}
