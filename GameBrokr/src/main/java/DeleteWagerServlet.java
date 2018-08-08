

import java.io.IOException;

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
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

/**
 * Servlet implementation class DeleteWagerServlet
 */
@WebServlet(name = "DeleteWagerServlet",
		urlPatterns = "/deletewager")
public class DeleteWagerServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory wagerKeyFactory;
	KeyFactory groupKeyFactory;
	UserService userService;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1) grab wager id from post request
		if (request.getParameter("wager_id") != null) {
			// 2) check if wager exists & verify contest has not started
			long wagerID = Long.parseLong(request.getParameter("wager_id"));
			Key wagerKey = wagerKeyFactory.newKey(wagerID);
			Entity wager = datastore.get(wagerKey);
			
			if (wager != null) {
				Entity contest = datastore.get(wager.getKey("contest"));
				if (contest.getTimestamp("date").toSqlTimestamp().after(Timestamp.now().toSqlTimestamp())) {
					// allowed to delete.. contest is in future
					// 3) if valid wager, find associated contest to adjust wager weights
					String sumCategory = getSumString(wager);
					long wagerAmt = (long) wager.getValue("amount").get();
					long sumAmt = (long) contest.getValue(sumCategory).get();
					Entity updatedContest = Entity.newBuilder(contest)
							.set(sumCategory, sumAmt-wagerAmt)
							.build();
					datastore.update(updatedContest);
					// 4) if valid wager, find associated bettor/group to adjust banks
					long defaultGroup = 5671831268753408L;
					Key defaultGroupKey = groupKeyFactory.newKey(defaultGroup);
					Key wagerGroupKey = wager.getKey("group");
					if (wagerGroupKey.equals(defaultGroupKey)) {
						// proprietary wager
					 	// update bettor here if group id = default id to return original wager amount
						Entity bettor = datastore.get(wager.getKey("bettor"));
						long bank = (long) bettor.getValue("bank").get();
						Entity updatedBettor = Entity.newBuilder(bettor)
								.set("bank", bank + wagerAmt)
								.build();
						datastore.update(updatedBettor);
					} else {
						// group wager
					 	// update group here to return original wager amount
						Query<Entity> query = Query.newEntityQueryBuilder().setKind("Membership")
								.setFilter(CompositeFilter.and(
										(PropertyFilter.eq("user", wager.getKey("bettor"))),
										(PropertyFilter.eq("group", wager.getKey("group")))))
								.build();
						QueryResults<Entity> results = datastore.run(query);
						if (results.hasNext()) {
							Entity membership = results.next();
							long bank = (long) membership.getValue("bank").get();
							Entity updatedMembership = Entity.newBuilder(membership)
									.set("bank", bank + wagerAmt)
									.build();
							datastore.update(updatedMembership);
						} else {
							// no membership found
							request.setAttribute("errorMsg", "Group membership not found. Try Again.");
						    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
						}
					}
					datastore.delete(wagerKey);
				} else {
					// cannot delete.. contest is in past
					request.setAttribute("errorMsg", "Oops! Contests which have already started cannot be deleted.");
				    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
				}
			} else {
				// wager object is null
				request.setAttribute("errorMsg", "Specified wager cannot be found. Try Again.");
			    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
			}
		} else {
			// no wager_id found in post
			request.setAttribute("errorMsg", "No Wager Specified. Try Again.");
		    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
		}
		
		response.sendRedirect("/profile");
//		doGet(request, response);
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		wagerKeyFactory = datastore.newKeyFactory().setKind("Wager");
		groupKeyFactory = datastore.newKeyFactory().setKind("Group");
		userService = UserServiceFactory.getUserService();
	}
	
	private String getSumString(Entity wager) {
		String type = wager.getString("type");
		String selection = wager.getString("selection");
		if (type.equals("overunder")) {
			return selection + "sum";
		} else {
			return type + selection + "sum";
		}
	}

}
