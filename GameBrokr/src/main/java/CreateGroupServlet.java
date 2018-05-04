

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

/**
 * Servlet implementation class PlaceBetServlet
 */
@WebServlet(name = "CreateGroupServlet",
	    urlPatterns = "/newgroup")
public class CreateGroupServlet extends HttpServlet {
	
	Datastore datastore;
	KeyFactory bettorKeyFactory;
	KeyFactory groupKeyFactory;
	KeyFactory membershipKeyFactory;
	UserService userService;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("errorMsg", "Reached this page in error. Please try again.");
	    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Key bettorKey = bettorKeyFactory.newKey(userService.getCurrentUser().getUserId());
		String groupName = request.getParameter("name");
		
		Entity bettor = datastore.get(bettorKey);
		if (bettor == null) {
			response.sendRedirect("/register");
		}
		
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Group")
				.setFilter(PropertyFilter.eq("name", groupName))
				.build();
		
		QueryResults<Entity> results = datastore.run(query);
		
		if (results.hasNext()) {
			request.setAttribute("errorMsg", "Given Group Name is not available. Please try another.");
		    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
		}
		
		FullEntity<IncompleteKey> groupEntry = Entity.newBuilder(groupKeyFactory.newKey())
				.set("owner", bettorKey)
				.set("name", groupName)
				.set("count", 1)
				.build();
		
		Entity group = datastore.put(groupEntry);
		
		FullEntity<IncompleteKey> membershipEntry = Entity.newBuilder(membershipKeyFactory.newKey())
				.set("bank", 1000)
				.set("user", bettorKey)
				.set("loss", 0)
				.set("win", 0)
				.set("group", group.getKey())
				.build();
		
		datastore.put(membershipEntry);
		response.sendRedirect("/groups");
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		bettorKeyFactory = datastore.newKeyFactory().setKind("Bettor");
		groupKeyFactory = datastore.newKeyFactory().setKind("Group");
		membershipKeyFactory = datastore.newKeyFactory().setKind("Membership");
		userService = UserServiceFactory.getUserService();
	}
}
