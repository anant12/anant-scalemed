package com.riceucla.mobilelogger;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Dialog fragment class for the pop-up mood reminder.
 * Currently, no actions are associated with the spinner items and submit button.
 * TODO: Add onClickListeners for spinner items
 * TODO: Add submit button functionality
 * Both of the above will addressed once I have more information on how this data should be packaged, logged, and sent to the server.
 *
 * @author Kevin Lin
 * @since 10/12/2014
 */
public class MoodReminderDialog extends DialogFragment {

    private EditText mEditText;

    public MoodReminderDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get current view
        View view = inflater.inflate(R.layout.fragment_mood_reminder, container);
        // initialize mood spinner
        Spinner moodSpinner = (Spinner)view.findViewById(R.id.mood_spinner);
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.mood_array, android.R.layout.simple_spinner_item);
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(moodAdapter);
        // set dialog title
        getDialog().setTitle("What's your current mood?");

        return view;
    }
}
