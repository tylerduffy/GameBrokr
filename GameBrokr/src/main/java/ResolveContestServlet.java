

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * Servlet implementation class ResolveContestServlet
 */
@WebServlet(
		name = "ResolveContestServlet",
		urlPatterns = "/resolvecontest")
public class ResolveContestServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory contestKeyFactory;
	Key contestKey;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("Sorry, nothing here! Please return to the home page.");
		out.println("<br><a href=\"../index.jsp\">Go Home</a>");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long contestid = Long.parseLong(request.getParameter("contestid"));
		int favoriteScore = Integer.parseInt(request.getParameter("favoritescore"));
		int dogScore = Integer.parseInt(request.getParameter("dogscore"));
		double margin = favoriteScore-dogScore;
		
		contestKey = contestKeyFactory.newKey(contestid);
		Entity contest = datastore.get(contestKey);
		double spread = contest.getDouble("spread");
		
		// TODO #1. Find winner/coverer
			// #1.A. Determine winner. If dog outright, pay. If favorite, continue.
			// #1.B. Find margin of victory (MoV), continue.
		Query<Entity> query;
		query = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(PropertyFilter.eq("contest", contestKey))
				.build();
		
		// TODO #2. Find unresolved wagers and settle payment
			// #2.A. If margin of victory (MoV) > spread, pay favorites (2x).
			// #2.B. If MoV < spread, pay dogs (2x).
			// #2.C. If MoV = spread, pay all (1x).
		if (margin > spread) {
			// favorite covered
			QueryResults<Entity> results = datastore.run(query);
			
			results.forEachRemaining((result) -> {
				if (result.getString("selection").equals("favorite")) {
					long amount = (long) result.getValue("amount").get();
					Entity bettor = datastore.get(result.getKey("bettor"));
					long bank = (long) bettor.getValue("bank").get();
					long payout = 2 * amount;
					
					Entity updatedBettor = Entity.newBuilder(bettor).set("bank", bank + payout).build();
					datastore.update(updatedBettor);
				}
				
				Entity updatedWager = Entity.newBuilder(result).set("resolved", true).build();
				datastore.update(updatedWager);
			});
		} else if (spread > margin) {
			// dog covered
			QueryResults<Entity> results = datastore.run(query);
			
			results.forEachRemaining((result) -> {
				if (result.getString("selection").equals("dog")) {
					long amount = (long) result.getValue("amount").get();
					Entity bettor = datastore.get(result.getKey("bettor"));
					long bank = (long) bettor.getValue("bank").get();
					long payout = 2 * amount;
					
					Entity updatedBettor = Entity.newBuilder(bettor).set("bank", bank + payout).build();
					datastore.update(updatedBettor);
				}
				
				Entity updatedWager = Entity.newBuilder(result).set("resolved", true).build();
				datastore.update(updatedWager);
			});
		} else {
			// push (tie)
			QueryResults<Entity> results = datastore.run(query);
				results.forEachRemaining((result) -> {
				long amount = (long) result.getValue("amount").get();
				Entity bettor = datastore.get(result.getKey("bettor"));
				long bank = (long) bettor.getValue("bank").get();
				long payout = amount;
					
				Entity updatedBettor = Entity.newBuilder(bettor).set("bank", bank + payout).build();
				datastore.update(updatedBettor);
					
				Entity updatedWager = Entity.newBuilder(result).set("resolved", true).build();
				datastore.update(updatedWager);
			});
		}
		
		Entity updatedContest = Entity.newBuilder(contest).set("resolved", true).build();
		datastore.update(updatedContest);
		
//		doGet(request, response);		
		response.sendRedirect("/contests");
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		contestKeyFactory = datastore.newKeyFactory().setKind("Contest");
	}

}
