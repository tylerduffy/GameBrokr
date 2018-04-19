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
		<div class="userdetail">
			<h2>${bettor.firstname} ${bettor.lastname} (${bettor.username})</h2>
			<h4>Record: ${bettor.win} - ${bettor.loss}</h4>
			<h4>Bank: \$${bettor.bank}</h4>
		</div>
		<h2>Open Wagers</h2>
	    <table class="wager">
		    <tr>
				<th>Matchup</th>
				<th>Selection</th>
				<th>Wager</th>
			</tr>
		    <c:forEach items="${openWagers}" var="wager">
		    	<tr>
		    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
		    		<td>${wager.selection}</td>
		    		<td>${wager.amount}</td>
		    	</tr>
		    </c:forEach>
	    </table>
	    <h2>Closed Wagers</h2>
	    <table class="wager">
		    <tr>
				<th>Matchup</th>
				<th>Selection</th>
				<th>Result</th>
			</tr>
		    <c:forEach items="${closedWagers}" var="wager">
		    	<tr>
		    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
		    		<td>${wager.selection}</td>
		    		<td>${wager.result}</td>
		    	</tr>
		    </c:forEach>
	    </table>
    </div>
  </body>
</html>