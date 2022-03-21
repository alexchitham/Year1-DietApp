package uk.ac.bath.dietpi.ui.log;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.os.Bundle;
import android.view.View;

public class LogViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LogViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Log");

        //Button addFoodBT = (Button) findViewById(R.id.button2);
    }

    public LiveData<String> getText() {
        return mText;
    }


}