<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.io.*" import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<h1>Math Questions:</h1>


	<!-- the servlet AddSerlet searches or adds to the database -->
	<form method="post" action="AddServlet">
	
	
	<!-- to search for a keyword -->
	<h2>Search For a Keyword: <input type="text" name="keyword">
	<input type="submit" value="Submit"> <br> </h2>
	

	
	
	<div style="border:1px solid black; padding: 5px 3px 5px 8px;">
	
	<!--  to add a quesion -->
	<h2>Add a Question: <input type="text" name="question"></h2>
	
	<h3>Enter Keyword(s):</h3>
	<!-- Make a list of text boxes (might only be one)  -->
	<ul id="boxlist" style="list-style-type:none;padding-left:0;">
		<li>
			<br>
			
			<input type="text" name="link1">
			<!-- If the user clicks add URl button then I call a function to make a new text box and add it to the list -->
			<button onclick="new_text_box()" type="button">Add Keyword</button>
		</li>
	
	
	
	</ul>

	<!-- used same script from project 1 if user wants to enter multiple keywords-->
	<script type="text/javascript"> 
		// counter is used to keep track of all text box names 
		var linkCounter = 2;
	
		// This is the function to make the new text box 
		function new_text_box(){
			// Creating a new text box for list
			var node = document.createElement("li");
			var text_box = document.createElement("input");
			text_box.setAttribute('type','text');
			
			//Naming text box "link" plus the number of the counter to keep track of them for the servlet
			// Example: if counter was 2 then the name of the text box would be "link2" 
			text_box.setAttribute('name','link' + linkCounter++);
			
			
			node.appendChild(text_box);
		
			// Getting the list element and then inserting new text box 
			var list = document.getElementById("boxlist");
			list.insertBefore(node, list.childNodes[0]);
			
			// Used to make the view look cleaner 
			var br = document.createElement('br');
			list.insertBefore(br,list.childNodes[0]);
			
			
			
		}	
	
		</script>
		
	<!-- Submit Button -->	
	<input type="submit" value="Submit">
	
	</div>
	
	</form>
	
	<h2>All the Math Questions: </h2>
<%
	// opening a connection to the database 
	Connection connection;
	Statement st;
	ResultSet rs;
	
	try {
		Class.forName("com.mysql.jdbc.Driver");
		
		connection = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/project2db", "root", "");
	
	
		// selecting all questions from mathquestions table in database
		st = connection.createStatement();
		rs = st.executeQuery("SELECT question FROM mathquestions");	
		
		

	} // for any exception throw an UnavailableException to
		// indicate that the servlet is not currently available
	catch (Exception exception) {
		out.println("<title>Error</title>");
		exception.printStackTrace();
		throw new UnavailableException(exception.getMessage());
	}

	try { // just printing all the questions
		while(rs.next()){		
			out.print("<h3>" + rs.getString(1) + "</h3>");			
		}
	}
	catch (SQLException sqlException){
		sqlException.printStackTrace();
		out.println("<title>Error</title>");
		out.println("</head>");
		out.println("<body><p>Database error occurred. ");
		out.println("Try again later.</p></body></html>");
		out.close();
	}
	
	rs.close();
	st.close();
	connection.close();

%>
	





</body>
</html>