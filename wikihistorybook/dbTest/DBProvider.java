package dbTest;

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
	
	private static String connectionsQuery = "SELECT person_to, person_from FROM wikihistory.connections USE INDEX (year_from,year_to) WHERE year_from < ? AND year_to > ?";
	private PreparedStatement connectionsOfYear;
	
	private static String peopleQuery = "SELECT id, name FROM wikihistory.people WHERE year_from < ? AND year_to > ?";
	private PreparedStatement peopleOfYear;
	
	private static String conFromQuery = "SELECT person_to FROM wikihistory.connections USE INDEX (person_from) WHERE person_from = ? AND year_from < ? AND year_to > ?";
	private PreparedStatement connectionsFrom;
	
	private static String peopleQuery2 = "SELECT person_to FROM wikihistory.connections USE INDEX (person_from) WHERE person_from = ? AND year_from < ? AND year_to > ?";
	private PreparedStatement peopleOfYear2;
	

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
				this.connectionsOfYear = connection.prepareStatement(connectionsQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				this.peopleOfYear = connection.prepareStatement(peopleQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				this.connectionsFrom = connection.prepareStatement(conFromQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				this.peopleOfYear2 = connection.prepareStatement(peopleQuery2,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

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

	/**
	 * F?hrt mysql select query aus
	 * 
	 * @param query
	 *            String Query, die ausgef?hrt werden soll
	 * @return ResultSet oder null
	 */
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
	
	
	// gibt alle Connections zu einem bestimmten Jahr zur��ck
	public ResultSet getConnections(int year){
		ResultSet res=null;
		
		if(connection==null)getConnection();
		
		try {
			connectionsOfYear.setInt(1, year);
			connectionsOfYear.setInt(2, year);
			//connectionsOfYear.setFetchSize(Integer.MIN_VALUE);
			res = connectionsOfYear.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL Error (getConnections)");
			e.printStackTrace();
		}
		
		return res;
		
	}
	
	public ResultSet getPeople(int year){
		ResultSet res=null;
		
		if(connection==null)getConnection();
		
		try {
			peopleOfYear.setInt(1, year);
			peopleOfYear.setInt(2, year);
			peopleOfYear.setFetchSize(Integer.MIN_VALUE);
			res = peopleOfYear.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL Error (getPeople)");
			e.printStackTrace();
		}
		
		return res;
		
	}
	
	public ResultSet getPeople2(int year){
		ResultSet res=null;
		
		if(connection==null)getConnection();
		
		try {
			peopleOfYear2.setInt(1, year);
			peopleOfYear2.setInt(2, year);
			peopleOfYear2.setFetchSize(Integer.MIN_VALUE);
			res = peopleOfYear2.executeQuery();
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
	

}
