package com.fencing.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="Match")
@XmlAccessorType(XmlAccessType.FIELD)
public class Match {

  @XmlElement(name = "matchFileTimeStamp")
  private String matchFileTimeStamp;

  @XmlElement(name = "matchResult")
  private String matchResult;

  @XmlElement(name = "matchStatus")
  private String matchStatus;
  
  @XmlElement(name = "matchFileName")
  private String matchFileName;

  @XmlElement(name = "tournament")
  private String tournament;

  @XmlElement(name = "matchIdent")
  private String matchIdent;

  @XmlElement(name = "matchType")
  private String matchType;

  @XmlElement(name = "numberOfRounds")
  private Integer numberOfRounds;

  @XmlElement(name = "pointsPerRound")
  private Integer pointsPerRound;
  
  @XmlElement(name = "homePlayerId")
  private int homePlayerId;

  @XmlElement(name = "awayPlayerId")
  private int awayPlayerId;

  @XmlElement(name = "homeTeamScore")
  private int homeTeamScore;

  @XmlElement(name = "awayTeamScore")
  private int awayTeamScore;

  @XmlElementWrapper(name = "Bouts")
  @XmlElement(name = "bout")
  private List<Bout> bouts;
 
  @XmlTransient
  private Player homePlayer;

  @XmlTransient
  private Player awayPlayer;
  
  @XmlElement(name = "clock")
  private Clock clock;

public String getMatchFileTimeStamp() {
	return matchFileTimeStamp;
}

public List<Bout> getBouts() {
	return bouts;
}

public void setBouts(List<Bout> bouts) {
	this.bouts = bouts;
}

public void setMatchFileTimeStamp(String matchFileTimeStamp) {
	this.matchFileTimeStamp = matchFileTimeStamp;
}

public Integer getNumberOfRounds() {
	return numberOfRounds;
}

public void setNumberOfRounds(Integer numberOfRounds) {
	this.numberOfRounds = numberOfRounds;
}

public Integer getPointsPerRound() {
	return pointsPerRound;
}

public void setPointsPerRound(Integer pointsPerRound) {
	this.pointsPerRound = pointsPerRound;
}

public String getMatchType() {
	return matchType;
}

public void setMatchType(String matchType) {
	this.matchType = matchType;
}

public String getMatchResult() {
	return matchResult;
}

public void setMatchResult(String matchResult) {
	this.matchResult = matchResult;
}

public String getMatchStatus() {
	return matchStatus;
}

public void setMatchStatus(String matchStatus) {
	this.matchStatus = matchStatus;
}

public String getMatchFileName() {
	return matchFileName;
}

public void setMatchFileName(String matchFileName) {
	this.matchFileName = matchFileName;
}

public String getTournament() {
	return tournament;
}

public void setTournament(String tournament) {
	this.tournament = tournament;
}

public String getMatchIdent() {
	return matchIdent;
}

public void setMatchIdent(String matchIdent) {
	this.matchIdent = matchIdent;
}

public int getHomePlayerId() {
	return homePlayerId;
}

public void setHomePlayerId(int homePlayerId) {
	this.homePlayerId = homePlayerId;
}

public int getAwayPlayerId() {
	return awayPlayerId;
}

public void setAwayPlayerId(int awayPlayerId) {
	this.awayPlayerId = awayPlayerId;
}

public int getHomeTeamScore() {
	return homeTeamScore;
}

public void setHomeTeamScore(int homeTeamScore) {
	this.homeTeamScore = homeTeamScore;
}

public int getAwayTeamScore() {
	return awayTeamScore;
}

public void setAwayTeamScore(int awayTeamScore) {
	this.awayTeamScore = awayTeamScore;
}

public Clock getClock() {
	return clock;
}

public void setClock(Clock clock) {
	this.clock = clock;
}

public Player getHomePlayer() {
	return homePlayer;
}

public void setHomePlayer(Player homePlayer) {
	this.homePlayer = homePlayer;
}

public Player getAwayPlayer() {
	return awayPlayer;
}

public void setAwayPlayer(Player awayPlayer) {
	this.awayPlayer = awayPlayer;
}

@Override
public String toString() {
	return "Match [matchFileTimeStamp=" + matchFileTimeStamp + ", matchResult=" + matchResult + ", matchStatus="
			+ matchStatus + ", matchFileName=" + matchFileName + ", tournament=" + tournament + ", matchIdent="
			+ matchIdent + ", matchType=" + matchType + ", numberOfRounds=" + numberOfRounds + ", pointsPerRound="
			+ pointsPerRound + ", homePlayerId=" + homePlayerId + ", awayPlayerId=" + awayPlayerId + ", homeTeamScore="
			+ homeTeamScore + ", awayTeamScore=" + awayTeamScore + ", bouts=" + bouts + ", homePlayer=" + homePlayer
			+ ", awayPlayer=" + awayPlayer + ", clock=" + clock + "]";
}

}