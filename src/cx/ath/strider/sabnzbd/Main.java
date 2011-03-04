package cx.ath.strider.sabnzbd;

import cx.ath.strider.sabnzbd.History.History;
import cx.ath.strider.sabnzbd.Queue.Queue;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;

public class Main extends Activity implements OnSharedPreferenceChangeListener {
	private SharedPreferences prefs;
	private ImageButton btnQueue, btnHistory, btnServerInfo, btnUpload;
	private ImageButton[] buttons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        
        btnQueue = (ImageButton)findViewById(R.id.btnQueue);
        btnHistory = (ImageButton)findViewById(R.id.btnHistory);
        btnServerInfo = (ImageButton)findViewById(R.id.btnUpload);
        btnUpload = (ImageButton)findViewById(R.id.btnServerInfo);
        buttons = new ImageButton[] {btnQueue,btnHistory,btnUpload,btnServerInfo};
        
        setButtonState();
        
        if(!hasServerInfo()) {
        	showSettings(null);
        }               
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		setButtonState();
	}
    		
	public void showHistory(View v) {
		Intent i = getNewIntent(History.class);
		i.putExtra("refresh", getHistoryRefresh());
		startActivity(i);
	}
	
	public void showQueue(View v) {
		Intent i = getNewIntent(Queue.class);
		i.putExtra("refresh", getQueueRefresh());
		startActivity(i);
	}
	
    public void showSettings(View v) {
		Intent i = new Intent(Main.this, Preferences.class);		
		startActivity(i);
    }
    
    public void showUpload(View v) {
    	Intent i = getNewIntent(Upload.class);
    	startActivity(i);
    }
    
    public void showServerInfo(View v) {
    	Intent i = getNewIntent(ServerInfo.class);
    	startActivity(i);
    }
    
    private Intent getNewIntent(Class<?> cls) {
    	Intent i = new Intent(Main.this, cls);
		i.putExtra("hostname", getHostname());
		i.putExtra("port", getPort());
		i.putExtra("ssl", getSSL());
		i.putExtra("apikey", getApiKey());
		return i;
    }
    
    private void setButtonState() {
    	boolean enabled = hasServerInfo();
    	int colorFilter = enabled ? 0x00000000 : 0xAF000000;
    	
    	for(ImageButton btn : buttons) {
    		btn.setEnabled(enabled);
    		btn.setColorFilter(colorFilter);
    	}
    }
    
    private String getHostname() {
    	return prefs.getString("hostname", "");
    }
    private int getPort() {
    	String port = prefs.getString("port", "0");
    	if(port.length() > 0)
    		return Integer.valueOf(port);
    	return 0;
    }
    private boolean getSSL() {
    	return prefs.getBoolean("secure", false);
    }
    private String getApiKey() {
    	return prefs.getString("apikey", "");
    }
    private int getQueueRefresh() {
    	return Integer.valueOf(prefs.getString("queue_refresh", "1000"));
    }
    private int getHistoryRefresh() {
    	return Integer.valueOf(prefs.getString("history_refresh", "10000"));
    }    
    private boolean hasServerInfo() {
    	return getHostname().trim().length() > 0 && getPort() > 0 && getApiKey().trim().length() > 0;
    }
}