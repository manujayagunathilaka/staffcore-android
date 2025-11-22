package lk.businessmanagement.staffcore.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.model.Employee;
import lk.businessmanagement.staffcore.ui.EmployeeProfileActivity;
import lk.businessmanagement.staffcore.ui.MarkAttendanceActivity;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private Context context;
    private List<Employee> employeeList;

    // New variable to store the mode
    private boolean isAttendanceMode;

    // Updated Constructor: Now accepts 3 parameters
    public EmployeeAdapter(Context context, List<Employee> employeeList, boolean isAttendanceMode) {
        this.context = context;
        this.employeeList = employeeList;
        this.isAttendanceMode = isAttendanceMode; // Save the mode
    }

    // Overloaded Constructor for backward compatibility (Optional but good practice)
    // If someone calls with just 2 parameters, we assume isAttendanceMode is false.
    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        this(context, employeeList, false);
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee_glass, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee emp = employeeList.get(position);

        holder.tvName.setText(emp.getName());
        holder.tvRole.setText(emp.getMobileNumber());
        holder.tvStatus.setText("Active");

        if (emp.getProfilePhotoPath() != null && !emp.getProfilePhotoPath().isEmpty()) {
            Glide.with(context)
                    .load(emp.getProfilePhotoPath())
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        // Click Logic based on 'isAttendanceMode'
        holder.itemView.setOnClickListener(v -> {
            if (isAttendanceMode) {
                // Go to Mark Attendance Screen
                Intent intent = new Intent(context, MarkAttendanceActivity.class);
                intent.putExtra("EMP_ID", emp.getId());
                intent.putExtra("EMP_NAME", emp.getName());
                context.startActivity(intent);
            } else {
                // Go to Profile Screen (Normal View)
                Intent intent = new Intent(context, EmployeeProfileActivity.class);
                intent.putExtra("EMP_ID", emp.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    // Method to update list during search
    public void setFilteredList(List<Employee> filteredList) {
        this.employeeList = filteredList;
        notifyDataSetChanged();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvRole, tvStatus;
        CircleImageView imgProfile;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }
    }
}