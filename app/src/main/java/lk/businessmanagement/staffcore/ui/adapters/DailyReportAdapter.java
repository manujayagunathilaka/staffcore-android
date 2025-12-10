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

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.model.DailyAttendance;

public class DailyReportAdapter extends RecyclerView.Adapter<DailyReportAdapter.ReportViewHolder> {

    private Context context;
    private List<DailyAttendance> reportList;
    private OnItemClickListener listener; // Click Listener Variable

    // 1. Interface for Click Event
    public interface OnItemClickListener {
        void onItemClick(DailyAttendance item);
    }

    // 2. Updated Constructor (Accepts Listener)
    public DailyReportAdapter(Context context, List<DailyAttendance> reportList, OnItemClickListener listener) {
        this.context = context;
        this.reportList = reportList;
        this.listener = listener;
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
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_default_avatar);
        }

        // Get the card view container (Make sure XML has ID: cardContainer)
        // If your XML root layout doesn't have an ID, you might need to add it or use holder.itemView
        View cardView = holder.itemView.findViewById(R.id.cardContainer);
        // If cardContainer ID is missing in XML, we use itemView directly for background,
        // BUT assuming item_daily_status has a nested layout for background.
        // Let's use the layout inside the FrameLayout which has the background.
        View backgroundView = (ViewGroup) holder.itemView;
        if (holder.itemView instanceof ViewGroup && ((ViewGroup) holder.itemView).getChildCount() > 0) {
            backgroundView = ((ViewGroup) holder.itemView).getChildAt(0);
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

            setCardBorder(backgroundView, R.color.status_green); // Green Border

        } else if (item.getStatus() == 2) {
            // Leave (Red)
            holder.tvStatus.setText("Leave");
            setColors(holder.tvStatus, R.color.status_leave_text, R.color.status_leave_bg);
            holder.tvTime.setText("On Leave");

            setCardBorder(backgroundView, R.color.status_red); // Red Border

        } else {
            // Not Marked (Grey)
            holder.tvStatus.setText("Pending");
            setColors(holder.tvStatus, R.color.white_50, R.color.white_10);
            holder.tvTime.setText("Not marked yet");

            setCardBorder(backgroundView, R.color.glass_stroke); // Default Border
        }

        // 3. Click Listener Implementation
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    // Helper to change text/bg colors
    private void setColors(TextView textView, int textColorRes, int bgTintRes) {
        textView.setTextColor(ContextCompat.getColor(context, textColorRes));
        textView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, bgTintRes)));
    }

    // Helper to change Card Border Color
    private void setCardBorder(View view, int colorResId) {
        android.graphics.drawable.GradientDrawable border = new android.graphics.drawable.GradientDrawable();
        border.setColor(Color.parseColor("#1AFFFFFF")); // Background 10% White
        border.setCornerRadius(dpToPx(12)); // Radius matching XML

        int strokeColor = ContextCompat.getColor(context, colorResId);
        border.setStroke(dpToPx(1), strokeColor); // Border Width & Color

        view.setBackground(border);
    }

    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
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