package com.festum.festumfield.TagView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;

import com.festum.festumfield.R;

public class ContactsCompletionView extends TokenCompleteTextView<Person> {

    InputConnection testAccessibleInputConnection;
    Person personToIgnore;

    public ContactsCompletionView(Context context) {
        super(context);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(Person person) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        token.setText(person.getName());
        return token;
    }

    @Override
    protected Person defaultObject(String completionText) {
        int index = completionText.indexOf('@');
        if (index == -1) {
            return new Person(completionText);
        } else {
            return new Person(completionText.substring(0, index));
        }
    }

    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        testAccessibleInputConnection = super.onCreateInputConnection(outAttrs);
        return testAccessibleInputConnection;
    }

    void setPersonToIgnore(Person person) {
        this.personToIgnore = person;
    }

    @Override
    public boolean shouldIgnoreToken(Person token) {
        return personToIgnore != null && personToIgnore.getName().equals(token.getName());
    }

    public void simulateSelectingPersonFromList(Person person) {
        convertSelectionToString(person);
        replaceText(currentCompletionText());
    }
}
