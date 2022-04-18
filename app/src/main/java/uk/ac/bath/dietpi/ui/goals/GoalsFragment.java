package uk.ac.bath.dietpi.ui.goals;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Hashtable;

import uk.ac.bath.dietpi.DBHandler;
import uk.ac.bath.dietpi.MainActivity;
import uk.ac.bath.dietpi.R;
import uk.ac.bath.dietpi.databinding.FragmentGoalsBinding;

public class GoalsFragment extends Fragment {

    private FragmentGoalsBinding binding;
    private EditText editTextGoal;
    private Button btnChangeGoal;
    private TextView textDisplayGoal;
    private TextView displayProgressTextView;
    private AutoCompleteTextView autoCompleteTextView;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    private String savedText;
    private String selectedMacronutrient;

    @Override
    public void onResume() {
        super.onResume();
        String[] macronutrients = getResources().getStringArray(R.array.macronutrients);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, macronutrients);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGoalsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String[] macronutrients = getResources().getStringArray(R.array.macronutrients);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, macronutrients);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);

        editTextGoal = binding.editTextGoal;
        btnChangeGoal = binding.btnChangeGoal;
        textDisplayGoal = binding.textDisplayGoal;
        displayProgressTextView = binding.displayProgressTextView;
        autoCompleteTextView = binding.autoCompleteTextView;

        setSelection();
        displayCurrentProgress();

        btnChangeGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newGoal = editTextGoal.getText().toString();
                if(!newGoal.equals(""))
                {
                    saveData();
                    setCurrentGoalText(newGoal);
                    saveData();
                    displayCurrentProgress();
                }
            }
        });

        loadData();

        return root;
    }

    public void setSelection()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String getMacronutrient = sharedPreferences.getString(selectedMacronutrient, "");

        if(!getMacronutrient.equals(null))
        {
            autoCompleteTextView.setText(getMacronutrient);
        }
        else
        {
            autoCompleteTextView.setText("Calories (kcal)");
        }
    }

    public void setCurrentGoalText(String newGoal)
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String getMacronutrient = sharedPreferences.getString(selectedMacronutrient, "");

        textDisplayGoal.setText(newGoal + " " + getMacronutrient);
        editTextGoal.setText("");
    }

    public void displayCurrentProgress()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String getMacronutrient = sharedPreferences.getString(selectedMacronutrient, "");
        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        Hashtable<String,Float> hT = dbHandler.retrieveTotal();
        String calorie_count;

        if(getMacronutrient.equals("Calories (kcal)"))
        {
            calorie_count = hT.get("Calories").toString();
        }
        else if(getMacronutrient.equals("Fat (g)"))
        {
            calorie_count = hT.get("Fat").toString();
        }
        else if(getMacronutrient.equals("Protein (g)"))
        {
            calorie_count = hT.get("Protein").toString();
        }
        else if(getMacronutrient.equals("Carbs (g)"))
        {
            calorie_count = hT.get("Carbohydrates").toString();
        }
        else
        {
            calorie_count = "";
        }

        /*
        if(!calorie_count.equals(""))
        {
            Integer integer_calorie_count = Integer.parseInt(calorie_count);


            String currentGoal = textDisplayGoal.getText().toString().replace(" Calories", "");
            Integer integerGoal = Integer.parseInt(currentGoal);
            Integer percentage = Math.round(integerGoal/integer_calorie_count);
        }
        */

        displayProgressTextView.setText(calorie_count + " " + getMacronutrient);

    }

    public void saveData()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, textDisplayGoal.getText().toString());
        editor.putString(selectedMacronutrient, autoCompleteTextView.getText().toString());

        editor.apply();
    }

    public void loadData()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        savedText = sharedPreferences.getString(TEXT, "");
        textDisplayGoal.setText(savedText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String getMacronutrient = sharedPreferences.getString(selectedMacronutrient, "");

        String currentNutrient = autoCompleteTextView.getText().toString();
        if(currentNutrient.equals(getMacronutrient))
        {
            saveData();
        }
        binding = null;
    }
}