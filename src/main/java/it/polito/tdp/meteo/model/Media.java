package it.polito.tdp.meteo.model;

public class Media {
	private double media;
	private String citta;
	
	public Media(double media, String citta) {
		super();
		this.media = media;
		this.citta = citta;
	}

	@Override
	public String toString() {
		return "Media [media=" + media + ", citta=" + citta + "]";
	}
	
	
	
}
