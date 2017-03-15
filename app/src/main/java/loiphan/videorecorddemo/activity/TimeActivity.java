package loiphan.videorecorddemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import loiphan.videorecorddemo.R;

/**
 * Copyright (c) 2017, Stacck Pte Ltd. All rights reserved.
 *
 * @author Lio <lphan@stacck.com>
 * @version 1.0
 * @since February 24, 2017
 */

public class TimeActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public static final String DATE_PICKER_DIALOG = "DatePickerDialog";

    @Bind(R.id.btnAction)
    Button btnAction;
    @Bind(R.id.txtLog)
    TextView txtLog;

    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        ButterKnife.bind(this);

        btnAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAction:
                showDateDialog();
                break;
        }
    }

    private void showDateDialog() {
        Calendar now = Calendar.getInstance();
        if (datePickerDialog == null) {
            datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }
        if (datePickerDialog.getDialog() != null && datePickerDialog.getDialog().isShowing()) {
            datePickerDialog.dismiss();
        }
        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setThemeDark(false);
        datePickerDialog.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.show(getFragmentManager(), DATE_PICKER_DIALOG);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
//        cal.add(Calendar.MINUTE, 1);
        Date date = cal.getTime();

        Date startDate = currentDateAtStart();
        Date endDate = currentDateAtEnd();

        Log.e("LIO", date + " | " + startDate + " | " + endDate);

        int dateTime = (int) (date.getTime() / 1000 / 60);
        int startTime = (int) (startDate.getTime() / 1000 / 60);
        int endTime = (int) (endDate.getTime() / 1000 / 60);
        Log.e("LIO", dateTime + "\n" + startTime + "\n" + endTime);

        int sub = (endTime - startTime);
        Log.e("LIO", "Sub " + sub);

        if (dateTime < startTime) {
            Log.e("LIO", "before");
        } else if (dateTime < endTime) {
            Log.e("LIO", "between");
        } else {
            Log.e("LIO", "after");
        }
    }

    public static Date currentDateAtStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MILLISECOND, 0);

        Date currentDate = cal.getTime();
        return currentDate;
    }

    public static Date currentDateAtEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
//        cal.add(Calendar.SECOND, -1);


        Date currentDate = cal.getTime();
        return currentDate;
    }
}
