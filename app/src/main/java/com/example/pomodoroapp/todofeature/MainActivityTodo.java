package com.example.pomodoroapp.todofeature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoroapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivityTodo extends AppCompatActivity {
    private String todoDate;
    private String todoTime;
    private String todoName;
    private String todoTimeHour;
    private String todoTimeMinute;
    private TodoAdapter adapter;
    private ArrayList<TodoModal> todoModalArrayList;
    private RecyclerView todoRV;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_todo);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "ToDo" + "</font>"));
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        todoRV = findViewById(R.id.idRVTodo);

        loadData();

        buildRecyclerView();

        try {
            editTask();
        }
        catch (ArrayIndexOutOfBoundsException exception){
            ;
        }
    }

    private void buildRecyclerView() {
        // initializing our adapter class.
        adapter = new TodoAdapter(todoModalArrayList, MainActivityTodo.this);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        todoRV.setHasFixedSize(true);

        // setting layout manager to our recycler view.
        todoRV.setLayoutManager(manager);

        // setting adapter to our recycler view.
        todoRV.setAdapter(adapter);
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("todos", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<TodoModal>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        todoModalArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (todoModalArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            todoModalArrayList = new ArrayList<>();
        }
    }

    private void saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(todoModalArrayList);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("todos", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

//        // after saving data we are displaying a toast message.
//        Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
    }

    public void startTodoActivity(View view){
        Intent intent = new Intent(this, TodoMaker.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                todoName = data.getStringExtra("todoName");
                todoDate = data.getStringExtra("todoDate");
                todoTime = data.getStringExtra("todoTime");
                todoTimeHour = data.getStringExtra("todoTimeHour");
                todoTimeMinute = data.getStringExtra("todoTimeMinute");
                todoModalArrayList.add(new TodoModal(todoName, todoDate, todoTime));
                // notifying adapter when new data added.
                adapter.notifyItemInserted(todoModalArrayList.size());
                // saving the arraylist created
                saveData();
            }
        }
    }

    public void editTask(){
        Intent data = getIntent();
        position = data.getIntExtra("taskValueAdapterPositionEdit", -1);
        todoName = data.getStringExtra("todoNameEdit");
        todoDate = data.getStringExtra("todoDateEdit");
        todoTime = data.getStringExtra("todoTimeEdit");
        todoModalArrayList.set(position, new TodoModal(todoName, todoDate, todoTime));
        adapter.notifyDataSetChanged();
        saveData();
    }

}