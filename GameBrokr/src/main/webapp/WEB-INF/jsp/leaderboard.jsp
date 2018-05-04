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
	  <li><a href="/groups">Groups</a></li>
	  <li><a href="/leaderboard">Leaderboard</a></li>
	  <li style="float:right"><a href="/profile">Profile</a></li>
	</ul>
	
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<div class="container">
		<h2>Leaderboard</h2>
	    <table class="bettor">
		    <tr>
		    	<th>Rank</th>
				<th>Bettor</th>
				<th>Bank</th>
				<th>Record</th>
			</tr>
		    <c:forEach items="${allBettors}" var="bettor" varStatus="loop">
		    	<tr>
		    		<td>${loop.count}</td>
		    		<td><a href="../viewbettor?bettor_id=${bettor.id}">${bettor.username}</a></td>
		    		<td>\$${bettor.bank}</td>
		    		<td>${bettor.win} - ${bettor.loss}</td>
		    	</tr>
		    </c:forEach>
	    </table>
    </div>
  </body>
</html>