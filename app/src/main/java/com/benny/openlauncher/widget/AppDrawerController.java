package com.benny.openlauncher.widget;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.benny.openlauncher.R;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.util.Tool;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import net.gsantner.opoc.util.Callback;

import io.codetail.widget.RevealFrameLayout;

public class AppDrawerController extends RevealFrameLayout {
	public AppDrawerPage _drawerViewPage;
	public AppDrawerGrid _drawerViewGrid;
	private ViewGroup _swipeRefreshLayout;

	public int _drawerMode;
	public boolean _isOpen = false;
	@Nullable
	private Callback.a2<Boolean,Boolean> _appDrawerCallback;
	private Animator _appDrawerAnimator;
	private int _drawerAnimationTime;
	public static int closeHeight = Tool.dp2px(72);
	private ClassicsHeader _swipeRefreshHeader;

	public static class Mode {
		public static final int LIST = 0;
		public static final int GRID = 1;
		public static final int PAGE = 2;
	}

	public AppDrawerController(Context context) {
		super(context);
	}

	public AppDrawerController(Context context,AttributeSet attrs) {
		super(context,attrs);
	}

	public AppDrawerController(Context context,AttributeSet attrs,int defStyle) {
		super(context,attrs,defStyle);
	}

	private void initSwipeRefreshLayout2() {
		_swipeRefreshLayout = new SwipeRefreshLayout(getContext());
		_swipeRefreshLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		// ((SwipeRefreshLayout)_swipeRefreshLayout).drawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_refresh_icon));
		// 	((SwipeRefreshLayout)_swipeRefreshLayout).
		// 	((SwipeRefreshLayout)_swipeRefreshLayout).setVerticalScrollbarThumbDrawable(null);

		((SwipeRefreshLayout) _swipeRefreshLayout).setOnRefreshListener(() -> {
			// if (_refreshListener != null) {
			// 	_refreshListener.onRefresh();
			// }
			((SwipeRefreshLayout) _swipeRefreshLayout).setRefreshing(false); // 停止刷新动画
			close(0,0);
		});
		addView(_swipeRefreshLayout);
	}

	private void initSwipeRefreshLayout() {
		_swipeRefreshLayout = new SmartRefreshLayout(getContext());
		_swipeRefreshLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		_swipeRefreshHeader = new ClassicsHeader(getContext());
		_swipeRefreshHeader.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
		_swipeRefreshHeader.setVisibility(INVISIBLE);

		// _swipeRefreshLayout.addView(_swipeRefreshHeader);
		((SmartRefreshLayout) _swipeRefreshLayout).setRefreshHeader(_swipeRefreshHeader);
		((SmartRefreshLayout) _swipeRefreshLayout).setDragRate(1);
		// ((SmartRefreshLayout) _swipeRefreshLayout).setEnableOverScrollBounce(false);
		// ((SmartRefreshLayout) _swipeRefreshLayout).setEnablePureScrollMode(true);
		((SmartRefreshLayout) _swipeRefreshLayout).setReboundDuration(1);
		((SmartRefreshLayout) _swipeRefreshLayout).setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshLayout refreshlayout) {
				// refreshlayout.finishRefresh();
				refreshlayout.finishRefresh(false);
				close(0,0);
			}
		});

		addView(_swipeRefreshLayout);
	}


	private View createHeaderView() {
		//TODO 创建下拉刷新头部的View样式
		View lin = new View(this.getContext());
		lin.setLayoutParams(new ViewGroup.LayoutParams(100,100));
		return lin;
	}

	public void setCallBack(Callback.a2<Boolean,Boolean> callBack) {
		_appDrawerCallback = callBack;
	}

	public View getDrawer() {
		switch (_drawerMode) {
			case Mode.GRID:
				return _drawerViewGrid;
			case Mode.PAGE:
			default:
				return _drawerViewPage;
		}
	}

	public void open(int cx,int cy) {
		if (_isOpen) {
			return;
		}
		_isOpen = true;
		setVisibility(VISIBLE);
		if (_appDrawerCallback != null) {
			_appDrawerCallback.callback(true,false);
		}
	}

	public void close(int cx,int cy) {
		if (!_isOpen) {
			return;
		}
		_isOpen = false;
		if (_appDrawerCallback != null) {
			_appDrawerCallback.callback(false,false);
		}
		setVisibility(GONE);
	}

	public void reset() {
		switch (_drawerMode) {
			case Mode.GRID:
				_drawerViewGrid._recyclerView.scrollToPosition(0);
				break;
			case Mode.PAGE:
			default:
				_drawerViewPage.setCurrentItem(0,false);
				break;
		}
	}

	public void init() {
		if (isInEditMode()) {
			return;
		}
		initSwipeRefreshLayout();
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		_drawerMode = Setup.appSettings().getDrawerStyle();
		setVisibility(GONE);
		setBackgroundColor(Setup.appSettings().getDrawerBackgroundColor());
		switch (_drawerMode) {
			case Mode.GRID:
				_drawerViewGrid = new AppDrawerGrid(getContext());
				_swipeRefreshLayout.addView(_drawerViewGrid); // 让 SwipeRefreshLayout 包裹 RecyclerView
				break;
			case Mode.PAGE:
			default:
				_drawerViewPage = (AppDrawerPage) layoutInflater.inflate(R.layout.view_app_drawer_page,this,false);
				PagerIndicator indicator = (PagerIndicator) layoutInflater.inflate(R.layout.view_drawer_indicator,this,false);
				_drawerViewPage.withHome(indicator);
				_swipeRefreshLayout.addView(_drawerViewPage); // 让 SwipeRefreshLayout 包裹 ViewPager
				_swipeRefreshLayout.addView(indicator);
				break;
		}

		// switch (_drawerMode) {
		// 	case Mode.GRID:
		// 		_drawerViewGrid = new AppDrawerGrid(getContext());
		// 		addView(_drawerViewGrid);
		// 		break;
		// 	case Mode.PAGE:
		// 	default:
		// 		_drawerViewPage = (AppDrawerPage) layoutInflater.inflate(R.layout.view_app_drawer_page,this,false);
		// 		addView(_drawerViewPage);
		// 		PagerIndicator indicator = (PagerIndicator) layoutInflater.inflate(R.layout.view_drawer_indicator,this,false);
		// 		addView(indicator);
		// 		_drawerViewPage.withHome(indicator);
		// 		break;
		// }
	}

	@Override
	public WindowInsets onApplyWindowInsets(WindowInsets insets) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			setPadding(0,insets.getSystemWindowInsetTop(),0,insets.getSystemWindowInsetBottom());
			return insets;
		}
		return insets;
	}
}
