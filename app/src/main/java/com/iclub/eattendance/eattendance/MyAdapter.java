package com.iclub.eattendance.eattendance;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import com.iclub.eattendance.eattendance.OverviewActivity.*;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Student> studlist;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView student_name,reg_no;
        public Switch status;
        public int idNumber;
        public MyViewHolder(View view){
            super(view);
            student_name = (TextView) view.findViewById(R.id.student_name);
            reg_no = (TextView) view.findViewById(R.id.reg_no);
            status = (Switch) view.findViewById(R.id.status);
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Student s = (Student)studlist.get(idNumber);
                    s.setStatus(status.isChecked());
                    Log.d("DEBUG_TAG", s.getname() + "\n" + s.getreg() + "\n" + s.getstatus() + "\n" + idNumber);
                }
            });
        }
    }

    public MyAdapter(List<Student> studlist){
        this.studlist = studlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_overview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studlist.get(position);
        holder.student_name.setText(student.getname());
        holder.reg_no.setText(student.getreg());
        holder.status.setChecked(student.getstatus());
        holder.idNumber = student.getid();
    }

    @Override
    public int getItemCount() {
        return studlist.size();
    }

}