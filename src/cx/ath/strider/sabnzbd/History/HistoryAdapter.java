package cx.ath.strider.sabnzbd.History;

import cx.ath.strider.sabnzbd.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {
	private Context context;
	private HistoryItem[] items;
	
	public HistoryAdapter(Context context, HistoryItem[] items) {
		this.context = context;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int arg0) {
		return items[arg0];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(v == null) {
			LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.history_item_layout, parent, false);
		}
		
		HistoryItem item = items[position];
		TextView txtTitle = (TextView)v.findViewById(R.id.txtTitle),
				 txtInfo  = (TextView)v.findViewById(R.id.txtInfo),
				 txtSize  = (TextView)v.findViewById(R.id.txtSize);
		
		String info = item.getAction().equals("") ? item.getStatus() : item.getAction();
		txtTitle.setText(item.getName());
		txtInfo.setText(String.format("%s %s", info, item.getCompletedDate().toLocaleString()));
		txtSize.setText(item.getSize());
		
		int bgColor = 0xFFFF;
		if(item.getStatus().equals("Failed"))
			bgColor = 0x5FFF0000;
		else if(item.getStatus().equals("Verifying"))
			bgColor = 0x5F0000FF;
		else if(item.getStatus().equals("Repairing"))
			bgColor = 0x5FFF00FF;
		else if(item.getStatus().equals("Extracting") ||
				item.getStatus().equals("Fetching") ||
				item.getStatus().equals("Queued"))
			bgColor = 0x5F00FF00;

		if(bgColor != -1)
			v.findViewById(R.id.historyLayout).setBackgroundColor(bgColor);
		
		return v;
	}
	public HistoryItem[] getItems() {
		return items;
	}
	
	public void refresh(HistoryItem[] items) {
		this.items = items;
		this.notifyDataSetChanged();
	}
}
