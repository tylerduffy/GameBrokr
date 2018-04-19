

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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

import javabeans.BettorBean;
import javabeans.WagerBean;

/**
 * Servlet implementation class ViewProfileServlet
 */
@WebServlet(name = "ViewProfileServlet",
			urlPatterns = "/profile")
public class ViewProfileServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory bettorKeyFactory;
	UserService userService;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Key bettorKey = bettorKeyFactory.newKey(userService.getCurrentUser().getUserId());
		
		Entity bettor = datastore.get(bettorKey);
		if (bettor == null) {
			response.sendRedirect("/register");
		}
		
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
		
		if (results.hasNext()) {
			results.forEachRemaining((result) -> {
				WagerBean bean = new WagerBean();
				bean.setMatchup(getMatchupString(result));
				bean.setMatchupLink(getMatchupLink(result));
				bean.setSelection(getPick(result));
				if (result.getBoolean("resolved")) {
					bean.setResult(getResultReport(result));
					closedWagers.add(bean);
				} else {
					bean.setAmount(getWager(result));
					openWagers.add(bean);
				}
			});
		}
		
		request.setAttribute("bettor", user);
		request.setAttribute("openWagers", openWagers);
		request.setAttribute("closedWagers", closedWagers);
	    request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
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
				if (pick.equals("favorite")) {
					return contest.getString("favorite") + " (-" + getSpread(contest) + ")";
				} else {
					return contest.getString("dog") + " (+" + getSpread(contest) + ")";
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

}
