package uk.ac.bath.dietpi.ui.log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.view.View;
import android.widget.TextView;

import uk.ac.bath.dietpi.R;

public class LogViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LogViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Log");
    }

    public void updateText(View v, String str) {
        TextView textView = v.getRootView().findViewById(R.id.textView);
        textView.setText(str);
    }

    public LiveData<String> getText() {
        return mText;
    }


}