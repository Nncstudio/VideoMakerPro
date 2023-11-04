package com.video.photoeditor.fragment.texttools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.video.maker.R;
import com.video.photoeditor.adapter.FontAdapter;
import com.video.photoeditor.interfaces.FontFragmentListener;
import com.video.photoeditor.view.PickerLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class FontFragment extends Fragment implements FontAdapter.FontAdapterClickListener {
    FontAdapter fontAdapter;
    List<String> fontList;
    FontFragmentListener listener;
    RecyclerView recyclerFont;

    public void onFontItemSelected(String str) {
    }

    private List<String> loadFontList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("ALGER.TTF");
        arrayList.add("Back to Black Demo.ttf");
        arrayList.add("BAUHS93.TTF");
        arrayList.add("BERNHC.TTF");
        arrayList.add("blessd.ttf");
        arrayList.add("BRITANIC.TTF");
        arrayList.add("BrushScriptStd.otf");
        arrayList.add("CASTELAR.TTF");
        arrayList.add("comic.ttf");
        arrayList.add("ELEPHNT.TTF");
        arrayList.add("FORTE.TTF");
        arrayList.add("Freehand521 BT.ttf");
        arrayList.add("HandyQuomteRegular-6YLLo.ttf");
        arrayList.add("HoboStd.otf");
        arrayList.add("SHOWG.TTF");
        arrayList.add("STENCIL.TTF");
        return arrayList;
    }

    public void setListener(FontFragmentListener fontFragmentListener) {
        this.listener = fontFragmentListener;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_text_font, viewGroup, false);
        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(getActivity(), 0, false);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.4f);
        pickerLayoutManager.setScaleDownDistance(0.8f);
        this.fontList = loadFontList();
        RecyclerView findViewById = inflate.findViewById(R.id.recycler_font);
        this.recyclerFont = findViewById;
        findViewById.setHasFixedSize(true);
        this.recyclerFont.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        FontAdapter fontAdapter2 = new FontAdapter(getActivity(), this, this.fontList);
        this.fontAdapter = fontAdapter2;
        this.recyclerFont.setAdapter(fontAdapter2);
        new LinearSnapHelper().attachToRecyclerView(this.recyclerFont);
        this.recyclerFont.setLayoutManager(pickerLayoutManager);
        pickerLayoutManager.setOnScrollStopListener(new PickerLayoutManager.onScrollStopListener() {
            public void selectedView(int i) {
                if (FontFragment.this.listener != null) {
                    FontFragment.this.listener.onFontSelected(FontFragment.this.fontList.get(i));
                }
            }
        });
        return inflate;
    }
}
