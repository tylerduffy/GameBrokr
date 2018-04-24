

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
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
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
	KeyFactory keyFactory;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("spreadWagers", fillBeans("spread"));
		request.setAttribute("moneylineWagers", fillBeans("moneyline"));
		request.setAttribute("overunderWagers", fillBeans("overunder"));
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
		keyFactory = datastore.newKeyFactory().setKind("Contest");
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
	
	private ArrayList<WagerBean> fillBeans(String type) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(CompositeFilter.and(
						(PropertyFilter.eq("resolved", false)),
						(PropertyFilter.eq("type", type))))
				.setOrderBy(OrderBy.desc("date"))
				.build();
		
		QueryResults<Entity> results = datastore.run(query);
		ArrayList<WagerBean> list = new ArrayList<WagerBean>();
		if (results.hasNext()) {
			results.forEachRemaining((result) -> {
				WagerBean bean = new WagerBean();
				bean.setMatchup(getMatchupString(result));
				bean.setMatchupLink(getMatchupLink(result));
				bean.setBettor(getBettor(result));
				bean.setBettorName(getBettorName(result));
				bean.setSelection(getPick(result));
				bean.setAmount(getWager(result));
				list.add(bean);
			});
		}
		
		return list;
	}
}
