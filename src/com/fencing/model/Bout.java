package com.fencing.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Bout")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bout {

  @XmlElement(name = "boutNumber")
  private Integer boutNumber;

  @XmlElement(name = "homePlayerPoints")
  private Integer homePlayerPoints;
  
  @XmlElement(name = "awayPlayerPoints")
  private Integer awayPlayerPoints;

  @XmlElement(name = "boutWinnerPlayerId")
  private Integer boutWinnerPlayerId;

public Bout() {
	super();
}

public Bout(Integer boutNumber, Integer homePlayerPoints, Integer awayPlayerPoints) {
	super();
	this.boutNumber = boutNumber;
	this.homePlayerPoints = homePlayerPoints;
	this.awayPlayerPoints = awayPlayerPoints;
}

public Integer getBoutNumber() {
	return boutNumber;
}

public void setBoutNumber(Integer boutNumber) {
	this.boutNumber = boutNumber;
}

public Integer getHomePlayerPoints() {
	return homePlayerPoints;
}

public void setHomePlayerPoints(Integer homePlayerPoints) {
	this.homePlayerPoints = homePlayerPoints;
}

public Integer getAwayPlayerPoints() {
	return awayPlayerPoints;
}

public void setAwayPlayerPoints(Integer awayPlayerPoints) {
	this.awayPlayerPoints = awayPlayerPoints;
}

public Integer getBoutWinnerPlayerId() {
	return boutWinnerPlayerId;
}

public void setBoutWinnerPlayerId(Integer boutWinnerPlayerId) {
	this.boutWinnerPlayerId = boutWinnerPlayerId;
}

@Override
public String toString() {
	return "Bout [boutNumber=" + boutNumber + ", homePlayerPoints=" + homePlayerPoints + ", awayPlayerPoints="
			+ awayPlayerPoints + ", boutWinnerPlayerId=" + boutWinnerPlayerId + "]";
}

}