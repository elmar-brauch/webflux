package de.bsi.webflux.oauth;

import lombok.Data;

@Data
public class GeographicAddress {

	private String city;
	private String country;
	private String postcode;
	private String stateOrProvince;
	private String streetName;
	private String streetNr;

}
