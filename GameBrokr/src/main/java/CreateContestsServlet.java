

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CreateContestsServlet
 */
@WebServlet(name = "CreateContestsServlet",
				urlPatterns = "/newcontests")
public class CreateContestsServlet extends HttpServlet {

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
		String[] favorite = request.getParameterValues("favorite[]");
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

}
