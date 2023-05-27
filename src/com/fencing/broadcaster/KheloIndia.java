package com.fencing.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.opencsv.exceptions.CsvException;
import com.fencing.containers.Scene;
import com.fencing.containers.ScoreBug;
import com.fencing.model.*;
import com.fencing.service.FencingService;

public class KheloIndia extends Scene{
	
	public String session_selected_broadcaster = "KHELO_INDIA";
	
	public PrintWriter print_writer;
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false;

	public KheloIndia() {
		super();
	}
	
	public ScoreBug updateScoreBug(List<Scene> scenes, Match match, Socket session_socket) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true,scorebug, session_socket, scenes.get(0).getScene_path(),match, session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(String whatToProcess,Match match,Clock clock, FencingService footballService,Socket session_socket,
			List<Scene> scenes, String valueToProcess) throws InterruptedException, NumberFormatException, MalformedURLException, IOException, CsvException
	{
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG":
		case "POPULATE-L3-NAMESUPER": case "POPULATE-L3-NAMESUPER-PLAYER": case "POPULATE-FF-MATCHID": case "POPULATE-FF-PLAYINGXI": case "POPULATE-L3-BUG-DB":
		case "POPULATE-L3-SCOREUPDATE": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-L3-NAMESUPER-CARD": case "POPULATE-L3-SUBSTITUTE": case "POPULATE-L3-MATCHPROMO":
		case "POPULATE-L3-STAFF": case "POPULATE-FF-MATCHSTATS": case "POPULATE-FF-PROMO": case "POPULATE-DOUBLE_PROMO": case "POPULATE-POINTS_TABLE": 
		case "POPULATE-OFFICIALS": case "POPULATE-PENALTY": case "POPULATE-L3-SINGLE_SUBSTITUTE":
			switch(whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
				break;
			default:
				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(1).scene_load(session_socket,session_selected_broadcaster);
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				populateScoreBug(false,scorebug,session_socket, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			}
			
		case "ANIMATE-IN-SCOREBUG": 
		case "CLEAR-ALL": 
		case "ANIMATE-OUT":
		case "ANIMATE-IN-SPONSOR": case "ANIMATE-OUT-SPONSOR": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-NAMESUPER": 
		case "ANIMATE-IN-NAMESUPERDB": case "ANIMATE-IN-BUG-DB": case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-NAMESUPER_CARD":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-MATCHPROMO": case "ANIMATE-IN-STAFF": case "ANIMATE-IN-MATCHSTATS": case "ANIMATE-IN-PROMO": 
		case "ANIMATE-IN-DOUBLE_PROMO": case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-IN-POINTS_TABLE": case "ANIMATE-IN-OFFICIALS":case "ANIMATE-IN-PENALTY": 
		case "ANIMATE-IN-SINGLE_SUBSTITUTE":
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-PENALTY":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PENALTY";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-NAMESUPERDB":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-MATCHID":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-MATCHSTATS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PLAYINGXI":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-BUG-DB":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "BUG-DB";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				TimeUnit.MILLISECONDS.sleep(500);
				if(match.getHomeTeamScore() > 0 || match.getAwayTeamScore() > 0) {
					if(match.getHomeTeamScore() > 4 || match.getAwayTeamScore() > 4) {
						processAnimation(session_socket, "Scorer3Line_In", "START", session_selected_broadcaster, 2);
					}else if(match.getHomeTeamScore() > 2 || match.getAwayTeamScore() > 2) {
						processAnimation(session_socket, "Scorer2Line_In", "START", session_selected_broadcaster, 2);
					}else {
						processAnimation(session_socket, "Scorer1Line_In", "START", session_selected_broadcaster, 2);
					}
				}
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-MATCHSTATUS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATUS";
				break;
			case "ANIMATE-IN-SUBSTITUTE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SUBSTITUTE";
				break;
			case "ANIMATE-IN-SINGLE_SUBSTITUTE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SINGLE_SUBSTITUTE";
				break;
			case "ANIMATE-IN-MATCHPROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHPROMO";
				break;
			case "ANIMATE-IN-STAFF":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "STAFF";
				break;
			case "ANIMATE-IN-PROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PROMO";
				break;
			case "ANIMATE-IN-DOUBLE_PROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "DOUBLE_PROMO";
				break;
			case "ANIMATE-IN-POINTS_TABLE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "POINTS_TABLE";
				break;
			case "ANIMATE-IN-SCOREBUG":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,1);
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
			case "ANIMATE-IN-SPONSOR":
				processAnimation(session_socket, "SponsorIn", "START", session_selected_broadcaster,1);
				break;
			case "ANIMATE-IN-OFFICIALS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "OFFICIALS";
				break;
			case "CLEAR-ALL":
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE CLEAR;");
				print_writer.println("LAYER2*EVEREST*SINGLE_SCENE CLEAR;");
				which_graphics_onscreen = "";
				break;
			
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					processAnimation(session_socket, "Out", "START", session_selected_broadcaster,1);
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
				}
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
				case "MATCHID": case "PLAYINGXI": case "POINTS_TABLE":
					processAnimation(session_socket, "In", "CONTINUE", session_selected_broadcaster,2);
					which_graphics_onscreen = "";
					break;
				
				case "NAMESUPERDB": case "NAMESUPER":  case "BUG-DB": case "SCOREUPDATE": case "MATCHSTATUS": case "NAMESUPER_CARD":
				case "SUBSTITUTE": case "MATCHPROMO": case "STAFF": case "MATCHSTATS": case "PROMO": case "DOUBLE_PROMO": case "OFFICIALS": 
				case "PENALTY": case "SINGLE_SUBSTITUTE":
					processAnimation(session_socket, "Out", "START", session_selected_broadcaster,2);
					which_graphics_onscreen = "";
					break;
				}
				break;
			}
			break;
			}
		return null;
	}
	
	public void processAnimation(Socket session_socket, String animationName,String animationCommand, String which_broadcaster,int which_layer) throws IOException
	{
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		switch(which_broadcaster.toUpperCase()) {
		case "I_LEAGUE":
			switch(which_layer) {
			case 1:
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			case 2:
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			}
			break;
		}
	}
	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String viz_sence_path,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore01 " + match.getHomeTeamScore() + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore02 " + match.getAwayTeamScore() + ";");
			
			
			//System.out.println("Player : " + match.getHomeSquad().get(0).getFull_name());
			if(is_this_updating == false) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tMatchInfo " + match.getTournament() + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam01 " + match.getHomePlayer().getFull_name() + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam02 " + match.getAwayPlayer().getFull_name() + ";");
				
			}
		}
		return scorebug;
	}
}