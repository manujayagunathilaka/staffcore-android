package lk.businessmanagement.staffcore.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.model.DailyAttendance;

public class DailyReportAdapter extends RecyclerView.Adapter<DailyReportAdapter.ReportViewHolder> {

    private Context context;
    private List<DailyAttendance> reportList;

    public DailyReportAdapter(Context context, List<DailyAttendance> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_status, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        DailyAttendance item = reportList.get(position);

        holder.tvName.setText(item.getEmployeeName());

        // Load Image
        if (item.getPhotoPath() != null && !item.getPhotoPath().isEmpty()) {
            Glide.with(context).load(item.getPhotoPath())
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        // --- STATUS LOGIC ---
        // 1=Present, 2=Leave, 3=Not Marked

        if (item.getStatus() == 1) {
            // Present (Green)
            holder.tvStatus.setText("Present");
            setColors(holder.tvStatus, R.color.status_active_text, R.color.status_active_bg);

            String time = (item.getInTime() != null ? item.getInTime() : "--") + " - " +
                    (item.getOutTime() != null ? item.getOutTime() : "--");
            holder.tvTime.setText(time);

        } else if (item.getStatus() == 2) {
            // Leave (Red)
            holder.tvStatus.setText("Leave");
            setColors(holder.tvStatus, R.color.status_leave_text, R.color.status_leave_bg);
            holder.tvTime.setText("On Leave");

        } else {
            // Not Marked (Grey)
            holder.tvStatus.setText("Pending");
            // Using generic grey colors (assuming text_secondary is grey)
            setColors(holder.tvStatus, R.color.white_50, R.color.white_10);
            holder.tvTime.setText("Not marked yet");
        }
    }

    // Helper to change colors easily
    private void setColors(TextView textView, int textColorRes, int bgTintRes) {
        textView.setTextColor(ContextCompat.getColor(context, textColorRes));
        // If bgTintRes is a drawable resource ID, usually we use backgroundTintList,
        // but here we assume color resources for simplicity or drawable tinting.
        // For strict color tinting:
        textView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, bgTintRes)));
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvStatus;
        CircleImageView imgProfile;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }
    }
}