package com.coveo.nashorn_modules;

public interface Folder {
  public Folder getParent();

  public String getPath();

  public CacheableString getFile(String name);

  public String getCacheValidator(String name);

  public Folder getFolder(String name);
}
