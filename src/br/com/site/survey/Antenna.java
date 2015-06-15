package br.com.site.survey;

import java.awt.Point;

public class Antenna {
	private Point point;
	private double height;
	private AntennaType antennaType;
	private double angle;
	private double freqMhz;
	private double ptxPower;
	
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public AntennaType getAntennaType() {
		return antennaType;
	}
	public void setAntennaType(AntennaType antennaType) {
		this.antennaType = antennaType;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public double getFreqMhz() {
		return freqMhz;
	}
	public void setFreqMhz(double freqMhz) {
		this.freqMhz = freqMhz;
	}
	public double getPtxPower() {
		return ptxPower;
	}
	public void setPtxPower(double ptxPower) {
		this.ptxPower = ptxPower;
	}
	
}
