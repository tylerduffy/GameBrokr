

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javabeans.BettorBean;
import javabeans.GroupBean;
import javabeans.WagerBean;

/**
 * Servlet implementation class ViewBettorServlet
 */
@WebServlet(name = "ViewBettorServlet",
		urlPatterns = "/viewbettor")
public class ViewBettorServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory bettorKeyFactory;
	UserService userService;
	Map<String, String> typeMap;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (request.getParameter("bettor_id") != null) {
			String entID = request.getParameter("bettor_id");
			Key bettorKey = bettorKeyFactory.newKey(entID);
			
			Entity bettor = datastore.get(bettorKey);
			
			if (bettor != null) {
				BettorBean user = new BettorBean();
				user.setFirstname(bettor.getString("firstname"));
				user.setLastname(bettor.getString("lastname"));
				user.setUsername(bettor.getString("username"));
				user.setWin(String.valueOf(bettor.getValue("win").get()));
				user.setLoss(String.valueOf(bettor.getValue("loss").get()));
				user.setBank(String.valueOf(bettor.getValue("bank").get()));
				
				Query<Entity> query = Query.newEntityQueryBuilder().setKind("Wager")
						.setFilter(PropertyFilter.eq("bettor", bettorKey))
						.setOrderBy(OrderBy.desc("date"))
						.build();
				
				QueryResults<Entity> results = datastore.run(query);
				
				ArrayList<WagerBean> openWagers = new ArrayList<WagerBean>();
				ArrayList<WagerBean> closedWagers = new ArrayList<WagerBean>();
				
				Map<String, GroupBean> groupWagerMap = new HashMap<String, GroupBean>();
				
				if (results.hasNext()) {
					results.forEachRemaining((result) -> {
						WagerBean bean = new WagerBean();
						bean.setMatchup(getMatchupString(result));
						bean.setMatchupLink(getMatchupLink(result));
						bean.setSelection(getPick(result));
						bean.setType(typeMap.get(result.getString("type")));
						Entity group = datastore.get(result.getKey("group"));
						if (result.getBoolean("resolved")) {
							// closed wager
							bean.setResult(result.getString("result"));
							if (isDefaultGroup(group.getKey().getId())) {
								// proprietary wager
								closedWagers.add(bean);
							} else {
								// group wager
								if (!groupWagerMap.containsKey(group.getString("name"))) {
									groupWagerMap.put(group.getString("name"), new GroupBean());
								}
								groupWagerMap.get(group.getString("name")).addClosedwager(bean);
							}
						} else {
							// open wager
							bean.setAmount(getWager(result));
							if (isDefaultGroup(group.getKey().getId())) {
								// proprietary wager
								openWagers.add(bean);
							} else {
								// group wager
								if (!groupWagerMap.containsKey(group.getString("name"))) {
									groupWagerMap.put(group.getString("name"), new GroupBean());
								}
								groupWagerMap.get(group.getString("name")).addOpenwager(bean);
							}
						}
					});
				}
				
				Query<Entity> membershipQuery = Query.newEntityQueryBuilder().setKind("Membership")
						.setFilter(PropertyFilter.eq("user", bettorKey))
						.build();
				
				QueryResults<Entity> membershipResults = datastore.run(membershipQuery);
				
				ArrayList<GroupBean> groups = new ArrayList<GroupBean>();
				
				if (membershipResults.hasNext()) {
					membershipResults.forEachRemaining((result) -> {
						groups.add(getGroupBean(datastore.get(result.getKey("group")), groupWagerMap));
					});
				}
				
				request.setAttribute("bettor", user);
				request.setAttribute("openWagers", openWagers);
				request.setAttribute("closedWagers", closedWagers);
				request.setAttribute("groups", groups);
			    request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
				
			} else {
				request.setAttribute("errorMsg", "Given User ID did not match an account. Try Again.");
			    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
			}
		} else {
			request.setAttribute("errorMsg", "No User ID given. Try Again.");
		    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
		}		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		bettorKeyFactory = datastore.newKeyFactory().setKind("Bettor");
		userService = UserServiceFactory.getUserService();
		typeMap = new HashMap<String, String>();
		typeMap.put("spread", "Spread");
		typeMap.put("moneyline", "Money Line");
		typeMap.put("overunder", "Over/Under");
	}
	
	private String getMatchupLink(Entity entity) {
		Entity contest = datastore.get(entity.getKey("contest"));
		if (contest != null) {
			return "../viewcontest?contest_id=" + String.valueOf(contest.getKey().getId());
		} else {
			return "../viewcontest?contest_id=x";
		}
	}
	
	private String getMatchupString(Entity entity) {
		Entity contest = datastore.get(entity.getKey("contest"));
		if (contest != null) {
			return contest.getString("favorite") + " v. " + contest.getString("dog");
		}
		return "Matchup Could Not Be Resolved";
	}
	
	private String getPick(Entity entity) {
		String pick = entity.getString("selection");
		if (pick != null) {
			Entity contest = datastore.get(entity.getKey("contest"));
			if (contest != null) {
				if (entity.getString("type").equals("spread")) {
					if (pick.equals("favorite")) {
						return contest.getString("favorite") + " (-" + getSpread(contest) + ")";
					} else {
						return contest.getString("dog") + " (+" + getSpread(contest) + ")";
					}
				} else if (entity.getString("type").equals("moneyline")) {
					if (pick.equals("favorite")) {
						return contest.getString("favorite") + " (-" + contest.getDouble("favoriteline") + ")";
					} else {
						return contest.getString("dog") + " (+" + contest.getDouble("dogline") + ")";
					}
				} else if (entity.getString("type").equals("overunder")) {
					if (pick.equals("over")) {
						return "Over (>" + contest.getDouble("overunder") + ")";
					} else {
						return "Under (<" + contest.getDouble("overunder") + ")";
					}
				} else {
					return "err 3 - Wager type not recognized";
				}
			} else {
				return "err 1 - Pick Could Not Be Resolved";
			}
		}
		return "err 2 - Pick Could Not Be Resolved";
	}
	
	private String getSpread(Entity entity) {
		return String.valueOf(entity.getDouble("spread"));
	}
	
	private String getWager(Entity entity) {
		return "$" + String.valueOf(entity.getValue("amount").get());
	}
	
	private boolean isDefaultGroup(long groupId) {
		return (groupId == 5671831268753408L);
	}
	
	private GroupBean getGroupBean(Entity group, Map<String, GroupBean> map) {
		if (group == null) {
			GroupBean bean = new GroupBean();
			bean.setName("Group Name Not Found");
			bean.setLink("../viewgroup?group_id=0");
			return bean;
		} else {
			GroupBean bean = new GroupBean();
			if (map.get(group.getString("name")) != null) {
				bean = map.get(group.getString("name"));
			}
			bean.setName(group.getString("name"));
			bean.setLink("../viewgroup?group_id=" + group.getKey().getNameOrId());
			return bean;
		}
	}
}
