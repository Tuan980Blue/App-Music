package com.example.musicapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Models.AdminRequest;
import com.example.musicapp.R;

import java.util.List;

public class AdminApprovalAdapter extends RecyclerView.Adapter<AdminApprovalAdapter.ViewHolder> {

    private List<AdminRequest> adminRequests;
    private OnRequestActionListener approveListener;
    private OnRequestActionListener rejectListener;

    public AdminApprovalAdapter(List<AdminRequest> adminRequests, OnRequestActionListener approveListener, OnRequestActionListener rejectListener) {
        this.adminRequests = adminRequests;
        this.approveListener = approveListener;
        this.rejectListener = rejectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminRequest request = adminRequests.get(position);
        holder.bind(request);
        holder.buttonApprove.setOnClickListener(v -> {
            if (approveListener != null) {
                approveListener.onRequestAction(request, true);
            }
        });
        holder.buttonReject.setOnClickListener(v -> {
            if (rejectListener != null) {
                rejectListener.onRequestAction(request, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return adminRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameAdminRequest;
        private TextView emailAdminRequest;
        private ImageButton buttonApprove;
        private ImageButton buttonReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameAdminRequest = itemView.findViewById(R.id.nameAdminRequest);
            emailAdminRequest = itemView.findViewById(R.id.emailAdminRequest);
            buttonApprove = itemView.findViewById(R.id.button_approve);
            buttonReject = itemView.findViewById(R.id.button_reject);
        }

        public void bind(AdminRequest request) {
            nameAdminRequest.setText(request.getNameUser());
            emailAdminRequest.setText(request.getEmail());
        }
    }

    public interface OnRequestActionListener {
        void onRequestAction(AdminRequest adminRequest, boolean isApprove);
    }
}
