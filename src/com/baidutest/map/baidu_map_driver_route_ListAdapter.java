package com.baidutest.map;

import java.util.List;
import java.util.Map;

import com.example.testdemo.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class baidu_map_driver_route_ListAdapter extends BaseAdapter {
	private List<Map<String, Object>> list;
	private Context context;

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;// list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * public RemindListAdapter(Context context, List<JSONObject> list) {
	 * this.context = context; this.list = list; }
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		LayoutInflater mInflater = LayoutInflater.from(context);
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(
					R.layout.baidu_map_driver_route_listview, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.name_textView);
			holder.imgv_img = (ImageView) convertView
					.findViewById(R.id.infolistimage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText((String) list.get(position).get("title"));
		holder.imgv_img.setImageDrawable((Drawable) list.get(position).get(
				"imgv_img"));
		return convertView;
	}

	public void addNewsItem(Map<String, Object> newsitem) {
		list.add(newsitem);
	}

	public final class ViewHolder {
		public TextView img;
		public TextView title;
		public ImageView imgv_img;
	}

}
