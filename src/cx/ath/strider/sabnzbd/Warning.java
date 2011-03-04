package cx.ath.strider.sabnzbd;

import android.util.Log;

public class Warning {
	private String date, type, msg;
	
	public Warning(String warning) {
		String[] s = warning.split("\n");
		date = s[0];
		type = s[1];
		msg = s[2];
		
		Log.i("SAB", date);
		Log.i("SAB", type);
		Log.i("SAB", msg);
	}
	
	public String getDate()    { return date; }
	public String getType()    { return type; }
	public String getMessage() { return msg; }
}
