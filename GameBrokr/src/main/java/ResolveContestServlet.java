

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
@WebServlet(name = "ResolveContestServlet",
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
		double favoriteline = contest.getDouble("favoriteline");
		double dogline = contest.getDouble("dogline");
		double overunder = contest.getDouble("overunder");
		
		/**
		 * SPREAD
		 */
		Query<Entity> query;
		query = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(CompositeFilter.and(
						(PropertyFilter.eq("contest", contestKey)),
						(PropertyFilter.eq("type", "spread"))))
				.build();
		
		if (margin > spread) {
			// favorite covered
			QueryResults<Entity> results = datastore.run(query);
			
			results.forEachRemaining((result) -> {
				Entity bettor = datastore.get(result.getKey("bettor"));
				if (result.getString("selection").equals("favorite")) {
					long amount = (long) result.getValue("amount").get();
					long payout = 2 * amount;
					
					bettorWin(bettor, payout);
					resolveWager(result, true, amount);
				} else {
					bettorLose(bettor);
					resolveWager(result, false, (long) result.getValue("amount").get());
				}
			});
		} else if (spread > margin) {
			// dog covered
			QueryResults<Entity> results = datastore.run(query);
			
			results.forEachRemaining((result) -> {
				Entity bettor = datastore.get(result.getKey("bettor"));
				if (result.getString("selection").equals("dog")) {
					long amount = (long) result.getValue("amount").get();
					long payout = 2 * amount;
					
					bettorWin(bettor, payout);
					resolveWager(result, true, amount);
				} else {
					bettorLose(bettor);
					resolveWager(result, false, (long) result.getValue("amount").get());
				}
			});
		} else {
			// push (tie)
			QueryResults<Entity> results = datastore.run(query);
				results.forEachRemaining((result) -> {
				long amount = (long) result.getValue("amount").get();
				Entity bettor = datastore.get(result.getKey("bettor"));
				long payout = amount;

				bettorWin(bettor, payout);
				resolveWager(result, true, 0);
			});
		}
		
		/**
		 * MONEYLINE
		 */
		Query<Entity> moneylineQuery = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(CompositeFilter.and(
						(PropertyFilter.eq("contest", contestKey)),
						(PropertyFilter.eq("type", "moneyline"))))
				.build();
		
		if (favoriteScore > dogScore) {
			// favorite wins
			QueryResults<Entity> results = datastore.run(moneylineQuery);
			results.forEachRemaining((result) -> {
				Entity bettor = datastore.get(result.getKey("bettor"));
				if (result.getString("selection").equals("favorite")) {
					long baseline = 100;
					long amount = (long) result.getValue("amount").get();
					double multiplier = baseline / favoriteline;
					long payout = (long) (multiplier * amount);
					
					bettorWin(bettor, payout + amount);
					resolveWager(result, true, payout);
				} else {
					bettorLose(bettor);
					resolveWager(result, false, (long) result.getValue("amount").get());
				}
			});
		} else if (dogScore > favoriteScore) {
			// dog wins
			QueryResults<Entity> results = datastore.run(moneylineQuery);
			results.forEachRemaining((result) -> {
				Entity bettor = datastore.get(result.getKey("bettor"));
				if (result.getString("selection").equals("dog")) {
					long baseline = 100;
					long amount = (long) result.getValue("amount").get();
					double multiplier = dogline / baseline;
					long payout = (long) (multiplier * amount);
					
					bettorWin(bettor, payout + amount);
					resolveWager(result, true, payout);
				} else {
					bettorLose(bettor);
					resolveWager(result, false, (long) result.getValue("amount").get());
				}
			});
		} else {
			// tie
			QueryResults<Entity> results = datastore.run(moneylineQuery);
			results.forEachRemaining((result) -> {
				Entity bettor = datastore.get(result.getKey("bettor"));
				long amount = (long) result.getValue("amount").get();
				long payout = amount;

				bettorWin(bettor, payout);
				resolveWager(result, true, 0);
			});
		}
		
		/**
		 * OVER/UNDER
		 */
		Query<Entity> overunderQuery = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(CompositeFilter.and(
						(PropertyFilter.eq("contest", contestKey)),
						(PropertyFilter.eq("type", "overunder"))))
				.build();
		
		if (favoriteScore + dogScore > overunder) {
			// over wins
			QueryResults<Entity> results = datastore.run(overunderQuery);
			results.forEachRemaining((result) -> {
				Entity bettor = datastore.get(result.getKey("bettor"));
				if (result.getString("selection").equals("over")) {
					long amount = (long) result.getValue("amount").get();
					long payout = 2 * amount;

					bettorWin(bettor, payout);
					resolveWager(result, true, amount);
				} else {
					bettorLose(bettor);
					resolveWager(result, false, (long) result.getValue("amount").get());
				}
			});
		} else if (favoriteScore + dogScore < overunder) {
			// under wins
			QueryResults<Entity> results = datastore.run(overunderQuery);
			results.forEachRemaining((result) -> {
				Entity bettor = datastore.get(result.getKey("bettor"));
				if (result.getString("selection").equals("under")) {
					long amount = (long) result.getValue("amount").get();
					long payout = 2 * amount;

					bettorWin(bettor, payout);
					resolveWager(result, true, amount);
				} else {
					bettorLose(bettor);
					resolveWager(result, false, (long) result.getValue("amount").get());
				}
			});
		} else {
			// push
			QueryResults<Entity> results = datastore.run(overunderQuery);
			results.forEachRemaining((result) -> {
				Entity bettor = datastore.get(result.getKey("bettor"));
				long amount = (long) result.getValue("amount").get();
				long payout = amount;
				
				bettorWin(bettor, payout);
				resolveWager(result, true, 0);
			});
		}
		
		String victor = "tie";
		if (favoriteScore > dogScore) {
			victor = "favorite";
		} else if (dogScore > favoriteScore) {
			victor = "dog";
		}
		
		Entity updatedContest = Entity.newBuilder(contest)
				.set("resolved", true)
				.set("victor", victor)
				.set("favoriteresult", favoriteScore)
				.set("dogresult", dogScore)
				.build();
		datastore.update(updatedContest);
		
		response.sendRedirect("/contests");
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		contestKeyFactory = datastore.newKeyFactory().setKind("Contest");
	}
	
	private void bettorWin(Entity bettor, long payout) {
		long bank = (long) bettor.getValue("bank").get();
		long wins = (long) bettor.getValue("win").get();
		
		Entity updatedBettor = Entity.newBuilder(bettor)
				.set("bank", bank + payout)
				.set("win", wins + 1)
				.build();
		datastore.update(updatedBettor);
	}
	
	private void bettorLose(Entity bettor) {
		long losses = (long) bettor.getValue("loss").get();
		Entity updatedBettor = Entity.newBuilder(bettor).set("loss", losses + 1).build();
		datastore.update(updatedBettor);
	}
	
	private void resolveWager(Entity wager, boolean win, long payout) {
		String resultStr = "L -$" + payout;
		if (win) {			
			resultStr = "W +$" + payout;
		}
		Entity updatedWager = Entity.newBuilder(wager)
				.set("resolved", true)
				.set("result", resultStr)
				.build();
		datastore.update(updatedWager);
	}

}
