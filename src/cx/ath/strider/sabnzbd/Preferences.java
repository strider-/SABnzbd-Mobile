package cx.ath.strider.sabnzbd;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class Preferences extends PreferenceActivity {	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        EditTextPreference port = (EditTextPreference) findPreference("port");
        EditText et = (EditText) port.getEditText();
        et.setKeyListener(DigitsKeyListener.getInstance());
        
        PreferenceScreen server_prefs = (PreferenceScreen)getPreferenceScreen().findPreference("server_prefs");        
        for(int i=0; i<server_prefs.getPreferenceCount(); i++) {
        	if(server_prefs.getPreference(i) instanceof EditTextPreference) {
        		EditTextPreference p = (EditTextPreference)server_prefs.getPreference(i);
        		CharSequence summary = isNullOrEmpty(p.getText()) ? 
        						       p.getSummary() :
        						       p.getText();
        		p.setSummary(summary);
        		p.setOnPreferenceChangeListener(onPrefChange);
        	} else if(server_prefs.getPreference(i).getKey().equals("server_test")) {
        		server_prefs.getPreference(i).setSummary(getResources().getString(R.string.test_connection_summary));
        		server_prefs.getPreference(i).setOnPreferenceClickListener(this.testConnection);
        	}
        }        
    }
    
    private OnPreferenceClickListener testConnection = new OnPreferenceClickListener() {
    	@Override
    	public boolean onPreferenceClick(Preference preference) {
	    	String host = ((EditTextPreference)findPreference("hostname")).getText(),
		       	   port = ((EditTextPreference)findPreference("port")).getText(),
		           apikey = ((EditTextPreference)findPreference("apikey")).getText();
	    	boolean ssl = ((CheckBoxPreference)findPreference("secure")).isChecked();
	    	
	    	String result;
	    	
	    	if(isNullOrEmpty(host) || isNullOrEmpty(port) || isNullOrEmpty(apikey)) {
	    		result = "Missing server information!";
	    	} else {	    	
		    	Model m = new Model(Preferences.this, host, Integer.valueOf(port), ssl, apikey);	    	
		    	if(m.testConnection()) {
		    		result = "Test Successful!";
		    	} else {
		    		result = "Failed to connect!";
		    	}
	    	}
	    	
	    	preference.setSummary(result);	    	
	    	return true;
	    }
    };
    
    private boolean isNullOrEmpty(String s) {
    	return s == null || s.trim().length() == 0;
    }
    
	private OnPreferenceChangeListener onPrefChange = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			arg0.setSummary(arg1.toString());
			return true;
		}		
	};
}
