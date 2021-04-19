package com.u8.sdk;

/**
 * 分享参数
 * @author xiaohei
 *
 */
public class ShareParams {

	private String title;	//分享的标题，最大30个字符
	private String titleUrl;	//标题链接
	private String sourceName;	//分享此内容显示的出处名称
	private String sourceUrl;	//出处链接
	private String content;		//内容，最大130个字符
	private String url;			//链接，微信分享的时候会用到
	private String imgUrl;		//图片地址
	private boolean dialogMode; //是否全屏还是对话框
	private int notifyIcon;	//Notification的图标
	private String notifyIconText;	//Notification的文字
	private String comment;			//内容的评论，人人网分享必须参数，不能为空
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleUrl() {
		return titleUrl;
	}
	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public boolean isDialogMode() {
		return dialogMode;
	}
	public void setDialogMode(boolean dialogMode) {
		this.dialogMode = dialogMode;
	}

	public int getNotifyIcon() {
		return notifyIcon;
	}
	public void setNotifyIcon(int notifyIcon) {
		this.notifyIcon = notifyIcon;
	}
	public String getNotifyIconText() {
		return notifyIconText;
	}
	public void setNotifyIconText(String notifyIconText) {
		this.notifyIconText = notifyIconText;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
