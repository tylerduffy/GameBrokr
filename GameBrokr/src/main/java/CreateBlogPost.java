import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CreateBlogPost
 */
@WebServlet(
		name="createBlogPost",
		urlPatterns="/create")
public class CreateBlogPost extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();

	    out.println(
	        "Article with the title: " + request.getParameter("title") + " by "
	            + request.getParameter("author") + " and the content: "
	            + request.getParameter("description") + " added.");
	}

}
