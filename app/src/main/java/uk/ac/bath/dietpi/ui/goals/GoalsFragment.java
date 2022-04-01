package uk.ac.bath.dietpi.ui.goals;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.util.Hashtable;

import uk.ac.bath.dietpi.DBHandler;
import uk.ac.bath.dietpi.MainActivity;
import uk.ac.bath.dietpi.databinding.FragmentGoalsBinding;

public class GoalsFragment extends Fragment {

    private FragmentGoalsBinding binding;
    private EditText editTextGoal;
    private Button btnChangeGoal;
    private TextView textDisplayGoal;
    private TextView displayProgressTextView;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    private String savedText;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGoalsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editTextGoal = binding.editTextGoal;
        btnChangeGoal = binding.btnChangeGoal;
        textDisplayGoal = binding.textDisplayGoal;
        displayProgressTextView = binding.displayProgressTextView;

        btnChangeGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newGoal = editTextGoal.getText().toString();
                if(!newGoal.equals(""))
                {
                    textDisplayGoal.setText(newGoal + " Calories");
                    editTextGoal.setText("");
                    saveData();
                }
            }
        });

        loadData();
        displayCurrentProgress();

        return root;
    }

    public void displayCurrentProgress()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();

        Hashtable<String,Float> hT = dbHandler.retrieveTotal();

        String calorie_count = hT.get("Calories").toString();

        /*
        if(!calorie_count.equals(""))
        {
            Integer integer_calorie_count = Integer.parseInt(calorie_count);


            String currentGoal = textDisplayGoal.getText().toString().replace(" Calories", "");
            Integer integerGoal = Integer.parseInt(currentGoal);
            Integer percentage = Math.round(integerGoal/integer_calorie_count);
        }
        */

        displayProgressTextView.setText(calorie_count + " Calories");

    }

    public void saveData()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, textDisplayGoal.getText().toString());

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
        binding = null;
    }
}