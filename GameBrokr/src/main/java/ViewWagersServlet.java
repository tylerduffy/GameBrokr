

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
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

/**
 * Servlet implementation class ViewWagersServlet
 */
@WebServlet(
		name = "ViewWagersServlet",
	    urlPatterns = "/wagers")
public class ViewWagersServlet extends HttpServlet {
	
	Datastore datastore;
	String wagerStringFormat;
	String htmlHeaderString;
	String tableHeaderString;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println(htmlHeaderString);
		out.println("<br><a href=\"../index.jsp\">Go Home</a><br>");
		out.println(tableHeaderString);
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Wager")
				.setFilter(PropertyFilter.eq("resolved", false))
				.setOrderBy(OrderBy.desc("date"))
				.build();

		QueryResults<Entity> results = datastore.run(query);

		if (results.hasNext()) {
			results.forEachRemaining((result) -> {

				// Build up string with values from the Datastore entity
				String recordOutput = String.format(wagerStringFormat, getMatchupString(result),
						getBettorName(result), getPick(result), getWager(result));

				out.println(recordOutput); // Print out HTML
			});

			out.println("</table></body></html>");

		} else {
			out.println("No Wagers Found. Try Again Later.");
		}
		
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String getMatchupString(Entity entity) {
		Entity contest = datastore.get(entity.getKey("contest"));
		if (contest != null) {
			return contest.getString("favorite") + " v. " + contest.getString("dog");
		}
		return "Matchup Could Not Be Resolved";
	}
	
	private String getBettorName(Entity entity) {
		Entity bettor = datastore.get(entity.getKey("bettor"));
		if (bettor != null) {
			return bettor.getString("firstname");
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
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		wagerStringFormat = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
		htmlHeaderString = "<!DOCTYPE html><html><head><style>table {font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}td, th {border: 1px solid #dddddd;text-align: left;padding: 8px;}tr:nth-child(even) {background-color: #dddddd;}</style></head><body>";
		tableHeaderString = "<table><tr><th>Matchup</th><th>Bettor</th><th>Pick</th><th>Wager</th></tr>";
	}

}
