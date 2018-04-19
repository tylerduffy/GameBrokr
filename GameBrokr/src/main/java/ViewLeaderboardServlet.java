

import java.io.IOException;
import java.util.ArrayList;

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

import javabeans.BettorBean;

/**
 * Servlet implementation class ViewLeaderboardServlet
 */
@WebServlet(name = "ViewLeaderboardServlet",
		urlPatterns = "/leaderboard")
public class ViewLeaderboardServlet extends HttpServlet {
	
	Datastore datastore;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Bettor")
				.setOrderBy(OrderBy.desc("bank"))
				.build();
		
		QueryResults<Entity> results = datastore.run(query);
		
		ArrayList<BettorBean> allBettors = new ArrayList<BettorBean>();
		
		if (results.hasNext()) {
			results.forEachRemaining((result) -> {
				BettorBean bean = new BettorBean();
				bean.setId(String.valueOf(result.getKey().getNameOrId()));
				bean.setUsername(result.getString("username"));
				bean.setBank(String.valueOf(result.getValue("bank").get()));
				bean.setWin(String.valueOf(result.getValue("win").get()));
				bean.setLoss(String.valueOf(result.getValue("loss").get()));
				allBettors.add(bean);
			});
		}
		
		request.setAttribute("allBettors", allBettors);
	    request.getRequestDispatcher("/WEB-INF/jsp/leaderboard.jsp").forward(request, response);		
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
	}

}
