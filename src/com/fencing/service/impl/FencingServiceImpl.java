package com.fencing.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fencing.dao.FencingDao;
import com.fencing.model.Player;
import com.fencing.model.Team;
import com.fencing.service.FencingService;

@Service("fencingService")
@Transactional
public class FencingServiceImpl implements FencingService {

 @Autowired
 private FencingDao fencingDao;
 
@Override
public Player getPlayer(String whatToProcess, String valueToProcess) {
	return fencingDao.getPlayer(whatToProcess, valueToProcess);
}

@Override
public Team getTeam(String whatToProcess, String valueToProcess) {
	return fencingDao.getTeam(whatToProcess, valueToProcess);
}

@Override
public List<Team> getTeams() {
	return fencingDao.getTeams();
}

@Override
public List<Player> getPlayers(String whatToProcess, String valueToProcess) {
	return fencingDao.getPlayers(whatToProcess, valueToProcess);
}

@Override
public List<Player> getAllPlayer() {
	return fencingDao.getAllPlayer();
}

}