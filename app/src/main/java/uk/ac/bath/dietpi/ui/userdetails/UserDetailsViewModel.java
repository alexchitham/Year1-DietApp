package uk.ac.bath.dietpi.ui.userdetails;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import uk.ac.bath.dietpi.R;

public class UserDetailsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UserDetailsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("User Details");
    }
    public LiveData<String> getText() {
        return mText;
    }

    public void setErrorMessage(View v, String str, Boolean isError) {
        TextView textView = v.getRootView().findViewById(R.id.textViewError);

        if (isError) {
            textView.setTextColor(Color.parseColor("#cf000f"));
        } else {
            textView.setTextColor(Color.parseColor("#009944"));
        }

        textView.setText(str);
    }
}