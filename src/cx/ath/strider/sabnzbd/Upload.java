package cx.ath.strider.sabnzbd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Upload extends Activity {
	EditText txtUrl, txtName;
	Model m;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        
        txtUrl = (EditText)findViewById(R.id.txtUrl);
        txtName = (EditText)findViewById(R.id.txtName);
        
        m = new Model(
            	this,
            	getIntent().getStringExtra("hostname"),
            	getIntent().getIntExtra("port", 0),
            	getIntent().getBooleanExtra("ssl", false),
            	getIntent().getStringExtra("apikey")
    		); 
    }
    
    public void uploadNZB(View v) {
    	if(txtUrl.getText().length() > 0 && txtName.getText().length() > 0) {
    		if(m.uploadNZB(txtUrl.getText().toString(), txtName.getText().toString())) {
    			this.finish();    			
    		} else {
    			Toast.makeText(this, "Failed to upload nzb!", Toast.LENGTH_SHORT).show();
    		}
    	}
    }
}
