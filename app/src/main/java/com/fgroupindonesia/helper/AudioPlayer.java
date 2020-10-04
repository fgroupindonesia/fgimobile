package com.fgroupindonesia.helper;

import android.app.Activity;
import android.media.MediaPlayer;

import com.fgroupindonesia.fgimobile.R;

public class AudioPlayer {

	public static final int DING_AUDIO=1, VOICE_REQ_SENT=2, VOUCHER_ORDER=3,
			VOICE_UPDATED=4, TIME_OUT=5, HORRAY=6;
	
	public static void play(Activity komp, int audioFileType){
		MediaPlayer mPlayer2 = null;
		if(audioFileType == DING_AUDIO){
			mPlayer2 = MediaPlayer.create(komp, R.raw.ding);
		}else if(audioFileType == VOICE_REQ_SENT){
			mPlayer2 = MediaPlayer.create(komp, R.raw.voice_req_sent);
		}else if(audioFileType == VOUCHER_ORDER){
			mPlayer2 = MediaPlayer.create(komp, R.raw.cash);
		} else if(audioFileType == VOICE_UPDATED){
			mPlayer2 = MediaPlayer.create(komp, R.raw.voice_updated);
		} else if(audioFileType == TIME_OUT){
			mPlayer2 = MediaPlayer.create(komp, R.raw.timeout);
		} else if(audioFileType == HORRAY){
			mPlayer2 = MediaPlayer.create(komp, R.raw.voice_horray);
		}
			  
			  mPlayer2.start();
		
	}
	
}
