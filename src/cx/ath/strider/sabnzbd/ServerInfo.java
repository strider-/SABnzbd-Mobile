package cx.ath.strider.sabnzbd;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ServerInfo extends Activity {
	Model m;
	TextView txtServerVersion;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_info);
        
        txtServerVersion = (TextView)findViewById(R.id.txtServerVersion);
        
        m = new Model(
            	this,
            	getIntent().getStringExtra("hostname"),
            	getIntent().getIntExtra("port", 0),
            	getIntent().getBooleanExtra("ssl", false),
            	getIntent().getStringExtra("apikey")
    		);
        
        txtServerVersion.setText("SABnzbd v" + m.getVersion());
	}
} 
