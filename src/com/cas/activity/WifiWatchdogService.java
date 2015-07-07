/*
 * The class for checking wifi status and sync data
 */
package com.cas.activity;
import com.cas.server.BackgroundSync;
import com.cas.utility.AppStatus;
import com.cas.utility.Globals;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class WifiWatchdogService.
 */
public class WifiWatchdogService extends Service {
	
	/** The Constant TAG. */
	private static final String TAG = "Wifi Watchdog Service";
	
	/** Whether wifi is on. */
	private static boolean IS_WIFI_ON = false;
	
	/** Whether is syncing. */
	static boolean IS_SYN = false;
	
	/** The download/upload synchronization helper. */
	static BackgroundSync bgSyncer;
	
	static Context context = null;
	
	/** The application variable. */
	static Globals app;
	
	/**
	 * The Class SynBinder for binding service to the context.
	 */
	public class SynBinder extends Binder {
		
		/**
		 * get service instance.
		 *
		 * @return the service
		 */
		public WifiWatchdogService getService() {
			return WifiWatchdogService.this;
		}
		
		/** The thread for checking wifi t. */
		Thread t = null;
		
		/**
		 * Sets the context.
		 *
		 * @param c the new context
		 */
		public void setContext(Context c) {
			context = c;
			app = ((Globals)context.getApplicationContext());
			Log.i(TAG, "current user:"+app.username);
			refreshWifiStatus();
		}
		
		/**
		 * Refresh wifi status.
		 */
		public void refreshWifiStatus() {
			t = new Thread(new Runnable() {
				@Override
				public void run() {
					while(true){
						try {
							String conType = AppStatus
									.getInstance(WifiWatchdogService.this)
									.getConnectionType(WifiWatchdogService.this);
							conType = conType == null ? "Unknown" : conType;
							IS_WIFI_ON = conType == "Unknown" ? false : true;
							Log.v("WIFI STATUS", conType);
							if(IS_WIFI_ON){
								bgSyncer = new BackgroundSync(context);
								//downSyncer = new BackgroundSync(context);
								//upSyncer = new BackgroundSync(context);
								bgSyncer.download();
								bgSyncer.upload();
							}
							// automatically watch for wifi and synchronize every 1 minute
							Thread.sleep(1*60*1000);
							//Thread.sleep(10*1000);
						} catch (InterruptedException e) {
							Log.v("error in" + TAG, e.getMessage());
						}
					}
				}
			});
			t.start();
		}
	}

	/** The binder. */
	public SynBinder sBinder;

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.v(TAG, "Wifi Watchdog onCreate");
		super.onCreate();
		sBinder = new SynBinder();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent i) {
		// TODO Auto-generated method stub
		return sBinder;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(Intent i) {
		// TODO Auto-generated method stub
		Log.v(TAG, "Wifi Watchdog onUnbind");
		sBinder.t = null;
		IS_SYN = false;
        return super.onUnbind(i);  
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Wifi Watchdog onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.v(TAG, "Wifi Watchdog onDestroy");
		super.onDestroy();
	}

}