

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javabeans.GroupBean;
import javabeans.MembershipBean;
import javabeans.WagerBean;

/**
 * Servlet implementation class ViewLeaderboardServlet
 */
@WebServlet(name = "ViewSingleGroupServlet",
		urlPatterns = "/viewgroup")
public class ViewSingleGroupServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory groupKeyFactory;
	KeyFactory bettorKeyFactory;
	Key groupKey;
	UserService userService;
	boolean isMember;
	Map<Key, Entity> contestCache;
	Map<Key, Entity> bettorCache;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		if (request.getParameter("group_id") != null) {
			
			long entID = Long.parseLong(request.getParameter("group_id"));
			groupKey = groupKeyFactory.newKey(entID);

			Entity group = datastore.get(groupKey);
			
			if (group != null) {
				
				GroupBean groupBean = new GroupBean();
				groupBean.setName(group.getString("name"));
				groupBean.setId(String.valueOf(group.getKey().getId()));
				groupBean.setCount(String.valueOf(group.getLong("count")));
				isMember = false;
				Key bettorKey = bettorKeyFactory.newKey(userService.getCurrentUser().getUserId());
				
				Query<Entity> query = Query.newEntityQueryBuilder().setKind("Membership")
						.setFilter(PropertyFilter.eq("group", groupKey))
						.setOrderBy(OrderBy.desc("bank"))
						.build();
				
				QueryResults<Entity> results = datastore.run(query);
				ArrayList<MembershipBean> membershipList = new ArrayList<MembershipBean>();
				
				if (results.hasNext()) {
					results.forEachRemaining((result) -> {
						MembershipBean membershipBean = new MembershipBean();
						membershipBean.setBank(String.valueOf(result.getLong("bank")));
						membershipBean.setLink(getLink(result));
						membershipBean.setLoss(String.valueOf(result.getLong("loss")));
						membershipBean.setUsername(getUsername(result));
						membershipBean.setWin(String.valueOf(result.getLong("win")));
						membershipList.add(membershipBean);
						if (result.getKey("user").equals(bettorKey)) {
							isMember = true;
						}
					});
				}
				
				request.setAttribute("group", groupBean);
				request.setAttribute("isMember", isMember);
				request.setAttribute("memberships", membershipList);
				/*
				
				Query<Entity> openQuery = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(CompositeFilter.and(
						(PropertyFilter.eq("group", groupKey)),
						(PropertyFilter.eq("resolved", false))))
				.setOrderBy(OrderBy.desc("date"))
				.build();
				
				QueryResults<Entity> openResults = datastore.run(openQuery);
				
				Query<Entity> closedQuery = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(CompositeFilter.and(
						(PropertyFilter.eq("group", groupKey)),
						(PropertyFilter.eq("resolved", true))))
				.setOrderBy(OrderBy.desc("date"))
				.build();
				
				QueryResults<Entity> closedResults = datastore.run(closedQuery);
				
				request.setAttribute("openSpreadWagers", fillBeansFromResults(openResults, true, "spread"));
				request.setAttribute("openMoneylineWagers", fillBeansFromResults(openResults, true, "moneyline"));
				request.setAttribute("openOverunderWagers", fillBeansFromResults(openResults, true, "overunder"));
				request.setAttribute("closedSpreadWagers", fillBeansFromResults(closedResults, false, "spread"));
				request.setAttribute("closedMoneylineWagers", fillBeansFromResults(closedResults, false, "moneyline"));
				request.setAttribute("closedOverunderWagers", fillBeansFromResults(closedResults, false, "overunder"));
				 */
				request.setAttribute("openSpreadWagers", fillBeans(true, "spread"));
				request.setAttribute("openMoneylineWagers", fillBeans(true, "moneyline"));
				request.setAttribute("openOverunderWagers", fillBeans(true, "overunder"));
				request.setAttribute("closedSpreadWagers", fillBeans(false, "spread"));
				request.setAttribute("closedMoneylineWagers", fillBeans(false, "moneyline"));
				request.setAttribute("closedOverunderWagers", fillBeans(false, "overunder"));
			    request.getRequestDispatcher("/WEB-INF/jsp/group.jsp").forward(request, response);
				
			} else {
				request.setAttribute("errorMsg", "Given Group ID did not match any groups. Try Again.");
			    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
			}

		} else {
			request.setAttribute("errorMsg", "No Group ID given. Try Again.");
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
		groupKeyFactory = datastore.newKeyFactory().setKind("Group");
		userService = UserServiceFactory.getUserService();
		contestCache = new HashMap<Key, Entity>();
		bettorCache = new HashMap<Key, Entity>();
	}
	
	private ArrayList<WagerBean> fillBeans(boolean open, String type) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(CompositeFilter.and(
						(PropertyFilter.eq("group", groupKey)),
						(PropertyFilter.eq("type", type)),
						(PropertyFilter.eq("resolved", !open))))
				.setOrderBy(OrderBy.desc("date"))
				.build();
		
		QueryResults<Entity> results = datastore.run(query);
		ArrayList<WagerBean> list = new ArrayList<WagerBean>();
		if (results.hasNext()) {
			results.forEachRemaining((result) -> {
				WagerBean bean = createWagerBean(result);
				bean.setMatchup(getMatchupString(result));
				bean.setMatchupLink(getMatchupLink(result));
				bean.setSelection(getPick(result));
				if (!open) {
					bean.setResult(result.getString("result"));
					if (bean.getResult().contains("W")) {
						bean.setWinloss("win");
					} else {
						bean.setWinloss("loss");
					}
				} else {
					bean.setAmount(getWager(result));
				}
				list.add(bean);
			});
		}
		return list;
	}
	
	private ArrayList<WagerBean> fillBeansFromResults(QueryResults<Entity> results, boolean open, String type) {
		ArrayList<WagerBean> list = new ArrayList<WagerBean>();
		if (results.hasNext()) {
			results.forEachRemaining((result) -> {
				if (result.getString("type").equals(type) && !result.getBoolean("resolved") == open) {
					WagerBean bean = createWagerBean(result);
					bean.setMatchup(getMatchupString(result));
					bean.setMatchupLink(getMatchupLink(result));
					bean.setSelection(getPick(result));
					
					if (!open) {
						bean.setResult(result.getString("result"));
						if (bean.getResult().contains("W")) {
							bean.setWinloss("win");
						} else {
							bean.setWinloss("loss");
						}
					} else {
						bean.setAmount(getWager(result));
					}
					list.add(bean);
				}
			});
		}
		return list;
	}
	
	private WagerBean createWagerBean(Entity wager) {
		WagerBean bean = new WagerBean();
		Key bettorKey = wager.getKey("bettor");
		Entity bettor = bettorCache.get(bettorKey);
		if (bettor == null) {
			bettor = datastore.get(bettorKey);
			bettorCache.put(bettorKey, bettor);
		}
//		Entity bettor = datastore.get(wager.getKey("bettor"));
		if (bettor != null) {
			bean.setBettor("../viewbettor?bettor_id=" + String.valueOf(bettor.getKey().getNameOrId()));
			bean.setBettorName(bettor.getString("username"));
		} else {
			bean.setBettor("../viewbettor?bettor_id=x");
			bean.setBettorName("Username Not Found");
		}
		return bean;
	}
	
	private String getPick(Entity entity) {
		String pick = entity.getString("selection");
		if (pick != null) {
			Key contestKey = entity.getKey("contest");
			Entity contest = contestCache.get(contestKey);
			if (contest == null) {
				contest = datastore.get(contestKey);
				contestCache.put(contestKey, contest);
			}
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
	
	private String getLink(Entity membership) {
		Key bettorKey = membership.getKey("user");
		return "../viewbettor?bettor_id=" + bettorKey.getNameOrId();
	}
	
	private String getUsername(Entity membership) {
		Key bettorKey = membership.getKey("user");
		Entity bettor = bettorCache.get(bettorKey);
		if (bettor == null) {
			bettor = datastore.get(bettorKey);
			bettorCache.put(bettorKey, bettor);
		}
		if (bettor != null) {
			return bettor.getString("username");
		}
		return "Username Not Found";
	}
	
	private String getMatchupLink(Entity entity) {
		Key contestKey = entity.getKey("contest");
		Entity contest = contestCache.get(contestKey);
		if (contest == null) {
			contest = datastore.get(contestKey);
			contestCache.put(contestKey, contest);
		}
		if (contest != null) {
			return "../viewcontest?contest_id=" + String.valueOf(contest.getKey().getId());
		} else {
			return "../viewcontest?contest_id=x";
		}
	}
	
	private String getMatchupString(Entity entity) {
		Key contestKey = entity.getKey("contest");
		Entity contest = contestCache.get(contestKey);
		if (contest == null) {
			contest = datastore.get(contestKey);
			contestCache.put(contestKey, contest);
		}
		if (contest != null) {
			return contest.getString("favorite") + " v. " + contest.getString("dog");
		}
		return "Matchup Could Not Be Resolved";
	}

}
