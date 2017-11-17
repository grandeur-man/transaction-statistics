package transaction.statictics.datasource;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import transaction.statictics.model.Statistics;
import transaction.statictics.model.Transaction;

/**
 * This Class will be the in-memory store for transactions
 */
@Component
public class TransactionDatasource {


    private static ConcurrentHashMap<Long, Double> store = new ConcurrentHashMap<>();

    public Statistics getStatistics() throws Exception{


        Statistics result = new Statistics();

        Supplier<Stream<Entry<Long, Double>>> streamSupplier = () -> store.entrySet()
        .stream()
        .filter(e -> Instant.now(Clock.systemUTC()).toEpochMilli() - e.getKey() <= 60000);
        
        result.setCount(streamSupplier.get().count());
        if(result.getCount()>0) {
        	result.setMin(streamSupplier.get().min(Comparator.comparingDouble(value -> value.getValue() <  result.getMin() ? value.getValue(): result.getMin())).get().getValue());
            result.setMax(streamSupplier.get().max(Comparator.comparingDouble((Map.Entry<Long, Double> value) -> { return value.getValue() >  result.getMax() ? value.getValue(): result.getMax();})).get().getValue());
            result.setSum(streamSupplier.get().mapToDouble((Map.Entry<Long, Double> e) -> e.getValue()).sum());
            result.setAvg(result.getSum()/result.getCount());
        }
        else {
        	result.setMax(0);
        	result.setMin(0);
        }
        
        streamSupplier.get().close();

        return result;
    }


    public void logTransaction(Transaction transaction) throws Exception{
        store.put(transaction.getTimestamp(),transaction.getAmount());
    }
}
