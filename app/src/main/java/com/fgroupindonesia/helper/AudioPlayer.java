package com.fgroupindonesia.helper;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;

import com.fgroupindonesia.fgimobile.R;

public class AudioPlayer {

	private static MediaPlayer mPlayer2;
	public static final int DING_AUDIO=1, VOICE_REQ_SENT=2, VOUCHER_ORDER=3,
			VOICE_UPDATED=4, TIME_OUT=5, HORRAY=6,
			VOICE_60_MIN_CLASS=7,
			VOICE_30_MIN_CLASS=8,
			VOICE_15_MIN_CLASS=9,
			VOICE_5_MIN_CLASS=10,
			VOICE_PAYMENT_EACH_MONTH= 11;
	
	public static void play(Context komp, int audioFileType){
		if(mPlayer2!=null) {
			mPlayer2.stop();
			mPlayer2 = null;
		}
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
		}else if(audioFileType == VOICE_5_MIN_CLASS){
			mPlayer2 = MediaPlayer.create(komp, R.raw.voice_kelas_dimulai_5menit);
		}else if(audioFileType == VOICE_15_MIN_CLASS){
			mPlayer2 = MediaPlayer.create(komp, R.raw.voice_kelas_dimulai_15menit);
		}else if(audioFileType == VOICE_30_MIN_CLASS){
			mPlayer2 = MediaPlayer.create(komp, R.raw.voice_kelas_dimulai_30menit);
		}else if(audioFileType == VOICE_60_MIN_CLASS){
			mPlayer2 = MediaPlayer.create(komp, R.raw.voice_kelas_dimulai_1jam);
		}else if(audioFileType == VOICE_PAYMENT_EACH_MONTH){
			mPlayer2 = MediaPlayer.create(komp, R.raw.voice_tagihan_awal_bulan);
		}
		mPlayer2.setLooping(false);
		mPlayer2.start();
		
	}
	
}
