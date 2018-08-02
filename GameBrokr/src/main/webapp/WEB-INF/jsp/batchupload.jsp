<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
    <title>GameBrokr</title>
    <link rel="stylesheet" type="text/css" href="/stylesheets/style.css">
  </head>

  <body>
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
		<h2>Bulk Upload</h2>	    
	    <div hidden="true">
	    	<fieldset id="template">
	    		<input name="favorite">
	    		<input name="dog[]">
	 			<input size="8" name="spread[]">
	 			<input size="10" name="favoriteline[]">
	 			<input size="10" name="dogline[]">
	 			<input size="10" name="overunder[]">
	 			<input type="date" name="date[]">
	 			<input type="time" name="time[]">
	 			<input name="sport[]">
	 			<select>
	 			  <option disabled hidden selected value=""></option>
				  <option value="nba">NBA</option>
				  <option value="nfl">NFL</option>
				  <option value="mlb">MLB</option>
				  <option value="nhl">NHL</option>
				  <option value="ncaab">NCAAB</option>
				  <option value="ncaaf">NCAAF</option>
				</select>
	    	</fieldset>
	    </div>
	    <br>
	    <form id="ID" method="POST" action="/newcontests">
	    	<fieldset id="form_section1">
	    		<legend>Bulk Contest Create</legend>
	    		<div style="display:inline-block;">
	    		<label style="display: block">Favorite</label><input name="favorite">
	    		</div>
	    		<div style="display:inline-block;">
	  			<label style="display: block;">Dog</label><input name="dog[]">
	 			</div>
	 			<div style="display:inline-block;">
	 			<label style="display: block">Spread</label><input size="8" name="spread[]">
	 			</div>
	 			<div style="display:inline-block;">
	 			<label style="display: block">Line (-)</label><input size="10" name="favoriteline[]">
	 			</div>
	 			<div style="display:inline-block;">
	 			<label style="display: block">Line (+)</label><input size="10" name="dogline[]">
	 			</div>
	 			<div style="display:inline-block;">
	 			<label style="display: block">Over/Under</label><input size="10" name="overunder[]">
	 			</div>
	 			<div style="display:inline-block;">
	 			<label style="display: block">Date</label><input type="date" name="date[]">
	 			</div>
	 			<div style="display:inline-block;">
	 			<label style="display: block">Time</label><input type="time" name="time[]">
	 			</div>
	 			<div style="display:inline-block;">
	 			<label style="display: block">Sport</label><input name="sport[]">
	 			</div>
	 			<div style="display:inline-block;">
	 			<label style="display: block">Sport</label>
	 			<select>
	 			  <option disabled hidden selected value=""></option>
				  <option value="nba">NBA</option>
				  <option value="nfl">NFL</option>
				  <option value="mlb">MLB</option>
				  <option value="nhl">NHL</option>
				  <option value="ncaab">NCAAB</option>
				  <option value="ncaaf">NCAAF</option>
				</select>
	 			</div>
	    	</fieldset>
		</form>
		<button onclick="add()">[ + ] Add</button>
		<button onclick="remove()">[ - ] Delete</button>
		<input type="submit" form="ID"/>
    </div>
    <script type="text/javascript">
		var add = function() {
		  var cln = document.getElementById("template").cloneNode(true);
		  document.getElementById("ID").appendChild(cln);
		  var childCount = document.getElementById("ID").childElementCount;
		  cln.setAttribute("id", "form_section" + childCount);
		};

		var remove = function() {
			var select = document.getElementById("ID");
			var childCount = select.childElementCount;
			if (childCount > 1) {
				select.removeChild(select.lastChild);
			}
		};
	</script>
  </body>
</html>