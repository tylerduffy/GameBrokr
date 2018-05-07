
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.Timestamp;
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

import javabeans.ContestBean;
import javabeans.GroupBean;
import javabeans.WagerBean;

/**
 * Servlet implementation class ViewSingleContestServlet
 */
@WebServlet(name = "ViewSingleContestServlet",
		urlPatterns = "/viewcontest")
public class ViewSingleContestServlet extends HttpServlet {

	Datastore datastore;
	KeyFactory keyFactory;
	KeyFactory bettorKeyFactory;
	Key contestKey;
	UserService userService;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		if (request.getParameter("contest_id") != null) {
			Key bettorKey = bettorKeyFactory.newKey(userService.getCurrentUser().getUserId());
			
			long entID = Long.parseLong(request.getParameter("contest_id"));
			contestKey = keyFactory.newKey(entID);

			Entity contest = datastore.get(contestKey);
			
			ContestBean contestBean = new ContestBean();
			
			if (contest != null) {
				contestBean.setId(String.valueOf(contestKey.getId()));
				contestBean.setDate(new Date(contest.getTimestamp("date").getSeconds()*1000));
				contestBean.setDog(contest.getString("dog"));
				contestBean.setFavorite(contest.getString("favorite"));
				contestBean.setSpread(String.valueOf(contest.getDouble("spread")));
				contestBean.setResolved(contest.getBoolean("resolved"));
				contestBean.setDogline(String.valueOf(contest.getDouble("dogline")));
				contestBean.setFavoriteline(String.valueOf(contest.getDouble("favoriteline")));
				contestBean.setOverunder(String.valueOf(contest.getDouble("overunder")));
				if (contestBean.isResolved()) {
					contestBean.setFavoriteresult(String.valueOf(contest.getLong("favoriteresult")));
					contestBean.setDogresult(String.valueOf(contest.getLong("dogresult")));
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat("E MM/dd/yy hh:mm a XXX");
				
				Query<Entity> groupQuery = Query.newEntityQueryBuilder().setKind("Membership")
						.setFilter(PropertyFilter.eq("user", bettorKey))
						.build();
				
				QueryResults<Entity> groupResults = datastore.run(groupQuery);
				
				ArrayList<GroupBean> groups = new ArrayList<GroupBean>();
				
				if (groupResults.hasNext()) {
					groupResults.forEachRemaining((result) -> {
						GroupBean bean = new GroupBean();
						Entity group = datastore.get(result.getKey("group"));
						if (group != null) {
							bean.setName(group.getString("name"));
							bean.setId(String.valueOf(group.getKey().getNameOrId()));
						} else {
							bean.setName("Group Name Not Found");
							bean.setId("0");
						}
						groups.add(bean);
					});
				}
				
				request.setAttribute("contest", contestBean);
				request.setAttribute("groups", groups);
				request.setAttribute("spreadWagers", fillBeans("spread"));
				request.setAttribute("moneylineWagers", fillBeans("moneyline"));
				request.setAttribute("overunderWagers", fillBeans("overunder"));
				request.setAttribute("canSpread", canSpread(contestBean));
				request.setAttribute("canMoneyline", canMoneyline(contestBean));
				request.setAttribute("canOverunder", canOverunder(contestBean));
//				request.setAttribute("datestr", sdf.format(new Date()));
				request.setAttribute("isAdmin", userService.isUserAdmin());
				request.setAttribute("open", contestBean.getDate().after(new Date()));
			    request.getRequestDispatcher("/WEB-INF/jsp/contest.jsp").forward(request, response);
				
			} else {
				request.setAttribute("errorMsg", "Given Contest ID did not match any contests. Try Again.");
			    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
			}

		} else {
			request.setAttribute("errorMsg", "No Contest ID given. Try Again.");
		    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		keyFactory = datastore.newKeyFactory().setKind("Contest");
		bettorKeyFactory = datastore.newKeyFactory().setKind("Bettor");
		userService = UserServiceFactory.getUserService();
	}
	
	private String getBettor(Entity entity) {
		Entity bettor = datastore.get(entity.getKey("bettor"));
		if (bettor != null) {
			return "../viewbettor?bettor_id=" + String.valueOf(bettor.getKey().getNameOrId());
		} else {
			return "../viewbettor?bettor_id=x";
		}
	}
	
	private String getBettorName(Entity entity) {
		Entity bettor = datastore.get(entity.getKey("bettor"));
		if (bettor != null) {
			return bettor.getString("username");
		}
		return "Username Could Not Be Resolved";
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
	
	private String getResult(Entity wager) {
		Entity contest = datastore.get(wager.getKey("contest"));
		String victor = contest.getString("victor");
		String selection = wager.getString("selection");
		if (victor.equals("push")) {
			return "push";
		} else if (victor.equals(selection)) {
			return "win";
		}
		return "loss";
	}
	
	private String getResultReport(Entity result) {
		String resultStr = getResult(result);
		String resultReportString;
		if (resultStr.equals("win")) {
			resultReportString = "W +" + getWager(result);
		} else if (resultStr.equals("loss")) {
			resultReportString = "L -" + getWager(result);
		} else {
			resultReportString = "W +$0";
		}
		return resultReportString;
	}
	
	private ArrayList<WagerBean> fillBeans(String type) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(CompositeFilter.and(
						(PropertyFilter.eq("contest", contestKey)),
						(PropertyFilter.eq("type", type))))
				.setOrderBy(OrderBy.desc("date"))
				.build();
		
		QueryResults<Entity> results = datastore.run(query);
		ArrayList<WagerBean> list = new ArrayList<WagerBean>();
		if (results.hasNext()) {
			results.forEachRemaining((result) -> {
				WagerBean bean = new WagerBean();
				bean.setBettor(getBettor(result));
				bean.setBettorName(getBettorName(result));
				bean.setSelection(getPick(result));
				if (result.getBoolean("resolved")) {
					bean.setAmount(getResultReport(result));
					if (bean.getAmount().contains("W")) {
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
	
	private boolean canSpread(ContestBean contest) {
		return (contest.getSpread() != null && (Double.parseDouble(contest.getSpread()) > -1));
	}
	
	private boolean canMoneyline(ContestBean contest) {
		boolean favline = (contest.getFavoriteline() != null && (Double.parseDouble(contest.getFavoriteline()) > -1));
		boolean dogline = (contest.getDogline() != null && (Double.parseDouble(contest.getDogline()) > -1));
		return (favline && dogline);
	}
	
	private boolean canOverunder(ContestBean contest) {
		return (contest.getOverunder() != null && (Double.parseDouble(contest.getOverunder()) > -1));
	}

}
