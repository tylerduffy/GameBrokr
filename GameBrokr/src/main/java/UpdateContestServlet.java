

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

/**
 * Servlet implementation class UpdateContestServlet
 */
@WebServlet(name = "UpdateContestServlet",
		urlPatterns = "/updatecontest")
public class UpdateContestServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory contestKeyFactory;
	UserService userService;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Entity contest = datastore.get(getContestKey(request.getParameter("contestid")));
		
		Entity updatedContest = Entity.newBuilder(contest)
			.set("spread", parseOdds(request.getParameter("spread")))
			.set("favoriteline", parseOdds(request.getParameter("favline")))
			.set("dogline", parseOdds(request.getParameter("dogline")))
			.set("overunder", parseOdds(request.getParameter("overunder")))
			.build();
		
		datastore.update(updatedContest);
		
		response.sendRedirect("/contests?sport=" + contest.getString("sport"));
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		contestKeyFactory = datastore.newKeyFactory().setKind("Contest");
	}
	
	private Key getContestKey(String contestid) {
		long id = Long.parseLong(contestid);
		return contestKeyFactory.newKey(id);
	}
	
	private double parseOdds(String param) {
		if (param != null && !param.equals("")) {
			return Double.parseDouble(param);
		} else {
			return -1;
		}
	}

}
