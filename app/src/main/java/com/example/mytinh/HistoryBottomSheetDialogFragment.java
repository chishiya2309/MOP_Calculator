package com.example.mytinh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

public class HistoryBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_HISTORY_LIST = "history_list";
    private List<String> historyList;

    public static HistoryBottomSheetDialogFragment newInstance(ArrayList<String> historyList) {
        HistoryBottomSheetDialogFragment fragment = new HistoryBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_HISTORY_LIST, historyList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            historyList = getArguments().getStringArrayList(ARG_HISTORY_LIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_history, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistory);
        HistoryAdapter adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
