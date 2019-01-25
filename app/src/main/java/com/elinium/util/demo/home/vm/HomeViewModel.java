package com.elinium.util.demo.home.vm;

import com.elinium.util.demo.items.SampleItem;
import java.util.List;

/**
 * Created by mojtabaa on 2017-11-02.
 */

public class HomeViewModel {
  private String id;
  List<SampleItem> items;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<SampleItem> getItems() {
    return items;
  }

  public void setItems(List<SampleItem> items) {
    this.items = items;
  }
}
