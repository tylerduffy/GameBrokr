package javabeans;

import java.io.Serializable;
import java.util.Date;

public class WagerBean implements Serializable {
	
	private String id;
	private String amount;
	private String bettor;
	private String bettorName;
	private String matchup;
	private String matchupLink;
	private Date date;
	private boolean resolved;
	private String result;
	private String selection;
	private String type;
	private String winloss;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2517544178320566514L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBettor() {
		return bettor;
	}

	public void setBettor(String bettor) {
		this.bettor = bettor;
	}
	
	public String getBettorName() {
		return bettorName;
	}

	public void setBettorName(String bettorName) {
		this.bettorName = bettorName;
	}

	public String getMatchup() {
		return matchup;
	}

	public void setMatchup(String matchup) {
		this.matchup = matchup;
	}
	
	public String getMatchupLink() {
		return matchupLink;
	}

	public void setMatchupLink(String matchupLink) {
		this.matchupLink = matchupLink;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWinloss() {
		return winloss;
	}

	public void setWinloss(String winloss) {
		this.winloss = winloss;
	}
}
