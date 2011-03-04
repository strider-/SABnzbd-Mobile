package cx.ath.strider.sabnzbd.Queue;

import org.json.JSONException;
import org.json.JSONObject;

public class QueueItem {
	private String status, eta, timeleft, avg_age, mb, filename, priority, cat, mbleft, size, nzo_id;		
	private int index, percentage;
	
	public QueueItem(JSONObject item) {		
		try {
			status = item.getString("status");
			eta = item.getString("eta");
			timeleft = item.getString("timeleft");
			avg_age = item.getString("avg_age");
			mb = item.getString("mb");
			filename = item.getString("filename");
			priority = item.getString("priority");
			cat = item.getString("cat");
			mbleft = item.getString("mbleft");
			size = item.getString("size");
			index = item.getInt("index");
			nzo_id = item.getString("nzo_id");
			percentage = Integer.valueOf(item.getString("percentage"));
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getID()			{ return nzo_id; }
	public String getStatus()		{ return status; }
	public String getETA() 			{ return eta; }
	public String getTimeLeft() 	{ return timeleft; }
	public String getAverageAge() 	{ return avg_age; }
	public String getMB() 			{ return mb; }
	public String getFilename() 	{ return filename; }
	public String getPriority() 	{ return priority; }
	public String getCategory() 	{ return cat; }
	public String getMBLeft() 		{ return mbleft; }
	public String getSize() 		{ return size; }
	public int getIndex() 			{ return index; }
	public int getPercentage() 		{ return percentage; }
}
