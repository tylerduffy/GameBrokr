package javabeans;

import java.io.Serializable;
import java.util.Date;

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
		return date;
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
}
