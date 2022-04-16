package uk.ac.bath.dietpi.ui.userdetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserDetailsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UserDetailsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("User Details");
    }
    public LiveData<String> getText() {
        return mText;
    }


}