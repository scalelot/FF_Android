package com.festum.festumfield.Activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.festum.festumfield.Adapter.AllContactAdapter;
import com.festum.festumfield.Adapter.UserSelectionAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.Model.CreateGroupModel;
import com.festum.festumfield.R;

import java.util.ArrayList;

public class CreateNewGroupActivity extends BaseActivity {
    ImageView ic_back;
    ImageView iv_next;
    TextView txt_title;
    TextView txt_selected_count;
    TextView txt_total_count;
    RecyclerView recyclerview_selected_list;
    RecyclerView recyclerview_contact_list;
    EditText edt_search_text;
    ImageView iv_search;
    ImageView iv_clear_text;
    String[] user_name = {"John Bryan", "Bryan", "Hunter Bryan", "Doris Collins", "Deann Sumpter", "Angel Egotrip", "Binary Bark", "Geez God", "Mindhack Diva", "ugar Lump", "Droolbug", "Zig Wagon", "Strife Life"};
    ArrayList<CreateGroupModel> arraylist = new ArrayList<>();
    ArrayList<CreateGroupModel> selectedarraylist = new ArrayList<>();

    AllContactAdapter allContactAdapter;
    UserSelectionAdapter userSelectionAdapter;
    ImageView selectimage;
    CreateGroupModel createGroupModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        ic_back = findViewById(R.id.ic_back);
        iv_next = findViewById(R.id.iv_next);
        txt_title = findViewById(R.id.txt_title);
        txt_selected_count = findViewById(R.id.txt_selected_count);
        txt_total_count = findViewById(R.id.txt_total_count);
        recyclerview_selected_list = findViewById(R.id.recyclerview_selected_list);
        recyclerview_contact_list = findViewById(R.id.recyclerview_contact_list);

        edt_search_text = findViewById(R.id.edt_search_text);
        iv_search = findViewById(R.id.iv_search);
        iv_clear_text = findViewById(R.id.iv_clear_text);

        for (int i = 0; i < user_name.length; i++) {
            createGroupModel = new CreateGroupModel(user_name[i]);
            arraylist.add(createGroupModel);
        }

        txt_total_count.setText(arraylist.size() + " Selected");

        recyclerview_selected_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));

        recyclerview_contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        allContactAdapter = new AllContactAdapter(this, arraylist);
        recyclerview_contact_list.setAdapter(allContactAdapter);

        if (selectedarraylist.isEmpty()) {
            recyclerview_selected_list.setVisibility(View.GONE);
        } else {
            recyclerview_selected_list.setVisibility(View.VISIBLE);

        }

        userSelectionAdapter = new UserSelectionAdapter(CreateNewGroupActivity.this, selectedarraylist);

        allContactAdapter.setOnItemClickListener(new AllContactAdapter.ClickListener() {
            @Override
            public void onItemClick(CreateGroupModel createGroupModel, ImageView v, boolean isToAdd) {
                selectimage = v;
                if (isToAdd) {
                    selectedarraylist.add(createGroupModel);
                } else {
                    selectedarraylist.remove(createGroupModel);
                }

                if (selectedarraylist.isEmpty()) {
                    recyclerview_selected_list.setVisibility(View.GONE);
                } else {
                    recyclerview_selected_list.setVisibility(View.VISIBLE);
                }

                userSelectionAdapter = new UserSelectionAdapter(CreateNewGroupActivity.this, selectedarraylist);
                recyclerview_selected_list.setAdapter(userSelectionAdapter);

                allContactAdapter.notifyDataSetChanged();
                userSelectionAdapter.notifyDataSetChanged();
                txt_selected_count.setText(selectedarraylist.size() + " of ");

            }
        });

        userSelectionAdapter.setOnItemClickListener(new UserSelectionAdapter.RemoveClickListener() {
            @Override
            public void onItemClick(CreateGroupModel createGroupModel, int position, View v) {
                Log.e("LLL_de_posi-->", String.valueOf(position));
                if (arraylist.size() > 0) {

                    arraylist.get(arraylist.indexOf(createGroupModel)).setSelected(false);

                    selectedarraylist.remove(position);

                    userSelectionAdapter = new UserSelectionAdapter(CreateNewGroupActivity.this, selectedarraylist);
                    recyclerview_selected_list.setAdapter(userSelectionAdapter);

                    allContactAdapter.notifyDataSetChanged();
                    userSelectionAdapter.notifyDataSetChanged();

                    txt_selected_count.setText(selectedarraylist.size() + " of ");

                    if (selectedarraylist.isEmpty()) {
                        recyclerview_selected_list.setVisibility(View.GONE);
                    } else {
                        recyclerview_selected_list.setVisibility(View.VISIBLE);
                    }

                } else {
                    Log.e("LLL_debinf finduser", "userList.size() == 0, AFTER REMOVED userListAdded.size() is " + selectedarraylist.size());
                }
            }
        });

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
                filter(text);

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

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateGroupTitleActivity.class);
                intent.putParcelableArrayListExtra("selected_list", selectedarraylist);
                startActivity(intent);
            }
        });
    }

    private void filter(String text) {
        ArrayList<CreateGroupModel> filteredlist = new ArrayList<>();

        for (CreateGroupModel item : arraylist) {
            if (item.getUsername().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            allContactAdapter.filterList(filteredlist);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateNewGroupActivity.this, MainActivity.class));
        finish();
    }
}