package transaction.statictics.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import transaction.statictics.exceptions.BadRequestException;
import transaction.statictics.model.Statistics;
import transaction.statictics.model.Transaction;
import transaction.statictics.service.TransactionService;

import javax.servlet.http.HttpServletResponse;

@RestController
public class StatisticsController {

    @Autowired
    TransactionService transactionService;

    @RequestMapping(method = RequestMethod.POST, value = "/transactions")
    public void logTransactions(@Validated @RequestBody Transaction transaction, HttpServletResponse response) throws BadRequestException {

        try{
            response.setStatus(transactionService.logTransaction(transaction) ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_NO_CONTENT);
        }
        catch (Exception ex) {
            throw new BadRequestException("400", "Timestamp and amount cannot be empty.");
        }

    }


    @RequestMapping(method = RequestMethod.GET, value = "/statistics")
    @ResponseStatus(HttpStatus.OK)
    public Statistics getStatistics() throws BadRequestException {

        try{
            return transactionService.getStatistics();
        }
        catch (Exception ex) {
            throw new BadRequestException("500", "An Unexpected Error has occured.");
        }

    }


}
