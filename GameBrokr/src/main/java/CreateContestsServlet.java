

import java.io.IOException;
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
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

/**
 * Servlet implementation class CreateContestsServlet
 */
@WebServlet(name = "CreateContestsServlet",
				urlPatterns = "/newcontests")
public class CreateContestsServlet extends HttpServlet {

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
		String[] favorite = request.getParameterValues("favorite");
		String[] dog = request.getParameterValues("dog");
		String[] spread = request.getParameterValues("spread");
		String[] favoriteline = request.getParameterValues("favoriteline");
		String[] dogline = request.getParameterValues("dogline");
		String[] overunder = request.getParameterValues("overunder");
		String[] date = request.getParameterValues("date");
		String[] time = request.getParameterValues("time");
		String[] sport = request.getParameterValues("sport");
		
		// iterate through all the new contest entries to build the entities
		for (int i = 0; i < favorite.length; i++) {
			// build contest entities
			FullEntity<IncompleteKey> contestEntry = Entity.newBuilder(keyFactory.newKey())
					.set("favorite", favorite[i])
					.set("dog", dog[i])
					.set("spread", parseOdds(spread[i]))
					.set("favoriteline", parseOdds(favoriteline[i]))
					.set("dogline", parseOdds(dogline[i]))
					.set("overunder", parseOdds(overunder[i]))
					.set("date", Timestamp.of(parseDate(date[i], time[i])))
					.set("sport", sport[i])
					.set("resolved", false)
					.set("spreadfavoritesum", 0)
					.set("spreaddogsum", 0)
					.set("moneylinefavoritesum", 0)
					.set("moneylinedogsum", 0)
					.set("oversum", 0)
					.set("undersum", 0)
					.build();
			
			// put new contests into datastore
			datastore.put(contestEntry);
		}
		
		String favoriteStr = "";
		if (favorite != null) {
			for (String s : favorite) {
				favoriteStr += s;
				favoriteStr += " | ";
			}
			request.setAttribute("errorMsg", "Here is your list of favorites: " + favoriteStr);
		} else {
			request.setAttribute("errorMsg", "No favorites found :(");
		}
		
	    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
//		doGet(request, response);
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
		// From March 11 @ 2:00 AM to November 4 @ 2:00 AM,
		// this needs to be -4:00 for EST Daylight Savings Time.
		// Otherwise -5:00 for EST
		String gamezone = "-04:00";
		try {
			Date date = input.parse(gamedate+gametime+gamezone);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
		
	}

}
