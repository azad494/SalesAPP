package peash.esales.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import peash.esales.App.AppController;
import Adi.esales.R;


public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeContainer;
    ViewFlipper v_flipper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        int images[] = {R.drawable.many_logo_1};//,R.drawable.esales, R.drawable.dsl

        //v_flipper = v.findViewById(R.id.v_flipper);
        v_flipper = v.findViewById(R.id.aa);

        for(int i=0; i<images.length;i++)
        {
            flipperImages(images[i]);
        }

//..................................... Pull refresh..............................................

        swipeContainer=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here


                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeContainer.setRefreshing(false);
                    }
                }, 4000); // Delay in millis
            }
        });

        // Scheme colors for animation

        swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
//............................. pull refresh end...................................................
        return v;
    }

    public void flipperImages(int image)
    {
        ImageView imageView = new ImageView(AppController.getContext());
        imageView.setBackgroundResource(image);
        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(4000);//3000=3sec
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(AppController.getContext(), android.R.anim.slide_in_left);
        v_flipper.setInAnimation(AppController.getContext(), android.R.anim.slide_out_right);
    }


}