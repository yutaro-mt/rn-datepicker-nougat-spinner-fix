package com.yutaro_mt.reactnative.datepicker.fix;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.DatePicker;
import com.facebook.react.modules.datepicker.DatePickerDialogFragment;
import com.facebook.react.modules.datepicker.DatePickerMode;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerDialogFragmentFix extends DatePickerDialogFragment {

  static final String MODE = "mode";
  static final String DATE = "date";

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    overwrite(dialog, getArguments());
    return dialog;
  }

  public Dialog overwrite(Dialog dialog, Bundle args){
    if (args == null || dialog == null || args.getString(MODE, null) == null) {
      return dialog;
    }
    DatePickerMode mode = DatePickerMode.valueOf(args.getString(MODE).toUpperCase(Locale.US));
    if ((Build.VERSION.SDK_INT == Build.VERSION_CODES.N)&&
        (mode == DatePickerMode.SPINNER)) {
      //set date
      final Calendar calendar = Calendar.getInstance();
      if (args != null && args.containsKey(DATE)) {
        calendar.setTimeInMillis(args.getLong(DATE));
      }
      final int year = calendar.get(Calendar.YEAR);
      final int month = calendar.get(Calendar.MONTH);
      final int day = calendar.get(Calendar.DAY_OF_MONTH);
      try {
        Field datePickerField = DatePickerDialog.class.getDeclaredField("mDatePicker");
        datePickerField.setAccessible(true);
        DatePicker datePicker = (DatePicker)datePickerField.get(dialog);
        Field delegateField = DatePicker.class.getDeclaredField("mDelegate");
        delegateField.setAccessible(true);
        Object delegate = delegateField.get(datePicker);
        Class<?> spinnerDelegateClass = Class.forName("android.widget.DatePickerSpinnerDelegate");
        if (delegate.getClass() != spinnerDelegateClass) {
          //release default objects
          delegateField.set(datePicker, null);
          datePicker.removeAllViews();
          //update
          Constructor spinnerDelegateConstructor = spinnerDelegateClass.getDeclaredConstructor(DatePicker.class, Context.class, AttributeSet.class, int.class, int.class);
          spinnerDelegateConstructor.setAccessible(true);
          delegate = spinnerDelegateConstructor.newInstance(datePicker, getActivity(), null, android.R.attr.datePickerStyle, 0);
          delegateField.set(datePicker, delegate);

          datePicker.setCalendarViewShown(false);
          datePicker.updateDate(year, month, day);
        }
      } catch (Exception e) {
      }
    }
    return dialog;
  }
}
