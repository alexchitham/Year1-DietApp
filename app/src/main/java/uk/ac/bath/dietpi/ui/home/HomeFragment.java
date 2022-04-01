package uk.ac.bath.dietpi.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Hashtable;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    public void updateDaysConsumption(){
        // Get hashtable containing amount of each nutrient eaten today
        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        Hashtable<String, Float> dailyConsumption = dbHandler.retrieveTotal();

        View root = binding.getRoot();

        // Get references to each text view object
        txtCalories = (TextView) root.findViewById(R.id.text_calories);
        txtCarbohydrates = (TextView) root.findViewById(R.id.text_carbohydrates);
        txtProtein = (TextView) root.findViewById(R.id.text_protein);
        txtFat = (TextView) root.findViewById(R.id.text_fat);

        // Set each text view to show today's consumption
        txtCalories.setText("Calories: " + dailyConsumption.get(DBHandler.getColumnCalories()).toString() + "kcal");
        txtCarbohydrates.setText("Carbohydrates: " + dailyConsumption.get(DBHandler.getColumnCarbohydrates()).toString() + "g");
        txtProtein.setText("Protein: " + dailyConsumption.get(DBHandler.getColumnProtein()).toString() + "g");
        txtFat.setText("Fat: " + dailyConsumption.get(DBHandler.getColumnFat()).toString() + "g");

        Log.d("Debug", dailyConsumption.toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityCreated = true;
        updateDaysConsumption();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityCreated){
            updateDaysConsumption();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}