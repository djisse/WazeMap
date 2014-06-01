package com.example.waysmap;

public class Membre {
	private int id;
	private String pseudo;
	private String type;
	private String mdp;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Membre(int id, String pseudo, String type, String mdp) {
		super();
		this.id = id;
		this.pseudo = pseudo;
		this.type = type;
		this.mdp = mdp;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
}
