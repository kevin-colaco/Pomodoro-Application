package com.example.pomodoroapp.upcomingtasks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoroapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivityUpcomingPomodoros extends AppCompatActivity {
    public static final String EXTRA_NAME = "com.example.tp.extra.NAME";

    // Declaring variables for capturing second activity data
    private String pomodoroInterval;
    private String breakInterval;
    private String pomodoroNumber;
    private String pomodoroDate;
    private String pomodoroTime;
    private String pomodoroLongBreak;
    private int position;
    private int taskColor;
    private EditText taskNameEdt;
    private ImageButton deleteBtnTask;
    private ImageView imageView;
    private TextView textView;
    private RecyclerView taskRV;
    private boolean longBreakEnable;

    // variable for our adapter class and array list
    private TaskAdapter adapter;
    private ArrayList<TaskModal> taskModalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Upcoming Tasks" + "</font>"));
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        setContentView(R.layout.activity_main_upcoming_pomodoro);

        taskNameEdt = findViewById(R.id.addNewTask);
        deleteBtnTask = findViewById(R.id.idBtnDeleteTask);
        taskRV = findViewById(R.id.idRVCourses);
        imageView = findViewById(R.id.noTaskImage);
        textView = findViewById(R.id.noTaskTV);

        // calling method to load data
        // from shared prefs.
        loadData();

        // calling method to build
        // recycler view.
        buildRecyclerView();

        if (taskModalArrayList.isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
        else{
            imageView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }

        try {
            editTask();
        }
        catch (Exception exception){
            ;
        }
    }

    private void buildRecyclerView() {
        // initializing our adapter class.
        adapter = new TaskAdapter(taskModalArrayList, MainActivityUpcomingPomodoros.this);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        taskRV.setHasFixedSize(true);

        // setting layout manager to our recycler view.
        taskRV.setLayoutManager(manager);

        // setting adapter to our recycler view.
        taskRV.setAdapter(adapter);
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
        String json = sharedPreferences.getString("courses", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<TaskModal>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        taskModalArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (taskModalArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            taskModalArrayList = new ArrayList<>();
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
        String json = gson.toJson(taskModalArrayList);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("courses", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

//        // after saving data we are displaying a toast message.
//        Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
    }

    public void editTask(){
        Intent data = getIntent();
        String title = data.getStringExtra("taskNameEdit");
        pomodoroNumber = data.getStringExtra("taskValuePomodoroNumberEdit");
        breakInterval = data.getStringExtra("taskValueBreakIntervalEdit");
        pomodoroInterval = data.getStringExtra("taskValuePomodoroIntervalEdit");
        pomodoroDate = data.getStringExtra("taskValuePomodoroDateEdit");
        pomodoroTime = data.getStringExtra("taskValuePomodoroTimeEdit");
        position = data.getIntExtra("taskValueAdapterPositionEdit", -1);
        pomodoroLongBreak = data.getStringExtra("taskValuePomodoroLongBreak");
        longBreakEnable = data.getBooleanExtra("taskValuePomodoroLongBreakEnabled", false);
        taskColor = data.getIntExtra("taskColorEdit", R.color.red);

        taskModalArrayList.set(position, new TaskModal(title, pomodoroDate, pomodoroInterval, pomodoroNumber, breakInterval, pomodoroLongBreak, longBreakEnable, taskColor));
        adapter.notifyDataSetChanged();
        saveData();
    }

    public void newTaskActivity(View view) {
        // Creating an intent to reach the second activity
        EditText input = findViewById(R.id.addNewTask);
        String itemText = input.getText().toString();
        if (itemText.equals("")){
            Snackbar.make(view, "Please name your task", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            })
            .show();
        }
        else {
            Intent intent = new Intent(this, CreatePomodoro.class);
            intent.putExtra(EXTRA_NAME, itemText);
            startActivityForResult(intent, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                // Getting the result from second activity
                pomodoroNumber = data.getStringExtra("taskValuePomodoroNumber");
                breakInterval = data.getStringExtra("taskValueBreakInterval");
                pomodoroInterval = data.getStringExtra("taskValuePomodoroInterval");
                pomodoroDate = data.getStringExtra("taskValuePomodoroDate");
                pomodoroTime = data.getStringExtra("taskValuePomodoroTime");
                pomodoroLongBreak = "20 minutes";
                longBreakEnable = false;
                taskColor = R.color.red;

                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

                taskModalArrayList.add(new TaskModal(taskNameEdt.getText().toString(), pomodoroDate, pomodoroInterval, pomodoroNumber, breakInterval, pomodoroLongBreak, longBreakEnable, taskColor));
                // notifying adapter when new data added.
                adapter.notifyItemInserted(taskModalArrayList.size());
                // saving the arraylist created
                saveData();
            }
        }
    }
}


