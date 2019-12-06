package com.dongnaoedu.live.pusher;


import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.dongnaoedu.live.jni.PushNative;
import com.dongnaoedu.live.params.VideoParams;

import java.io.IOException;

public class VideoPusher extends Pusher implements SurfaceHolder.Callback, Camera.PreviewCallback {

	private Camera mCamera;
	private SurfaceHolder surfaceHolder;
	private VideoParams videoParams;
	private byte[] buffers;
	private boolean isPushing = false;
	private PushNative pushNative;

	public VideoPusher(SurfaceHolder surfaceHolder, VideoParams videoParams, PushNative pushNative){
		this.surfaceHolder = surfaceHolder;
		this.videoParams = videoParams;
		this.pushNative = pushNative;
		surfaceHolder.addCallback(this);
	}


	@Override
	public void startPush() {
		//设置视频的参数
		pushNative.setVideoOptions(videoParams.getWidth(),
				videoParams.getHeight(),videoParams.getBitrate(),videoParams.getFps());
		isPushing  = true;
	}

	@Override
	public void stopPush() {
		isPushing  = false;
	}

	@Override
	public void release() {
		stopPriview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/**
	 * 开始预览
	 */
	private void startPreview() {
		try {
			//SurfaceView初始化完成，开始相机预览
			mCamera = Camera.open(videoParams.getCameraId());
			mCamera.setPreviewDisplay(surfaceHolder);
			//获取预览图像数据
			buffers = new byte[videoParams.getWidth() * videoParams.getHeight() * 4];
			mCamera.addCallbackBuffer(buffers);
			mCamera.setPreviewCallbackWithBuffer(this);

			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止预览
	 */
	private void stopPriview(){
		if(mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	/**
	 * 切换摄像头
	 */
	public void switchCamera(){
		if(videoParams.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT){
			videoParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
		}else {
			videoParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
		}
		stopPriview();
		startPreview();
	}


	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if(mCamera != null){
			mCamera.addCallbackBuffer(buffers);
		}

		if(isPushing) {
			//回调函数中获取图像数据，然后给Native代码编码
			System.out.println("onPreviewFrame...............................");
			pushNative.fireVideo(data);
		}
	}
}
