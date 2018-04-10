

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
 * Servlet implementation class CreateContestServlet
 */
@WebServlet(name = "CreateContestServlet",
	    urlPatterns = "/newcontest")
public class CreateContestServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory keyFactory;
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
		FullEntity<IncompleteKey> contestEntry = Entity.newBuilder(keyFactory.newKey())
				.set("favorite", request.getParameter("favorite"))
				.set("dog", request.getParameter("dog"))
				.set("spread",Double.parseDouble(request.getParameter("spread")))
				.set("date", Timestamp.now())
				.set("resolved", false)
				.build();
		
		datastore.put(contestEntry);
		response.sendRedirect("/contests");
//		doGet(request, response);
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		keyFactory = datastore.newKeyFactory().setKind("Contest");
	}

}
