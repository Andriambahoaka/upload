package com.app.madiapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BlankFragment extends Fragment {

    private String[] names = new String[]{"Fitahiana" , "Jo Max", "Harena"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View i =  inflater.inflate(R.layout.fragment_blank, container, false);
        Button l = i.findViewById(R.id.loadme);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(i,container);
            }
        });
        return i;
    }

    private void showFragment(View view, ViewGroup container){
        ProgressBar p = view.findViewById(R.id.progressBar1);
        p.setVisibility(View.VISIBLE);
        //Handler handler =new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                p.setVisibility(View.GONE);
                loadData(view,container);
            }
        });
    }

    private void loadData(View v, ViewGroup container){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout l = v.findViewById(R.id.mere);
        for(int i = 0; i < names.length; i++) {
            LinearLayout tmp = (LinearLayout) inflater.inflate(R.layout.personne,null,false);
            LinearLayout s = tmp.findViewById(R.id.souche);
            TextView tmpName = tmp.findViewById(R.id.nom);
            tmpName.setText(names[i]);
            l.addView(s);
            s.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            s.getLayoutParams().height = 100;
        }
    }
}