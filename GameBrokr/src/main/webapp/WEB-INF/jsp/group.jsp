<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
    <title>GameBrokr</title>
    <link rel="stylesheet" type="text/css" href="/stylesheets/style.css">
  </head>

  <body>
  	<!--  <ul>
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
			<h2>${group.name}</h2>
			<h4>${group.count} Members</h4>
		</div>
		<c:if test = "${!isMember}">
			<div class="wagerForm">
				<h2>Join this Group</h2>
				<form method="POST" action="/joingroup">
					<input type="hidden" name="groupid" id="groupid" value="${group.id}"/>
					<button type="submit">Join</button>
				</form>
	    	</div>
    	</c:if>
		
    	<div class="tables">
    		<h2>Member Leaderboard</h2>
		    <table class="bettor">
			    <tr>
			    	<th>Rank</th>
					<th>Bettor</th>
					<th>Bank</th>
					<th>Record</th>
				</tr>
				<c:forEach items="${memberships}" var="member" varStatus="loop">
					<tr>
			    		<td>${loop.count}</td>
			    		<td><a href="${member.link}">${member.username}</a></td>
			    		<td>\$${member.bank}</td>
			    		<td>${member.win}-${member.loss}</td>
			    	</tr>
				</c:forEach>
		    </table>
			<h2>Open Wagers</h2>
			<details>
				<summary>Spread</summary>
			    <table class="wager">
				    <tr>
				    	<th>Matchup</th>
						<th>Bettor</th>
						<th>Selection</th>
						<th>Wager</th>
					</tr>
					<c:forEach items="${openSpreadWagers}" var="wager">
				    	<tr>
				    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
				    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
				    		<td>${wager.selection}</td>
				    		<td>${wager.amount}</td>
				    	</tr>
				    </c:forEach>
			    </table>
		    </details>
		    <br>
		    <details>
		    	<summary>Moneyline</summary>
		    	<table class="wager">
				    <tr>
				    	<th>Matchup</th>
						<th>Bettor</th>
						<th>Selection</th>
						<th>Wager</th>
					</tr>
					<c:forEach items="${openMoneylineWagers}" var="wager">
				    	<tr>
				    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
				    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
				    		<td>${wager.selection}</td>
				    		<td>${wager.amount}</td>
				    	</tr>
				    </c:forEach>
			    </table>
		    </details>
		    <br>
		    <details>
		    	<summary>Over/Under</summary>
		    	<table class="wager">
				    <tr>
				    	<th>Matchup</th>
						<th>Bettor</th>
						<th>Selection</th>
						<th>Wager</th>
					</tr>
					<c:forEach items="${openOverunderWagers}" var="wager">
				    	<tr>
				    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
				    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
				    		<td>${wager.selection}</td>
				    		<td>${wager.amount}</td>
				    	</tr>
				    </c:forEach>
			    </table>
		    </details>
		    
		    <h2>Closed Wagers</h2>
			<details>
				<summary>Spread (Closed)</summary>
			    <table class="wager">
				    <tr>
				    	<th>Matchup</th>
						<th>Bettor</th>
						<th>Selection</th>
						<th>Result</th>
					</tr>
					<c:forEach items="${closedSpreadWagers}" var="wager">
				    	<tr>
				    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
				    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
				    		<td>${wager.selection}</td>
				    		<td class="${wager.winloss}">${wager.result}</td>
				    	</tr>
				    </c:forEach>
			    </table>
		    </details>
		    <br>
		    <details>
		    	<summary>Money Line (Closed)</summary>
		    	<table class="wager">
				    <tr>
				    	<th>Matchup</th>
						<th>Bettor</th>
						<th>Selection</th>
						<th>Result</th>
					</tr>
					<c:forEach items="${closedMoneylineWagers}" var="wager">
				    	<tr>
				    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
				    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
				    		<td>${wager.selection}</td>
				    		<td class="${wager.winloss}">${wager.result}</td>
				    	</tr>
				    </c:forEach>
			    </table>
		    </details>
		    <br>
		    <details>
		    	<summary>Over/Under (Closed)</summary>
		    	<table class="wager">
				    <tr>
				    	<th>Matchup</th>
						<th>Bettor</th>
						<th>Selection</th>
						<th>Result</th>
					</tr>
					<c:forEach items="${closedOverunderWagers}" var="wager">
				    	<tr>
				    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
				    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
				    		<td>${wager.selection}</td>
				    		<td class="${wager.winloss}">${wager.result}</td>
				    	</tr>
				    </c:forEach>
			    </table>
		    </details>
		</div>
    </div>
  </body>
</html>