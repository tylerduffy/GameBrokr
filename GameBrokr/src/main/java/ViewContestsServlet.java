import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javabeans.ContestBean;

/**
 * Servlet implementation class SeeEntitiesServlet
 */
@WebServlet(name = "ViewContestsServlet",
	    urlPatterns = "/contests")
public class ViewContestsServlet extends HttpServlet {

	Datastore datastore;
	UserService userService;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Contest")
				.setFilter(PropertyFilter.eq("resolved", false))
				.setOrderBy(OrderBy.asc("date"))
				.build();
		QueryResults<Entity> results = datastore.run(query);
		
		ArrayList<ContestBean> allContests = new ArrayList<ContestBean>();
		
		results.forEachRemaining((result) -> {
			ContestBean bean = new ContestBean();
			bean.setId("../viewcontest?contest_id=" + String.valueOf(result.getKey().getId()));
			bean.setDate(new Date(result.getTimestamp("date").getSeconds()*1000));
			bean.setDog(result.getString("dog"));
			bean.setFavorite(result.getString("favorite"));
			bean.setMoneyline(getMoneyline(result));
			bean.setOverunder(processOdds(result.getDouble("overunder")));
			bean.setSpread(getSpread(result.getDouble("spread")));
			allContests.add(bean);
		});
		request.setAttribute("isAdmin", userService.isUserAdmin());
		request.setAttribute("allContests", allContests);
	    request.getRequestDispatcher("/WEB-INF/jsp/contests.jsp").forward(request, response);
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
		userService = UserServiceFactory.getUserService();
	}
	
	private String processOdds(double value) {
		if (value > -1) {
			return String.valueOf(value);
		} else {
			return "-";
		}
	}
	
	private String getSpread(double value) {
		if (value > -1) {
			return "+/- " + String.valueOf(value);
		} else {
			return processOdds(value);
		}
	}
	
	private String getMoneyline(Entity entity) {
		double favoriteLine = entity.getDouble("favoriteline");
		double dogLine = entity.getDouble("dogline");
		if (favoriteLine > -1 && dogLine > -1) {
			return "-" + String.valueOf(favoriteLine) + " / +" + String.valueOf(dogLine);
		} else {
			return processOdds(-1);
		}
	}

}
