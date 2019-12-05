package com.dongnaoedu.live.pusher;


import android.hardware.Camera.CameraInfo;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import com.dongnaoedu.live.jni.PushNative;
import com.dongnaoedu.live.params.AudioParam;
import com.dongnaoedu.live.params.VideoParams;

public class LivePusher implements Callback{

	private SurfaceHolder surfaceHolder;
	private VideoPusher videoPusher;
	private AudioPusher audioPusher;
	private PushNative pushNative;

	public LivePusher(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;
		prepare();
	}

	/**
	 * 预览准备
	 */
	private void prepare() {
		pushNative = new PushNative();

		//实例化音频推流器
//		VideoParams videoParams = new VideoParams(480,320,CameraInfo.CAMERA_FACING_BACK);
		VideoParams videoParams = new VideoParams(1080,1920,CameraInfo.CAMERA_FACING_BACK);
		videoPusher = new VideoPusher(surfaceHolder,videoParams, pushNative);

		//实例化音频推流器
		AudioParam audioParam = new AudioParam();
		audioPusher = new AudioPusher(audioParam, pushNative);
	}


	/**
	 * 切换摄像头
	 */
	public void switchCamera(){
		videoPusher.switchCamera();
	}


	/**
	 * 开始推流
	 */
	public void startPush(String url){
		videoPusher.startPush();
		audioPusher.startPush();
		pushNative.startPush(url);
	}

	/**
	 * 停止推流
	 */
	public void stopPush(){
		videoPusher.stopPush();
		audioPusher.stopPush();
		pushNative.stopPush();
	}

	/**
	 * 释放资源
	 */
	public void release(){
		videoPusher.release();
		audioPusher.release();
		pushNative.release();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopPush();
		release();
	}
}
