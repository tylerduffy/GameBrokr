

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
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javabeans.GroupBean;

/**
 * Servlet implementation class PlaceBetServlet
 */
@WebServlet(name = "PlaceBetServlet",
	    urlPatterns = "/placebet")
public class PlaceBetServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory wagerKeyFactory;
	KeyFactory bettorKeyFactory;
	KeyFactory contestKeyFactory;
	KeyFactory groupKeyFactory;
	UserService userService;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String errMsg = "No bet was placed. Unknown error. Please try again.";
		if (request.getAttribute("err") != null) {
			if (request.getAttribute("err").equals("funds")) {
				errMsg = "Insufficient Funds! Please try again.";
			}
			request.setAttribute("errorMsg", errMsg);
		    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
		}
		request.setAttribute("errorMsg", "Reached this page in error. Please try again.");
	    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long amount = Long.parseLong(request.getParameter("wageramount"));
		Key bettorKey = bettorKeyFactory.newKey(userService.getCurrentUser().getUserId());
		
		Entity bettor = datastore.get(bettorKey);
		if (bettor == null) {
			response.sendRedirect("/register");
		}
		
		if (request.getParameter("group").equals("0")) {
			// proprietary wager, continue under default workflow
			long bank = (long) bettor.getValue("bank").get();
			
			if (bank > amount) {
				
				Entity updatedBettor = Entity.newBuilder(bettor).set("bank", bank-amount).build();
				datastore.update(updatedBettor);
				
				FullEntity<IncompleteKey> wagerEntry = Entity.newBuilder(wagerKeyFactory.newKey())
						.set("amount", amount)
						.set("bettor", bettorKey)
						.set("contest", getContestKey(request.getParameter("contestid")))
						.set("group", getGroupKey(request.getParameter("group")))
						.set("date", Timestamp.now())
						.set("resolved", false)
						.set("selection", request.getParameter("selection"))
						.set("type", request.getParameter("type"))
						.build();
				
				datastore.put(wagerEntry);
				response.sendRedirect("/profile");
			}
			
			request.setAttribute("err", "funds");
			doGet(request, response);
			
		} else {
			// group wager, continue under group workflow
			Query<Entity> membershipQuery = Query.newEntityQueryBuilder().setKind("Membership")
					.setFilter(CompositeFilter.and(
							PropertyFilter.eq("user", bettorKey),
							PropertyFilter.eq("group", getGroupKey(request.getParameter("group")))))
					.build();
			
			QueryResults<Entity> membershipResults = datastore.run(membershipQuery);
			
			if (membershipResults.hasNext()) {
				Entity membership = membershipResults.next();
				long bank = (long) membership.getValue("bank").get();
				
				if (bank > amount) {
					Entity updatedMembership = Entity.newBuilder(membership).set("bank", bank-amount).build();
					datastore.update(updatedMembership);
					
					FullEntity<IncompleteKey> wagerEntry = Entity.newBuilder(wagerKeyFactory.newKey())
							.set("amount", amount)
							.set("bettor", bettorKey)
							.set("contest", getContestKey(request.getParameter("contestid")))
							.set("group", getGroupKey(request.getParameter("group")))
							.set("date", Timestamp.now())
							.set("resolved", false)
							.set("selection", request.getParameter("selection"))
							.set("type", request.getParameter("type"))
							.build();
					
					datastore.put(wagerEntry);
					response.sendRedirect("/profile");
				}
				
				request.setAttribute("err", "funds");
				doGet(request, response);
			}
		}
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		wagerKeyFactory = datastore.newKeyFactory().setKind("Wager");
		bettorKeyFactory = datastore.newKeyFactory().setKind("Bettor");
		contestKeyFactory = datastore.newKeyFactory().setKind("Contest");
		groupKeyFactory = datastore.newKeyFactory().setKind("Group");
		userService = UserServiceFactory.getUserService();
	}
	
	private Key getContestKey(String contestid) {
		long id = Long.parseLong(contestid);
		return contestKeyFactory.newKey(id);
	}
	
	private Key getGroupKey(String contestid) {
//		this is the unique ID for the "Personal (Default) group"
		long defaultGroup = 5671831268753408L;
		long id = Long.parseLong(contestid);
		if (id > 1) {
			return groupKeyFactory.newKey(id);
		} else {
			// if default group, id will be 0.
			return groupKeyFactory.newKey(defaultGroup);
		}
		
	}

}
