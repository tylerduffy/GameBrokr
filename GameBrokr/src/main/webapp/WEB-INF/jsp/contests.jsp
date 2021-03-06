<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
    <title>GameBrokr</title>
    <link rel="stylesheet" type="text/css" href="/stylesheets/style.css">
  </head>

  <body>
  	<!-- <ul>
	  <li><a href="/index.jsp">Home</a></li>
	  <li><a href="/contests">Contests</a></li>
	  <li><a href="/wagers">Wagers</a></li>
	  <li><a href="/groups">Groups</a></li>
	  <li><a href="/leaderboard">Leaderboard</a></li>
	  <li style="float:right"><a href="/profile">Profile</a></li>
	</ul> -->
	<div class="navbar">
	  <a href="/index.jsp">Home</a>
	  <div class="dropdown">
	    <button class="dropbtn">Contests 
	      <i class="fa fa-caret-down"></i>
	    </button>
	    <div class="dropdown-content">
	      <a href="/contests?sport=nba">NBA</a>
	      <a href="/contests?sport=nfl">NFL</a>
	      <a href="/contests?sport=mlb">MLB</a>
	      <a href="/contests?sport=nhl">NHL</a>
	      <a href="/contests?sport=ncaab">NCAAB</a>
	      <a href="/contests?sport=ncaaf">NCAAF</a>
	    </div>
	  </div>
	  <a href="/wagers">Wagers</a>
	  <a href="/groups">Groups</a>
	  <a href="/leaderboard">Leaderboard</a>
	  <a style="float:right" href="/profile">Profile</a>
	</div>
	
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<div class="container">
	<c:if test = "${isAdmin}">
		<div class="newContestForm">
			<h2>
			  Add a New Contest
			</h2>
			<form method="POST" action="/newcontest">
			<input type="hidden" name="sport" id="sport" value="${sport}"/>
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
			  <div>
			  	<label for="gamedate">Date </label>
			  	<input type="date" name="gamedate" id="gamedate" placeholder="yyyy-mm-dd">
			  </div>
			  <br>
			  <div>
			  	<label for="gametime">Time (EST) </label>
			  	<input type="time" name="gametime" id="gametime" placeholder="hh:mm">
			  </div>
			  <br>
			  <button type="submit">Add</button>
			</form>
	    </div>
	    </c:if>
		<div class="tables">
			<h1>${properSport} Contests</h1>
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
			    		<td>${contest.datestr}</td>
			    	</tr>
			    </c:forEach>
		    </table>
		    <c:if test="${showHistory}">
			<h1>Contest History</h1>
		    <table class="contest">
			    <tr>
					<th>Result</th>
					<th>Favorite</th>
					<th>Spread</th>
					<th>Money Line</th>
					<th>Over/Under</th>
					<th>Date</th>
				</tr>
			    <c:forEach items="${contestHistory}" var="contest">
			    	<tr>
			    		<td><a href="${contest.id}">${contest.favorite}, ${contest.favoriteresult} v. ${contest.dog}, ${contest.dogresult}</a></td>
			    		<td>${contest.favorite}</td>
			    		<td>${contest.spread}</td>
			    		<td>${contest.moneyline}</td>
			    		<td>${contest.overunder}</td>
			    		<td>${contest.datestr}</td>
			    	</tr>
			    </c:forEach>
		    </table>
			</c:if>
		</div>
		<c:if test="${!showHistory}">
			<p><a href="../contests?sport=${sport}&showhistory=true">Show Past Games</a></p>
		</c:if>
    </div>
  </body>
</html>