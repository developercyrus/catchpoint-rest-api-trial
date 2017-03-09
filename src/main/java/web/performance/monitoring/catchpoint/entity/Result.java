package web.performance.monitoring.catchpoint.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Result {
	private String country;
	private String city;
	private double availability;
	private double responseTime;
	
	/**
	 * @param country		summary/items/breakdown_1/name
	 * @param city			summary/items/breakdown_2/name
	 * @param responseTime	summary/items/synthetic_metrics[0]
	 * @param availability	summary/items/synthetic_metrics[1]
	 */
	public Result(String country, String city, double responseTime, double availability) {
		this.country = country;
		this.city = city;
		this.availability = availability;
		this.responseTime = responseTime;
	}
	
	/**
	 * @return
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return
	 */
	public double getAvailability() {
		return availability;
	}
	/**
	 * @param availability
	 */
	public void setAvailability(double availability) {
		this.availability = availability;
	}
	/**
	 * @return
	 */
	public double getResponseTime() {
		return responseTime;
	}
	/**
	 * @param responseTime
	 */
	public void setResponseTime(double responseTime) {
		this.responseTime = responseTime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
