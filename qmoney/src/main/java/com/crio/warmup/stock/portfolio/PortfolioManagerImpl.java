package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {




  private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo thirdparty APIs to a separate function.
  //  It should be split into fto parts.
  //  Part#1 - Prepare the Url to call Tiingo based on a template constant,
  //  by replacing the placeholders.
  //  Constant should look like
  //  https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  //  Where ? are replaced with something similar to <ticker> and then actual url produced by
  //  replacing the placeholders with actual parameters.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {

    String url = buildUri(symbol, from, to);
    ObjectMapper om = PortfolioManagerImpl.getObjectMapper();
    
    String result = restTemplate.getForObject(url, String.class);
    TiingoCandle[] collection = om.readValue(result, TiingoCandle[].class);
    List<Candle> res = new ArrayList<>();
    for(TiingoCandle candle: collection) {
      res.add(candle);
    }

    return res;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String url;
    if (endDate == null) {
      url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate
        + "&token=15cbd790225c9b73bf706e1f22d3348655f0d760";
    } else {
      url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate + "&endDate="
        + endDate + "&token=15cbd790225c9b73bf706e1f22d3348655f0d760";
    }
    return url;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
    List<AnnualizedReturn> res = new ArrayList<>();
    
    for (PortfolioTrade trade : portfolioTrades) {
      LocalDate startDate = trade.getPurchaseDate();

      //Check if start date is after end date
      if (startDate.isAfter(endDate)) {
        throw new RuntimeException();
      }
      List<Candle> collection = new ArrayList<>();
      try {
        collection = getStockQuote(trade.getSymbol(), startDate, endDate);
      } catch (JsonProcessingException exception) {
        //System.out.println(exception.getMessage());
      }

      Candle startCandle = new TiingoCandle();
      Candle endCandle = new TiingoCandle();
      for (Candle candle : collection) {
        if (candle.getDate().equals(startDate) || candle.getDate().isAfter(startDate)) {
          startCandle = candle;
          break;
        }
      }
      
      if (endDate == null) {
        endCandle = collection.get(collection.size()-1);
        //endCandle = collection[collection.length - 1];
      } else {
        for (int i = collection.size() - 1; i >= 0; i--) {
          Candle candle = collection.get(i);
          //Candle candle = collection[i];
          if (candle.getDate().equals(endDate)) {
            endCandle = candle;
            break;
          } else if (endDate.isAfter(candle.getDate())) {
            endCandle = candle;
            break;
          }
        }
      }

      Double buyPrice = new Double(startCandle.getOpen());
      Double sellPrice = new Double(endCandle.getClose());


      Double noOfYears = new Double(ChronoUnit.DAYS.between(startDate, endDate) / 365.24);
      Double totalReturns = new Double((sellPrice - buyPrice) / buyPrice);
      Double annualizedReturns = new Double(Math.pow((1 + totalReturns), (1 / noOfYears)) - 1);
        

      //totalReturns = (sellPrice - buyPrice) / buyPrice;
      //annualizedReturns = Math.pow((1 + totalReturns), (1 / noOfYears)) - 1;
      res.add(new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns));
    }
    Collections.sort(res, Collections.reverseOrder());
    return res;
  }
}