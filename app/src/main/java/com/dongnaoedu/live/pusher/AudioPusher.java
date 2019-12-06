package com.dongnaoedu.live.pusher;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.dongnaoedu.live.jni.PushNative;
import com.dongnaoedu.live.params.AudioParam;
import com.dongnaoedu.live.params.VideoParams;

public class AudioPusher extends Pusher{

	private AudioParam audioParam;
	private boolean isPushing  = false;
	private int minBufferSize;
	private AudioRecord audioRecord;
	private PushNative pushNative;

	public AudioPusher(AudioParam audioParam, PushNative pushNative) {
		this.audioParam = audioParam;
		this.pushNative = pushNative;
		int channelConfig = audioParam.getChannel() == 1 ?
				AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
		minBufferSize = AudioRecord.getMinBufferSize(audioParam.getSampleRateInHz(),channelConfig, AudioFormat.ENCODING_PCM_16BIT);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				audioParam.getSampleRateInHz(),
				channelConfig,
				AudioFormat.ENCODING_PCM_16BIT,
				minBufferSize);
	}

	@Override
	public void startPush() {
        pushNative.setAudioOptions(audioParam.getSampleRateInHz(),audioParam.getChannel());
		isPushing = true;
		//启动一个录音子线程
		new Thread(new AudioRecordTask()).start();
	}

	@Override
	public void stopPush() {
		isPushing = false;
		audioRecord.stop();
	}

	@Override
	public void release() {
		if(audioRecord != null){
			audioRecord.release();
			audioRecord = null;
		}
	}

	class AudioRecordTask implements Runnable {
		@Override
		public void run() {
			//开始录音
			audioRecord.startRecording();
			while (isPushing){
				//通过AudioRecord不断读取音频数据
				byte[] buffer = new byte[minBufferSize];
				int len = audioRecord.read(buffer,0,buffer.length);
				if(len > 0){
					//传给Native代码，进行音频编码
					System.out.println("音频编码");
					pushNative.fireAudio(buffer,len);
				}
			}
		}
	}
}
