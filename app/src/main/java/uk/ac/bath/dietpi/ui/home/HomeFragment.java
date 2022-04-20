package uk.ac.bath.dietpi.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import uk.ac.bath.dietpi.DBHandler;
import uk.ac.bath.dietpi.MainActivity;
import uk.ac.bath.dietpi.R;
import uk.ac.bath.dietpi.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TableLayout tblTotals;
    private boolean activityCreated = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onSpinnerChange(AdapterView<?> parent, View view, int pos, long id) {
        switch (pos) {
            case 1:
                setLineGraphData(DBHandler.getColumnFat(), parent.getItemAtPosition(pos).toString());
                break;
            case 2:
                setLineGraphData(DBHandler.getColumnCarbohydrates(), parent.getItemAtPosition(pos).toString());
                break;
            case 3:
                setLineGraphData(DBHandler.getColumnProtein(), parent.getItemAtPosition(pos).toString());
                break;
            default:
                setLineGraphData(DBHandler.getColumnCalories(), parent.getItemAtPosition(pos).toString());
                break;

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String[] macronutrients = getResources().getStringArray(R.array.macronutrients);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, macronutrients);
        binding.graphViewField.setAdapter(arrayAdapter);

        binding.graphViewField.setOnItemClickListener(this::onSpinnerChange);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDaysConsumption(){
        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        Hashtable<String, Float> dailyConsumption = dbHandler.retrieveTotal();

        // Set each text view to show today's consumption
        binding.textCalories.setText(dailyConsumption.get(DBHandler.getColumnCalories()).toString());
        binding.textCarbohydrates.setText(dailyConsumption.get(DBHandler.getColumnCarbohydrates()).toString());
        binding.textProtein.setText(dailyConsumption.get(DBHandler.getColumnProtein()).toString());
        binding.textFat.setText(dailyConsumption.get(DBHandler.getColumnFat()).toString());

        // Get reference to the table of totals
        tblTotals = (TableLayout) binding.tblTotals;

        // Clears the table
        tblTotals.removeViews(1,Math.max(0,tblTotals.getChildCount()-1));

        String[] columns = {DBHandler.getColumnCalories(),DBHandler.getColumnFat(),DBHandler.getColumnCalories(),DBHandler.getColumnProtein()};
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Instant now = Instant.now();
        Context currentContext = getActivity();
        TableRow.LayoutParams textLayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        // Adds the totals for each of the last 14 days to the table
        for (int i = 0; i < 14; i++){
            String currentDateString = dateFormat.format(Date.from(now.minus(Duration.ofDays(i))));
            Hashtable<String,Float> values = dbHandler.retrieveTotal(currentDateString);
            TableRow tr = new TableRow(currentContext);

            // Adds the date
            TextView dateText = new TextView(currentContext);
            dateText.setText(currentDateString);
            dateText.setLayoutParams(textLayout);
            dateText.setTextSize(11);
            dateText.setPadding(10,10,10,10);
            dateText.setGravity(Gravity.CENTER_HORIZONTAL);
            tr.addView(dateText);

            // Adds each of the macronutrient values
            for (String column : columns){
                TextView textToAdd = new TextView(currentContext);
                textToAdd.setText(values.get(column).toString());
                textToAdd.setLayoutParams(textLayout);
                textToAdd.setTextSize(11);
                textToAdd.setPadding(10,10,10,10);
                textToAdd.setGravity(Gravity.CENTER_HORIZONTAL);
                tr.addView(textToAdd);
            }
            tblTotals.addView(tr);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setLineGraphData(String col, String label) {
        List<Entry> entries = new ArrayList<>();

        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Instant now = Instant.now();

        for (int i = 6; i >= 0; i--) {
            Date currentDate = Date.from(now.minus(Duration.ofDays(i)));

            String date = dateFormat.format(currentDate);
            float total = dbHandler.retrieveTotal(date).get(col);
            entries.add(new Entry(currentDate.getDate(), total));
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        LineData lineData = new LineData(dataSet);
        binding.lineChart.setData(lineData);
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.invalidate();
    }

    public void setPieGraphData() {
        List<PieEntry> entries = new ArrayList<>();

        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        Hashtable<String, Float> totals = dbHandler.retrieveTotal();
        totals.remove(DBHandler.getColumnCalories());

        if (Collections.frequency(totals.values(), 0.0f) == totals.size()) {
            binding.pieChart.setVisibility(View.GONE);
            return;
        }

        entries.add(new PieEntry(totals.get(DBHandler.getColumnFat()), "Fat"));
        entries.add(new PieEntry(totals.get(DBHandler.getColumnCarbohydrates()), "Carbohydrates"));
        entries.add(new PieEntry(totals.get(DBHandler.getColumnProtein()), "Protein"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[] { Color.rgb(255,51,51), Color.rgb(51,181,255), Color.rgb(51,100,175) });
        PieData pieData = new PieData(dataSet);

        binding.pieChart.setVisibility(View.VISIBLE);
        binding.pieChart.setData(pieData);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activityCreated = true;
        updateDaysConsumption();
        setLineGraphData(DBHandler.getColumnCalories(), "Calories (kcal)");
        setPieGraphData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        if (activityCreated){
            binding.graphViewField.setText("Calories (kcal)");

            String[] macronutrients = getResources().getStringArray(R.array.macronutrients);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, macronutrients);
            binding.graphViewField.setAdapter(arrayAdapter);
            setLineGraphData(DBHandler.getColumnCalories(), "Calories (kcal)");
            setPieGraphData();

            updateDaysConsumption();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}