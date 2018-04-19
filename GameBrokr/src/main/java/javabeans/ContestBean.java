package javabeans;

import java.io.Serializable;
import java.util.Date;

public class ContestBean implements Serializable {

	private String id;
	private Date date;
	private String dog;
	private String favorite;
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

	public String getFavorite() {
		return favorite;
	}

	public void setFavorite(String favorite) {
		this.favorite = favorite;
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
