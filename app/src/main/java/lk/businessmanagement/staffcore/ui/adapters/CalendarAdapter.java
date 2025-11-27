package lk.businessmanagement.staffcore.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private Context context;
    private List<Date> dateList;
    private Date selectedDate;
    private Calendar currentVisibleMonth; // To identify days outside the current month
    private OnDateClickListener listener;

    // Interface for click events
    public interface OnDateClickListener {
        void onDateClick(Date date);
    }

    // Constructor
    public CalendarAdapter(Context context, List<Date> dateList, Date selectedDate, Calendar currentVisibleMonth, OnDateClickListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.selectedDate = selectedDate;
        this.currentVisibleMonth = currentVisibleMonth;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for a single day cell
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Date date = dateList.get(position);

        // Format the day number (e.g., "01", "15", "30")
        SimpleDateFormat dayNumberFormat = new SimpleDateFormat("d", Locale.getDefault());
        holder.tvDayNumber.setText(dayNumberFormat.format(date));

        // -- Highlight Logic --

        // 1. Check if this date is the Selected Date
        if (isSameDay(date, selectedDate)) {
            // Selected: Gold Circle Background, Black Text
            holder.tvDayNumber.setBackgroundResource(R.drawable.bg_calendar_selected); // Requires oval drawable
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(context, R.color.black));
        } else {
            // Not Selected: No Background
            holder.tvDayNumber.setBackgroundResource(0); // Transparent

            // 2. Check if the date belongs to the Current Month being viewed
            if (isSameMonth(date, currentVisibleMonth.getTime())) {
                // Current Month: White Text
                holder.tvDayNumber.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                // Previous/Next Month: Grey/Dimmed Text
                holder.tvDayNumber.setTextColor(ContextCompat.getColor(context, R.color.white_10)); // Or text_secondary
            }
        }

        // Click Listener
        holder.itemView.setOnClickListener(v -> {
            listener.onDateClick(date);
            // Update selected date internally and refresh UI
            selectedDate = date;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    // Helper to check if two dates are the same day
    private boolean isSameDay(Date d1, Date d2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return fmt.format(d1).equals(fmt.format(d2));
    }

    // Helper to check if two dates are in the same month
    private boolean isSameMonth(Date d1, Date d2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        return fmt.format(d1).equals(fmt.format(d2));
    }

    // Method to update data from Activity
    public void updateData(List<Date> newDates, Date newSelected, Calendar newMonth) {
        this.dateList = newDates;
        this.selectedDate = newSelected;
        this.currentVisibleMonth = newMonth;
        notifyDataSetChanged();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
        }
    }
}