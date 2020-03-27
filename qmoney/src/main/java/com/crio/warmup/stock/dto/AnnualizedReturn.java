package com.crio.warmup.stock.dto;

public class AnnualizedReturn implements Comparable<AnnualizedReturn> {

  private final String symbol;
  private final Double annualizedReturn;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturn = annualizedReturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return annualizedReturn;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }

  @Override
  public int compareTo(AnnualizedReturn other) {
    if (this.getAnnualizedReturn() > other.getAnnualizedReturn()) {
      return 1;
    } else if (Double.compare(this.getAnnualizedReturn(),other.getAnnualizedReturn()) == 0) {
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
    AnnualizedReturn that = (AnnualizedReturn) object;
    return java.util.Objects.equals(getAnnualizedReturn(), that.getAnnualizedReturn());
  }

  public int hashCode() {
    return java.util.Objects.hash(super.hashCode(), getAnnualizedReturn());
  }
}