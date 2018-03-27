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
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

/**
 * Servlet implementation class SeeEntitiesServlet
 */
@WebServlet(
		name = "ViewContestsServlet",
	    urlPatterns = "/contests")
public class ViewContestsServlet extends HttpServlet {

	String htmlHeaderString;
	String tableHeaderString;
	KeyFactory keyfactory;
	Datastore datastore;
	KeyFactory keyFactory;
	Key contestKey;
	String contestStringFormat;
	String wagerFormString;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println(htmlHeaderString);
		out.println("<br><a href=\"../index.jsp\">Go Home</a><br>");
		out.println(tableHeaderString);
		
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Contest")
				.setFilter(PropertyFilter.eq("resolved", false))
				.setOrderBy(OrderBy.desc("date"))
				.build();
		QueryResults<Entity> results = datastore.run(query);
		
		results.forEachRemaining((result) -> {
			
			// Build up string with values from the Datastore entity
			String recordOutput = 
					String.format(contestStringFormat,
							"../viewcontest?contest_id=" + String.valueOf(result.getKey().getId()),
							result.getString("favorite") + " v. " + result.getString("dog"),
							result.getString("favorite"), result.getString("dog"),
							String.valueOf(result.getDouble("spread")),
							new Date(result.getTimestamp("date").getSeconds()*1000));

			out.println(recordOutput); // Print out HTML
		});
		out.println("</table></body></html>");
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
//		setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
//	  keyfactory = datastore.newKeyFactory().setKind("Contest");
		contestStringFormat = "<tr><td><a href=\"%s\">%s</a></td><td>%s</td><td>%s</td><td>%s</td><td>%tc</td></tr>";
		htmlHeaderString = "<!DOCTYPE html><html><head><style>table {font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}td, th {border: 1px solid #dddddd;text-align: left;padding: 8px;}tr:nth-child(even) {background-color: #dddddd;}</style></head><body>";
		tableHeaderString = "<table><tr><th>Matchup</th><th>Favorite</th><th>Dog</th><th>Spread</th><th>Date</th></tr>";
	}


}
