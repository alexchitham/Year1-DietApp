package uk.ac.bath.dietpi.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import uk.ac.bath.dietpi.DBHandler;
import uk.ac.bath.dietpi.MainActivity;
import uk.ac.bath.dietpi.R;
import uk.ac.bath.dietpi.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView txtCalories;
    private TextView txtCarbohydrates;
    private TextView txtProtein;
    private TextView txtFat;
    private boolean activityCreated = false;

    private void onSpinnerChange(AdapterView<?> parent, View view, int pos, long id) {
        switch (pos) {
            case 1:
                setGraphData(DBHandler.getColumnFat(), parent.getItemAtPosition(pos).toString());
                break;
            case 2:
                setGraphData(DBHandler.getColumnCarbohydrates(), parent.getItemAtPosition(pos).toString());
                break;
            case 3:
                setGraphData(DBHandler.getColumnProtein(), parent.getItemAtPosition(pos).toString());
                break;
            default:
                setGraphData(DBHandler.getColumnCalories(), parent.getItemAtPosition(pos).toString());
                break;

        }
    }


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

    public void updateDaysConsumption(){
        // Get hashtable containing amount of each nutrient eaten today
        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        Hashtable<String, Float> dailyConsumption = dbHandler.retrieveTotal();

        // Set each text view to show today's consumption
        binding.textCalories.setText(dailyConsumption.get(DBHandler.getColumnCalories()).toString());
        binding.textCarbohydrates.setText(dailyConsumption.get(DBHandler.getColumnCarbohydrates()).toString());
        binding.textProtein.setText(dailyConsumption.get(DBHandler.getColumnProtein()).toString());
        binding.textFat.setText(dailyConsumption.get(DBHandler.getColumnFat()).toString());

        Log.d("Debug", dailyConsumption.toString());
    }

    public void setGraphData(String col, String label) {
        List<Entry> entries = new ArrayList<>();

        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);

        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            String date = dateFormat.format(cal.getTime());
            float total = dbHandler.retrieveTotal(date).get(col);
            entries.add(new Entry(cal.getTime().getDate(), total));
        }

        System.out.println(entries);

        LineDataSet dataSet = new LineDataSet(entries, label);
        LineData lineData = new LineData(dataSet);
        binding.lineChart.setData(lineData);
        binding.lineChart.invalidate();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activityCreated = true;
        updateDaysConsumption();
        setGraphData(DBHandler.getColumnCalories(), "Calories (kcal)");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityCreated){
            binding.graphViewField.setText("Calories (kcal)");

            String[] macronutrients = getResources().getStringArray(R.array.macronutrients);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, macronutrients);
            binding.graphViewField.setAdapter(arrayAdapter);
            setGraphData(DBHandler.getColumnCalories(), "Calories (kcal)");

            updateDaysConsumption();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}