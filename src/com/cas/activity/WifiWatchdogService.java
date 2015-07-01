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

public class WifiWatchdogService extends Service {
	private static final String TAG = "Wifi Watchdog Service";
	private static boolean IS_WIFI_ON = false;
	static boolean IS_SYN = false;
	static BackgroundSync bgSyncer;
	//static BackgroundSync upSyncer = null;
	//static BackgroundSync downSyncer = null;
	static Context context = null;
	static Globals app;
	static int T=0;
	static String old_username;

	public class SynBinder extends Binder {
		/**
		 * get service instance
		 * 
		 * @return
		 */
		public WifiWatchdogService getService() {
			return WifiWatchdogService.this;
		}
		
		Thread t = null;
		
		public void setContext(Context c) {
			context = c;
			app = ((Globals)context.getApplicationContext());
			old_username = app.username;
			Log.i(TAG, "current user:"+app.username);
			refreshWifiStatus();
			T=0;
		}
		
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
		
		/*
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
								downSyncer = new BackgroundSync(context);
								upSyncer = new BackgroundSync(context);
								if (T>120 || T==0 || old_username != app.username){
									if(app.isDownloading==false){
										downSyncer.download();
										old_username=app.username;
										T=0;
									}
								}
								if(app.isUploading==false){
									upSyncer.upload();
								}
							}
							if(!IS_WIFI_ON & IS_SYN){
								IS_SYN = false;
								upSyncer.stop();
								downSyncer.stop();
							}
							// automatically watch for wifi and upload every 15 seconds
							// automatically try download user info every 30 minutes
							T = T+1;
							Thread.sleep(15*1000);
						} catch (InterruptedException e) {
							Log.v("error in" + TAG, e.getMessage());
						}
					}
				}
			});
			t.start();
		}
		*/
	}

	public SynBinder sBinder;

	@Override
	public void onCreate() {
		Log.v(TAG, "Wifi Watchdog onCreate");
		super.onCreate();
		sBinder = new SynBinder();
	}

	@Override
	public IBinder onBind(Intent i) {
		// TODO Auto-generated method stub
		return sBinder;
	}
	
	@Override
	public boolean onUnbind(Intent i) {
		// TODO Auto-generated method stub
		Log.v(TAG, "Wifi Watchdog onUnbind");
		sBinder.t = null;
		IS_SYN = false;
        return super.onUnbind(i);  
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Wifi Watchdog onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "Wifi Watchdog onDestroy");
		super.onDestroy();
	}

}