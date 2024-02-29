package gamelogic;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ContactProgram {
	
	public ContactProgram() {
	};

	public void select() {
		String jdbcURL = "jdbc:postgresql://db.fe.up.pt/meec1a0502";
		String user = "meec1a0502";
		String pass = "Sporting";
		try {
			Connection connection = DriverManager.getConnection(jdbcURL, user, pass);
			System.out.println("CONNECTED TO POSTGRESQL");
			
			
			String sq1 = "SELECT * FROM client";
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sq1);
			
			while (result.next()) {
				int id = result.getInt("client_id");
				String username = result.getString("client_username");
				String password = result.getString("client_password");
				int wins = result.getInt("client_wins");
				int losses = result.getInt("client_losses");
				int ties = result.getInt("client_ties");
				System.out.printf("%d %s %s %d %d %d\n", id, username, password, wins, losses, ties);
			}
			
			
			connection.close();
		} catch (SQLException e) {
			System.out.println("ERROR IN CONNECTING TO POSTGRESQL");
			e.printStackTrace();
		}

	}

	public boolean loginusername(String input) {
		boolean r=false;
		String jdbcURL = "jdbc:postgresql://db.fe.up.pt/meec1a0502";
		String user = "meec1a0502";
		String pass = "Sporting";
		try {
			Connection connection = DriverManager.getConnection(jdbcURL, user, pass);
			System.out.println("CONNECTED TO POSTGRESQL");
			
			
			String sq1 = "SELECT * FROM client";
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sq1);
			
			while (result.next()) {
				//int id = result.getInt("client_id");
				String username = result.getString("client_username");
				//String password = result.getString("client_password");
				//int wins = result.getInt("client_wins");
				//int losses = result.getInt("client_losses");
				//int ties = result.getInt("client_ties");
				if(username.equals(input)) {
					r = true;
				}
			}
			
			connection.close();
		} catch (SQLException e) {
			System.out.println("ERROR IN CONNECTING TO POSTGRESQL");
			e.printStackTrace();
		}

		return r;
	}

	public boolean loginpassword(String inuser, String inpass) {
		boolean r=false;
		String jdbcURL = "jdbc:postgresql://db.fe.up.pt/meec1a0502";
		String user = "meec1a0502";
		String pass = "Sporting";
		try {
			Connection connection = DriverManager.getConnection(jdbcURL, user, pass);
			System.out.println("CONNECTED TO POSTGRESQL");
			
			
			String sq1 = "SELECT * FROM client";
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sq1);
			
			while (result.next()) {
				//int id = result.getInt("client_id");
				String username = result.getString("client_username");
				String password = result.getString("client_password");
				//int wins = result.getInt("client_wins");
				//int losses = result.getInt("client_losses");
				//int ties = result.getInt("client_ties");
				if(password.equals(inpass) && username.equals(inuser)) {
					r = true;
				}
			}
			
			connection.close();
		} catch (SQLException e) {
			System.out.println("ERROR IN CONNECTING TO POSTGRESQL");
			e.printStackTrace();
		}

		return r;
	}

	public boolean create(String inuser, String inpass) {
		boolean r=false;
		String jdbcURL = "jdbc:postgresql://db.fe.up.pt/meec1a0502";
		String user = "meec1a0502";
		String pass = "Sporting";
		try {
			Connection connection = DriverManager.getConnection(jdbcURL, user, pass);
			System.out.println("CONNECTED TO POSTGRESQL");
			
			
			String sq1 = "INSERT INTO client (client_username, client_password, client_wins, client_losses, client_ties) VALUES (?, ?, 0, 0, 0)";
			
			PreparedStatement statement = connection.prepareStatement(sq1);
			
			statement.setString(1, inuser);
			statement.setString(2, inpass);
			
			int rows = statement.executeUpdate();
			
			if(rows > 0) {
				r = true;
				System.out.println("Inserted new account into database");
			}
			
			connection.close();
		} catch (SQLException e) {
			System.out.println("ERROR IN CONNECTING TO POSTGRESQL");
			e.printStackTrace();
		}

		return r;
	}
	
	public void get(data data) {
		String jdbcURL = "jdbc:postgresql://db.fe.up.pt/meec1a0502";
		String user = "meec1a0502";
		String pass = "Sporting";
		try {
			Connection connection = DriverManager.getConnection(jdbcURL, user, pass);
			System.out.println("CONNECTED TO POSTGRESQL");
			
			
			String sq1 = "SELECT * FROM client";
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sq1);
			
			while (result.next()) {
				//int id = result.getInt("client_id");
				String username = result.getString("client_username");
				//String password = result.getString("client_password");
				int wins = result.getInt("client_wins");
				int losses = result.getInt("client_losses");
				int ties = result.getInt("client_ties");
				if(username.equals(data.username)) {
					data.wins = wins;
					data.ties = ties;
					data.losses = losses;
					data.getData();
				}
			}

			
			connection.close();
		} catch (SQLException e) {
			System.out.println("ERROR IN CONNECTING TO POSTGRESQL");
			e.printStackTrace();
		}

	}
	
	public void set(int wins, int ties, int losses, String username) {
		String jdbcURL = "jdbc:postgresql://db.fe.up.pt/meec1a0502";
		String user = "meec1a0502";
		String pass = "Sporting";
		try {
			Connection connection = DriverManager.getConnection(jdbcURL, user, pass);
			System.out.println("CONNECTED TO POSTGRESQL");
			
			
			String sq1 = "UPDATE client SET client_wins = ?, client_ties = ?, client_losses = ? WHERE client_username = ?";
			PreparedStatement statement = connection.prepareStatement(sq1);
			

			statement.setInt(1, wins);
			statement.setInt(2, ties);
			statement.setInt(3, losses);
			statement.setString(4, username);
			
			int rows = statement.executeUpdate();	
			if(rows > 0) {
				System.out.println("Updated!");
			}
		
			connection.close();
		} catch (SQLException e) {
			System.out.println("ERROR IN CONNECTING TO POSTGRESQL");
			e.printStackTrace();
		}

	}
}