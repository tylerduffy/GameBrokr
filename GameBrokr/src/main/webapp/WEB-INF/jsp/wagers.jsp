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
		<h1>Wagers</h1>
		<h2>Spread</h2>
	    <table class="contest">
		    <tr>
				<th>Matchup</th>
				<th>Bettor</th>
				<th>Pick</th>
				<th>Wager</th>
			</tr>
		    <c:forEach items="${spreadWagers}" var="wager">
		    	<tr>
		    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
		    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
		    		<td>${wager.selection}</td>
		    		<td>${wager.amount}</td>
		    	</tr>
		    </c:forEach>
	    </table>
	    <h2>Money Line</h2>
	    <table class="contest">
		    <tr>
				<th>Matchup</th>
				<th>Bettor</th>
				<th>Pick</th>
				<th>Wager</th>
			</tr>
		    <c:forEach items="${moneylineWagers}" var="wager">
		    	<tr>
		    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
		    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
		    		<td>${wager.selection}</td>
		    		<td>${wager.amount}</td>
		    	</tr>
		    </c:forEach>
	    </table>
	    <h2>Over/Under</h2>
	    <table class="contest">
		    <tr>
				<th>Matchup</th>
				<th>Bettor</th>
				<th>Pick</th>
				<th>Wager</th>
			</tr>
		    <c:forEach items="${overunderWagers}" var="wager">
		    	<tr>
		    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
		    		<td><a href="${wager.bettor}">${wager.bettorName}</a></td>
		    		<td>${wager.selection}</td>
		    		<td>${wager.amount}</td>
		    	</tr>
		    </c:forEach>
	    </table>
    </div>
  </body>
</html>