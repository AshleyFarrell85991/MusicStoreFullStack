package cs636.music.dao;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import cs636.music.domain.Product;
@RunWith(SpringRunner.class)
//Needed to handle DataSource config
@JdbcTest
//to be minimalistic, configure only the needed beans
@ContextConfiguration(classes= {CatalogDbDAO.class, DbUtils.class, ProductDAO.class})
//use application-test.properties in src/main/resources instead of application.properties
@ActiveProfiles("test")
public class ProductDAOTest {
	@Autowired
	private CatalogDbDAO catalogDbDAO;
	@Autowired
	private ProductDAO productdao;
	
	@Before
	// each test runs in its own transaction, on same db setup
	public void setup() throws SQLException {
		catalogDbDAO.initializeDb(); 
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
// TODO		dbDAO.close();
	}

	
	@Test
	public void testFindProductByCode() throws Exception
	{
		Connection connection = catalogDbDAO.startTransaction();
		Product p2 = productdao.findProductByCode(connection, "8601");
		assertTrue(1 == p2.getId());
		catalogDbDAO.commitTransaction(connection);
	}
}