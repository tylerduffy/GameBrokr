

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
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javabeans.BettorBean;
import javabeans.GroupBean;

/**
 * Servlet implementation class ViewLeaderboardServlet
 */
@WebServlet(name = "ViewGroupsServlet",
		urlPatterns = "/groups")
public class ViewGroupsServlet extends HttpServlet {
	
	Datastore datastore;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Group")
				.setFilter(PropertyFilter.gt("count", 0))
				.setOrderBy(OrderBy.desc("count"))
				.build();
		
		QueryResults<Entity> results = datastore.run(query);
		
		ArrayList<GroupBean> allGroups = new ArrayList<GroupBean>();
		
		if (results.hasNext()) {
			results.forEachRemaining((result) -> {
				GroupBean bean = new GroupBean();
				bean.setLink(buildUrl(result));
				bean.setName(result.getString("name"));
				bean.setOwner(getOwner(result));
				bean.setCount(String.valueOf(result.getValue("count").get()));
				allGroups.add(bean);
			});
		}
		
		request.setAttribute("allGroups", allGroups);
	    request.getRequestDispatcher("/WEB-INF/jsp/groups.jsp").forward(request, response);		
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
	
	private String buildUrl(Entity result) {
		return "../viewgroup?group_id=" + String.valueOf(result.getKey().getNameOrId());
	}
	
	private String getOwner(Entity result) {
		Entity owner = datastore.get(result.getKey("owner"));
		if (owner != null) {
			return owner.getString("username");
		} else {
			return "Owner Not Found";
		}
	}

}
