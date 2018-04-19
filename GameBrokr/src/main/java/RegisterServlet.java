

import java.io.IOException;
import java.io.PrintWriter;

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
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(name = "ViewRegisterServlet",
		urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

	Datastore datastore;
	KeyFactory bettorKeyFactory;
	UserService userService;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Key bettorKey = bettorKeyFactory.newKey(userService.getCurrentUser().getUserId());
		
		if (datastore.get(bettorKey) != null) {
			request.setAttribute("errMsg", "Already registered. You can now place wagers!");
			request.setAttribute("registered", true);
			request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
		}
		
		if (request.getParameter("err") != null) {
			request.setAttribute("errMsg", request.getParameter("err"));
		}
	    request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// TODO: Error check for existence of username. 
		// If found, redirect to the get method but pass an error argument?
		String username = request.getParameter("username");
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Bettor")
				.setFilter(PropertyFilter.eq("username", username)).build();
		QueryResults<Entity> results = datastore.run(query);
		if (results.hasNext()) {
			// TODO: Find way to pass argument to show that username is taken or not
			request.setAttribute("err", "Username already exists. Please try a different one.");
			request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
		} else {
			String first = request.getParameter("firstname");
			String last = request.getParameter("lastname");
			
			Key bettorKey = bettorKeyFactory.newKey(userService.getCurrentUser().getUserId());
			FullEntity<Key> bettor = Entity.newBuilder(bettorKey)
					.set("bank", 1000)
					.set("firstname", first)
					.set("lastname", last)
					.set("username", username)
					.set("loss", 0)
					.set("win", 0)
					.build();
			
			datastore.put(bettor);
			response.sendRedirect("/index.jsp");
		}
	}
	
	@Override
	public void init() throws ServletException {
		// setup datastore service
		datastore = DatastoreOptions.getDefaultInstance().getService();
		bettorKeyFactory = datastore.newKeyFactory().setKind("Bettor");
		userService = UserServiceFactory.getUserService();
	}
}
