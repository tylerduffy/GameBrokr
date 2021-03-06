import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "SampleServlet",
		urlPatterns = "/sampleservlet")
public class SampleServlet extends HttpServlet {

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    	response.setContentType("text/plain");
//    	response.setCharacterEncoding("UTF-8");
    	
    	//response.getWriter().append("Served at: ").append(request.getContextPath());
//    	response.getWriter().append("Visiting from: ").append(request.getRemoteAddr());
    	String[] groups = request.getParameterValues("group");
    	String groupStr = "";
    	for (String s : groups) {
    		groupStr += s;
    		groupStr += "...";
    	}
    	request.setAttribute("errorMsg", "Groups: " + groupStr);
	    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
