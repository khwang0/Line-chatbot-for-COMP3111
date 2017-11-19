package com.example.bot.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.bot.spring.database.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DBEngineTester.class, DBEngine.class })
public class DBEngineTester {
	@Autowired
	private DBEngine DBE;
	@Autowired	
	private DoubleElevDBEngine DEDBE;
	@Autowired
	private ConfirmDBEngine CDBE; 
	
	
	@Test
	public void getTextTypeTester() throws Exception {
		int textSize = 6;
		
		String[] input = {
				"how to apply?",
				"what if the tour is cancelled?",
				"can i book this trip?",
				"can you recommend some trip to shenzhen",
				"i d like to book this one",
				"can you recommend a book to me"
		};		
		String[] expectedLabel = {"gq", "gq", "book", "reco", "book", "reco"};		
		String[] type = new String[textSize];
		
		for (int i = 0; i < textSize; i++) {
			System.err.println(" input [" + i + "]: " + input[i]);
			type[i] = DBE.getTextType(input[i]);
			System.err.println(" type [" + i + "]: " + type[i]);
			System.err.println();
		}
		
		for (int i = 0; i < textSize; i++) {
			assertThat(expectedLabel[i].equals(type[i])).isEqualTo(true);
		}
		
	}

	public void doubleDBETester() throws Exception() {
		initTestTour();
		
		String discount_id = DEDBE.getDiscountBookid();
		String testing_id = TEST_BOOT_ID;
		boolean tour_full = DEDBE.ifTourFull(testing_id);		
		Set<String> all_clients = DEDBE.getAllClient();
	}

	public void confirmDBETester() throws Exception() {
		initTestTour();
		
		List<String> discount_id = CDBE.getAllUnconfirmedTours();
		String testing_id = TEST_BOOT_ID;	
		Set<String> all_unpaid_clients = CDBE.getAllContactors(testing_id, false);
		Set<String> all_paid_clients = CDBE.getAllContactors(testing_id, true);
	}
	
	private void initTestTour() {
		PreparedStatement nstmt = null;
		
		openConnection();
		
		String statement = "DELETE FROM double11 WHERE bootableid = ? ";
		try {
			nstmt = connection.prepareStatement(statement);
			nstmt.setString(1, TEST_BOOT_ID);
			ResultSet rs = nstmt.executeUpdate();
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		statement = "INSERT INTO double11 VALUES (?, 2, 2, 'sent')";
		try {
			nstmt = connection.prepareStatement(statement);
			nstmt.setString(1, TEST_BOOT_ID);
			ResultSet rs = nstmt.executeUpdate();
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		close();		
		return discount_tours;		
	}
	
	private static final String TEST_BOOT_ID = "199000";
}


