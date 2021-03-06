

import java.io.IOException;
import java.util.ArrayList;
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

import javabeans.ContestBean;

/**
 * Servlet implementation class IndexServlet
 */
@WebServlet(name = "IndexServlet",
		urlPatterns = "/sample")
public class IndexServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory keyFactory;
	Key contestKey;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Contest")
				.setFilter(PropertyFilter.eq("resolved", true))
				.setOrderBy(OrderBy.desc("date"))
				.build();
		QueryResults<Entity> results = datastore.run(query);
		
		ArrayList<ContestBean> allContests = new ArrayList<ContestBean>();
		
		results.forEachRemaining((result) -> {
			ContestBean bean = new ContestBean();
			bean.setFavorite(result.getString("favorite"));
			bean.setDog(result.getString("dog"));
			bean.setSpread(String.valueOf(result.getDouble("spread")));
			bean.setDate(new Date(result.getTimestamp("date").getSeconds()*1000));
			bean.setId("../viewcontest?contest_id=" + String.valueOf(result.getKey().getId()));
			allContests.add(bean);
		});
		
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
	}

}
