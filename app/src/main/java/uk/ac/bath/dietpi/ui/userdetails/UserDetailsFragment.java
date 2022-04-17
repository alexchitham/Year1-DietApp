package uk.ac.bath.dietpi.ui.userdetails;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import uk.ac.bath.dietpi.DBHandler;
import uk.ac.bath.dietpi.MainActivity;
import uk.ac.bath.dietpi.databinding.FragmentUserDetailsBinding;

public class UserDetailsFragment extends Fragment {
    private FragmentUserDetailsBinding binding;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UserDetailsViewModel userDetailsViewModel =
                new ViewModelProvider(this).get(UserDetailsViewModel.class);

        binding = FragmentUserDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonUpdate.setOnClickListener(this::updateUserDetails);
        fillUserDetails(view);
    }

    public void updateUserDetails(View view) {
        // Hide Keyboard
        ((MainActivity) getActivity()).hideKeyboard(view);

        // Get dbHandler and userDetailsViewModel
        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();
        UserDetailsViewModel userDetailsViewModel =
                new ViewModelProvider(this).get(UserDetailsViewModel.class);

        // Get First name
        String firstName = binding.editTextFirstName.getText().toString();
        if (firstName.isEmpty()){
            userDetailsViewModel.setErrorMessage(view, "Must Enter first name", true);
            return;
        }

        // Get Age
        int age;
        try{
            age = Integer.parseInt(binding.editTextAge.getText().toString());
            if (age < 0){
                userDetailsViewModel.setErrorMessage(view,"Age must be postive!", true);
                return;
            }
        }
        catch(Exception ex){
            userDetailsViewModel.setErrorMessage(view, "Must enter numeric age!", true);
            return;
        }

        // Get gender
        int gender;
        int checkedButton = binding.RadioGroupGender.getCheckedRadioButtonId();
        if(checkedButton == binding.RadioButtonMale.getId()){
            gender = 0;
        }
        else if (checkedButton == binding.RadioButtonFemale.getId()){
            gender = 1;
        }
        else if (checkedButton == binding.RadioButtonOptOut.getId()){
            gender = 2;
        }
        else{
            userDetailsViewModel.setErrorMessage(view, "Gender Option hasn't been selected", true);
            return;
        }

        // Get Height
        double height;
        try{
            height = Double.parseDouble(binding.editTextHeight.getText().toString());
            if (height <= 0){
                userDetailsViewModel.setErrorMessage(view, "Must have postive height!", true);
                return;
            }
        }
        catch(Exception ex){
            userDetailsViewModel.setErrorMessage(view, "Must enter height!", true);
            return;
        }

        // Get Weight
        double weight;
        try{
            weight = Double.parseDouble(binding.editTextWeight.getText().toString());
            if (weight <= 0){
                userDetailsViewModel.setErrorMessage(view, "Must have postitive weight!", true);
                return;
            }
        }
        catch(Exception ex){
            userDetailsViewModel.setErrorMessage(view, "Must enter weight!", true);
            return;
        }

        // Write record to database if successful
        dbHandler.newUserDetails(firstName, age, gender, height, weight);
        userDetailsViewModel.setErrorMessage(view, "User Details updated!", false);
    }

    // Fill out user details with existing values
    public void fillUserDetails(View view){
        DBHandler dbHandler = ((MainActivity) getActivity()).getDbHandler();
        UserDetailsViewModel userDetailsViewModel =
                new ViewModelProvider(this).get(UserDetailsViewModel.class);

        ContentValues userDetails = dbHandler.getCurrentUserDetails();
        if (userDetails != null){
            // Set first name and age
            binding.editTextFirstName.setText(userDetails.get(DBHandler.getColumnFirstName()).toString());
            binding.editTextAge.setText(userDetails.get(DBHandler.getColumnAge()).toString());

            // Set gender
            int gender = Integer.parseInt(userDetails.get(DBHandler.getColumnGender()).toString());
            if (gender == 0){
                binding.RadioGroupGender.check(binding.RadioButtonMale.getId());
            }
            else if (gender == 1){
                binding.RadioGroupGender.check(binding.RadioButtonFemale.getId());
            }
            else{
                binding.RadioGroupGender.check(binding.RadioButtonOptOut.getId());
            }

            // Set height and weight
            binding.editTextHeight.setText(userDetails.get(DBHandler.getColumnHeight()).toString());
            binding.editTextWeight.setText(userDetails.get(DBHandler.getColumnWeight()).toString());
        }
    }

}