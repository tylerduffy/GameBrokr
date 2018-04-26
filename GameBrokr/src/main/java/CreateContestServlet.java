

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.google.cloud.datastore.Value;

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
		request.setAttribute("errorMsg", "Nothing here. Please try again.");
	    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FullEntity<IncompleteKey> contestEntry = Entity.newBuilder(keyFactory.newKey())
				.set("favorite", request.getParameter("favorite"))
				.set("dog", request.getParameter("dog"))
				.set("spread", parseOdds(request.getParameter("spread")))
				.set("favoriteline", parseOdds(request.getParameter("favline")))
				.set("dogline", parseOdds(request.getParameter("dogline")))
				.set("overunder", parseOdds(request.getParameter("overunder")))
				.set("date", Timestamp.of(parseDate(request.getParameter("gamedate"), request.getParameter("gametime"))))
				.set("resolved", false)
				.build();
		
		datastore.put(contestEntry);
		response.sendRedirect("/contests");
	}

	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		keyFactory = datastore.newKeyFactory().setKind("Contest");
	}
	
	private double parseOdds(String param) {
		if (param != null && !param.equals("")) {
			return Double.parseDouble(param);
		} else {
			return -1;
		}
	}
	
	private Date parseDate(String gamedate, String gametime) {
		SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-ddhh:mmXXX");
		String gamezone = "-05:00";
		try {
			Date date = input.parse(gamedate+gametime+gamezone);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
		
	}

}
