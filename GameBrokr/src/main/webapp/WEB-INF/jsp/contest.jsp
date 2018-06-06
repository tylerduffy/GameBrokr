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
			
			<h4>${contest.datestr}</h4>
		</div>
		<c:if test = "${!contest.resolved && open}">
			<c:if test = "${canSpread}">
				<div class="wagerForm">
					<h2>Bet the Spread</h2>
					<form method="POST" action="/placebet">
						<input type="hidden" name="contestid" id="contestid" value="${contest.id}"/>
						<input type="hidden" name="type" id="type" value="spread"/>
						<input type="hidden" name="sport" id="sport" value="${contest.sport}"/>
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
						<label for="group">Group </label>
						<select name="group" id="group" class="form-control">
							<option value="0">Open (Default)</option>
							<c:forEach items="${groups}" var="group">
							<option value="${group.id}">${group.name}</option>
							</c:forEach>
						</select>
						<br>
						<button type="submit">Submit</button>
					</form>
		    	</div>
	    	</c:if>
	
	    	<c:if test = "${canMoneyline}">
		    	<div class="wagerForm">
					<h2>Bet the Moneyline</h2>
					<form method="POST" action="/placebet">
						<input type="hidden" name="contestid" id="contestid" value="${contest.id}"/>
						<input type="hidden" name="type" id="type" value="moneyline"/>
						<input type="hidden" name="sport" id="sport" value="${contest.sport}"/>
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
						<label for="group">Group </label>
						<select name="group" id="group" class="form-control">
							<option value="0">Open (Default)</option>
							<c:forEach items="${groups}" var="group">
							<option value="${group.id}">${group.name}</option>
							</c:forEach>
						</select>
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
						<input type="hidden" name="sport" id="sport" value="${contest.sport}"/>
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
						<label for="group">Group </label>
						<select name="group" id="group" class="form-control">
							<option value="0">Open (Default)</option>
							<c:forEach items="${groups}" var="group">
							<option value="${group.id}">${group.name}</option>
							</c:forEach>
						</select>
						<br>
						<button type="submit">Submit</button>
					</form>
		    	</div>
	    	</c:if>
    	</c:if>
		
		<c:if test="${!contest.resolved && isAdmin && !open}">
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
    		<c:if test="${!((spreadfavoritepercent + spreaddogpercent) > 0)}">
    			<h2>Spread Wagers</h2>
    		</c:if>
    		<c:if test="${(spreadfavoritepercent + spreaddogpercent) > 0}">
    			<div class="w3-container" style="width:90%;display:inline-block;clear:right;">
			    	<span style="text-align:left;width:40%;display:inline-block;">${contest.favorite}</span>
			    	<span style="text-align:center;width:18%;display:inline-block;font-size:1.8em;font-weight:bold;">Spread</span>
			    	<span style="text-align:right;width:40%;display:inline-block;">${contest.dog}</span>
			    	<span style="text-align:left;width:49%;display:inline-block;">${spreadfavoritepercent}%</span>
			    	<span style="text-align:right;width:49%;display:inline-block;">${spreaddogpercent}%</span>
				    <div class="w3-border" style="width:50%;display:inline-block;">
					  <div class="w3-game-gray" style="height:24px;width:${spreadfavoritepercent}%;float:right;"></div>
					</div>
					<div class="w3-border" style="width:50%;display:inline-block;float:right;">
					  <div class="w3-rose" style="height:24px;width:${spreaddogpercent}%;white-space:nowrap;overflow:visible;"></div>
					</div>
			    </div>
    		</c:if>
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
			    		<td <c:if test = "${contest.resolved}">class="${wager.winloss}"</c:if>>${wager.amount}</td>
			    	</tr>
			    </c:forEach>
		    </table>
		    
		    <c:if test="${!((moneylinefavoritepercent + moneylinedogpercent) > 0)}">
		    	<h2>Money Line Wagers</h2>
		    </c:if>
		    <c:if test="${(moneylinefavoritepercent + moneylinedogpercent) > 0}">
		    	<br><br>
				<div class="w3-container" style="width:90%;display:inline-block;clear:right;">
			    	<span style="text-align:left;width:40%;display:inline-block;">${contest.favorite}</span>
			    	<span style="text-align:center;width:18%;display:inline-block;font-size:1.8em;font-weight:bold;">Moneyline</span>
			    	<span style="text-align:right;width:40%;display:inline-block;">${contest.dog}</span>
			    	<span style="text-align:left;width:49%;display:inline-block;">${moneylinefavoritepercent}%</span>
			    	<span style="text-align:right;width:49%;display:inline-block;">${moneylinedogpercent}%</span>
				    <div class="w3-border" style="width:50%;display:inline-block;">
					  <div class="w3-game-gray" style="height:24px;width:${moneylinefavoritepercent}%;float:right;"></div>
					</div>
					<div class="w3-border" style="width:50%;display:inline-block;float:right;">
					  <div class="w3-rose" style="height:24px;width:${moneylinedogpercent}%;white-space:nowrap;overflow:visible;"></div>
					</div>
			    </div>
		    </c:if>
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
			    		<td <c:if test = "${contest.resolved}">class="${wager.winloss}"</c:if>>${wager.amount}</td>
			    	</tr>
			    </c:forEach>
		    </table>
		    
		    <c:if test="${!((overpercent + underpercent) > 0)}">
		    	<h2>Over/Under Wagers</h2>
		    </c:if>
		    <c:if test="${(overpercent + underpercent) > 0}">
			    <br><br>
			    <div class="w3-container" style="width:90%;display:inline-block;clear:right;">
			    	<span style="text-align:left;width:40%;display:inline-block;">Over</span>
			    	<span style="text-align:center;width:18%;display:inline-block;font-size:1.8em;font-weight:bold;">Over/Under</span>
			    	<span style="text-align:right;width:40%;display:inline-block;">Under</span>
			    	<span style="text-align:left;width:49%;display:inline-block;">${overpercent}%</span>
			    	<span style="text-align:right;width:49%;display:inline-block;">${underpercent}%</span>
				    <div class="w3-border" style="width:50%;display:inline-block;">
					  <div class="w3-game-gray" style="height:24px;width:${overpercent}%;float:right;"></div>
					</div>
					<div class="w3-border" style="width:50%;display:inline-block;float:right;">
					  <div class="w3-rose" style="height:24px;width:${underpercent}%;white-space:nowrap;overflow:visible;"></div>
					</div>
			    </div>
		    </c:if>
		    
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
			    		<td <c:if test = "${contest.resolved}">class="${wager.winloss}"</c:if>>${wager.amount}</td>
			    	</tr>
			    </c:forEach>
		    </table>
	    </div>
    </div>
  </body>
</html>