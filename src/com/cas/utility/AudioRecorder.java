package com.cas.utility;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class AudioRecorder {
	final MediaRecorder recorder = new MediaRecorder();
	final String path;

	public AudioRecorder(String path) {
		createSDCardDir();
		this.path = sanitizePath(path);
	}

	private String sanitizePath(String path) {
		File sdcardDir = Environment.getExternalStorageDirectory();
		String path1 = sdcardDir.getPath() + "/CAS_Audio/";
		String p = path1+path;
		return p;
	}

	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/CAS_Audio";
			String upath = sdcardDir.getPath() + "/CAS_Audio_Uploaded";
			File path1 = new File(path);
			File upath1 = new File(upath);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			if (!upath1.exists()) {
				upath1.mkdirs();
			}
		} else {
			return;
		}
	}

	public void start() throws IOException {
		/*
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted,It is  " + state
					+ ".");
		}
		File directory = new File(path).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created");
		}
		*/
		Log.i("AUDIO_RECORDER",path);
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(path);
		recorder.prepare();
		recorder.start();
	}

	public void stop() throws IOException {
		recorder.stop();
		recorder.release();
	}
}
