package transaction.statictics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import transaction.statictics.datasource.TransactionDatasource;
import transaction.statictics.model.Statistics;
import transaction.statictics.model.Transaction;

import java.time.Clock;
import java.time.Instant;

@Component
public class TransactionService {

	@Autowired
	private TransactionDatasource transactionDatasource;

    public Statistics getStatistics() throws Exception {

        return transactionDatasource.getStatistics();
    }


    public boolean logTransaction(Transaction transaction) throws Exception {
        if(Instant.now(Clock.systemUTC()).toEpochMilli() - transaction.getTimestamp() <= 60000){
        	transactionDatasource.logTransaction(transaction);
            return true;
        }

        return  false;


    }


	public TransactionDatasource getTransactionDatasource() {
		return transactionDatasource;
	}


	public void setTransactionDatasource(TransactionDatasource transactionDatasource) {
		this.transactionDatasource = transactionDatasource;
	}

    
}
