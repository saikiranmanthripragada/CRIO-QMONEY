
package com.crio.warmup.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.Comparator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomTicker implements Comparable<CustomTicker> {

  private String ticker;
  private Double close;

  public CustomTicker(String ticker, Double close) {
    this.ticker = ticker;
    this.close = close;
  }

  @Override
  public int compareTo(CustomTicker other) {
    if (this.getClose() > other.getClose()) {
      return 1;
    } else if (Double.compare(this.getClose(),other.getClose()) == 0) {
      return 0;
    }
    return -1;
  }

  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    if (!super.equals(object)) {
      return false;
    }
    CustomTicker that = (CustomTicker) object;
    return java.util.Objects.equals(getClose(), that.getClose());
  }

  public int hashCode() {
    return java.util.Objects.hash(super.hashCode(), getClose());
  }

  public String getTicker() {
    return this.ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public double getClose() {
    return this.close;
  }

  public void setClose(Double close) {
    this.close = close;
  }

  @Override
  public String toString() {
    return "CustomTicker: Ticker" + ticker + " Close: " + close + "\n";
  }

}
