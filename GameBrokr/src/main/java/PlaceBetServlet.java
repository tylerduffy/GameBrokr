

import java.io.IOException;
import java.io.PrintWriter;

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

/**
 * Servlet implementation class PlaceBetServlet
 */
@WebServlet(name = "PlaceBetServlet",
	    urlPatterns = "/placebet")
public class PlaceBetServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory keyFactory;
	KeyFactory bettorKeyFactory;
	KeyFactory contestKeyFactory;
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
		
		long bank = (long) bettor.getValue("bank").get();
		
		if (bank > amount) {
			
			Entity updatedBettor = Entity.newBuilder(bettor).set("bank", bank-amount).build();
			datastore.update(updatedBettor);
			
			FullEntity<IncompleteKey> wagerEntry = Entity.newBuilder(keyFactory.newKey())
					.set("amount", amount)
					.set("bettor", bettorKey)
					.set("contest", getContestKey(request.getParameter("contestid")))
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
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		keyFactory = datastore.newKeyFactory().setKind("Wager");
		bettorKeyFactory = datastore.newKeyFactory().setKind("Bettor");
		contestKeyFactory = datastore.newKeyFactory().setKind("Contest");
		userService = UserServiceFactory.getUserService();
	}
	
	private Key getContestKey(String contestid) {
		long id = Long.parseLong(contestid);
		return contestKeyFactory.newKey(id);
	}

}
