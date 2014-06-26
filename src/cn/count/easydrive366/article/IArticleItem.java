package cn.count.easydrive366.article;

import android.widget.ImageView;


public interface IArticleItem{
	void do_share_on(final Object obj);
	void openRating(final Object obj);
	void do_favor_on(final Object obj,ImageView view);
}