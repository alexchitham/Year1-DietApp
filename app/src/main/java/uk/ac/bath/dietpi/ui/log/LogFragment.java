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
        binding.addFoodBT.setOnClickListener(this::buttonOnClick);
    }

    public void buttonOnClick(View v) {
        LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);

        // Update text
        binding.textView.setText("button clicked!");

        // Call methods in update text
        logViewModel.updateText();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}