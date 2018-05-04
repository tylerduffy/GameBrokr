

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

/**
 * Servlet implementation class PlaceBetServlet
 */
@WebServlet(name = "JoinGroupServlet",
	    urlPatterns = "/joingroup")
public class JoinGroupServlet extends HttpServlet {
	
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
		
		Key groupKey = groupKeyFactory.newKey(Long.parseLong(request.getParameter("groupid")));
		
		Entity bettor = datastore.get(bettorKey);
		if (bettor == null) {
			response.sendRedirect("/register");
		}
		
		
		Entity group = datastore.get(groupKey);
		Entity updatedGroup = Entity.newBuilder(group).set("count", group.getLong("count") + 1).build();
		datastore.update(updatedGroup);
		
		FullEntity<IncompleteKey> membershipEntry = Entity.newBuilder(membershipKeyFactory.newKey())
				.set("bank", 1000)
				.set("user", bettorKey)
				.set("loss", 0)
				.set("win", 0)
				.set("group", groupKey)
				.build();
		
		datastore.put(membershipEntry);
		response.sendRedirect("/profile");
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
