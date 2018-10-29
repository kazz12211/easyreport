package jp.tsubakicraft.easyreport.domain.dto;

public class PostalAddressDTO {

	private String streetName;
	private String additionalSteetName;
	private String buildingNumber;
	private String cityName;
	private String postalZone;
	private String country;
	
	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public void setAdditionalStreetName(String additionalSteetName) {
		this.additionalSteetName = additionalSteetName;
	}

	public String getAdditionalSteetName() {
		return additionalSteetName;
	}

	public void setAdditionalSteetName(String additionalSteetName) {
		this.additionalSteetName = additionalSteetName;
	}

	public String getBuildingNumber() {
		return buildingNumber;
	}

	public String getCityName() {
		return cityName;
	}

	public String getPostalZone() {
		return postalZone;
	}

	public String getCountry() {
		return country;
	}

	public void setBuildingNumber(String buildingNumber) {
		this.buildingNumber = buildingNumber;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public void setPostalZone(String postalZone) {
		this.postalZone = postalZone;
	}

	public void setCountry(String country) {
		this.country = country;
	}



}
