package uk.ac.bath.dietpi.ui.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
        String calories = binding.editTextCalories.getText().toString();
        String carbohydrates = binding.editTextCarbohydrates.getText().toString();
        String protein = binding.editTextProtein.getText().toString();
        String fat = binding.editTextFat.getText().toString();

        // Call methods in update text (testing)
        logViewModel.updateText(v, foodName);

        // Call database insert function
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}