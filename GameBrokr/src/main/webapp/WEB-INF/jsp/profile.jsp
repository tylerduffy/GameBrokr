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
		<div class="userdetail">
			<h2>${bettor.firstname} ${bettor.lastname} (${bettor.username})</h2>
			<h4>Proprietary Record: ${bettor.win} - ${bettor.loss}</h4>
			<h4>Proprietary Bank: \$${bettor.bank}</h4>
			<details>
				<summary>Groups</summary>
				<c:forEach items="${groups}" var="group">
				<a href="${group.link}">${group.name}</a>
				</c:forEach>
			</details>
		</div>
		<h2>Open Proprietary Wagers (No Group)</h2>
	    <table class="wager">
		    <tr>
				<th>Matchup</th>
				<th>Type</th>
				<th>Selection</th>
				<th>Wager</th>
			</tr>
		    <c:forEach items="${openWagers}" var="wager">
		    	<tr>
		    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
		    		<td>${wager.type}</td>
		    		<td>${wager.selection}</td>
		    		<td>${wager.amount}</td>
		    	</tr>
		    </c:forEach>
	    </table>
	    <br>
	    <br>
	    <details>
	    	<summary>Closed Proprietary Wagers (No Group)</summary>
		    <table class="wager">
			    <tr>
					<th>Matchup</th>
					<th>Type</th>
					<th>Selection</th>
					<th>Result</th>
				</tr>
			    <c:forEach items="${closedWagers}" var="wager">
			    	<tr>
			    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
			    		<td>${wager.type}</td>
			    		<td>${wager.selection}</td>
			    		<td class="${wager.winloss}">${wager.result}</td>
			    	</tr>
			    </c:forEach>
		    </table>
		</details>
		<h2>Group Wagers</h2>
		<c:forEach items="${groups}" var="group">
	    	<details>
	    		<summary>${group.name}</summary>
	    		<h2>Open ${group.name} Wagers</h2>
	    		<table class="wager">
	    			<tr>
	    				<th>Matchup</th>
	    				<th>Type</th>
	    				<th>Selection</th>
	    				<th>Wager</th>
	    			</tr>
	    			<c:forEach items="${group.openwagers}" var="wager">
				    	<tr>
				    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
				    		<td>${wager.type}</td>
				    		<td>${wager.selection}</td>
				    		<td>${wager.amount}</td>
				    	</tr>
			    	</c:forEach>
	    		</table>
	    		<h2>Closed ${group.name} Wagers</h2>
	    		<table class="wager">
	    			<tr>
	    				<th>Matchup</th>
	    				<th>Type</th>
	    				<th>Selection</th>
	    				<th>Result</th>
	    			</tr>
	    			<c:forEach items="${group.closedwagers}" var="wager">
				    	<tr>
				    		<td><a href="${wager.matchupLink}">${wager.matchup}</a></td>
				    		<td>${wager.type}</td>
				    		<td>${wager.selection}</td>
				    		<td class="${wager.winloss}">${wager.result}</td>
				    	</tr>
			    	</c:forEach>
	    		</table>
	    	</details>
	    	<br>
	    </c:forEach>
    </div>
  </body>
</html>