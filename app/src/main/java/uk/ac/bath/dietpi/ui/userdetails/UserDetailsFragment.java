package uk.ac.bath.dietpi.ui.userdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
}