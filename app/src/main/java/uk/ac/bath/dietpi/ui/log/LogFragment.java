package uk.ac.bath.dietpi.ui.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        final TextView textView = binding.textDashboard;
        logViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void buttonOnClick(View v) {
        Button button = (Button) v;
        ((Button) v).setText("clicked");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}