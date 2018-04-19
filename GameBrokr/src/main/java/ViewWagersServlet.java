

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javabeans.WagerBean;

/**
 * Servlet implementation class ViewWagersServlet
 */
@WebServlet(name = "ViewWagersServlet",
	    urlPatterns = "/wagers")
public class ViewWagersServlet extends HttpServlet {
	
	Datastore datastore;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(PropertyFilter.eq("resolved", false))
				.setOrderBy(OrderBy.desc("date"))
				.build();

		QueryResults<Entity> results = datastore.run(query);

		ArrayList<WagerBean> allWagers = new ArrayList<WagerBean>();
		
		if (results.hasNext()) {
			results.forEachRemaining((result) -> {
				WagerBean bean = new WagerBean();
				bean.setMatchup(getMatchupString(result));
				bean.setMatchupLink(getMatchupLink(result));
				bean.setBettor(getBettor(result));
				bean.setBettorName(getBettorName(result));
				bean.setSelection(getPick(result));
				bean.setAmount(getWager(result));
				allWagers.add(bean);
			});
		}
		
		request.setAttribute("allWagers", allWagers);
	    request.getRequestDispatcher("/WEB-INF/jsp/wagers.jsp").forward(request, response);
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
}
