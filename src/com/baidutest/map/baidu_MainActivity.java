package com.baidutest.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.testdemo.R;


public class baidu_MainActivity extends Activity implements OnClickListener {

	private Button btn_loading, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
			btn10, btn11,btn12,btn13,btn14,btn15,btn16,btn17,btn18,btn19;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baidumap_main);
		findView();
	}

	public void findView() {
		btn_loading = (Button) findViewById(R.id.btn_loading); // 加载一张地图
		btn_loading.setOnClickListener(this);

		btn2 = (Button) findViewById(R.id.btn2); // 2
		btn2.setOnClickListener(this);

		btn3 = (Button) findViewById(R.id.btn3); // 3
		btn3.setOnClickListener(this);

		btn4 = (Button) findViewById(R.id.btn4); // 4
		btn4.setOnClickListener(this);

		btn5 = (Button) findViewById(R.id.btn5); // 5
		btn5.setOnClickListener(this);

		btn6 = (Button) findViewById(R.id.btn6); // 6
		btn6.setOnClickListener(this);

		btn7 = (Button) findViewById(R.id.btn7); // 7
		btn7.setOnClickListener(this);

		btn8 = (Button) findViewById(R.id.btn8); // 8
		btn8.setOnClickListener(this);

		btn9 = (Button) findViewById(R.id.btn9); // 9
		btn9.setOnClickListener(this);

		btn10 = (Button) findViewById(R.id.btn10); // 10
		btn10.setOnClickListener(this);

		btn11 = (Button) findViewById(R.id.btn11); // 11
		btn11.setOnClickListener(this);
		
		btn12 = (Button) findViewById(R.id.btn12); // 12
		btn12.setOnClickListener(this);
		
		btn13 = (Button) findViewById(R.id.btn13); // 13
		btn13.setOnClickListener(this);
		
		btn14 = (Button) findViewById(R.id.btn14); // 14
		btn14.setOnClickListener(this);
		
		btn15 = (Button) findViewById(R.id.btn15); // 15
		btn15.setOnClickListener(this);
		
		btn16 = (Button) findViewById(R.id.btn16); // 16
		btn16.setOnClickListener(this);
		
		btn17 = (Button) findViewById(R.id.btn17); // 17
		btn17.setOnClickListener(this);
		
		btn18 = (Button) findViewById(R.id.btn18); // 18
		btn18.setOnClickListener(this);
		
		btn19 = (Button) findViewById(R.id.btn19); // 19
		btn19.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_loading:

			startActivity(new Intent(baidu_MainActivity.this,
					baidu_load_map_full_Activity.class));
			break;
		case R.id.btn2: // 2一般设置
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_public_set_Activity.class));
			break;

		case R.id.btn3: // 3 单个标记
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_single_mark_Activity.class));
			break;

		case R.id.btn4: // 4 根据点 得到相应的信息 (单个点的信息)
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_single_mark_info_Activity.class));
			break;
		case R.id.btn5: // 5 点对点变颜色
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_single_point_point_Activity.class));
			break;

		case R.id.btn6: // 6 多个点进行标记
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_more_mark_Activity.class));
			break;

		case R.id.btn7: // 7 默认展开多个标记
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_more_mark_expand_Activity.class));
			break;

		case R.id.btn8: // 8.信息与坐标互换
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_point_to_info_Activity.class));
			break;

		case R.id.btn9: // 9.地图搜索
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_search_Activity.class));
			break;

		case R.id.btn10: // 10.公交线路查询
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_search_car_Activity.class));
			break;
		case R.id.btn11: // 11.路径规划查询
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_search_diy_Activity.class));
			break;
			
		case R.id.btn12: // 12.GPS
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_search_GPS_Activity.class));
			break;
			
		case R.id.btn13: // 13. 百度GPS
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_search_GPS_2_Activity.class));
			break;
		case R.id.btn14: // 14.百度地图界面_1
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_map_jiemian_1_Activity.class));
			break;
			
		case R.id.btn15: // 15.百度地图界面_2
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_map_jiemian_2_Activity.class));
			break;
			
		case R.id.btn16: // 16.百度地图界面_搜索显示
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_map_jiemian_search_show_Activity.class));
			break;
		case R.id.btn17: // 17.百度地图界面_更多标志
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_map_jiemian_search_show_more_Activity.class));
			break;
			
		case R.id.btn18: // 18.百度地图界面_更多标志2
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_map_jiemian_search_show_more_2_Activity.class));
			break;
			
		case R.id.btn19: // 19.百度地图界面_搜索显示_列表
			startActivity(new Intent(baidu_MainActivity.this,
					baidu_map_jiemian_search_show_list_Activity.class));
			
			break;
		default:
			break;
		}

	}

}
