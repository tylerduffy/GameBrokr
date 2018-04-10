<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
    <title>GameBrokr</title>
    <style>
		ul {
		    list-style-type: none;
		    margin: 0;
		    padding: 0;
		    overflow: hidden;
		    background-color: #333;
		}
		li {
		    float: left;
		}
		li a {
		    display: block;
		    color: white;
		    text-align: center;
		    padding: 14px 16px;
		    text-decoration: none;
		}
		/* Change the link color to #111 (black) on hover */
		li a:hover {
		    background-color: #111;
		}
	</style>
  </head>

  <body>
  	<ul>
	  <li><a href="/index.jsp">Home</a></li>
	  <li><a href="/contests">Contests</a></li>
	  <li><a href="/wagers">Wagers</a></li>
	  <li><a href="/bank">Bank</a></li>
	  <li style="float:right"><a href="#">Login</a></li>
	</ul>
    <h1>Hello Brokr!</h1>
    
    <!-- Sample Form From GAE help pages "Getting Started" -->
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
	<div class="container">
	  <h2>
	    Add a New Contest
	  </h2>
	
	  <form method="POST" action="/newcontest">
	
	    <div>
	      <label for="favorite">Favorite </label>
	      <input type="text" name="favorite" id="favorite" size="40" class="form-control" />
	    </div>
	
	    <div>
	      <label for="dog">Dog </label>
	      <input type="text" name="dog" id="dog" size="40" class="form-control" />
	    </div>
	    
	    <div>
	      <label for="spread">Spread </label>
	      <input type="number" step="0.1" name="spread" id="spread" size="40" class="form-control" />
	    </div>
	    
	    <!-- 
	    <div>
	      <label for="date">Date </label>
	      <input type="datetime-local" name="date" id="date" size="40" class="form-control" />
	    </div>
	    -->
	
	    <!--  <div>
	      <label for="">Post content</label>
	      <textarea name="description" id="description" rows="10" cols="50" class="form-control">${fn:escapeXml(blog.content)}</textarea>
	    </div> -->
	
	    <button type="submit">Save</button>
	  </form>
	  
	</div>
    <!-- End of Form -->
    
  </body>
</html>