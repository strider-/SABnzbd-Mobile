package cx.ath.strider.sabnzbd.Queue;

import cx.ath.strider.sabnzbd.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QueueAdapter extends BaseAdapter {
	private Context context;
	private QueueItem[] items;
	
	public QueueAdapter(Context context, QueueItem[] items) {
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
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(v == null) {
			LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.queue_item_layout, parent, false);
		}
		
		QueueItem item = items[position];
		TextView txtTitle = (TextView)v.findViewById(R.id.txtTitle),
				 txtETA = (TextView)v.findViewById(R.id.txtETA),
				 txtMB = (TextView)v.findViewById(R.id.txtMBLeft),
				 txtSize = (TextView)v.findViewById(R.id.txtSize);
		ProgressBar pbStatus = (ProgressBar)v.findViewById(R.id.pbStatus);
		
		txtTitle.setText(item.getFilename());
		if(item.getStatus().equals("Downloading")) {
			txtETA.setText("ETA: " + item.getTimeLeft());
		} else {
			txtETA.setText(item.getStatus());
		}
		txtMB.setText(item.getMBLeft() + " MB left");
		txtSize.setText(item.getSize());
		pbStatus.setProgress(item.getPercentage());
		
		return v;
	}

	public QueueItem[] getItems() {
		return items;
	}
	
	public void refresh(QueueItem[] items) {
		this.items = items;
		this.notifyDataSetChanged();
	}
}
