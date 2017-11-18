package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;


public class CancelDBEngine extends DBEngine {
	private ConfirmDBEngine CDB;
	public CancelDBEngine(){
		CDB=new ConfirmDBEngine();
	}
	public List<String> getAllUnconfirmedTours(){
		return CDB.getAllUnconfirmedTours(false);
	}
	public Set<String> getAllContactors(String booktableid){
		return CDB.getAllContactors(booktableid);
	}
	public void updateCanceledTours(String booktableid) throws Exception{	
		PreparedStatement stmt;	
		Connection connection;
		String statement = "UPDATE TABLE booking_table "
				+ "SET status = canceled "
				+ "WHERE bootableid = ?";
		try {
			connection=getConnection();
			stmt = connection.prepareStatement(statement);			
			stmt.setString(1,booktableid);
			stmt.executeUpdate();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
