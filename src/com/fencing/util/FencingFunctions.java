package com.fencing.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.fencing.model.Match;
import com.fencing.service.FencingService;

public class FencingFunctions {
	
	public static String twoDigitString(long number) {
	    if (number == 0) {
	        return "00";
	    }
	    if (number / 10 == 0) {
	        return "0" + number;
	    }
	    return String.valueOf(number);
	}
	
	public static String replace(float number) {
	    return String.valueOf(number).replace(".0", "");
	}
	
	public static Match populateMatchVariables(FencingService fencingService,Match match) 
	{
		match.setHomePlayer(fencingService.getPlayer(FencingUtil.PLAYER, String.valueOf(match.getHomePlayerId())));
		match.setAwayPlayer(fencingService.getPlayer(FencingUtil.PLAYER, String.valueOf(match.getAwayPlayerId())));
		return match;
	}
	
	public static String getOnlineCurrentDate() throws IOException
	{
		HttpURLConnection httpCon = (HttpURLConnection) new URL("https://mail.google.com/").openConnection();
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(httpCon.getDate()));
	}	
	
}
