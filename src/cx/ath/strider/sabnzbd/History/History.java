package cx.ath.strider.sabnzbd.History;

import java.util.Timer;
import java.util.TimerTask;

import cx.ath.strider.sabnzbd.Model;
import cx.ath.strider.sabnzbd.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class History extends Activity {
	Handler handler = new Handler();
	ListView lvHistory;
	HistoryAdapter adapter;
	Model m;
	Timer timer = new Timer();
	TimerTask task;
	int refreshInterval;
	Dialog dialog;
	HistoryItem currentItem;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        lvHistory = (ListView)findViewById(R.id.lvHistory);
        lvHistory.setEmptyView(findViewById(R.id.empty_history));
        lvHistory.setOnItemClickListener(historyItemClicked);
        
        m = new Model(
        	this,
        	getIntent().getStringExtra("hostname"),
        	getIntent().getIntExtra("port", 0),
        	getIntent().getBooleanExtra("ssl", false),
        	getIntent().getStringExtra("apikey")
		);   
        
        task = new TimerTask() {
        	public void run() {
        		handler.post(refreshHistory);
        	}
        };        
        
        adapter = m.getHistory();
        refreshInterval = getIntent().getIntExtra("refresh", 10000);
        timer.schedule(task, 0, refreshInterval);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	timer.cancel();
    }
    
    private void showDialog(HistoryItem item) {
    	dialog = new Dialog(this);
    	
    	dialog.setContentView(R.layout.history_dialog);
    	dialog.setTitle(item.getName());
    	
    	((TextView)dialog.findViewById(R.id.txtHistoryD)).setText(item.getStageLog()[0]);
    	((TextView)dialog.findViewById(R.id.txtHistoryR)).setText(item.getStageLog()[1]);
    	if(item.getStageLog().length > 2)
    		((TextView)dialog.findViewById(R.id.txtHistoryU)).setText(item.getStageLog()[2]);
		((Button)dialog.findViewById(R.id.btnHistoryOk)).setOnClickListener(closeDialog);
		((Button)dialog.findViewById(R.id.btnHistoryDelete)).setOnClickListener(deleteHistoryItem);
		
    	dialog.show();
    }
    
    public void clearHistory(View v) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);    	
		dialog.setMessage("Are you sure you want to clear the entire history?")
    		  .setTitle("Delete History")
    		  .setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					m.clearHistory();
					task.run();
				}    			  
    		  })
    		  .setNegativeButton("No", null)
    		  .setIcon(R.drawable.clear)
    		  .show();    	
    }
    
    private OnClickListener closeDialog = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dialog.cancel();
		}    	
    };
    
    private OnClickListener deleteHistoryItem = new OnClickListener() {
		@Override
		public void onClick(View v) {
			m.deleteHistoryItem(currentItem);
			currentItem = null;
			task.run();
			dialog.cancel();
		}    	
    };
    
    private Runnable refreshHistory = new Runnable() {
    	public void run() {    		
    		if(lvHistory.getAdapter() == null)
    			lvHistory.setAdapter(adapter);
    		else
    			adapter.refresh(m.getHistory().getItems());
    	}
    };
    
    private OnItemClickListener historyItemClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			currentItem = (HistoryItem)arg0.getItemAtPosition(arg2); 
			showDialog(currentItem);
		}    	
    };
   
}
