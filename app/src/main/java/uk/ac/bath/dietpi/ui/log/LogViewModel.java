package uk.ac.bath.dietpi.ui.log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import uk.ac.bath.dietpi.R;

public class LogViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LogViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Log");
    }

    public void setErrorMessage(View v, String str, Boolean isError) {
        TextView textView = v.getRootView().findViewById(R.id.errorMessage);

        if (isError) {
            textView.setTextColor(Color.parseColor("#cf000f"));
        } else {
            textView.setTextColor(Color.parseColor("#009944"));
        }

        textView.setText(str);
    }

    public LiveData<String> getText() {
        return mText;
    }


}