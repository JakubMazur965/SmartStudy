package android.smartstudy;

import static android.smartstudy.CalendarOperations.monthYearFormatter;
import static android.smartstudy.CalendarOperations.daysOfMonthMethod;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarMain extends AppCompatActivity implements CalendarAdapter.OnItemListener, NoteAdapter.OnItemListener {
    private TextView currentMonth, previousMonth, nextMonth;
    private RecyclerView recyclerView; // okno z wszystkimi dniami
    private ListView notesList;
    private Button goToCurrentMonth, deleteNote, addNote;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        recyclerView = findViewById(R.id.recyclerView);
        currentMonth = findViewById(R.id.currentMonth);
        previousMonth = findViewById(R.id.previousMonthButton);
        nextMonth = findViewById(R.id.nextMonthButton);
        goToCurrentMonth = findViewById(R.id.goToCurrentMonth);
        deleteNote = findViewById(R.id.deleteNote);
        addNote = findViewById(R.id.addNoteButton);
        notesList = findViewById(R.id.notesList);

        Bundle bundle = getIntent().getExtras();
        login = bundle.getString("Login");

        CalendarOperations.selectedDate = LocalDate.now();
        setMonthView();

        //------------------------------------------------------------------------------------------
        // nie dziala


        /*
        String notesListToString = notesList.toString();

        // wybrana notatka do usuniecia
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesListToString);
        notesList.setAdapter(adapter);
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(CalendarMain.this, Note.selectedNote.toString(), Toast.LENGTH_SHORT).show();
            }
        });

         */

        //------------------------------------------------------------------------------------------

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNote();
            }
        });

        goToCurrentMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarOperations.selectedDate = LocalDate.now();
                setMonthView();
            }
        });

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note.notesList.remove(Note.selectedNote);
                setMonthView();
            }
        });
    }

    private void setMonthView() {
        currentMonth.setText(monthYearFormatter(CalendarOperations.selectedDate));
        previousMonth.setText(monthYearFormatter(CalendarOperations.selectedDate.minusMonths(1)));
        nextMonth.setText(monthYearFormatter(CalendarOperations.selectedDate.plusMonths(1)));

        ArrayList<LocalDate> daysOfMonth = daysOfMonthMethod(CalendarOperations.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysOfMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(calendarAdapter);
        setNoteAdapter();

        // wroc do aktualnej daty
        if (CalendarOperations.selectedDate.getMonth().equals(LocalDate.now().getMonth()) &&
                CalendarOperations.selectedDate.getYear() == LocalDate.now().getYear()) {
            // niewidoczny, gdy jestesmy na aktualnym miesiacu
            goToCurrentMonth.setVisibility(View.INVISIBLE);
        } else {
            goToCurrentMonth.setVisibility(View.VISIBLE);
        }

        /*
        // usun notatke
        if (Note.selectedNote.equals(null)) {
            deleteNote.setVisibility(View.INVISIBLE);
        } else {
            deleteNote.setVisibility(View.VISIBLE);
        }
         */
    }

    // ----------------------------------------
    // zmiana miesiaca po kliknieciu przyciskow
    public void previousMonth(View view) {
        CalendarOperations.selectedDate = CalendarOperations.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonth(View view) {
        CalendarOperations.selectedDate = CalendarOperations.selectedDate.plusMonths(1);
        setMonthView();
    }
    // ----------------------------------------

    // wybrana data
    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalendarOperations.selectedDate = date;
            setMonthView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNoteAdapter();
    }

    private void setNoteAdapter() {
        ArrayList<Note> dayilyNotes = Note.notesForDate(CalendarOperations.selectedDate);
        NoteAdapter noteAdapter = new NoteAdapter(getApplicationContext(), dayilyNotes);
        notesList.setAdapter(noteAdapter);
    }

    public void openAddNote() {
        Intent intent = new Intent(this, AddNote.class);
        Bundle bundle = new Bundle();
        bundle.putString("Login", login);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // wybrana notatka
    @Override
    public void onItemClick(int position, Note note) {
        System.out.println("click");
        if (note != null) {
            Note.selectedNote = note;
            System.out.println("toast");
            Toast.makeText(CalendarMain.this, note.toString(), Toast.LENGTH_LONG).show();
            setMonthView();
        }
    }
}