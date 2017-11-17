package transaction.statictics.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import transaction.statictics.Application;
import transaction.statictics.model.Statistics;
import transaction.statictics.model.Transaction;
import transaction.statictics.service.TransactionService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
@DirtiesContext(classMode=ClassMode.BEFORE_EACH_TEST_METHOD)
public class TransactionTest {


	@Autowired
	TransactionService transactionService;
	@Mock
	TransactionDatasource transactionDatasource;
	
	
	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		Statistics stats = new Statistics(); 
		stats.setAvg(24.5/3);
		stats.setCount(3);
		stats.setMax(12.3);
		stats.setMin(3.2);
		stats.setSum(24.5);
		when(transactionDatasource.getStatistics()).thenReturn(stats);
		
		transactionService.setTransactionDatasource(transactionDatasource);
	}

	@Test
	public void getStatisticsStoreIsEmpty() throws Exception {
		Statistics empty = new Statistics(); 
		empty.setMax(0);
		empty.setMin(0);
		when(transactionDatasource.getStatistics()).thenReturn(empty);
		
		Statistics stats = transactionService.getStatistics();
		
		assertEquals("Response Parameter Mismatch", "0.0", String.valueOf(stats.getAvg()));
		assertEquals("Response Parameter Mismatch", "0.0", String.valueOf(stats.getSum()));
		assertEquals("Response Parameter Mismatch", "0", String.valueOf(stats.getCount()));
		assertEquals("Response Parameter Mismatch", "0.0", String.valueOf(stats.getMax()));
		assertEquals("Response Parameter Mismatch", "0.0", String.valueOf(stats.getMin()));
	}
	
	@Test
	public void logtransactionValid() throws Exception {
		Transaction trans = new Transaction();
		trans.setAmount(12);
		trans.setTimestamp(System.currentTimeMillis());
		
		boolean isSuccessful = transactionService.logTransaction(trans);
		assertTrue(isSuccessful);
	}
	
	@Test
	public void logtransactionWithOutdatedTimestamp() throws Exception {
		Transaction trans = new Transaction();
		trans.setAmount(12);
		trans.setTimestamp(1325817879);
		
		boolean isSuccessful = transactionService.logTransaction(trans);
		assertFalse(isSuccessful);
	}
	
	@Test
	public void getStatisticsWithDataPresentInStore() throws Exception {
		
		Transaction trans = new Transaction();
		
		trans.setAmount(2009);
		trans.setTimestamp(Instant.now(Clock.systemUTC()).toEpochMilli()-1);
		transactionService.logTransaction(trans);
		
		trans.setAmount(212.002);
		trans.setTimestamp(Instant.now(Clock.systemUTC()).toEpochMilli());
		transactionService.logTransaction(trans);
		
		trans.setAmount(2.16);
		trans.setTimestamp(Instant.now(Clock.systemUTC()).toEpochMilli()+50);
		transactionService.logTransaction(trans);
		
		Statistics stats = transactionService.getStatistics();
		
		
		
		assertEquals("Response Parameter Mismatch", "8.166666666666666", String.valueOf(stats.getAvg()));
		assertEquals("Response Parameter Mismatch", "24.5", String.valueOf(stats.getSum()));
		assertEquals("Response Parameter Mismatch", "3", String.valueOf(stats.getCount()));
		assertEquals("Response Parameter Mismatch", "12.3", String.valueOf(stats.getMax()));
		assertEquals("Response Parameter Mismatch", "3.2", String.valueOf(stats.getMin()));
	}
	
	
}