package javabeans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ContestBean implements Serializable {

	private String id;
	private Date date;
	private String dog;
	private String dogline;
	private String dogresult;
	private String favorite;
	private String favoriteline;
	private String favoriteresult;
	private String moneyline;
	private String overunder;
	private boolean resolved;
	private String spread;
	private String victor;
	private long spreadfavoritesum;
	private long spreaddogsum;
	private long moneylinefavoritesum;
	private long moneylinedogsum;
	private long oversum;
	private long undersum;
	private String sport;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4633565053394018314L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}
	
	public String getDatestr() {
		SimpleDateFormat sdf = new SimpleDateFormat("E MM/dd hh:mm a");
		sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
		return sdf.format(date) + " (EST)";
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDog() {
		return dog;
	}

	public void setDog(String dog) {
		this.dog = dog;
	}
	
	public String getDogline() {
		return dogline;
	}

	public void setDogline(String dogline) {
		this.dogline = dogline;
	}

	public String getDogresult() {
		return dogresult;
	}

	public void setDogresult(String dogresult) {
		this.dogresult = dogresult;
	}

	public String getFavorite() {
		return favorite;
	}

	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}
	
	public String getFavoriteline() {
		return favoriteline;
	}

	public void setFavoriteline(String favoriteline) {
		this.favoriteline = favoriteline;
	}

	public String getFavoriteresult() {
		return favoriteresult;
	}

	public void setFavoriteresult(String favoriteresult) {
		this.favoriteresult = favoriteresult;
	}
	
	public String getMoneyline() {
		return moneyline;
	}

	public void setMoneyline(String moneyline) {
		this.moneyline = moneyline;
	}
	
	public String getOverunder() {
		return overunder;
	}

	public void setOverunder(String overunder) {
		this.overunder = overunder;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	public String getSpread() {
		return spread;
	}

	public void setSpread(String spread) {
		this.spread = spread;
	}

	public String getVictor() {
		return victor;
	}

	public void setVictor(String victor) {
		this.victor = victor;
	}

	public long getSpreadfavoritesum() {
		return spreadfavoritesum;
	}

	public void setSpreadfavoritesum(long spreadfavoritesum) {
		this.spreadfavoritesum = spreadfavoritesum;
	}

	public long getSpreaddogsum() {
		return spreaddogsum;
	}

	public void setSpreaddogsum(long spreaddogsum) {
		this.spreaddogsum = spreaddogsum;
	}

	public long getMoneylinefavoritesum() {
		return moneylinefavoritesum;
	}

	public void setMoneylinefavoritesum(long moneylinefavoritesum) {
		this.moneylinefavoritesum = moneylinefavoritesum;
	}

	public long getMoneylinedogsum() {
		return moneylinedogsum;
	}

	public void setMoneylinedogsum(long moneylinedogsum) {
		this.moneylinedogsum = moneylinedogsum;
	}

	public long getOversum() {
		return oversum;
	}

	public void setOversum(long oversum) {
		this.oversum = oversum;
	}

	public long getUndersum() {
		return undersum;
	}

	public void setUndersum(long undersum) {
		this.undersum = undersum;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}
}
