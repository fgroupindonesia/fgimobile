package com.fgroupindonesia.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateManipulator {

	static DateFormat formatLengkap = null;
	static DateFormat formatTanggal = null;
	static DateFormat formatWaktu = null;
	static DateFormat formatNamaHari = null;
	static String dayName;
	static int hours, minutes, days, months, years;
	public static final int ENGLISH_MODE = 1, BAHASA_MODE = 2;
	
	public static String getDayName(int ops){
		
		if(ops==BAHASA_MODE){
			if(dayName.toLowerCase().equalsIgnoreCase("friday")){
				return "jumat";
			}else if(dayName.toLowerCase().equalsIgnoreCase("saturday")){
				return "sabtu";
			}else if(dayName.toLowerCase().equalsIgnoreCase("sunday")){
				return "ahad";
			}else if(dayName.toLowerCase().equalsIgnoreCase("monday")){
				return "senin";
			}else if(dayName.toLowerCase().equalsIgnoreCase("tuesday")){
				return "selasa";
			}else if(dayName.toLowerCase().equalsIgnoreCase("wednesday")){
				return "rabu";
			}else if(dayName.toLowerCase().equalsIgnoreCase("thursday")){
				return "kamis";
			}
		}
		
		
		return dayName.toLowerCase();
	}
	
	public static int getDay(){
		return days;
	}
	
	public static int getMonth(){
		return months;
	}
	
	public static int getYear(){
		return years;
	}
	
	public static int getMinute(){
		return minutes;
	}
	
	public static int getHour(){
		return hours;
	}
	
	public static int getDayAfter(int howMany){
		return days+howMany;
	}
	
	public static String getRandomDateNumbers(){
		formatLengkap = new SimpleDateFormat("yyyyMMddHHmmss"); // or "hh:mm" for 12 hour format
		Date tgl = new Date();
		return (formatLengkap.format(tgl));
	}
	
	public static String parseIntoDayName(String tanggalPenuh, int mode){
		String namanya = null;
		Date skarang = null;
		
		try {
			formatLengkap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // or "hh:mm" for 12 hour format
			formatNamaHari = new SimpleDateFormat("EEEE", Locale.ENGLISH);
			skarang = formatLengkap.parse(tanggalPenuh);
			
		} catch (ParseException e) {
			namanya="error";
		}
		
		// day in english
		namanya = formatNamaHari.format(skarang);
		dayName=namanya;
		// convert into bahasa
		namanya=getDayName(mode);
		return namanya;
	}
	
	public static String now(){
		String tgl=null;
		
		formatLengkap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // or "hh:mm" for 12 hour format
		Date skarang = new Date();
		
		tgl = formatLengkap.format(skarang);
		
		formatNamaHari = new SimpleDateFormat("EEEE", Locale.ENGLISH);
		// day in bahasa
		dayName = formatNamaHari.format(skarang);
		
		// splitting the details
		String dataTanggal[] = tgl.split(" ");
		String dataTanggalLagi[] = dataTanggal[0].split("-");
		
		years = Integer.parseInt(dataTanggalLagi[0]);
		months = Integer.parseInt(dataTanggalLagi[1]);
		days = Integer.parseInt(dataTanggalLagi[2]);
		
		String dataWaktu[] = dataTanggal[1].split(":");
		
		hours = Integer.parseInt(dataWaktu[0]);
		minutes= Integer.parseInt(dataWaktu[1]);
		
		return tgl;
	}
	
	public static String nextDate(String tanggalPenuh, String namaHariDituju, int modeBahasa){
		
		// lets do iteration until we reach that namaHariDituju
		String tanggalTujuan=null;
		boolean belumKetemu=true;
		int hariKe=1;
		
		while(belumKetemu==true){
			tanggalTujuan = nextDate(tanggalPenuh, hariKe);
			String namaHari = parseIntoDayName(tanggalTujuan,modeBahasa);
			if(namaHari.equalsIgnoreCase(namaHariDituju)){
				belumKetemu=false;
			}
			hariKe++;
		}
		
		return tanggalTujuan;
		
	}
	
	
	
	public static String nextDate(String tanggalPenuh, int afterDays){
		String hariBerikutNya=null;
		
		formatLengkap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // or "hh:mm" for 12 hour format
		
		Calendar cal = Calendar.getInstance();    
		try {
			cal.setTime( formatLengkap.parse(tanggalPenuh));
			cal.add( Calendar.DATE, afterDays );
			
			hariBerikutNya = formatLengkap.format(cal.getTime());
		} catch (ParseException e) {
			hariBerikutNya= "";
		}    

		return hariBerikutNya;
	}
	
	public static String nowDate(){
		String tglSaja=null;
		
		formatTanggal = new SimpleDateFormat("yyyy-MM-dd"); // or "hh:mm" for 12 hour format
		Date skarang = new Date();
		
		tglSaja = formatTanggal.format(skarang);
		
		// splitting the details
		String dataTanggalLagi[] = tglSaja.split("-");
		
		years = Integer.parseInt(dataTanggalLagi[0]);
		months = Integer.parseInt(dataTanggalLagi[1]);
		days = Integer.parseInt(dataTanggalLagi[2]);
		
		
		return tglSaja;
	}
	
	public static String nowTime(){
		String waktu=null;
		
		formatWaktu = new SimpleDateFormat("HH:mm"); // or "hh:mm" for 12 hour format
		Date skarang = new Date();
		
		waktu = formatWaktu.format(skarang);
		
		// splitting the details
		String dataWaktu[] = waktu.split(":");
		hours = Integer.parseInt(dataWaktu[0]);
		minutes= Integer.parseInt(dataWaktu[1]);
		
		return waktu;
	}
	
	public static long getDifferentDays(String fullDateWithTime1, String fullDateWithTime2){
		
		long bedaHari = -1;
		
		// try to remove the time
		// because calculation of days difference can only
		// achieved if the time was removed earlier
		fullDateWithTime1 = fullDateWithTime1.split(" ")[0];
		fullDateWithTime2 = fullDateWithTime2.split(" ")[0];
		
		//formatLengkap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatLengkap = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date coba1 = formatLengkap.parse(fullDateWithTime1);
			Date coba2 = formatLengkap.parse(fullDateWithTime2);

			long milliseconds = Math.abs(coba2.getTime() - coba1.getTime());
			//long terhitungDays = milliseconds / (1000 * 60 * 60 * 24);
			
			bedaHari= TimeUnit.DAYS.convert(milliseconds, TimeUnit.MILLISECONDS);

		} catch (ParseException e) {
			
		}
		
		return bedaHari;
	}
	
	public static String parseIntoDate(String fullDateWithTime){
		String tgl = null;
		
		formatLengkap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatTanggal = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date coba = formatLengkap.parse(fullDateWithTime);
			tgl = formatTanggal.format(coba);
		} catch (ParseException e) {
			tgl = "uknown";
		}
		
		return tgl;
		
	}
	
	public static String parseIntoTime(String fullDateWithTime){
		String waktu = null;
		
		formatLengkap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatWaktu = new SimpleDateFormat("HH:mm");
		try {
			Date coba = formatLengkap.parse(fullDateWithTime);
			waktu = formatWaktu.format(coba);
		} catch (ParseException e) {
			waktu = "uknown";
		}
		
		return waktu;
		
	}
	
}
