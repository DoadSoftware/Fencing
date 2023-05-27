package com.fencing.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Clock")
@XmlAccessorType(XmlAccessType.FIELD)
public class Clock {

  @XmlElement(name = "matchHalves")
  private String matchHalves;

  @XmlElement(name = "matchStartTime")
  private String matchStartTime;
  
  @XmlElement(name = "matchTimeStatus")
  private String matchTimeStatus;

  @XmlElement(name = "matchTotalSeconds")
  private long matchTotalSeconds;

public String getMatchStartTime() {
	return matchStartTime;
}

public void setMatchStartTime(String matchStartTime) {
	this.matchStartTime = matchStartTime;
}

public String getMatchHalves() {
	return matchHalves;
}

public void setMatchHalves(String matchHalves) {
	this.matchHalves = matchHalves;
}

public String getMatchTimeStatus() {
	return matchTimeStatus;
}

public void setMatchTimeStatus(String matchTimeStatus) {
	this.matchTimeStatus = matchTimeStatus;
}

public long getMatchTotalSeconds() {
	return matchTotalSeconds;
}

public void setMatchTotalSeconds(long matchTotalSeconds) {
	this.matchTotalSeconds = matchTotalSeconds;
}

}