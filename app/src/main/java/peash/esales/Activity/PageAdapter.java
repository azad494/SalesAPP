package peash.esales.Activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private int numoftabs;

    public PageAdapter(FragmentManager fm, int numofTabs) {
        super(fm);
        this.numoftabs = numofTabs;
    }

    @Override
    public Fragment getItem(int posiction) {
        switch (posiction){
            case 0:
                //return new tab1();
            case 1:
                //return new tab2();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numoftabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object){
        return POSITION_NONE;
    }

}
