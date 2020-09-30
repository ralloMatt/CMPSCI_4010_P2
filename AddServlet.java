package com.javux.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddServlet")
public class AddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	//used if person wanted to add a question
	private PreparedStatement updateQuestions;
	private PreparedStatement updateKeywords;
	private PreparedStatement updateMappings;
	
	//used if person wanted to search for a keyword
	private PreparedStatement SearchKey;
	private PreparedStatement SearchMap;
	private PreparedStatement SearchMath;

	
	// process survey response

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// set up response to client
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String key = request.getParameter("keyword");
		
		if (key.length() == 0){ // no keyword to search for
			out.println("No Key Entered to Search! <br>");
			out.println("\n");
		}
		else{ // search for the key word
			out.println("Questions that contain keyword: " + key + "<br>");


			try {
				// open connection
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/project2db", "root", "");

				
				
			
				
				// find id associated with keyword using keywords table
				
			
				SearchKey = connection.prepareStatement("SELECT * FROM keywords "
						+ "WHERE Keyword = ?");
				SearchKey.setString(1, key);
				ResultSet rs = SearchKey.executeQuery();
				
				// create array list in case of duplicate keywords entered
				List<Integer> key_id = new ArrayList<Integer>();
				int id = 0;
				
				// loop through query and add keyword id to array list
				while(rs.next()){
					id = rs.getInt("ID");
					key_id.add(id);
				}
					
				// find question id associated with keyword id using mappings table
				SearchMap = connection.prepareStatement("SELECT * FROM mappings "
						+ "WHERE keyword_ID = ?");
				
				
				// create an array list in case of multiple questions
				List<Integer> math_id = new ArrayList<Integer>();
				id = 0;
				
				// loop through key_id array list and search for that id in mappings table
				int i = 0;
				for(i=0; i<key_id.size(); i++){
					SearchMap.setInt(1, key_id.get(i));
					rs = SearchMap.executeQuery();
					
					while(rs.next()){ // if found a match add it to the math_id array list
						id = rs.getInt("questions_ID");
						math_id.add(id);
					}
					
				
				}
				// search through mathquestions database for the id found in the math_id array list 
				SearchMath = connection.prepareStatement("SELECT * FROM mathquestions "
						+ "WHERE ID = ?");
				
				
		
				String q = null;
				
				
				i = 0;
				for(i=0; i<math_id.size(); i++){
					SearchMath.setInt(1, math_id.get(i));
					rs = SearchMath.executeQuery();
					
					// find all matching id's then get string (question)
					while(rs.next()){
						q = rs.getString("question");
						//display the question
						out.println("<h2>"+ q + "</h2>");
					}
				}
				
				
				
				out.close();
			} // if database exception occurs, return error page
			catch (Exception exception) {
				exception.printStackTrace();
				throw new UnavailableException(exception.getMessage());
			}
			
			
		}
		
		
		
		// mq is the math question entered by user
		String mq = request.getParameter("question");
		if(mq.length() == 0) {
			out.println("No question Entered!!! <br>");
		}
		else { // someone entered a queston so add to database
		try {
		
			//enable connection
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/project2db", "root", "");

			
			
			// put new question in table
			updateQuestions = connection.prepareStatement("INSERT INTO mathquestions"
					+ " (question) VALUES (?)");	
		
			updateQuestions.setString(1, mq);
			updateQuestions.executeUpdate();
			
			// find id associated with new question (for mapping table)
			Statement st = connection.createStatement();
			String sql = "SELECT * FROM mathquestions ORDER BY id DESC LIMIT 1;";
			ResultSet rs = st.executeQuery(sql);
			
			int id = 0;
			
			if(rs.next()){
				id = rs.getInt("ID");
			}
			
			
			
			// add keywords into other table
			
			updateKeywords = connection.prepareStatement("INSERT INTO keywords"
					+ " (Keyword) VALUES (?)");			
			
			
			//update mappings table
			updateMappings = connection.prepareStatement("INSERT INTO mappings"
					+ " (keyword_ID, questions_ID) VALUES (?, ?)");	
			
			int counter = 1;
			
			//get first keyword entered by user
			String keyword = request.getParameter("link" + counter);
			
			while(keyword != null || counter == 8 ){
				
				//insert a keyword into table
				updateKeywords.setString(1, keyword);
				updateKeywords.executeUpdate();
				
				// find Id associated with keyword
				Statement st2 = connection.createStatement();
				String sql2 = "SELECT * FROM keywords ORDER BY id DESC LIMIT 1;";
				ResultSet rs2 = st2.executeQuery(sql2);
				
				int id2 = 0;
				
				if(rs2.next()){
					id2 = rs2.getInt("ID");
				}
				
				// once we have the keyword id and the math questio id we can add it to the mappings table
				updateMappings.setInt(1, id2);
				updateMappings.setInt(2, id);
				
				
				
				updateMappings.executeUpdate();
				
				
				// out.println("keyword is : " + keyword + "\n");
				counter++;
				keyword = request.getParameter("link" + counter);
			}
			
			
			
			
	
		
			// update total for current survey response

			out.println("Data Successfully Added to Database!");
			
			out.close();
		} // if database exception occurs, return error page
		catch (Exception exception) {
			exception.printStackTrace();
			throw new UnavailableException(exception.getMessage());
		}
		} // ends else
		
			
	}// ends doPost

	
	
	// close SQL statements and database when servlet terminates

	public void destroy() {
		// attempt to close statements and database connection
		try {
		
			updateQuestions.close();
			connection.close();
		} // handle database exceptions by returning error to client
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	} // end of destroy method

}
