package wikiBook2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBProvider {

	//singleton
	private DBProvider() {}
	
	private static DBProvider provider = null;
	private static Connection connection;
	private Statement statement;
	private final String CONN_QUERY= "jdbc:mysql://91.250.82.104:3306/wikihistory?user=wikipulse&password=COINcourse2014";
	
	private static String peopleQuery = "SELECT p.id, a.indegree, p.name ,p.year_from, p.year_to "
			+ "FROM wikihistory.people p "
			+ "INNER JOIN wikihistory.people_aux a "
			+ "ON p.id = a.id AND p.year_from < ? AND p.year_to > ? "
			+ "ORDER BY a.indegree DESC "
			+ "LIMIT ? ";
	private PreparedStatement peopleOfYear;
	
	private static String conFromQuery = "SELECT person_to "
			+ "FROM wikihistory.connections "
			+ "WHERE person_from = ? "
			+ "AND year_from < ? "
			+ "AND year_to > ?";
	private PreparedStatement connectionsFrom;
	
	private static String conOfYearQuery = "SELECT c.person_from, c.person_to "
			+ "FROM ("+peopleQuery+") p "
			+ "INNER JOIN wikihistory.connections c "
			+ "ON c.person_to = p.id AND c.year_from < ? AND c.year_to > ? ";
	private PreparedStatement conOfYear;

	public static DBProvider getInstance() {
		if (provider == null)
			provider = new DBProvider();
		return provider;
	}
	
	
	public Connection getConnection()
	{
		if(connection==null)		
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				DBProvider.connection = DriverManager.getConnection(CONN_QUERY);
				this.peopleOfYear = connection.prepareStatement(peopleQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_READ_ONLY);
				this.connectionsFrom = connection.prepareStatement(conFromQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_READ_ONLY);
				this.conOfYear = connection.prepareStatement(conOfYearQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_READ_ONLY);

			} catch (ClassNotFoundException e) {
				System.out.println("Class not found!");
			} catch (SQLException e) {
				System.out.println("SQL Exception! Connection failed");
			} catch (Exception ex) {
				System.out.println("Exception");
			}
		return connection;		
	}
	
	public void closeConnection()
	{
		try {
			if (connection!=null && !connection.isClosed())
			{
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Connection close Error");
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String query){
		ResultSet res=null;
		if(connection==null)getConnection();
		
		try {
			statement = connection.createStatement();
			res = statement.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Execute Query Error (executeQuery)");
			e.printStackTrace();
		}
		
		return res;
	}
	
	
	public ResultSet getPeople(int year, int maxNodes){
		ResultSet res=null;
		
		if(connection==null)getConnection();
		
		try {
			peopleOfYear.setInt(1, year);
			peopleOfYear.setInt(2, year);
			peopleOfYear.setFetchSize(Integer.MIN_VALUE);
			peopleOfYear.setInt(3, maxNodes);
			res = peopleOfYear.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL Error (getPeople)");
			e.printStackTrace();
		}
		
		return res;
		
	}
	

	public ResultSet getConnectionsFrom(String id, int year){
		ResultSet res=null;
		
		if(connection==null)getConnection();
		
		try {
			connectionsFrom.setString(1, id);
			connectionsFrom.setInt(2, year);
			connectionsFrom.setInt(3, year);
			res = connectionsFrom.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL Error (getConnectionsFrom)");
			e.printStackTrace();
		}
		
		return res;
		
	}
	
	public ResultSet getConnections(int year, int maxNodes){
		ResultSet res=null;
		
		if(connection==null)getConnection();
		
		try {
			conOfYear.setInt(1, year);
			conOfYear.setInt(2, year);
			conOfYear.setInt(3, maxNodes);
			conOfYear.setInt(4, year);
			conOfYear.setInt(5, year);
			res = conOfYear.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL Error (getConnections)");
			e.printStackTrace();
		}
		
		return res;
		
	}

	

}
