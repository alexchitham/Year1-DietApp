package uk.ac.bath.dietpi.ui.log;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.ac.bath.dietpi.DBHandler;
import uk.ac.bath.dietpi.MainActivity;
import uk.ac.bath.dietpi.R;
import uk.ac.bath.dietpi.databinding.FragmentLogBinding;

public class LogFragment extends Fragment {

    private FragmentLogBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);

        binding = FragmentLogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        TableLayout table = binding.intakeTable;

        for (ContentValues cn : dbHandler.retrieveTable()) {
            TableRow row = createRow(cn.get(dbHandler.getColumnFoodName()).toString(),
                    cn.get(dbHandler.getColumnCalories()).toString(),
                    cn.get(dbHandler.getColumnCarbohydrates()).toString(),
                    cn.get(dbHandler.getColumnProtein()).toString(),
                    cn.get(dbHandler.getColumnFat()).toString());
            table.addView(row);
        }

        table.requestLayout();

        return root;
    }

    public TableRow createRow(String foodName, String calories, String carbohydrates, String protein, String fat) {
        TableRow row = (TableRow) this.getLayoutInflater().inflate(R.layout.table_row_log, null);
        ((TextView)row.findViewById(R.id.intakeFoodName)).setText(foodName);
        ((TextView)row.findViewById(R.id.intakeCalories)).setText(calories);
        ((TextView)row.findViewById(R.id.intakeFat)).setText(fat);
        ((TextView)row.findViewById(R.id.intakeCarbohydrates)).setText(carbohydrates);
        ((TextView)row.findViewById(R.id.intakeProtein)).setText(protein);

        return row;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        binding.addFoodBT.setOnClickListener(this::addNewFood);
        binding.autofillDataBT.setOnClickListener(this::autofillNutritionInfo);
    }

    public void addNewFood(View v) {
        ((MainActivity) getActivity()).hideKeyboard(v);

        LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);

        String foodName = binding.editTextName.getText().toString();

        if (foodName.isEmpty()) {
            logViewModel.setErrorMessage(v, "Must enter food name", true);
            return;
        }

        try {
            String calories = binding.editTextCalories.getText().toString();
            String carbohydrates = binding.editTextCarbohydrates.getText().toString();
            String protein = binding.editTextProtein.getText().toString();
            String fat = binding.editTextFat.getText().toString();

            // Call database insert function
            ((MainActivity) getActivity()).getDbHandler().insert(foodName,
                    Double.parseDouble(calories),
                    Double.parseDouble(carbohydrates),
                    Double.parseDouble(protein),
                    Double.parseDouble(fat));
            logViewModel.setErrorMessage(v, "New food added", false);

            TableLayout table = binding.intakeTable;

            TableRow row = createRow(foodName,
                    calories,
                    carbohydrates,
                    protein,
                    fat);
            table.addView(row);
            table.requestLayout();

            binding.editTextName.setText("");
            binding.editTextCalories.setText("");
            binding.editTextCarbohydrates.setText("");
            binding.editTextProtein.setText("");
            binding.editTextFat.setText("");
        } catch (Exception ex) {
            // change this to a different textView
            logViewModel.setErrorMessage(v, "Must enter nutritional info", true);
        }
    }

    public void autofillNutritionInfo(View v) {
        ((MainActivity) getActivity()).hideKeyboard(v);

        LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);

        StringBuilder result = new StringBuilder();

        String foodQuery = binding.editTextQuantity.getText().toString() + " " + binding.editTextName.getText().toString();

        new Thread() {
            public void run() {
                try {
                    URL reqURL = new URL("https://api.calorieninjas.com/v1/nutrition?query=" + foodQuery);
                    HttpURLConnection connection = (HttpURLConnection) reqURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("accept", "application/json");
                    connection.setRequestProperty("X-Api-Key", "Aa4izejrh55AJvZtG3MyhA==1EYsxuhcAPied541");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );

                    for (String line; (line = reader.readLine()) != null; ) {
                        result.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject foodData = new JSONObject(result.toString());
                            JSONObject nutrientData = foodData.getJSONArray("items").getJSONObject(0);

                            binding.editTextCalories.setText(nutrientData.get("calories").toString());
                            binding.editTextCarbohydrates.setText(nutrientData.get("carbohydrates_total_g").toString());
                            binding.editTextProtein.setText(nutrientData.get("protein_g").toString());
                            binding.editTextFat.setText(nutrientData.get("fat_total_g").toString());

                            logViewModel.setErrorMessage(v, "Autofilled data for " + nutrientData.get("name").toString() + " (" + nutrientData.get("serving_size_g") + "g)", false);
                        } catch (Exception ex) {
                            binding.editTextCalories.setText("");
                            binding.editTextCarbohydrates.setText("");
                            binding.editTextProtein.setText("");
                            binding.editTextFat.setText("");

                            logViewModel.setErrorMessage(v, "Food not in database", true);
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}