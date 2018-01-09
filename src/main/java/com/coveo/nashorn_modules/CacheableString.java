package com.coveo.nashorn_modules;

public class CacheableString {
  private final String string;
  private final String cacheValidator;

  public CacheableString(String string) {
    this(string, null);
  }

  public CacheableString(String string, String cacheValidator) {
    this.string = string;
    this.cacheValidator = cacheValidator;
  }

  public String getString() {
    return string;
  }

  public String getCacheValidator() {
    return cacheValidator;
  }
}
