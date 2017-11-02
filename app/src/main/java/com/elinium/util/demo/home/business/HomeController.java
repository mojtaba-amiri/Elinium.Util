package com.elinium.util.demo.home.business;

import com.elinium.mvc.BaseController;
import com.elinium.util.demo.home.vm.HomeViewModel;

/**
 * Created by mojtabaa on 2017-11-02.
 */

public class HomeController extends BaseController<HomeViewModel> {
  private static HomeController instance;

  @Override public BaseController getInstance() {
    if (instance == null) instance = new HomeController();
    return instance;
  }

  @Override public void clearController() {

  }
}
