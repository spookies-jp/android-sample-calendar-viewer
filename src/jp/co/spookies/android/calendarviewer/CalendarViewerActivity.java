package jp.co.spookies.android.calendarviewer;

import java.util.ArrayList;
import java.util.List;

import jp.co.spookies.android.calendarviewer.adapter.CalendarAdapter;
import jp.co.spookies.android.calendarviewer.model.CalendarEntry;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CalendarViewerActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.calendar);
	}

	@Override
	public void onResume() {
		super.onResume();

		final ArrayAdapter<CalendarEntry> adapter = new CalendarAdapter(this,
				R.layout.list_calendar);
		// カレンダー一覧を取得
		for (CalendarEntry c : getCalendarEntries()) {
			adapter.add(c);
		}

		// カレンダーのリスト生成
		ListView view = (ListView) findViewById(R.id.calendar_list_view);
		view.setAdapter(adapter);
		view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 選択したときはそのカレンダーのイベント一覧へ
				CalendarEntry calendar = adapter.getItem(position);
				Intent intent = new Intent(CalendarViewerActivity.this,
						EventActivity.class);
				intent.putExtra(CalendarEntry.KEY_ID, calendar.getId());
				startActivity(intent);
			}
		});
	}

	/**
	 * 編集可能なカレンダー一覧を取得
	 * 
	 * @return
	 */
	public List<CalendarEntry> getCalendarEntries() {
		ContentResolver resolver = this.getContentResolver();
		Uri calendarUri = Uri.parse("content://com.android.calendar/calendars");
		// query生成
		Cursor cursor = resolver.query(calendarUri, (new String[] { "_id",
				"name" }), "access_level = ? and name is not null",
				new String[] { "700" }, null);
		if (cursor == null) {
			return null;
		}

		List<CalendarEntry> calendars = new ArrayList<CalendarEntry>();
		// 1行ずつfetch
		while (cursor.moveToNext()) {
			int _id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
			String name = cursor
					.getString(cursor.getColumnIndexOrThrow("name"));
			calendars.add(new CalendarEntry(_id, name));
		}
		// cursorのclose
		cursor.close();
		return calendars;
	}
}