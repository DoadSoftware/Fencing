package com.fencing.dao;

import java.util.List;

import com.fencing.model.Player;
import com.fencing.model.Team;

public interface FencingDao {
  Player getPlayer(String whatToProcess, String valueToProcess);
  Team getTeam(String whatToProcess, String valueToProcess);
  List<Player> getPlayers(String whatToProcess, String valueToProcess);
  List<Team> getTeams();
  List<Player> getAllPlayer();
}