package cx.ath.strider.sabnzbd.Queue;

import java.util.Timer;
import java.util.TimerTask;

import cx.ath.strider.sabnzbd.Model;
import cx.ath.strider.sabnzbd.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Queue extends Activity {
	Handler handler = new Handler();
	ImageButton btnState;
	ListView lvQueue;
	TextView txtSpeed, txtRemaining;
	QueueResult result;
	Model m;
	Timer timer = new Timer();
	TimerTask task;
	String title;
	int refreshInterval;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue);
        
        title = this.getResources().getString(R.string.queue_name);
        lvQueue = (ListView)findViewById(R.id.lvQueue);
        btnState = (ImageButton)findViewById(R.id.btnState);
        txtSpeed = (TextView)findViewById(R.id.txtSpeed);
        txtRemaining = (TextView)findViewById(R.id.txtRemaining);
        
        lvQueue.setEmptyView(findViewById(R.id.empty_queue));
        lvQueue.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				QueueItem q = (QueueItem)arg0.getItemAtPosition(arg2);
				if(q.getStatus().equals("Downloading"))
					m.Pause(q.getID());
				else if(q.getStatus().equals("Paused"))
					m.Resume(q.getID());
			}        	
        });
        
        m = new Model(
        	this,
        	getIntent().getStringExtra("hostname"),
        	getIntent().getIntExtra("port", 0),
        	getIntent().getBooleanExtra("ssl", false),
        	getIntent().getStringExtra("apikey")
		);
        
        task = new TimerTask() {
        	public void run() {
        		handler.post(refreshQueue);
        	}
        };        
                
        refreshInterval = getIntent().getIntExtra("refresh", 1000);
        timer.schedule(task, 0, refreshInterval);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();    	
    	timer.cancel();   
    }
    
    public void toggleQueue(View v) {
    	if(result.isPaused()) {
    		m.Resume();
    		btnState.setImageDrawable(getResources().getDrawable(R.drawable.pause));
    	} else {
    		m.Pause();
    		btnState.setImageDrawable(getResources().getDrawable(R.drawable.play));
    	}
    }
    
    private Runnable refreshQueue = new Runnable() {
    	public void run() {
    		result = m.getQueue();
    		int drawableID = result.isPaused() ? R.drawable.play : R.drawable.pause;
    		btnState.setImageDrawable(getResources().getDrawable(drawableID));
    		
    		Queue.this.setTitle(title + " - " + result.getStatus());
    		if(result.getSpeed().equals("0 "))
    			txtSpeed.setText("Speed: N/A");
    		else
    			txtSpeed.setText("Speed: " + result.getSpeed() + "/sec");
    		txtRemaining.setText(result.getSizeLeft() + " left");
    		
    		if(lvQueue.getAdapter() == null)
    			lvQueue.setAdapter(result.getAdapter());
    		else
    			((QueueAdapter)lvQueue.getAdapter()).refresh(result.getAdapter().getItems());
    	}
    };
}
