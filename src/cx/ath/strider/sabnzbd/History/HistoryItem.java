package cx.ath.strider.sabnzbd.History;

import java.sql.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryItem {
	private String size, category, storage, status, name, id, action;
	private int download_time, postproc_time;
	private Date completed;
	private long downloaded, bytes;
	private String[] stage_log;
	
	public HistoryItem(JSONObject item) {
		try {
			id = item.getString("nzo_id");
			action = item.getString("action_line");
			size = item.getString("size");
			category = item.getString("category");
			storage = item.getString("storage");
			status = item.getString("status");
			name = item.getString("name");
			download_time = item.getInt("download_time");
			postproc_time = item.getInt("postproc_time");
			completed = new Date(item.getLong("completed") * 1000);
			downloaded = item.getLong("downloaded");
			bytes = item.getLong("bytes");
			
			JSONArray log = item.getJSONArray("stage_log");
			stage_log = new String[log.length()];
			for(int i=0; i<log.length(); i++) {
				stage_log[i] = log.getJSONObject(i).getJSONArray("actions").getString(0);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getAction() 			{ return action;		}
	public String getID()			    { return id;		    }
	public String getSize()				{ return size; 			}
	public String getCategory()			{ return category; 		}
	public String getStorage()			{ return storage; 		}
	public String getStatus()			{ return status; 		}
	public String getName()				{ return name; 			}
	public int getDownloadTime()		{ return download_time; }
	public int getPostProcessingTime()	{ return postproc_time; }
	public Date getCompletedDate()		{ return completed; 	}
	public long getDownloaded()			{ return downloaded; 	}
	public long getBytes()				{ return bytes; 		}
	public String[] getStageLog()		{ return stage_log; 	}
}
