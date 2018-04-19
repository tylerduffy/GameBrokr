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
		<div class="contestdetail">
			<h2>${contest.favorite} v ${contest.dog} (+${contest.spread})</h2>
			<h4>${contest.date}</h4>
		</div>
		<c:if test = "${!contest.resolved}">
		<div class="wagerForm">
			<h3>Place Wager</h3>
			<form style="display:inline;" method="POST" action="/placebet">
				<input type="hidden" name="contestid" id="contestid" value="${contest.id}"/>
				<label for="selection">Wager </label>
				<select name="selection" id="selection">
					<option disabled selected value="">-- place your wager --</option>
				 	<option value="favorite">${contest.favorite} (-${contest.spread})</option>
				 	<option value="dog">${contest.dog} (+${contest.spread})</option>
				</select>
				<label for="wageramount">Amount </label>
				<input type="number" name="wageramount" id="wageramount" class="form-control"/>
				<button type="submit">Submit</button>
			</form>
		</div>
		<div class="resolveContestForm">
			<h2>Resolve Contest</h2>
			<form method="POST" action="/resolvecontest">
				<div>
					<input type="hidden" name="contestid" id="contestid" value="${contest.id}"/>
				</div>
				<div>
					<label for="favoritescore">${contest.favorite} </label>
					<input type="number" name="favoritescore" id="favoritescore" required class="form-control"/>
				</div>
				<div>
					<label for="dogscore">${contest.dog} </label>
					<input type="number" name="dogscore" id="dogscore" required class="form-control"/>
				</div>
				<button type="submit">Submit</button>
			</form>
    	</div>
    	</c:if>
		<h2>Wagers</h2>
	    <table class="wager">
		    <tr>
				<th>Username</th>
				<th>Selection</th>
				<th>Wager</th>
			</tr>
			<c:forEach items="${allWagers}" var="wager">
		    	<tr>
		    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
		    		<td>${wager.selection}</td>
		    		<td>${wager.amount}</td>
		    	</tr>
		    </c:forEach>
	    </table>
    </div>
  </body>
</html>