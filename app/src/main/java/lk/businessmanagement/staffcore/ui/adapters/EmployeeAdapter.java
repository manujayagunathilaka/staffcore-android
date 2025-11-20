package lk.businessmanagement.staffcore.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.model.Employee;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private Context context;
    private List<Employee> employeeList;

    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee_card, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee emp = employeeList.get(position);

        holder.tvName.setText(emp.getName());
        holder.tvNic.setText("NIC: " + emp.getNic());
        holder.tvMobile.setText(emp.getMobileNumber());

        if (emp.getProfilePhotoPath() != null && !emp.getProfilePhotoPath().isEmpty()) {
            File imgFile = new File(emp.getProfilePhotoPath());
            if (imgFile.exists()) {
                holder.imgThumb.setImageURI(Uri.fromFile(imgFile));
            } else {
                holder.imgThumb.setImageResource(android.R.drawable.ic_menu_camera);
            }
        } else {
            holder.imgThumb.setImageResource(android.R.drawable.ic_menu_camera);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                android.content.Intent intent = new android.content.Intent(context, lk.businessmanagement.staffcore.ui.EmployeeProfileActivity.class);
                intent.putExtra("EMP_ID", emp.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvNic, tvMobile;
        ImageView imgThumb;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvNic = itemView.findViewById(R.id.tvNic);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            imgThumb = itemView.findViewById(R.id.imgThumb);
        }
    }
}
