

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
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

/**
 * Servlet implementation class ViewBettorServlet
 */
@WebServlet(name = "ViewBettorServlet",
		urlPatterns = "/viewbettor")
public class ViewBettorServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory bettorKeyFactory;
	KeyFactory wagerKeyFactory;
	Key bettorKey;
	String bettorStringFormat;
	String historyStringFormat;
	String historyTableHeaderString;
	String htmlHeaderString;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println(htmlHeaderString);
		
		if (request.getParameter("bettor_id") != null) {
			
			long entID = Long.parseLong(request.getParameter("bettor_id"));
			bettorKey = bettorKeyFactory.newKey(entID);

			Query<Entity> query = Query.newEntityQueryBuilder().setKind("Bettor")
					.setFilter(PropertyFilter.eq("__key__", bettorKey)).build();
			
			QueryResults<Entity> results = datastore.run(query);
			
			if (results.hasNext()) {
				results.forEachRemaining((result) -> {
					
					// Build up string with values from the Datastore entity
					String recordOutput = String.format(bettorStringFormat, result.getString("firstname"), result.getString("lastname"), result.getString("username"),
							String.valueOf(result.getValue("bank").get()), String.valueOf(result.getValue("win").get()), String.valueOf(result.getValue("loss").get()));

					out.println(recordOutput); // Print out HTML
				});

			} else {
				out.println("Given Bettor ID returned no results. Try Again. <br>");
			}
			
			Query<Entity> querytwo = Query.newEntityQueryBuilder().setKind("Wager")
					.setFilter(CompositeFilter.and(
							PropertyFilter.eq("bettor", bettorKey),PropertyFilter.eq("resolved", true)))
					.setOrderBy(OrderBy.desc("date"))
					.build();
			
			QueryResults<Entity> resultstwo = datastore.run(querytwo);
			
			if (resultstwo.hasNext()) {
				out.println(historyTableHeaderString);
				resultstwo.forEachRemaining((result) -> {
					String resultString = getResult(result);
					String resultReportString = "";
					if (resultString.equals("win")) {
						resultReportString = "W +" + getWager(result);
					} else if (resultString.equals("loss")) {
						resultReportString = "L -" + getWager(result);
					} else {
						resultReportString = "W +$0";
					}
					
					// Build up string with values from the Datastore entity
					String recordOutput = String.format(historyStringFormat, getMatchupString(result), getPick(result),
							resultReportString);

					out.println(recordOutput); // Print out HTML
				});
				
				out.println("</table></body></html>");
			}
		} else {
			out.println("No Bettor ID found in URL. Try Again.");
		}
		out.close();
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
		wagerKeyFactory = datastore.newKeyFactory().setKind("Wager");
		bettorStringFormat = "%s %s (%s): $%s<br>"
				+ "Record: %s-%s";
		historyTableHeaderString = "<table><tr><th>Matchup</th><th>Selection</th><th>Result</th></tr>";
		historyStringFormat = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
		htmlHeaderString = htmlHeaderString = "<!DOCTYPE html><html><head><style>table {font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}td, th {border: 1px solid #dddddd;text-align: left;padding: 8px;}tr:nth-child(even) {background-color: #dddddd;}</style></head><body>";
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

}
