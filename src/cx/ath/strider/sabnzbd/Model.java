package cx.ath.strider.sabnzbd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cx.ath.strider.sabnzbd.History.HistoryAdapter;
import cx.ath.strider.sabnzbd.History.HistoryItem;
import cx.ath.strider.sabnzbd.Queue.QueueResult;

import android.content.Context;
import android.util.Log;

public class Model {
	private final String API_URL = "http%s://%s:%d/api?apikey=%s&output=json&mode=%s";
	
	private Context context;
	private String host, apikey;
	private int port;
	private boolean ssl;	
	
	// always verify the host - don't check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
	                return true;
	        }
	};
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) 
            	throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) 
            	throws CertificateException {
            }
    } };
    
	public Model(Context Context, String Host, int Port, boolean SSL, String ApiKey) {
		this.context = Context;
		this.host = Host;
		this.port = Port;
		this.ssl = SSL;
		this.apikey = ApiKey;
	}
	
	public HistoryAdapter getHistory() {
		JSONObject json = request("history");
		try {
			JSONArray slots = json.getJSONObject("history").getJSONArray("slots");
			HistoryItem[] items = new HistoryItem[slots.length()];
			for(int i=0; i<items.length; i++)
				items[i] = new HistoryItem(slots.getJSONObject(i));
			return new HistoryAdapter(context, items);
		} catch(JSONException e) {
			return null;
		}
	}
	
	public QueueResult getQueue() {
		JSONObject json = request("queue");
		try {
			return new QueueResult(context, json.getJSONObject("queue"));
		} catch (JSONException e) {
			return null;
		}
	}
	
	public boolean Pause() {
		JSONObject json = request("pause");
		try {
			return json.getBoolean("status");
		} catch (JSONException e) {
			return false;
		}
	}
	public boolean Pause(String ID) {
		JSONObject json = request("queue&name=pause&value=" + ID);
		try {
			return json.getBoolean("status");
		} catch (JSONException e) {
			return false;
		}
	}	
	public boolean Resume() {
		JSONObject json = request("resume");
		try {
			return json.getBoolean("status");
		} catch (JSONException e) {
			return false;
		}
	}
	public boolean Resume(String ID) {
		JSONObject json = request("queue&name=resume&value=" + ID);
		try {
			return json.getBoolean("status");
		} catch (JSONException e) {
			return false;
		}
	}	
	
	public String getVersion() {
		JSONObject json = request("version");
		try {
			return json.getString("version");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public Warning[] getWarnings() {
		JSONObject json = request("warnings");
		try {
			JSONArray a = json.getJSONArray("warnings");
			Warning[] warnings = new Warning[a.length()];
			for(int i=0;i<a.length();i++)
				new Warning(a.getString(i));
			return warnings;
		} catch (JSONException e){
			return null;
		}
	}
	
	public boolean uploadNZB(String url, String name) {
		String cleanUrl = clean(url);
		String cleanName = clean(name);

		JSONObject json = request("addurl&nzbname=" + cleanName + "&name=" + cleanUrl);
		if(json == null)
			return false;
		
		try {
			return json.getBoolean("status");
		} catch (JSONException e) {
			return false;
		}
	}
	
	public boolean deleteHistoryItem(HistoryItem item) {
		JSONObject json = request("history&name=delete&value=" + item.getID());
		try {
			return json.getBoolean("status");
		} catch (JSONException e) {
			return false;
		}
	}
	
	public boolean clearHistory() {
		JSONObject json = request("history&name=delete&value=all");
		try {
			return json.getBoolean("status");
		} catch (JSONException e) {
			return false;
		}
	}
	
	public Object getConfig() {
		JSONObject json = request("get_config");
		
		try {
			// stub
			return json.getJSONObject("config");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String setConfig(String section, String keyword, String value) {
		JSONObject json = request(String.format("set_config&section=%s&keyword=%s&value=%s",
												section, keyword, value));
		try {
			return json.getJSONObject(section).getJSONObject(keyword).getString(value);
		} catch (JSONException e){
			return null;
		}
	}

	private String clean(String s) {
		String clean = "";
		for(int i=0; i<s.length(); i++) {
			Character c = s.charAt(i);
			if(Character.isLetterOrDigit(c) || c.equals('.') || c.equals('-'))
				clean += c;
			else {
				byte b = (byte)((char)c);
				clean += "%" + Integer.toHexString((int)b).toUpperCase();
			}
		}
		return clean;
	}
	
	public boolean testConnection() {	
		HttpClient client = new DefaultHttpClient();		
		HttpGet get = new HttpGet(String.format(API_URL, ssl ? "s" : "", host, port, apikey, "version"));
		
		try {
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 3000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 3000);			
			client.execute(get);
			
			return true;
		} catch(SSLException e) {
			// trusted certificate error but who cares, we connected.  
			return true;
		} catch(IOException e) {
			return false;
		}
	}
	
    private JSONObject request(String mode) {
		String address = String.format(API_URL, ssl ? "s" : "", host, port, apikey, mode);
		HttpURLConnection http;
		
		try {
			URL url = new URL(address);
			
			if(ssl) {
				SSLContext sc = SSLContext.getInstance("TLS");
	        	sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        	HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        	http = (HttpsURLConnection)url.openConnection();
	        	((HttpsURLConnection)http).setHostnameVerifier(DO_NOT_VERIFY);
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
	
			String raw = getJsonString(http.getInputStream());
			http.disconnect();
			
			return new JSONObject(raw);
		} catch(ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (KeyManagementException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} finally {
			Log.i("SAB", "Request Complete");
		}    	
    }

	private String getJsonString(InputStream stream) throws IOException {
		StringWriter writer = new StringWriter();
		char[] buffer = new char[0x1000];
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			int r;
			while((r = reader.read(buffer, 0, buffer.length)) != -1)
				writer.write(buffer, 0, r);
		} finally {
			stream.close();
		}
		
		return writer.toString();
	}	
}
