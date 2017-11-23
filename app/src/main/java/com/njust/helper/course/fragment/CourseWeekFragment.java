package com.njust.helper.course.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.njust.helper.R;
import com.njust.helper.model.Course;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseWeekFragment extends Fragment {
    @BindView(R.id.textView1)
    TextView textView;
    @BindView(R.id.courseView)
    CourseView courseView;

    private long beginTimeInMillis;
    private SimpleDateFormat dateFormat;
    private Listener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fgmt_course_week, container, false);
        ButterKnife.bind(this, view);
        courseView.setListener(listener::showCourseList);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        dateFormat = new SimpleDateFormat(getString(R.string.date_course_week), Locale.CHINA);
        super.onAttach(context);
        listener = (Listener) context;
    }

    private String getTime(int week) {
        long time = beginTimeInMillis + (week - 1) * 604800000L;
        return convert(time) + "~" + convert(time + 518400000L);
    }

    private String convert(long millis) {
        return dateFormat.format(new Date(millis));
    }

    public void setWeek(int week) {
        textView.setText(getTime(week));
        courseView.setWeek(week);
    }

    public void setList(List<Course> courses) {
        courseView.setCourses(courses);
    }

    public void setBeginTimeInMillis(long time) {
        beginTimeInMillis = time;
    }

    public interface Listener {
        void showCourseList(List<Course> courses, int day, int section);
    }
}
