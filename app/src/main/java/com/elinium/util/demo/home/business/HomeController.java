package com.elinium.util.demo.home.business;

import android.content.Context;
import android.view.View;
import com.elinium.mvc.BaseController;
import com.elinium.util.demo.home.vm.HomeViewModel;
import com.elinium.util.demo.items.SampleItem;
import com.elinium.util.ui.recyclerview.BaseItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mojtabaa on 2017-11-02.
 */

public class HomeController extends BaseController<HomeViewModel> {
  private static HomeController instance;
  private HomeViewModel vm;

  private static HomeViewModel getVm() {
    if (instance.vm == null) instance.vm = new HomeViewModel();
    return instance.vm;
  }

  @Override public BaseController getInstance() {
    if (instance == null) instance = new HomeController();
    return instance;
  }

  @Override public void clearController() {
    instance = null;
  }

  public static void getItems(Context context) {
    List<SampleItem> items = new ArrayList<>();
    //for (int i = 0; i < 20000; i++)
    //  items.add(new SampleItem(" item #" + i));

  }
}
