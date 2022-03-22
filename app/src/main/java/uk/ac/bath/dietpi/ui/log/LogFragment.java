package uk.ac.bath.dietpi.ui.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import uk.ac.bath.dietpi.MainActivity;
import uk.ac.bath.dietpi.databinding.FragmentLogBinding;

public class LogFragment extends Fragment {

    private FragmentLogBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);

        binding = FragmentLogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        binding.addFoodBT.setOnClickListener(this::addNewFood);
    }

    public void addNewFood(View v) {
        LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);

        String foodName = binding.editTextName.getText().toString();

        try {
            double calories = Double.parseDouble(binding.editTextCalories.getText().toString());
            double carbohydrates = Double.parseDouble(binding.editTextCarbohydrates.getText().toString());
            double protein = Double.parseDouble(binding.editTextProtein.getText().toString());
            double fat = Double.parseDouble(binding.editTextFat.getText().toString());

            // Call database insert function
            ((MainActivity) getActivity()).getDbHandler().insert(foodName, calories, carbohydrates, protein, fat);
            logViewModel.updateText(v, "Inserted");
        } catch (Exception ex) {
            // change this to a different textView
            logViewModel.updateText(v, "Must be numbers");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}