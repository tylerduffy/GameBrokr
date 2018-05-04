package javabeans;

import java.io.Serializable;

public class MembershipBean implements Serializable {
	
	private String id;
	private String bank;
	private String link;
	private String loss;
	private String username;
	private String win;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1510384941013205078L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLoss() {
		return loss;
	}

	public void setLoss(String loss) {
		this.loss = loss;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getWin() {
		return win;
	}

	public void setWin(String win) {
		this.win = win;
	}
}
