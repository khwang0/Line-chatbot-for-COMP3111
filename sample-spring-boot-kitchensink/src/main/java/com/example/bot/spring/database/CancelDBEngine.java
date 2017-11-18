package com.example.bot.spring.database;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import com.example.bot.spring.database.*;


public class CancelDBEngine extends DBEngine {
	private ConfirmDBEngine CDB;
	CancelDBEngine(){
		CDB=new ConfirmDBEngine();
	}
	public List<String> getAllUnconfirmedTours(){
		return CDB.getAllUnconfirmedTours(false);
	}
	public Set<String> getAllContactors(String booktableid){
		return CDB.getAllContactors(booktableid);
	}
	public void updateCanceledTours(String booktableid){	
		PreparedStatement stmt;	
		Connection connection;
		String statement = "UPDATE TABLE booking_table "
				+ "SET status = canceled "
				+ "WHERE bootableid = ?";
		try {
			connection=getConnection();
			stmt = connection.prepareStatement(statement);			
			stmt.setString(1,booktableid);
			ResultSet rs = stmt.executeUpdate();
			stmt.close();
			rs.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
