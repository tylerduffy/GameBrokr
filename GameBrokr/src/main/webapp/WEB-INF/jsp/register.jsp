<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
    <title>GameBrokr</title>
    <link rel="stylesheet" type="text/css" href="/stylesheets/style.css">
  </head>

  <body>
  	<ul>
	  <li><a href="/index.jsp">Home</a></li>
	  <li><a href="/contests">Contests</a></li>
	  <li><a href="/wagers">Wagers</a></li>
	  <li><a href="/groups">Groups</a></li>
	  <li><a href="/leaderboard">Leaderboard</a></li>
	  <li style="float:right"><a href="/profile">Profile</a></li>
	</ul>
	
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<div class="container">
		<c:if test = "${errMsg != null}">
			<h4>${errMsg}</h4>
		</c:if>
		<c:if test = "${!registered}">
			<h2>Register</h2>
		    <form method="POST" action="/register">
		    	<div>
		    		<label for="firstname">First Name </label>
		    		<input type="text" name="firstname" id="firstname" required/>
		    	</div>
		    	<div>
		    		<label for="lastname">Last Name </label>
		    		<input type="text" name="lastname" id="lastname" required/>
		    	</div>
		    	<div>
		    		<label for="username">Username </label>
		    		<input type="text" name="username" id="username" required/>
		    	</div>
		    	<div>
	        		<button type="submit">Submit</button>
	        	</div>
	        </form>
		</c:if>
    </div>
  </body>
</html>