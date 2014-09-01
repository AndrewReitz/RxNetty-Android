
package com.andrewreitz.rxnetty.model;

import java.util.ArrayList;
import java.util.List;

public final class Data {

  private String modhash;
  private List<Child> children = new ArrayList<Child>();
  private String after;
  private Object before;

  public String getModhash() {
    return modhash;
  }

  public List<Child> getChildren() {
    return children;
  }

  public String getAfter() {
    return after;
  }

  public Object getBefore() {
    return before;
  }
}
