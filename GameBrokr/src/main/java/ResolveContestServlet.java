

import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
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
	Map<Key, String> keyMap;
	Map<String, Long> winMap;
	Map<String, Long> loseMap;
	Map<String, Long> payoutMap;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("errorMsg", "Nothing here! Please return to the home page.");
	    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
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
					
					bettorWin(bettor, result, payout);
					resolveWager(result, true, amount);
				} else {
					bettorLose(bettor, result);
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
					
					bettorWin(bettor, result, payout);
					resolveWager(result, true, amount);
				} else {
					bettorLose(bettor, result);
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

				bettorWin(bettor, result, payout);
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
					long payout = (long) (Math.ceil(multiplier * amount));
					
					bettorWin(bettor, result, payout + amount);
					resolveWager(result, true, payout);
				} else {
					bettorLose(bettor, result);
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
					long payout = (long) (Math.ceil(multiplier * amount));
					
					bettorWin(bettor, result, payout + amount);
					resolveWager(result, true, payout);
				} else {
					bettorLose(bettor, result);
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

				bettorWin(bettor, result, payout);
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

					bettorWin(bettor, result, payout);
					resolveWager(result, true, amount);
				} else {
					bettorLose(bettor, result);
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

					bettorWin(bettor, result, payout);
					resolveWager(result, true, amount);
				} else {
					bettorLose(bettor, result);
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
				
				bettorWin(bettor, result, payout);
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
		
		/**/
		for (Key key : keyMap.keySet()) {
			Entity updatedEntity = Entity.newBuilder(datastore.get(key))
			.set("bank", payoutMap.get(keyMap.get(key)))
			.set("win", winMap.get(keyMap.get(key)))
			.set("loss", loseMap.get(keyMap.get(key)))
			.build();
			datastore.update(updatedEntity);
		}
		
		response.sendRedirect("/contests?sport=" + contest.getString("sport"));
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		contestKeyFactory = datastore.newKeyFactory().setKind("Contest");
		keyMap = new HashMap<Key, String>();
		winMap = new HashMap<String, Long>();
		loseMap = new HashMap<String, Long>();
		payoutMap = new HashMap<String, Long>();
	}
	
	private void bettorWin(Entity bettor, Entity wager, long payout) {
		if (isDefaultGroup(wager.getKey("group").getId())) {
			// proprietary wager, continue with default workflow
//			long bank = (long) bettor.getValue("bank").get();
//			long wins = (long) bettor.getValue("win").get();
			
			Key bettorKey = bettor.getKey();
			String keyStr = String.valueOf(bettorKey.getNameOrId());
			if (!winMap.containsKey(keyStr)) {
				keyMap.put(bettorKey, keyStr);
				winMap.put(keyStr, (long) bettor.getValue("win").get());
				loseMap.put(keyStr, (long) bettor.getValue("loss").get());
				payoutMap.put(keyStr, (long) bettor.getValue("bank").get());
			}
			
			long newWins = winMap.get(keyStr) + 1;
			long newBank = payoutMap.get(keyStr) + payout;
			winMap.put(keyStr, newWins);
			payoutMap.put(keyStr, newBank);
						
			/* Entity updatedBettor = Entity.newBuilder(bettor)
					.set("bank", bank + payout)
					.set("win", wins + 1)
					.build();
			datastore.update(updatedBettor);
			*/
		} else {
			// group wager, continue with group workflow
			Query<Entity> membershipQuery = Query.newEntityQueryBuilder().setKind("Membership")
					.setFilter(CompositeFilter.and(
							PropertyFilter.eq("user", bettor.getKey()),
							PropertyFilter.eq("group", wager.getKey("group"))))
					.build();
			
			QueryResults<Entity> membershipResults = datastore.run(membershipQuery);
			if (membershipResults.hasNext()) {
				Entity membership = membershipResults.next();
//				long bank = (long) membership.getValue("bank").get();
//				long wins = (long) membership.getValue("win").get();
				
				Key memKey = membership.getKey();
				String keyStr = String.valueOf(memKey.getNameOrId());
				if (!winMap.containsKey(keyStr)) {
					keyMap.put(memKey, keyStr);
					winMap.put(keyStr, (long) membership.getValue("win").get());
					loseMap.put(keyStr, (long) membership.getValue("loss").get());
					payoutMap.put(keyStr, (long) membership.getValue("bank").get());
				}
				
				long newWins = winMap.get(keyStr) + 1;
				long newBank = payoutMap.get(keyStr) + payout;
				winMap.put(keyStr, newWins);
				payoutMap.put(keyStr, newBank);
				
				/*
				Entity updatedMembership = Entity.newBuilder(membership)
						.set("bank", bank + payout)
						.set("win", wins + 1)
						.build();
				datastore.update(updatedMembership);
				*/
			}
			// TODO: some form of error handling in case group/user pair cannot be found
		}
		
		
	}
	
	private void bettorLose(Entity bettor, Entity wager) {
		if (isDefaultGroup(wager.getKey("group").getId())) {
			// proprietary wager, continue with default workflow
//			long losses = (long) bettor.getValue("loss").get();
			
			Key bettorKey = bettor.getKey();
			String keyStr = String.valueOf(bettorKey.getNameOrId());
			if (!loseMap.containsKey(keyStr)) {
				keyMap.put(bettorKey, keyStr);
				winMap.put(keyStr, (long) bettor.getValue("win").get());
				loseMap.put(keyStr, (long) bettor.getValue("loss").get());
				payoutMap.put(keyStr, (long) bettor.getValue("bank").get());
			}
			
			long newLosses = loseMap.get(keyStr) + 1;
			loseMap.put(keyStr, newLosses);
			
			/*
			Entity updatedBettor = Entity.newBuilder(bettor).set("loss", losses + 1).build();
			datastore.update(updatedBettor);
			*/
		} else {
			// group wager, continue with group workflow
			Query<Entity> membershipQuery = Query.newEntityQueryBuilder().setKind("Membership")
					.setFilter(CompositeFilter.and(
							PropertyFilter.eq("user", bettor.getKey()),
							PropertyFilter.eq("group", wager.getKey("group"))))
					.build();
			
			QueryResults<Entity> membershipResults = datastore.run(membershipQuery);
			if (membershipResults.hasNext()) {
				Entity membership = membershipResults.next();
//				long losses = (long) membership.getValue("loss").get();
				
				Key memKey = membership.getKey();
				String keyStr = String.valueOf(memKey.getNameOrId());
				if (!loseMap.containsKey(keyStr)) {
					keyMap.put(memKey, keyStr);
					winMap.put(keyStr, (long) membership.getValue("win").get());
					loseMap.put(keyStr, (long) membership.getValue("loss").get());
					payoutMap.put(keyStr, (long) membership.getValue("bank").get());
				}
				
				long newLosses = loseMap.get(keyStr) + 1;
				loseMap.put(keyStr, newLosses);
				
				/*
				Entity updatedMembership = Entity.newBuilder(membership).set("loss", losses + 1).build();
				datastore.update(updatedMembership);
				*/
			}
			// TODO: some form of error handling in case group/user pair cannot be found
		}
		
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
	
	private boolean isDefaultGroup(long groupId) {
		return (groupId == 5671831268753408L);
	}

}
