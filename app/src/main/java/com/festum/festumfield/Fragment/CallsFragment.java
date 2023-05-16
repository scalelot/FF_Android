package com.festum.festumfield.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.festum.festumfield.Adapter.CallAdapter;
import com.festum.festumfield.R;

import java.util.ArrayList;

public class CallsFragment extends Fragment {
    EditText edt_search_text;
    ImageView iv_search;
    ImageView iv_clear_text;
    RecyclerView recycle_call;
    CallAdapter callAdapter;
    String[] user_name = {"Hunter Bryan", "Caroline Case", "Hunter Bryan", "John Bryan", "Bryan", "Doris Collins"};
    int[] call_img = {R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user};
    ArrayList<String> arraylist = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        recycle_call = view.findViewById(R.id.recycle_call);
        edt_search_text = view.findViewById(R.id.edt_search_text);
        iv_search = view.findViewById(R.id.iv_search);
        iv_clear_text = view.findViewById(R.id.iv_clear_text);

        for (int i = 0; i < user_name.length; i++) {
            arraylist.add(user_name[i]);
        }

        callAdapter = new CallAdapter(CallsFragment.this, arraylist, call_img);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recycle_call.setLayoutManager(linearLayoutManager);
        recycle_call.setAdapter(callAdapter);

        edt_search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        edt_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    iv_clear_text.setVisibility(View.GONE);
                    iv_search.setVisibility(View.VISIBLE);
                } else {
                    iv_clear_text.setVisibility(View.VISIBLE);
                    iv_search.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                callAdapter.filter(text);

                iv_search.setVisibility(View.GONE);
                iv_clear_text.setVisibility(View.VISIBLE);
            }
        });

        iv_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search_text.setText("");
                iv_clear_text.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }
}