package lk.businessmanagement.staffcore.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.model.Attendance;

public class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<Attendance> historyList;

    public AttendanceHistoryAdapter(Context context, List<Attendance> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Attendance att = historyList.get(position);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(att.getDate());

            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

            holder.tvDay.setText(dayFormat.format(date));
            holder.tvMonth.setText(monthFormat.format(date).toUpperCase());

        } catch (ParseException e) {
            holder.tvDay.setText("--");
            holder.tvMonth.setText("-");
            e.printStackTrace();
        }

        if (att.isLeave()) {
            holder.tvStatus.setText("Leave");

            int redColor = ContextCompat.getColor(context, R.color.status_leave_text);
            int redBg = ContextCompat.getColor(context, R.color.status_leave_bg);

            holder.tvStatus.setTextColor(redColor);
            holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(redBg));

            holder.tvTitle.setText("Reason: " + att.getLeaveReason());
            holder.tvSubtitle.setText("Absent");
            holder.tvSubtitle.setTextColor(redColor);

        } else {
            holder.tvStatus.setText("Present");

            int greenColor = ContextCompat.getColor(context, R.color.status_active_text);
            int greenBg = ContextCompat.getColor(context, R.color.status_active_bg);

            holder.tvStatus.setTextColor(greenColor);
            holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(greenBg));

            String inTime = (att.getInTime() != null && !att.getInTime().isEmpty()) ? att.getInTime() : "--:--";
            String outTime = (att.getOutTime() != null && !att.getOutTime().isEmpty()) ? att.getOutTime() : "--:--";

            holder.tvTitle.setText(inTime + " - " + outTime);
            holder.tvSubtitle.setText("Working Day");
            holder.tvSubtitle.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvMonth, tvTitle, tvSubtitle, tvStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}