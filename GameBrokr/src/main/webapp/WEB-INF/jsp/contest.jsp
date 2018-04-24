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
			<c:if test = "${contest.resolved}">
				<h2>${contest.favorite}, ${contest.favoriteresult} v ${contest.dog}, ${contest.dogresult}</h2>
			</c:if>
			<c:if test = "${!contest.resolved}">
				<h2>${contest.favorite} v ${contest.dog}</h2>
			</c:if>
			<c:if test = "${canSpread}">
				<h4>${contest.favorite} (-${contest.spread}) v ${contest.dog} (+${contest.spread})</h4>
			</c:if>
			<c:if test = "${canMoneyline}">
				<h4>${contest.favorite} (-${contest.favoriteline}) v ${contest.dog} (+${contest.dogline})</h4>
			</c:if>
			<c:if test = "${canOverunder}">
				<h4>Total Points: ${contest.overunder}</h4>
			</c:if>
			
			<h4>${contest.date}</h4>
		</div>
		<c:if test = "${!contest.resolved}">
		
		<c:if test = "${canSpread}">
		<div class="wagerForm">
			<h2>Bet the Spread</h2>
			<form method="POST" action="/placebet">
				<input type="hidden" name="contestid" id="contestid" value="${contest.id}"/>
				<input type="hidden" name="type" id="type" value="spread"/>
				<label for="selection">Selection </label>
				<select name="selection" id="selection">
					<option disabled selected value="">-- make your pick --</option>
				 	<option value="favorite">${contest.favorite} (-${contest.spread})</option>
				 	<option value="dog">${contest.dog} (+${contest.spread})</option>
				</select>
				<br>
				<label for="wageramount">Wager ($) </label>
				<input type="number" name="wageramount" id="wageramount" class="form-control"/>
				<br>
				<button type="submit">Submit</button>
			</form>
    	</div>
    	</c:if>

    	<c:if test = "${canMoneyline}">
    	<div class="wagerForm">
			<h2>Bet the Money Line</h2>
			<form method="POST" action="/placebet">
				<input type="hidden" name="contestid" id="contestid" value="${contest.id}"/>
				<input type="hidden" name="type" id="type" value="moneyline"/>
				<label for="selection">Selection </label>
				<select name="selection" id="selection">
					<option disabled selected value="">-- make your pick --</option>
				 	<option value="favorite">${contest.favorite} (-${contest.favoriteline})</option>
				 	<option value="dog">${contest.dog} (+${contest.dogline})</option>
				</select>
				<br>
				<label for="wageramount">Wager ($) </label>
				<input type="number" name="wageramount" id="wageramount" class="form-control"/>
				<br>
				<button type="submit">Submit</button>
			</form>
    	</div>
    	</c:if>

    	<c:if test = "${canOverunder}">
    	<div class="wagerForm">
			<h2>Bet the Over/Under</h2>
			<form method="POST" action="/placebet">
				<input type="hidden" name="contestid" id="contestid" value="${contest.id}"/>
				<input type="hidden" name="type" id="type" value="overunder"/>
				<label for="selection">Selection </label>
				<select name="selection" id="selection">
					<option disabled selected value="">-- make your pick --</option>
				 	<option value="over">Over (&gt;${contest.overunder})</option>
				 	<option value="under">Under (&lt;${contest.overunder})</option>
				</select>
				<br>
				<label for="wageramount">Wager ($) </label>
				<input type="number" name="wageramount" id="wageramount" class="form-control"/>
				<br>
				<button type="submit">Submit</button>
			</form>
    	</div>
    	</c:if>
		
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
    	
    	<div class="tables">
			<h2>Spread Wagers</h2>
		    <table class="wager">
			    <tr>
					<th>Username</th>
					<th>Selection</th>
					<c:if test = "${!contest.resolved}">
					<th>Wager</th>
					</c:if>
					<c:if test = "${contest.resolved}">
					<th>Result</th>
					</c:if>
				</tr>
				<c:forEach items="${spreadWagers}" var="wager">
			    	<tr>
			    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
			    		<td>${wager.selection}</td>
			    		<td>${wager.amount}</td>
			    	</tr>
			    </c:forEach>
		    </table>
		    
			<h2>Money Line Wagers</h2>
		    <table class="wager">
			    <tr>
					<th>Username</th>
					<th>Selection</th>
					<th>Wager</th>
				</tr>
				<c:forEach items="${moneylineWagers}" var="wager">
			    	<tr>
			    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
			    		<td>${wager.selection}</td>
			    		<td>${wager.amount}</td>
			    	</tr>
			    </c:forEach>
		    </table>
		    
		    <h2>Over/Under Wagers</h2>
		    <table class="wager">
			    <tr>
					<th>Username</th>
					<th>Selection</th>
					<th>Wager</th>
				</tr>
				<c:forEach items="${overunderWagers}" var="wager">
			    	<tr>
			    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
			    		<td>${wager.selection}</td>
			    		<td>${wager.amount}</td>
			    	</tr>
			    </c:forEach>
		    </table>
	    </div>
    </div>
  </body>
</html>