package com.example.swimmingpoolmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swimmingpoolmanager.R;
import com.example.swimmingpoolmanager.entities.Student;

import java.util.List;

public class StudentAdapter
        extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    public interface EditListener { void onEdit(Student s); }
    public interface DeleteListener { void onDelete(Student s); }

    private List<Student> items;
    private final EditListener  editCb;
    private final DeleteListener delCb;

    public StudentAdapter(List<Student> data, EditListener e, DeleteListener d) {
        items = data; editCb = e; delCb = d;
    }

    public void setItems(List<Student> data) {
        items = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student s = items.get(position);
        holder.name.setText(String.format("%s %s", s.getFirstName(), s.getLastName()));
        holder.edit.setOnClickListener(v -> editCb.onEdit(s));
        holder.del.setOnClickListener(v -> delCb.onDelete(s));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Make this private so it's not visible outside the adapter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView name;
        ImageButton edit, del;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            edit = itemView.findViewById(R.id.btnEdit);
            del  = itemView.findViewById(R.id.btnDelete);
        }
    }
}