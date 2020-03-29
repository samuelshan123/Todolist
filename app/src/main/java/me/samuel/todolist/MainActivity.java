package me.samuel.todolist;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {
    private ListAdapter todoListAdapter;
    private TodoListSQLHelper todoListSQLHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateTodoList();

    }
    public void add(View view) {
        AlertDialog.Builder todoTaskBuilder = new AlertDialog.Builder(this);
        todoTaskBuilder.setMessage("Enter your task...");
        final EditText todoET = new EditText(this);
        todoTaskBuilder.setView(todoET);
        todoTaskBuilder.setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String todoTaskInput = todoET.getText().toString();
                todoListSQLHelper = new TodoListSQLHelper(MainActivity.this);
                SQLiteDatabase sqLiteDatabase = todoListSQLHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.clear();

                //write the Todo task input into database table
                values.put(TodoListSQLHelper.COL1_TASK, todoTaskInput);
                sqLiteDatabase.insertWithOnConflict(TodoListSQLHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                //update the Todo task list UI
                updateTodoList();
            }
        });

        todoTaskBuilder.setNegativeButton("Cancel", null);

        todoTaskBuilder.create().show();

    }



    //update the todo task list UI
    private void updateTodoList() {
        todoListSQLHelper = new TodoListSQLHelper(MainActivity.this);
        SQLiteDatabase sqLiteDatabase = todoListSQLHelper.getReadableDatabase();

        //cursor to read todo task list from database
        Cursor cursor = sqLiteDatabase.query(TodoListSQLHelper.TABLE_NAME,
                new String[]{TodoListSQLHelper._ID, TodoListSQLHelper.COL1_TASK},
                null, null, null, null, null);

        //binds the todo task list with the UI
        todoListAdapter = new SimpleCursorAdapter(
                this,
                R.layout.todotask,
                cursor,
                new String[]{TodoListSQLHelper.COL1_TASK},
                new int[]{R.id.todoTaskTV},
                0
        );

        this.setListAdapter(todoListAdapter);
    }

    //closing the todo task item
    public void onDoneButtonClick(View view) {
        View v = (View) view.getParent();
        TextView todoTV = (TextView) v.findViewById(R.id.todoTaskTV);
        String todoTaskItem = todoTV.getText().toString();

        String deleteTodoItemSql = "DELETE FROM " + TodoListSQLHelper.TABLE_NAME +
                " WHERE " + TodoListSQLHelper.COL1_TASK + " = '" + todoTaskItem + "'";

        todoListSQLHelper = new TodoListSQLHelper(MainActivity.this);
        SQLiteDatabase sqlDB = todoListSQLHelper.getWritableDatabase();
        sqlDB.execSQL(deleteTodoItemSql);
        updateTodoList();
    }


}