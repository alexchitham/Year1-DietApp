package uk.ac.bath.dietpi.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

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

    public void setLineGraphData(String col, String label) {
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

        LineDataSet dataSet = new LineDataSet(entries, label);
        LineData lineData = new LineData(dataSet);
        binding.lineChart.setData(lineData);
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.invalidate();
    }

    public void setPieGraphData() {
        List<PieEntry> entries = new ArrayList<>();

        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        entries.add(new PieEntry(dbHandler.retrieveTotal().get(DBHandler.getColumnFat()), "Fat"));
        entries.add(new PieEntry(dbHandler.retrieveTotal().get(DBHandler.getColumnCarbohydrates()), "Carbohydrates"));
        entries.add(new PieEntry(dbHandler.retrieveTotal().get(DBHandler.getColumnProtein()), "Protein"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[] { Color.rgb(255,51,51), Color.rgb(51,181,255), Color.rgb(51,100,175) });
        PieData pieData = new PieData(dataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.invalidate();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activityCreated = true;
        updateDaysConsumption();
        setLineGraphData(DBHandler.getColumnCalories(), "Calories (kcal)");
        setPieGraphData();
    }

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