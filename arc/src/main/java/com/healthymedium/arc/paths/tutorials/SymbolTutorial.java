package com.healthymedium.arc.paths.tutorials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.SymbolTutorialButton;
import com.healthymedium.arc.library.R;

public class SymbolTutorial extends BaseFragment {

    SymbolTutorialButton buttonTop1;
    SymbolTutorialButton buttonTop2;
    SymbolTutorialButton buttonTop3;
    SymbolTutorialButton buttonBottom1;
    SymbolTutorialButton buttonBottom2;

    public SymbolTutorial() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_symbols_tutorial, container, false);

        buttonTop1 = view.findViewById(R.id.symbolbutton_top1);
        buttonTop2 = view.findViewById(R.id.symbolbutton_top2);
        buttonTop3 = view.findViewById(R.id.symbolbutton_top3);

        buttonBottom1 = view.findViewById(R.id.symbolbutton_bottom1);
        buttonBottom2 = view.findViewById(R.id.symbolbutton_bottom2);

        setInitialImages();

        return view;
    }

    private void setInitialImages() {
        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_8_tutorial, R.drawable.ic_symbol_1_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_7_tutorial, R.drawable.ic_symbol_3_tutorial);
    }

    private void setSecondImages() {
        buttonTop1.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_6_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);
    }

    private void setLastImages() {
        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);
    }

}
