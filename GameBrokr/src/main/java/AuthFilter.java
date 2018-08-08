

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Servlet Filter implementation class AuthFilter
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
	
	UserService userService;

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		String url = req.getRequestURL().toString();
		String homeURL = "(http)?(s)?(://)?(www\\.)?(gamebrokr)(\\.appspot)?(\\.com)(/)?(index\\.jsp)?";
		
		StringBuffer requestURL = req.getRequestURL();
		if (req.getQueryString() != null) {
		    requestURL.append("?").append(req.getQueryString());
		}
		String completeURL = requestURL.toString();
		
		String trueLoginURL = userService.createLoginURL(completeURL);
		
		// check if user is on home page || user is logged in || user is on its way to logging in
		if (url.matches(homeURL) || userService.isUserLoggedIn() || url.equals(trueLoginURL)) {
			// pass the request along the filter chain
			request.setAttribute("userLoggedIn", userService.isUserLoggedIn());
			chain.doFilter(request, response);
		} else {
			resp.sendRedirect(trueLoginURL);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		userService = UserServiceFactory.getUserService();
	}

}
