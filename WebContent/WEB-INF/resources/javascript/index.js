var match_data;
function secondsTimeSpanToHMS(s) {
  var h = Math.floor(s / 3600); //Get whole hours
  s -= h * 3600;
  var m = Math.floor(s / 60); //Get remaining minutes
  s -= m * 60;
  return h + ":" + (m < 10 ? '0' + m : m) + ":" + (s < 10 ? '0' + s : s); //zero padding on minutes and seconds
} 
function displayMatchTime() {
	processFencingProcedures('READ_CLOCK',null);
}
function processWaitingButtonSpinner(whatToProcess) 
{
	switch (whatToProcess) {
	case 'START_WAIT_TIMER': 
		$('.spinner-border').show();
		$(':button').prop('disabled', true);
		break;	
	case 'END_WAIT_TIMER': 
		$('.spinner-border').hide();
		$(':button').prop('disabled', false);
		break;
	}
}
function afterPageLoad(whichPageHasLoaded)
{
	switch (whichPageHasLoaded) {
	case 'SETUP':
		$('#homePlayerId').select2();
		$('#awayPlayerId').select2();
		break;
	case 'MATCH':
		addItemsToList('LOAD_EVENTS',null);
		break;
	}
}
function initialiseForm(whatToProcess, dataToProcess)
{
	switch (whatToProcess) {
	case 'TIME':
	
		if(match_data) {
			if(document.getElementById('match_time_hdr')) {
				document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
					secondsTimeSpanToHMS(match_data.clock.matchTotalSeconds);
			}
		}
		break;
	
	case 'SETUP':
		
		if(dataToProcess) {
			document.getElementById('matchFileName').value = dataToProcess.matchFileName;
			document.getElementById('tournament').value = dataToProcess.tournament;
			document.getElementById('matchIdent').value = dataToProcess.matchIdent;
			document.getElementById('matchType').value = dataToProcess.matchType;
			document.getElementById('homePlayerId').value = dataToProcess.homePlayerId;
			document.getElementById('awayPlayerId').value = dataToProcess.awayPlayerId;
			document.getElementById('numberOfRounds').value = dataToProcess.numberOfRounds;
			document.getElementById('pointsPerRound').value = dataToProcess.pointsPerRound;
		} else {
			document.getElementById('matchFileName').value = '';
			document.getElementById('tournament').value = '';
			document.getElementById('matchIdent').value = '';
			document.getElementById('matchType').selectedIndex = 0;
			document.getElementById('homePlayerId').selectedIndex = 0;
			document.getElementById('awayPlayerId').selectedIndex = 1;
			document.getElementById('numberOfRounds').value = '3';
			document.getElementById('pointsPerRound').value = '11';
		}
		$('#homePlayerId').prop('selectedIndex', document.getElementById('homePlayerId').options.selectedIndex).change();
		$('#awayPlayerId').prop('selectedIndex', document.getElementById('awayPlayerId').options.selectedIndex).change();
		
		break;
	}
}
function uploadFormDataToSessionObjects(whatToProcess)
{
	var formData = new FormData();
	var url_path;

	$('input, select, textarea').each(
		function(index){  
			if($(this).is("select")) {
				formData.append($(this).attr('id'),$('#' + $(this).attr('id') + ' option:selected').val());  
			} else {
				formData.append($(this).attr('id'),$(this).val());  
			}	
		}
	);
	
	switch(whatToProcess.toUpperCase()) {
	case 'RESET_MATCH':
		url_path = 'reset_and_upload_match_setup_data';
		break;
	case 'SAVE_MATCH':
		url_path = 'upload_match_setup_data';
		break;
	}
	
	$.ajax({    
		headers: {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')},
        url : url_path,     
        data : formData,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',     
        success : function(data) {

        	switch(whatToProcess.toUpperCase()) {
			case 'RESET_MATCH_BEFORE_SETUP_MATCH':
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'RESET_MATCH':
        		alert('Match has been reset');
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'SAVE_MATCH':
        		document.setup_form.method = 'post';
        		document.setup_form.action = 'back_to_match';
        	   	document.setup_form.submit();
        		break;
        	}
        	
        },    
        error : function(e) {    
       	 	console.log('Error occured in uploadFormDataToSessionObjects with error description = ' + e);     
        }    
    });		
	
}
function processUserSelection(whichInput)
{	
	switch ($(whichInput).attr('name')) {
	case 'load_scene_btn':
	
	  	document.initialise_form.submit();
		break;
	
	case 'cancel_graphics_btn':
		$('#select_graphic_options_div').empty();
		document.getElementById('select_graphic_options_div').style.display = 'none';
		$("#select_event_div").show();
		$("#match_configuration").show();
		$("#fencing_div").show();
		break;
	case 'selectedBroadcaster':
		switch ($('#selectedBroadcaster :selected').val()) {
		case 'KHELO_INDIA': 
			$('#vizPortNumber').attr('value','1980');
			$('label[for=vizScene], input#vizScene').hide();
			$('label[for=which_scene], select#which_scene').hide();
			$('label[for=which_layer], select#which_layer').hide();
			break;
		}
		break;
	case 'homePlayers': case 'awayPlayers':
		$('#selected_player_name').html(whichInput.innerHTML);
		$('#selected_player_id').val(whichInput.value);
		document.getElementById('select_event_div').style.display = '';
		break;
	case 'number_of_undo_txt':
		if(whichInput.value < 0 && whichInput.value > match_data.events.length) {
			alert('Number of undos is invalid.\r\n Must be a positive number and less than the number of events available [' + match_data.events.length + ']');
			whichInput.selected = true;
			return false;
		}
		break;
	case 'selectTeam': case 'selectCaptianWicketKeeper':
		addItemsToList('POPULATE-PLAYER',match_data);
		break;
	case 'select_existing_fencing_matches':
		if(whichInput.value.toLowerCase().includes('new_match')) {
			initialiseForm('SETUP',null);
		} else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processFencingProcedures('LOAD_SETUP',$('#select_existing_fencing_matches option:selected'));
		}
		break;
	case 'log_undo_btn':
		if(match_data.events.length > 0) {
			if($('#number_of_undo_txt').val() > match_data.events.length) {
				if(confirm('Number of undo [' + $('#number_of_undo_txt').val() + '] is bigger than number of events [' 
						+ match_data.events.length + ']. We will make both of them similiar') == false) {
					return false;
				}
				$('#number_of_undo_txt').val(match_data.events.length);
			}
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processFencingProcedures('UNDO',$('#number_of_undo_txt'));
		} else {
			alert('No events found');
		}
		break;
	case 'cancel_match_setup_btn':
		document.setup_form.method = 'post';
		document.setup_form.action = 'back_to_match';
	   	document.setup_form.submit();
		break;
	case 'matchFileName':
		if(document.getElementById('matchFileName').value) {
			document.getElementById('matchFileName').value = 
				document.getElementById('matchFileName').value.replace('.xml','') + '.xml';
		}
		break;
	case 'save_match_btn': case 'reset_match_btn':
		switch ($(whichInput).attr('name')) {
		case 'reset_match_btn':
	    	if (confirm('The setup selections of this match will be retained ' +
	    			'but the match data will be deleted permanently. Are you sure, you want to RESET this match?') == false) {
	    		return false;
	    	}
			break;
		}
		if (!checkEmpty(document.getElementById('matchFileName'),'Match Name')) {
			return false;
		} 
		if($('#homePlayerId option:selected').val() == $('#awayPlayerId option:selected').val()) {
			alert('Both players cannot be same. Please choose different home and away team');
			return false;
		}
		switch ($(whichInput).attr('name')) {
		case 'save_match_btn': 
			uploadFormDataToSessionObjects('SAVE_MATCH');
			break;
		case 'reset_match_btn':
			processWaitingButtonSpinner('START_WAIT_TIMER');
			uploadFormDataToSessionObjects('RESET_MATCH');
			break;
		}
		break;
	case 'load_default_team_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		if($('#homeTeamId option:selected').val() == $('#awayTeamId option:selected').val()) {
			alert('Both teams cannot be same. Please choose different home and away team');
    		processWaitingButtonSpinner('END_WAIT_TIMER');
			return false;
		}
		processFencingProcedures('LOAD_TEAMS',whichInput);
		document.getElementById('save_match_div').style.display = '';
		break;
	case 'setup_match_btn':
		document.fencing_form.method = 'post';
		document.fencing_form.action = 'setup';
	   	document.fencing_form.submit();
	   	processWaitingButtonSpinner('START_WAIT_TIMER');
		break;
	case 'load_match_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		processFencingProcedures('LOAD_MATCH',$('#select_fencing_matches option:selected'));
		break;
	case 'Home_goal_btn':
		processFencingProcedures('LOG_EVENT',whichInput);
		break;	
	case 'cancel_undo_btn': case 'cancel_overwrite_btn': case 'cancel_event_btn': case 'cancel_replace_btn': case 'cancel_penalty_btn':
		document.getElementById('select_event_div').style.display = 'none';
		addItemsToList('LOAD_EVENTS',match_data); 
		processWaitingButtonSpinner('END_WAIT_TIMER');
		break;
	case 'select_teams':
		addItemsToList('POPULATE-OFF_PLAYER',match_data);
		addItemsToList('POPULATE-ON_PLAYER',match_data);
		break;
	default:
		switch ($(whichInput).attr('id')) {
		case 'overwrite_teams_total': case 'overwrite_match_time': 
			addItemsToList('LOAD_' + $(whichInput).attr('id').toUpperCase(),null);
			document.getElementById('select_event_div').style.display = '';
			break;
		default:
			if($(whichInput).attr('id').includes('_btn') && $(whichInput).attr('id').split('_').length >= 4) {
	    		switch ($(whichInput).attr('id').split('_')[1]) {
	    		case 'increment':
	    			$('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] + '_txt').val(
						parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] + '_txt').val()) + 1
					);
					break;
	    		case 'decrement':
					if(parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] + '_txt').val()) > 0) {
						$('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] + '_txt').val(
							parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] + '_txt').val()) - 1
						);
					}
					break;
				}				
				processWaitingButtonSpinner('START_WAIT_TIMER');
				processFencingProcedures('LOG_STAT',whichInput);
			}
			break;
		}
		break;
	}
}
function processFencingProcedures(whatToProcess, whichInput)
{
	var value_to_process; 
	var prev_match_data = match_data;
	
	switch(whatToProcess) {
	case 'READ_CLOCK':
		valueToProcess = $('#matchFileTimeStamp').val();
		break;
	case 'LOG_STAT':
		value_to_process = $('#select_bout_number option:selected').val() + ',' + whichInput.id;
		break;
	case 'LOG_OVERWRITE_TEAM_SCORE': 
		switch (whatToProcess) {
		case 'LOG_OVERWRITE_TEAM_SCORE':
			value_to_process = $('#overwrite_home_team_score').val() + ',' + $('#overwrite_away_team_score').val();
			break;
		}
		break;
		
	case 'LOAD_MATCH': case 'LOAD_SETUP':
		value_to_process = whichInput.val();
		break;
		
	case 'UNDO':
		value_to_process = $('#number_of_undo_txt').val();
		break;
	}
	
	$.ajax({    
        type : 'Get',     
        url : 'processFencingProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + value_to_process, 
        dataType : 'json',
        success : function(data) {
			match_data = data;
        	switch(whatToProcess) {
			case 'READ_CLOCK':
				if(match_data.clock) {
					if(document.getElementById('match_time_hdr')) {
						document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
							secondsTimeSpanToHMS(match_data.clock.matchTotalSeconds);
					}
				}
				
				if(data){
					if($('#matchFileTimeStamp').val() != data.matchFileTimeStamp) {
						document.getElementById('matchFileTimeStamp').value = data.matchFileTimeStamp;
						session_match = data;
						addItemsToList('LOAD_MATCH',data);
						addItemsToList('LOAD_EVENTS',data);
						document.getElementById('select_event_div').style.display = 'none';
					}
				}
				break;
    		case 'LOG_OVERWRITE_TEAM_SCORE': case 'LOG_OVERWRITE_MATCH_STATS': case 'LOG_OVERWRITE_MATCH_SUBS': 
    		case 'UNDO': case 'REPLACE': case 'HOME_UNDO': case 'AWAY_UNDO':
        		addItemsToList('LOAD_MATCH',data);
				addItemsToList('LOAD_EVENTS',data);
				document.getElementById('select_event_div').style.display = 'none';
        		break;
        	case 'LOAD_TEAMS':
        		addItemsToList('LOAD_TEAMS',data);
        		break;
        	case 'HOME_GOAL': case 'AWAY_GOAL':
        		addItemsToList('LOAD_MATCH',data);
        		break;	
			case 'LOG_EVENT': case 'LOAD_MATCH':
        		addItemsToList('LOAD_MATCH',data);
	        	switch(whatToProcess) {
	        	case 'LOAD_MATCH':
					document.getElementById('fencing_div').style.display = '';
					document.getElementById('select_event_div').style.display = 'none';
					setInterval(displayMatchTime, 500);
					break;
				}
        		break;
        	case 'LOAD_SETUP':
        		initialiseForm('SETUP',data);
        		break;
        	}
    		processWaitingButtonSpinner('END_WAIT_TIMER');
	    },    
	    error : function(e) {    
	  	 	console.log('Error occured in ' + whatToProcess + ' with error description = ' + e);     
	    }    
	});
}
function addItemsToList(whatToProcess, dataToProcess)
{
	var max_cols,div,linkDiv,anchor,row,cell,header_text,select,option,tr,th,thead,text,table,tbody,playerName,api_value_home,api_value_away;
	var cellCount = 0;
	var addSelect = false;
	
	switch (whatToProcess) {
	case 'LOAD_UNDO':

		$('#select_event_div').empty();
		
		if(dataToProcess.events.length > 0) {

			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
			row = tbody.insertRow(tbody.rows.length);
			
			select = document.createElement('select');
			select.id = 'select_undo';
			dataToProcess.events = dataToProcess.events.reverse();
			var max_loop = dataToProcess.events.length;
			if(max_loop > 5) {
				max_loop = 5;
			}
			for(var i = 0; i < max_loop; i++) {
				option = document.createElement('option');
				option.value = dataToProcess.events[i].eventNumber;
			    option.text = dataToProcess.events[i].eventNumber + '. ' + dataToProcess.events[i].eventType;
			    select.appendChild(option);
			}
			header_text = document.createElement('label');
			header_text.innerHTML = 'Last Five Events: '
			header_text.htmlFor = select.id;
			row.insertCell(0).appendChild(header_text).appendChild(select);

		    option = document.createElement('input');
		    option.type = 'text';
		    option.name = 'number_of_undo_txt';
		    option.value = '1';
		    option.id = option.name;
		    option.setAttribute('onblur','processUserSelection(this)');
			header_text = document.createElement('label');
			header_text.innerHTML = 'Number of undos: '
			header_text.htmlFor = option.id;
			row.insertCell(1).appendChild(header_text).appendChild(option);
			
		    div = document.createElement('div');

		    option = document.createElement('input');
		    option.type = 'button';
		    option.name = 'log_undo_btn';
		    option.id = option.name;
		    option.value = 'Undo Last Event';
		    option.setAttribute('onclick','processUserSelection(this);');
		    
		    div.append(option);

			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_undo_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');

		    div.append(document.createElement('br'));
		    div.append(option);

		    row.insertCell(2).appendChild(div);

			table.appendChild(tbody);
			document.getElementById('select_event_div').appendChild(table);

		} else {
			return false;
		}
		
		break;
	
	case 'LOAD_EVENTS':
		
		$('#select_event_div').empty();

		header_text = document.createElement('label');
		header_text.id = 'selected_player_name';
		header_text.innerHTML = '';
		document.getElementById('select_event_div').appendChild(header_text);
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		
		for(var iRow=0;iRow<=0;iRow++) {
			
			row = tbody.insertRow(tbody.rows.length);
			max_cols = 5;
			
			for(var iCol=0;iCol<=max_cols;iCol++) {
				
				cell = row.insertCell(iCol);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'log_event_btn';
				
				switch (iRow) {
				case 0:
					
					switch (iCol) {
					case 0:
						option.id = 'goal';
						option.value = 'Goal';
						break;
					case 1:
						option.id = 'replace';
						option.value = 'Replace';
						break;
					case 2:
						option.id = 'undo';
						option.value = 'Undo';
						break;
					case 3:
						option.id = 'overwrite';
						option.value = 'Overwrite';
						break;
					case 4:
						option.id = 'penalty';
						option.value = 'Penalty';
						break;
					case 5:
						option.name = 'cancel_event_btn';
						option.id = option.name;
						option.value = 'Cancel';
					}
					
					break;
					
				}
				
				if(option.id) {
					
					switch (option.id) {
					case 'overwrite': case 'goal': case 'card': case 'stats':
						
						option.setAttribute('data-toggle', 'dropdown');
						option.setAttribute('aria-haspopup', 'true');
						option.setAttribute('aria-expanded', 'false');					
						
						div = document.createElement('div');
					    div.append(option);
					    div.className='dropdown';
					    
					    linkDiv = document.createElement('div');
					    linkDiv.id = option.id + '_div';
					    linkDiv.className='dropdown-menu';
					    linkDiv.setAttribute('aria-labelledby',option.id);

						switch (option.id) {
						case 'stats':
					
							for(var ibound=1; ibound<=8; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
								switch(ibound) {
								case 1:
								    anchor.id = 'off_side';
								    anchor.innerText = 'Off Side';
									break;
								case 2:
								    anchor.id = 'assists';
								    anchor.innerText = 'Assists';
									break;
								case 3:
								    anchor.id = 'shots';
								    anchor.innerText = 'Shots';
									break;
								case 4:
								    anchor.id = 'shots_on_target';
								    anchor.innerText = 'Shots On Target';
									break;
								case 5:
								    anchor.id = 'fouls';
								    anchor.innerText = 'Fouls';
									break;
								case 6:
								    anchor.id = 'corners';
								    anchor.innerText = 'Corners';
									break;
								case 7:
								    anchor.id = 'corners_converted';
								    anchor.innerText = 'Corners Converted';
									break;
								}
								switch(ibound){
									case 1: case 2: case 3: case 4: case 5: case 6: case 7:
										 anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processFencingProcedures("LOG_EVENT",this);');
										break;
								}
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'overwrite': 
							
							for(var ibound=1; ibound<=3; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
							    switch(ibound) {
								case 1:
								    anchor.id = 'overwrite_team_score';
								    anchor.innerText = 'Team Score';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_TEAMS_SCORE",this);');
									break;
								case 2:
								    anchor.id = 'overwrite_match_stats';
								    anchor.innerText = 'Match Stats';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_MATCH_STATS",this);');
									break;
								case 3:
								    anchor.id = 'overwrite_match_substitute';
								    anchor.innerText = 'Match Subs';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_MATCH_SUB",this);');
									break;
								}
							    
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'goal':
						
							for(var ibound=1; ibound<=3; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
							    if(ibound == 1) {
								    anchor.id = 'goal';
								    anchor.innerText = 'Goal';
							    } else if(ibound == 2) {
								    anchor.id = 'own_goal';
								    anchor.innerText = 'Own goal';
							    }else{
									anchor.id = 'penalty';
								    anchor.innerText = 'Penalty';
								}
							    anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processFencingProcedures("LOG_EVENT",this);');
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'card':
							
							for(var ibound=1; ibound<=2; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
								
								if(ibound == 1) {
								    anchor.id = 'yellow';
								    anchor.innerText = 'Yellow Card';
							    } else if(ibound == 2) {
								    anchor.id = 'red';
								    anchor.innerText = 'Red Card';
							    }
							    
							    anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processFencingProcedures("LOG_EVENT",this);');
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
						}
					    div.append(linkDiv);				    
						cell.append(div);
						break;
						
					default:
					
						option.onclick = function() {processUserSelection(this)};
						cell.appendChild(option);
						
						break;
					
					}
				}
			}
		}
			
		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);

		break;
				
	case 'LOAD_MATCH':
		
		$('#fencing_div').empty();
		
		if (dataToProcess)
		{
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			tbody = document.createElement('tbody');

			table.appendChild(tbody);
			document.getElementById('fencing_div').appendChild(table);

			row = tbody.insertRow(tbody.rows.length);
			header_text = document.createElement('h6');
			header_text.id = 'match_time_hdr';
			header_text.innerHTML = 'Match Time: 00:00:00';
			row.insertCell(0).appendChild(header_text);
			
			//Teams Score and other details
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			thead = document.createElement('thead');
			tr = document.createElement('tr');
			for (var j = 0; j <= 1; j++) {
			    th = document.createElement('th'); // Column
				th.scope = 'col';
			    switch (j) {
				case 0:
				    th.innerHTML = dataToProcess.homePlayer.full_name + ': ' + dataToProcess.homeTeamScore ;
					break;
				case 1:
					th.innerHTML = dataToProcess.awayPlayer.full_name + ': ' + dataToProcess.awayTeamScore ;
					break;
				}
			    tr.appendChild(th);
			}
			thead.appendChild(tr);
			table.appendChild(thead);
			document.getElementById('fencing_div').appendChild(table);

	        for(var i=1; i<=1; i++) {
		   		div = document.createElement('div');
				div.style = 'text-align:center;';
		   		switch(i) {
				case 1:
					text = 'bout';
					break;
				}
		        for(var j=1; j<=3; j++) {
					switch(j) {
					case 1: case 3:
						select=document.createElement('input');
						select.type = "text";
						select.style = 'width:5%;text-align:center;';
						if(j == 1){
							select.id = 'home_' + text + '_count';
						}
						else{
							select.id = 'away_' + text + '_count';
						}
						if(!select.value) {
							select.value = '0';
						}	
						break;
					case 2:
						select=document.createElement('select');
						select.id = 'select_bout_number';
						select.style = 'width:10%;text-align:center;';
				        for(var k=1; k<=3; k++) {
							option = document.createElement('option');
							option.value = k;
							option.text = 'Bout ' + k;
							select.appendChild(option);
						}
						break;
					}
					div.appendChild(select);
				}
				document.getElementById('fencing_div').appendChild(div);
			}

		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
		tbody = document.createElement('tbody');

        var home_or_away = '', stats_type= '';
        for(var i=0; i<=0; i++) {
            row = tbody.insertRow(tbody.rows.length);
    		switch (i) {
    		case 0:
        		stats_type = 'Points';
        		break;
			}
    		for(var j=0; j<=2; j++) {
        		div = document.createElement('div');
   				div.style = 'text-align:center;';
				switch (j) {
				case 0: case 2:
	    			switch (j) {
	    			case 0: //home
						home_or_away = 'home';
	    				break;
	    			case 2: //away
						home_or_away = 'away';
	    				break;
					}
					for(var k=0; k<=2; k++) {
						switch (k) {
						case 0: case 2:
		        			option = document.createElement('input');
		    				option.type = "button";
		    				if (k == 0) {
		        				option.id = home_or_away + "_" + 'increment' + "_" + stats_type + '_btn';
		        				option.value="+";
		        				option.setAttribute('onclick','processUserSelection(this);');
		        				}
		    				else {
								option.id = home_or_away + "_" + "decrement" + "_" + stats_type + '_btn';
								option.value="-";
								option.setAttribute('onclick','processUserSelection(this);');
								break;
							}
		    				option.style = 'text-align:center;';
							break;
						case 1: 
		        			option = document.createElement('input');
		    				option.type = "text";
		    				option.id = home_or_away + "_" + stats_type + '_txt';
		    				option.style = 'width:25%;text-align:center;';
		    				break;
						}
								
						if(!option.value) {
							option.value = '0';
						}			
						div.appendChild(option);
				    }	
				    break;
				case 1:
					option=document.createElement('label');
    				option.style = 'width:150px;text-align:center;';
					switch(i){
					case 0:
				    	option.innerHTML = 'Points';
				    	break;
					}
					div.appendChild(option);
					break;		
			    }
			    row.insertCell(j).appendChild(div);
	     	}
		  }
		  table.appendChild(tbody);
		  document.getElementById('fencing_div').appendChild(table);
		}
		break;
	}
}
function removeSelectDuplicates(select_id)
{
	var this_list = {};
	$("select[id='" + select_id + "'] > option").each(function () {
	    if(this_list[this.text]) {
	        $(this).remove();
	    } else {
	        this_list[this.text] = this.value;
	    }
	});
}
function checkEmpty(inputBox,textToShow) {

	var name = $(inputBox).attr('id');
	
	document.getElementById(name + '-validation').innerHTML = '';
	document.getElementById(name + '-validation').style.display = 'none';
	$(inputBox).css('border','');
	if(document.getElementById(name).value.trim() == '') {
		$(inputBox).css('border','#E11E26 2px solid');
		document.getElementById(name + '-validation').innerHTML = textToShow + ' required';
		document.getElementById(name + '-validation').style.display = '';
		document.getElementById(name).focus({preventScroll:false});
		return false;
	}
	return true;	
}	
