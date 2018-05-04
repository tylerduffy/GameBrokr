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
		<div class="newContestForm">
			<h2>
			  Create a New Group
			</h2>
			<form method="POST" action="/newgroup">
			  <div>
			    <label for="name">Name </label>
			    <input type="text" name="name" id="name" class="form-control" required />
			  </div>
			  <br>
			  <button type="submit"> Add </button>
			</form>
	    </div>
	    <div class="tables">
			<h2>Groups</h2>
		    <table class="bettor">
			    <tr>
			    	<th>Group Name</th>
					<th>Owner</th>
					<th>Count</th>
				</tr>
			    <c:forEach items="${allGroups}" var="group">
			    	<tr>
			    		<td><a href="${group.link}">${group.name}</a></td>
			    		<td>${group.owner}</td>
			    		<td>${group.count}</td>
			    	</tr>
			    </c:forEach>
		    </table>
		</div>
    </div>
  </body>
</html>