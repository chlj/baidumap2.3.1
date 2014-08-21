package com.baidutest.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class baidu_treeAdapter extends BaseExpandableListAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private int myPaddingLeft = 0;// 如果是由SuperTreeView调用，则作为子项需要往右移

	public static final int ItemHeight = 48;// 每项的高度
	public static final int PaddingLeft = 36;// 每项的高度

	private List<baidu_bus_demo> mParentList = new ArrayList<baidu_bus_demo>(); //预留用
	public baidu_treeAdapter(Context context, int myPaddingLeft,
			List<baidu_bus_demo> parentList) {
		this.mContext = context;
		this.myPaddingLeft = myPaddingLeft;
		this.mParentList = parentList;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	static public class TreeNode {
		public Object parent;
		public List<baidu_bus_demo_info> childs = new ArrayList<baidu_bus_demo_info>();
	}

	List<TreeNode> treeNodes = new ArrayList<TreeNode>();

	public List<TreeNode> GetTreeNode() {
		return treeNodes;
	}

	public void UpdateTreeNode(List<TreeNode> nodes) {
		treeNodes = nodes;
	}

	public void RemoveAll() {
		treeNodes.clear();
	}

	@Override
	public int getGroupCount() {

		return treeNodes.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		return treeNodes.get(groupPosition).childs.size();
	}

	@Override
	public Object getGroup(int groupPosition) {

		return treeNodes.get(groupPosition).parent;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {

		return treeNodes.get(groupPosition).childs.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return childPosition;
	}

	@Override
	public boolean hasStableIds() {

		return true;
	}

	static public TextView getTextView(Context context) {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);

		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		return textView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		TextView textView = getTextView(this.mContext);
		textView.setText(getGroup(groupPosition).toString());
		textView.setPadding(70, 0, 0, 0);
		return textView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// 子对象引用布局
		final ViewCache btnList;
		if (convertView == null) {
			convertView = mInflater.inflate(
					com.example.testdemo.R.layout.baidumap_activity_bus_item, null);
			btnList = new ViewCache();
			btnList.mPName = (TextView) convertView
					.findViewById(com.example.testdemo.R.id.name);
			btnList.mItemLayout = (LinearLayout) convertView
					.findViewById(com.example.testdemo.R.id.workers_item_layout);
			convertView.setTag(btnList);
		} else {
			btnList = (ViewCache) convertView.getTag();
		}
		btnList.mPName.setText(treeNodes.get(groupPosition).childs
				.get(childPosition).getTip().toString());
		convertView.setPadding(80, 0, 0, 0);

//		if (treeNodes.get(groupPosition).childs.get(childPosition).getFlg()) {
//			btnList.mChosen.setImageResource(R.drawable.rem_pwd_sel);
//		} else {
//			btnList.mChosen.setImageResource(R.drawable.rem_pwd);
//		}
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

	public class ViewCache {
		public TextView mPName = null;
	
		public LinearLayout mItemLayout = null;
	}
}
