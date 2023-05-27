package com.fencing.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.xml.sax.SAXException;
import com.fencing.broadcaster.KheloIndia;
import com.fencing.containers.Scene;
import com.fencing.containers.ScoreBug;
import com.fencing.model.Bout;
import com.fencing.model.Clock;
import com.fencing.model.Configurations;
import com.fencing.model.Match;
import com.fencing.service.FencingService;
import com.fencing.util.FencingFunctions;
import com.fencing.util.FencingUtil;
import com.opencsv.exceptions.CsvException;
import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	FencingService fencingService;
	
	public static String expiry_date = "2023-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static Clock session_clock = new Clock();
	public static Configurations session_configurations;
	public static Match session_match;
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static KheloIndia session_khelo_india;
	public static List<Scene> session_selected_scenes;
	
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) 
		throws IOException, JAXBException 
	{
		
		if(current_date == null || current_date.isEmpty()) {
			current_date = FencingFunctions.getOnlineCurrentDate();
		}
		model.addAttribute("session_viz_scenes", new File(FencingUtil.FENCING_DIRECTORY + 
				FencingUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".via") && pathname.isFile();
		    }
		}));

		model.addAttribute("match_files", new File(FencingUtil.FENCING_DIRECTORY 
				+ FencingUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".xml") && pathname.isFile();
		    }
		}));
		
		if(new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.CONFIGURATIONS_DIRECTORY + FencingUtil.OUTPUT_XML).exists()) {
			session_configurations = (Configurations)JAXBContext.newInstance(
					Configurations.class).createUnmarshaller().unmarshal(
					new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.CONFIGURATIONS_DIRECTORY 
					+ FencingUtil.OUTPUT_XML));
		} else {
			session_configurations = new Configurations();
			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.CONFIGURATIONS_DIRECTORY + 
					FencingUtil.OUTPUT_XML));
		}
		
		model.addAttribute("session_configurations",session_configurations);
	
		return "initialise";
	}
	
	@RequestMapping(value = {"/setup"}, method = RequestMethod.POST)
	public String setupPage(ModelMap model) throws JAXBException, IllegalAccessException, 
		InvocationTargetException, IOException, ParseException  
	{
		model.addAttribute("match_files", new File(FencingUtil.FENCING_DIRECTORY + 
				FencingUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".xml") && pathname.isFile();
		    }
		}));
		model.addAttribute("session_match", session_match);
		model.addAttribute("players", fencingService.getAllPlayer());
		model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));

		return "setup";
	}

	@RequestMapping(value = {"/match"}, method = {RequestMethod.POST,RequestMethod.GET})
	public String footballMatchPage(ModelMap model,
		@RequestParam(value = "selectedBroadcaster", required = false, defaultValue = "") String selectedBroadcaster,
		@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss,
		@RequestParam(value = "vizPortNumber", required = false, defaultValue = "") String vizPortNumber,
		@RequestParam(value = "vizScene", required = false, defaultValue = "") String vizScene)
			throws IOException, ParseException, JAXBException, InterruptedException  
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {

			session_selected_broadcaster = selectedBroadcaster;
			session_selected_scenes = new ArrayList<Scene>();
			if(!vizIPAddresss.trim().isEmpty() && !vizPortNumber.trim().isEmpty()) {
				//System.out.println("Broad : " + session_selected_broadcaster + " Port : " + vizPortNumber);
				session_socket = new Socket(vizIPAddresss, Integer.valueOf(vizPortNumber));
				switch (session_selected_broadcaster.toUpperCase()) {
				case FencingUtil.KHELO_INDIA:
					//session_selected_scenes.add(new Scene(FencingUtil.KHELO_INDIA_SCORE_BUG_SCENE_PATH,FencingUtil.ONE)); // Front layer
					session_selected_scenes.add(new Scene("",FencingUtil.TWO));
					session_selected_scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
					session_khelo_india = new KheloIndia();
					session_khelo_india.scorebug = new ScoreBug();
					break;
				}
			}
			
			model.addAttribute("match_files", new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".xml") && pathname.isFile();
			    }
			}));

			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			session_match = new Match();
			if(session_match.getClock() == null) 
				session_match.setClock(new Clock());
			
			session_configurations.setBroadcaster(selectedBroadcaster);
			session_configurations.setVizscene(vizScene);
			session_configurations.setIpAddress(vizIPAddresss);
			
			if(!vizPortNumber.trim().isEmpty()) {
				session_configurations.setPortNumber(Integer.valueOf(vizPortNumber));
			}

			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.CONFIGURATIONS_DIRECTORY + 
					FencingUtil.OUTPUT_XML));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);
			model.addAttribute("session_configurations", session_configurations);
			model.addAttribute("session_socket", session_socket);
			model.addAttribute("session_khelo_india", session_khelo_india);
			model.addAttribute("session_selected_scenes", session_selected_scenes);
			
			return "match";
		}
	}
	
	@RequestMapping(value = {"/back_to_match"}, method = RequestMethod.POST)
	public String backToMatchPage(ModelMap model) throws ParseException
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {
		
			model.addAttribute("match_files", new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".xml") && pathname.isFile();
			    }
			}));
			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);

			return "match";
		
		}
	}	
	
	@RequestMapping(value = {"/upload_match_setup_data","/reset_and_upload_match_setup_data"}
		,method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String uploadFormDataToSessionObjects(MultipartHttpServletRequest request) 
			throws IllegalAccessException, InvocationTargetException, JAXBException, IOException
	{
		if (request.getRequestURI().contains("upload_match_setup_data") 
				|| request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
			
	   		boolean reset_all_variables = false;
			if(request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
				reset_all_variables = true;
			} else if(request.getRequestURI().contains("upload_match_setup_data")) {
				for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					if(entry.getKey().equalsIgnoreCase("select_existing_football_matches") && entry.getValue()[0].equalsIgnoreCase("new_match")) {
						reset_all_variables = true;
						break;
					}
				}
			}
			if(reset_all_variables == true) {
				session_match = new Match(); 
			}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
   				BeanUtils.setProperty(session_match, entry.getKey(), entry.getValue()[0]);
	   		}
			
			new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.EVENT_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			
			session_match = FencingFunctions.populateMatchVariables(fencingService, session_match);

			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));

		}
		return JSONObject.fromObject(session_match).toString();
	}
	
	@RequestMapping(value = {"/processFencingProcedures"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processFootballProcedures(
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess)
					throws JAXBException, IllegalAccessException, InvocationTargetException, IOException, NumberFormatException, InterruptedException, 
						CsvException, SAXException, ParserConfigurationException
	{	
		if(!whatToProcess.equalsIgnoreCase(FencingUtil.LOAD_TEAMS)) {
			if(valueToProcess.contains(",")) {
				if(session_match.getMatchFileName() == null || session_match.getMatchFileName().isEmpty()) {
					session_match = (Match) JAXBContext.newInstance(Match.class).createUnmarshaller().unmarshal(
							new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.MATCHES_DIRECTORY + valueToProcess.split(",")[0]));
					
					session_match = FencingFunctions.populateMatchVariables(fencingService,session_match);
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		case "LOG_STAT":
			System.out.println("valueToProcess = " + valueToProcess);
			boolean bout_found = false;
			if(session_match.getBouts() != null) {
				for (Bout bout : session_match.getBouts()) {
					if(bout.getBoutNumber() == Integer.valueOf(valueToProcess.split(",")[0])) {
						bout_found = true;
						if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.HOME)) {
							if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.INCREMENT)) {
								bout.setHomePlayerPoints(bout.getHomePlayerPoints() + 1);
							}else if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.DECREMENT)) {
								bout.setHomePlayerPoints(bout.getHomePlayerPoints() - 1);
							}
						}else if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.AWAY)) {
							if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.INCREMENT)) {
								bout.setAwayPlayerPoints(bout.getAwayPlayerPoints() + 1);
							}else if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.DECREMENT)) {
								bout.setAwayPlayerPoints(bout.getAwayPlayerPoints() - 1);
							}
						}
					}
				}
			}
			if(bout_found == false) {
				List<Bout> this_bout = new ArrayList<Bout>();
				if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.HOME)) {
					if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.INCREMENT)) {
						this_bout.add(new Bout(session_match.getBouts().size() + 1,1,0));
					}else if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.DECREMENT)) {
						this_bout.add(new Bout(session_match.getBouts().size() + 1,0,0));
					}
				}else if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.AWAY)) {
					if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.INCREMENT)) {
						this_bout.add(new Bout(session_match.getBouts().size() + 1,0,1));
					}else if(valueToProcess.split(",")[1].toUpperCase().contains(FencingUtil.DECREMENT)) {
						this_bout.add(new Bout(session_match.getBouts().size() + 1,0,0));
					}
				}
				session_match.setBouts(this_bout);
			}

			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
			
			return JSONObject.fromObject(session_match).toString();			

		case FencingUtil.LOAD_MATCH: case FencingUtil.LOAD_SETUP:

			session_match = (Match) JAXBContext.newInstance(Match.class).createUnmarshaller().unmarshal(
					new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.MATCHES_DIRECTORY + valueToProcess));
			
			switch (whatToProcess.toUpperCase()) {
			case FencingUtil.LOAD_MATCH:
				
				if(new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.CLOCK_XML).exists()) {
					session_match.setClock((Clock) JAXBContext.newInstance(Clock.class).createUnmarshaller().unmarshal(
							new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.CLOCK_XML)));
				} else {
					session_match.setClock(new Clock());
				}
				break;
				
			}
			
			session_match = FencingFunctions.populateMatchVariables(fencingService,session_match);

			return JSONObject.fromObject(session_match).toString();			

		case "READ_CLOCK":

			if(new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.CLOCK_XML).exists()) {
				session_clock = (Clock) JAXBContext.newInstance(Clock.class).createUnmarshaller().unmarshal(
						new File(FencingUtil.FENCING_DIRECTORY + FencingUtil.CLOCK_XML));
				session_match.setClock(session_clock);
				
				switch (session_selected_broadcaster) {
				case FencingUtil.KHELO_INDIA:
					session_khelo_india.updateScoreBug(session_selected_scenes,session_match, session_socket);
					break;
				}
			}
			return JSONObject.fromObject(session_match).toString();
						
		default:
			return JSONObject.fromObject(session_match).toString();
		}
	}
}