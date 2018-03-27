
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

/**
 * Servlet implementation class ViewBankServlet
 */
@WebServlet(name = "ViewBankServlet", urlPatterns = "/bank")
public class ViewBankServlet extends HttpServlet {

	Datastore datastore;
	String bettorStringFormat;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("<br><a href=\"../index.jsp\">Go Home</a><br>");
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Bettor").build();

		QueryResults<Entity> results = datastore.run(query);

		if (results.hasNext()) {
			results.forEachRemaining((result) -> {

				// Build up string with values from the Datastore entity
				String recordOutput = String.format(bettorStringFormat, result.getString("firstname"),
						String.valueOf(result.getValue("bank").get()));

				out.println(recordOutput); // Print out HTML
			});

			out.println("</body></html>");

		} else {
			out.println("No Bettors Found. Try Again Later.");
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
		bettorStringFormat = "<p> Bettor: %s, $%s</p>";
	}

}
