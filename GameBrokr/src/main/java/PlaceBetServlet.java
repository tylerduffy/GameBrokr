

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("Oops insufficient funds! Please return to the home page.");
		out.println("<br><a href=\"../index.jsp\">Go Home</a>");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long amount = Long.parseLong(request.getParameter("wageramount"));
		Key bettorKey = getBettorKey(request.getParameter("userid"));
		
		Entity bettor = datastore.get(bettorKey);
		long bank = (long) bettor.getValue("bank").get();
		
		if (bank > amount) {
			
			Entity updatedBettor = Entity.newBuilder(bettor).set("bank", bank-amount).build();
			datastore.update(updatedBettor);
			
			FullEntity<IncompleteKey> wagerEntry = Entity.newBuilder(keyFactory.newKey())
					.set("amount", amount)
					.set("bettor", bettorKey)
					.set("contest", getContestKey(request.getParameter("contestid")))
					.set("selection", request.getParameter("selection"))
					.set("resolved", false)
					.set("date", Timestamp.now())
					.build();
			
			datastore.put(wagerEntry);
			response.sendRedirect("/wagers");
		}
		doGet(request, response);
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		keyFactory = datastore.newKeyFactory().setKind("Wager");
		bettorKeyFactory = datastore.newKeyFactory().setKind("Bettor");
		contestKeyFactory = datastore.newKeyFactory().setKind("Contest");
	}
	
	private Key getBettorKey(String bettorid) {
		long id = Long.parseLong(bettorid);
		return bettorKeyFactory.newKey(id);
	}
	
	private Key getContestKey(String contestid) {
		long id = Long.parseLong(contestid);
		return contestKeyFactory.newKey(id);
	}

}
