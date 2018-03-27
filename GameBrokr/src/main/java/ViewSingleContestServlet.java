
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

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
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

/**
 * Servlet implementation class ViewSingleContestServlet
 */
@WebServlet(
		name = "ViewSingleContestServlet",
		urlPatterns = "/viewcontest")
public class ViewSingleContestServlet extends HttpServlet {

	Datastore datastore;
	KeyFactory keyFactory;
	Key contestKey;
	String contestStringFormat;
	String wagerFormString;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		if (request.getParameter("contest_id") != null) {
			
			long entID = Long.parseLong(request.getParameter("contest_id"));
			contestKey = keyFactory.newKey(entID);
			
//			Entity contest = datastore.get(contestKey);
//			if (contest != null) {
//				out.println("<p> non empty search using simple get </p>");
//				out.println("<p> Key Name - " + String.valueOf(contest.getKey().getId()) + " </p>");
//			}

			Query<Entity> query = Query.newEntityQueryBuilder().setKind("Contest")
					.setFilter(PropertyFilter.eq("__key__", contestKey)).build();
			
			QueryResults<Entity> results = datastore.run(query);
			
			if (results.hasNext()) {
				results.forEachRemaining((result) -> {
					
					// Build up string with values from the Datastore entity
					String recordOutput = String.format(contestStringFormat, result.getString("favorite"), String.valueOf(entID), "favorite",
							result.getString("dog"), String.valueOf(entID), "dog", String.valueOf(result.getDouble("spread")), new Date(result.getTimestamp("date").getSeconds()*1000));

					out.println(recordOutput); // Print out HTML
					
					out.println(getResolveContainerString(result, entID));
				});

				out.println("</body></html>");

			} else {
				out.println("Given Contest ID returned no results. Try Again. <br>");
				out.println("Key Kind --- " + contestKey.getKind());
				out.println("<br>Key Name --- " + contestKey.getName());
				out.println("<br>Key Id --- " + contestKey.getId());
			}

		} else if (request.getParameter("contest_ids") != null) {
			out.println("Found parameter for contest - " + request.getParameter("contest_ids"));
		} else {
			out.println("No Contest ID found in URL. Try Again.");
		}
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		keyFactory = datastore.newKeyFactory().setKind("Contest");
		contestStringFormat = " Favorite: %s " + getWagerString() + "<br> Dog: %s " + getWagerString() + "<p> Spread: %s</p><p> Date: %tc</p>";
	}
	
	private String getResolveContainerString(Entity entity, long entID) {
		return "<br><div class=\"container\"><h2>Resolve Game</h2>" + 
				"<form method=\"POST\" action=\"/resolvecontest\">" +
				"<div><input type=\"hidden\" name=\"contestid\" id=\"contestid\" value=\""+String.valueOf(entID)+"\"/></div>" +
				"<div><label for=\"favoritescore\">"+ entity.getString("favorite") +" </label>" +
				"<input type=\"number\" name=\"favoritescore\" id=\"favoritescore\" size=\"25\" class=\"form-control\"/></div>" + 
				"<div><label for=\"dogscore\">"+ entity.getString("dog") +" </label>" + 
				"<input type=\"number\" name=\"dogscore\" id=\"dogscore\" size=\"25\" class=\"form-control\"/>" + 
				"</div><button type=\"submit\">Submit</button></form></div>";
	}
	
	private String getWagerString() {
		return "<form style=\"display:inline;\" method=\"POST\" action=\"/placebet\">" +
				"<input type=\"hidden\" name=\"contestid\" id=\"contestid\" value=\"%s\"/>" +
				"<input type=\"hidden\" name=\"selection\" id=\"selection\" value=\"%s\"/>" +
				"<div><label for=\"wageramount\">Wager </label>" +
				"<input type=\"number\" name=\"wageramount\" id=\"wageramount\" size=\"25\" class=\"form-control\"/>" + 
				"<input type=\"radio\" name=\"userid\" value=\"5715999101812736\">Charlie " +
				"<input type=\"radio\" name=\"userid\" value=\"5649391675244544\">Tyler " +
				"<button type=\"submit\">Submit</button></form></div>";
	}

}
