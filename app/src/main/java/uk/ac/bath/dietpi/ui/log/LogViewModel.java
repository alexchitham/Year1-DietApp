package uk.ac.bath.dietpi.ui.log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.widget.TextView;

public class LogViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LogViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Log");
    }

    public void updateText() {
        // Do something here
    }

    public LiveData<String> getText() {
        return mText;
    }


}