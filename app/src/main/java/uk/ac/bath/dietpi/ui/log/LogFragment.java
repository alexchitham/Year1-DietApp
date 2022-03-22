package uk.ac.bath.dietpi.ui.log;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
    }

    public void addNewFood(View v) {
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

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getRootView().getWindowToken(), 0);
        } catch (Exception ex) {
            // change this to a different textView
            logViewModel.setErrorMessage(v, "Must enter nutritional info", true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}