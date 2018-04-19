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
	  <li><a href="/leaderboard">Leaderboard</a></li>
	  <li style="float:right"><a href="/profile">Profile</a></li>
	</ul>
	
    <!-- Sample Form From GAE help pages "Getting Started" -->
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<div class="container">
		<h1>Contests</h1>
	    <table class="contest">
		    <tr>
				<th>Matchup</th>
				<th>Favorite</th>
				<th>Dog</th>
				<th>Spread</th>
				<th>Date</th>
			</tr>
		    <c:forEach items="${allContests}" var="contest">
		    	<tr>
		    		<td><a href="${contest.id}">${contest.favorite} v. ${contest.dog}</a></td>
		    		<td>${contest.favorite}</td>
		    		<td>${contest.dog}</td>
		    		<td>${contest.spread}</td>
		    		<td>${contest.date}</td>
		    	</tr>
		    </c:forEach>
	    </table>
	    <div class="newContestForm">
			<h2>
			  Add a New Contest
			</h2>
			<form method="POST" action="/newcontest">
			  <div>
			    <label for="favorite">Favorite </label>
			    <input type="text" name="favorite" id="favorite" class="form-control" required />
			  </div>
			
			  <div>
			    <label for="dog">Dog </label>
			    <input type="text" name="dog" id="dog" class="form-control" required />
			  </div>
			  
			  <div>
			    <label for="spread">Spread </label>
			    <input type="number" step="0.1" name="spread" id="spread" class="form-control" required />
			  </div>
			  <button type="submit">Save</button>
			</form>
	    </div>
    </div>
  </body>
</html>