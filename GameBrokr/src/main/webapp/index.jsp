<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
    <title>GameBrokr</title>
  </head>

  <body>
    <h1>Hello Brokr!</h1>
    
    <p> See all <a href='/contests'>contests</a>.</p>
    <p> See all <a href='/wagers'>wagers</a>.</p>
    <p> Visit the <a href='/bank'>bank</a>.</p>
    
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