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
			  <br>
			  <div>
			    <label for="spread">Spread </label>
			    <input type="number" step="0.1" name="spread" id="spread" class="form-control" />
			  </div>
			  <br>
			  <div>
			    <label for="favline">$ Line (- Fav) </label>
			    <input type="number" step="0.1" name="favline" id="favline" class="form-control" />
			  </div>
			  <div>
			    <label for="dogline">$ Line (+ Dog) </label>
			    <input type="number" step="0.1" name="dogline" id="dogline" class="form-control" />
			  </div>
			  <br>
			  <div>
			    <label for="overunder">Over/Under </label>
			    <input type="number" step="0.1" name="overunder" id="overunder" class="form-control" />
			  </div>
			  <br>
			  <button type="submit">Add</button>
			</form>
	    </div>
	    
		<div class="tables">
			<h1>Contests</h1>
		    <table class="contest">
			    <tr>
					<th>Matchup</th>
					<th>Favorite</th>
					<th>Spread</th>
					<th>Money Line</th>
					<th>Over/Under</th>
					<th>Date</th>
				</tr>
			    <c:forEach items="${allContests}" var="contest">
			    	<tr>
			    		<td><a href="${contest.id}">${contest.favorite} v. ${contest.dog}</a></td>
			    		<td>${contest.favorite}</td>
			    		<td>${contest.spread}</td>
			    		<td>${contest.moneyline}</td>
			    		<td>${contest.overunder}</td>
			    		<td>Proper date TBD</td>
			    	</tr>
			    </c:forEach>
		    </table>
		</div>
    </div>
  </body>
</html>