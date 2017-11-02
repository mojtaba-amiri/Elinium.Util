package com.elinium.util.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.elinium.util.R;
import com.elinium.util.broadcast.BroadcastListener;
import com.elinium.util.exceptionhandling.ExceptionHandler;
import com.elinium.util.ui.fragment.ETabFragment;
import com.elinium.util.ui.fragment.FragmentAction;
import com.elinium.util.ui.fragment.OnFragmentAction;
import com.elinium.util.ui.layout.TabbedLayout;

import java.lang.reflect.Method;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by amiri on 10/2/2017.
 */

public abstract class ETabbedActivity extends AppCompatActivity implements ExceptionHandler.IExceptionHandler, FragmentAction {
    protected final String TAG = getClass().getSimpleName();
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SparseArray<ETabFragment> fragments = new SparseArray<>();
    private EFragmentPagerAdapter fragmentPagerAdapter;
    private Map<String, Method> actions = new ArrayMap<>();
    private BroadcastListener broadcastListener;

    public void addLocalBroadcastAction(String action, String methodName) {
        broadcastListener.addLocalAnnotatedAction(action, methodName);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            broadcastListener = BroadcastListener.initialize(this);
            ExceptionHandler.register(this);
            TabbedLayout layout = getLayout();

            if (layout == null) {
                Log.e(TAG, "you must add @Layout annotation to you activity class");
                return;
            }

            if (layout.windowFeature() >= 0) {
                requestWindowFeature(layout.windowFeature());
            }

            if (layout.noTitle()) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            }

            setContentView(getLayoutId());

            if (layout.toolbar() != 0) {
                toolbar = (Toolbar) findViewById(layout.toolbar());
                if (toolbar != null) setSupportActionBar(toolbar);
            }

            if (layout.fab() != 0) {
                fab = findViewById(layout.fab());
                if (fab != null) fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFabClicked(view);
                    }
                });
            }

            if (!layout.fabVisible()) {
                fab = findViewById(layout.fab());
                fab.setVisibility(View.GONE);
            }

            if (layout.pager() != 0) {
                viewPager = findViewById(layout.pager());
            }

            if (layout.tabLayout() != 0) {
                tabLayout = findViewById(layout.tabLayout());
            }

            if (viewPager != null && tabLayout != null) {
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        ETabbedActivity.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        ETabbedActivity.this.onPageSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        ETabbedActivity.this.onPageScrollStateChanged(state);
                    }
                });
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        ETabbedActivity.this.onTabSelected(tab);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        ETabbedActivity.this.onTabUnselected(tab);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        ETabbedActivity.this.onTabReselected(tab);
                    }
                });
            }

            fragmentPagerAdapter = new EFragmentPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(fragmentPagerAdapter);

            tabLayout.setupWithViewPager(viewPager);
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                try {
                    if (fragments.get(i) == null) {
                        ETabFragment fragment = ((getLayout().fragments()[i])).newInstance();
                        fragment.setArguments(getInitialBundleFor((getLayout().fragments()[i])));
                        fragments.setValueAt(i, fragment);
                    }
                    View view1 = getLayoutInflater().inflate(R.layout.custom_tab, null);
                    view1.findViewById(R.id.imgIcon).setBackgroundResource(fragments.get(i).getTabIcon());
                    tabLayout.getTabAt(i).setCustomView(view1);
                    //tabLayout.getTabAt(i).setIcon(fragments.get(i).getTabIcon());
                    //tabLayout.getTabAt(i).setText(fragments.get(i).getTabTitle());
//                    for (int i = 0; i < tabLayout.getTabAt(1).getChildCount(); i++)
//                    {
//                        tablayout.getTabWidget().getChildAt(i).setPadding(10,10,10,10);
//                    }
                } catch (Exception e) {
                    Log.e(TAG, "set TabIcons Error:" + e.getMessage());
                }
            }

            // tabLayout.getTabAt(0).setIcon()
            //  getSupportActionBar().hide();

            if (layout.transparent()) {
                getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }

            if (layout.fullScreen()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            for (Method method : getClass().getMethods()) {
                OnFragmentAction annotation = method.getAnnotation(OnFragmentAction.class);
                if (annotation != null) {
                    try {
                        String methodName = method.getName();
                        if (methodName != null)
                            actions.put(annotation.value(), method);
                        //if (methodName != null) getClass().getMethod(methodName).invoke(this, data);
                    } catch (Exception e) {
                        Log.e(TAG, "onCreate get Fragment action methods:" + e.getMessage());
                    }
                }
            }

            ButterKnife.bind(this);
        } catch (Exception e) {
            Log.e(TAG, "EActivity onCreate error:" + e.getMessage());
        }

    }

    private TabbedLayout getLayout() throws Exception {
        TabbedLayout layout = getClass().getAnnotation(TabbedLayout.class);
        if (layout != null) {
            return layout;
        } else {
            throw new Exception("EActivity layout id is not specified. use @Layout annotation above your Activity class.");
        }
    }

    private int getLayoutId() {
        TabbedLayout layout = null;
        try {
            layout = getLayout();
            return layout.layout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //region tab change events
    protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    protected void onPageSelected(int position) {
        if (getSupportActionBar() != null && fragments.get(position) != null)
            getSupportActionBar().setTitle(fragments.get(position).getTabTitle());
    }

    protected void onPageScrollStateChanged(int state) {

    }

    protected void onTabSelected(TabLayout.Tab tab) {

    }

    protected void onTabUnselected(TabLayout.Tab tab) {

    }

    protected void onTabReselected(TabLayout.Tab tab) {

    }
    //endregion

    protected Bundle getInitialBundleFor(Class<? extends ETabFragment> frag) {
        return new Bundle();
    }

    protected void onFabClicked(View view) {

    }

    public class EFragmentPagerAdapter extends FragmentPagerAdapter {

        public EFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            try {
                if (fragments.get(position) != null) return fragments.get(position);

                ETabFragment fragment = ((getLayout().fragments()[position])).newInstance();
                fragment.setArguments(getInitialBundleFor((getLayout().fragments()[position])));
                fragments.put(position, fragment);

                return fragments.get(position);
            } catch (Exception e) {
                Log.e(TAG, "EFragmentPagerAdapter.getItem Error:" + e.getMessage());
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                if (fragments.get(position) == null) {
                    ETabFragment fragment = ((getLayout().fragments()[position])).newInstance();
                    fragment.setArguments(getInitialBundleFor((getLayout().fragments()[position])));
                    fragments.put(position, fragment);
                }
                return fragments.get(position).getTabTitle();
            } catch (Exception e) {
                Log.e(TAG, "EFragmentPagerAdapter.getPageTitle Error:" + e.getMessage());
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            try {
                return getLayout().fragments().length;
            } catch (Exception e) {
                Log.e(TAG, "getCount error:" + e.getMessage());
            }
            return 0;
        }
    }

    @Override
    public void onFragmentActionRequest(String action, Object... data) {
        try {
            if (actions.get(action) != null) actions.get(action).invoke(this, data);
        } catch (Exception e) {
            Log.e(TAG, "onFragmentActionRequest:" + e.getMessage());
        }
    }

    public void notifyFragmentDataChange(Class<? extends ETabFragment> fragment, Object... data) {
        for (int i = 0; i < fragments.size(); i++) {
            if (fragments.valueAt(i) != null) {
                fragments.valueAt(i).onDataChanged(data);
            }
        }
    }

    protected void gotoFragment(Class<? extends ETabFragment> fragment, Object... data) throws Exception {
        TabbedLayout tabbedLayout = getLayout();
        for (int i = 0; i < tabbedLayout.fragments().length; i++) {
            if (fragment.isInstance(tabbedLayout.fragments()[i])) {
                viewPager.setCurrentItem(i);
                if (fragments.valueAt(i) != null) {
                    fragments.valueAt(i).onDataChanged(data);
                }
            }
        }
    }
}
