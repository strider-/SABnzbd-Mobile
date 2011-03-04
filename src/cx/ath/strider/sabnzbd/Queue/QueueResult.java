package cx.ath.strider.sabnzbd.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class QueueResult {
	private String size, sizeleft, uptime, kbpersec, speed, status;
	private boolean paused;
	private QueueAdapter adapter;
	private QueueItem[] items;
	
	public QueueResult(Context context, JSONObject obj) {
		try {			
			paused = obj.getBoolean("paused");
			speed = obj.getString("speed");
			size = obj.getString("size");
			sizeleft = obj.getString("sizeleft");
			uptime = obj.getString("uptime");
			kbpersec = obj.getString("kbpersec");
			status = obj.getString("status");
			
			JSONArray slots = obj.getJSONArray("slots");
			items = new QueueItem[slots.length()];
			for(int i=0; i<items.length; i++)
				items[i] = new QueueItem(slots.getJSONObject(i));
			adapter = new QueueAdapter(context, items);
		} catch(JSONException e) {
			Log.e("SAB", "Error", e);
		}
	}
	
	public String getStatus()		 { return status; }
	public boolean isPaused()		 { return paused; }
	public String getSpeed()		 { return speed; }
	public String getSize()			 { return size; }
	public String getSizeLeft()		 { return sizeleft; }
	public String getUptime()		 { return uptime; }
	public String getKBPerSec()		 { return kbpersec; }
	public QueueAdapter getAdapter() { return adapter; }
	public QueueItem[] getItems()    { return items; }
}
