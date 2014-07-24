package com.dexetra.pulltorefresh;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class MainActivity extends Activity {
	LayoutInflater layoutInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layoutInflater = LayoutInflater.from(this);
		PullToRefreshListView listView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
		Adapter adapter = new Adapter();
		listView.setAdapter(adapter);
		final int width = getResources().getDisplayMetrics().widthPixels;
		Log.d("Scroll", "width: " + width);
		listView.setPullToRefresh(width, new PullToRefreshListView.PullToRefresh() {

			@Override
			public void onRefresh() {
				Log.d("Scroll", "onRefresh");

			}

			@Override
			public void onPullStarted() {
				Log.d("Scroll", "onPullStarted, limit: " + width);
			}

			@Override
			public void onPull(int progress) {
				Log.d("Scroll", "onPull: " + progress);
				((CustomProgressBar) findViewById(R.id.pb))
						.setProgress(progress);

			}

			@Override
			public void onCancel() {
				Log.d("Scroll", "onCancel: ");
				((CustomProgressBar) findViewById(R.id.pb)).setProgress(0);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class Adapter extends BaseAdapter {
		@Override
		public int getCount() {
			return 100;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.layout_textview,
						null, false);
			}
			((TextView) convertView).setText("position: " + position);
			return convertView;
		}

	}

	public void stopLoading(View view) {
        Log.d(MainActivity.class.getName(),"Stop Loading");
		((CustomProgressBar) findViewById(R.id.pb)).stopLoading();
		((PullToRefreshListView) findViewById(R.id.pullToRefreshListView))
				.setRefreshDone();
	}
}
