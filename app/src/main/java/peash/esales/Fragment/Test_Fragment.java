package peash.esales.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import peash.esales.Activity.PageAdapter;
import Adi.esales.R;


public class Test_Fragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tab1,tab2;
    public PageAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.demo_fragment, container, false);

//        tabLayout = v.findViewById(R.id.tabLayout);
//        tab1 = v.findViewById(R.id.tab1);
//        tab2 = v.findViewById(R.id.tab2);
//        viewPager = v.findViewById(R.id.ViewPager);
//        pagerAdapter = new PageAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(pagerAdapter);
//
//        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//                if (tab.getPosition() == 0)
//                {
//                    pagerAdapter.notifyDataSetChanged();
//                }
//                else if(tab.getPosition() == 1){
//                    pagerAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return v;
    }

}
