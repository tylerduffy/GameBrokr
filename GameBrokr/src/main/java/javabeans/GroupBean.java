package javabeans;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupBean implements Serializable {
	
	private String id;
	private String name;
	private String count;
	private String link;
	private String owner;
	private ArrayList<WagerBean> openwagers;
	private ArrayList<WagerBean> closedwagers;

	/**
	 * 
	 */
	private static final long serialVersionUID = 9074906467211613831L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public ArrayList<WagerBean> getOpenwagers() {
		return openwagers;
	}

	public void setOpenwagers(ArrayList<WagerBean> openwagers) {
		this.openwagers = openwagers;
	}
	
	public void addOpenwager(WagerBean bean) {
		if (this.openwagers == null) {
			this.openwagers = new ArrayList<WagerBean>();
		}
		this.openwagers.add(bean);
	}

	public ArrayList<WagerBean> getClosedwagers() {
		return closedwagers;
	}

	public void setClosedwagers(ArrayList<WagerBean> closedwagers) {
		this.closedwagers = closedwagers;
	}
	
	public void addClosedwager(WagerBean bean) {
		if (this.closedwagers == null) {
			this.closedwagers = new ArrayList<WagerBean>();
		}
		this.closedwagers.add(bean);
	}
}
