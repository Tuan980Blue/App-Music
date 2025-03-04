// AdminApprovalActivity.java
package com.example.musicapp.Activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Adapters.AdminApprovalAdapter;
import com.example.musicapp.Models.AdminRequest;
import com.example.musicapp.R;
import com.example.musicapp.Utils.EmailHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminApprovalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton back;
    private List<AdminRequest> adminRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approval);

        initView();
        setupRecyclerView();
        fetchAdminRequests();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_admin_requests);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());
    }


    private void setupRecyclerView() {
        AdminApprovalAdapter adapter = new AdminApprovalAdapter(adminRequests,
                (adminRequest, isApprove) -> approveRequest(adminRequest, isApprove),
                (adminRequest, isApprove) -> rejectRequest(adminRequest, isApprove));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchAdminRequests() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        db.orderByChild("isAdmin").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminRequests.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AdminRequest adminRequest = dataSnapshot.getValue(AdminRequest.class);
                    if (adminRequest != null) {
                        adminRequests.add(adminRequest);
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminApprovalActivity.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveRequest(AdminRequest adminRequest, boolean isApprove) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.orderByChild("email").equalTo(adminRequest.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    userSnapshot.getRef().child("isAdmin").setValue(isApprove);
                }
                adminRequests.remove(adminRequest);
                recyclerView.getAdapter().notifyDataSetChanged();
                Toast.makeText(AdminApprovalActivity.this, "Xác nhận Admin thành công", Toast.LENGTH_SHORT).show();

                // Send approval email to the user
                EmailHelper.sendApprovalEmail(AdminApprovalActivity.this, adminRequest.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminApprovalActivity.this, "Failed to update admin status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectRequest(AdminRequest adminRequest, boolean isApprove) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.orderByChild("email").equalTo(adminRequest.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    userSnapshot.getRef().child("isAdmin").removeValue(); // Remove the isAdmin field
                }

                // Remove the rejected request from the local list
                adminRequests.remove(adminRequest);
                recyclerView.getAdapter().notifyDataSetChanged();
                Toast.makeText(AdminApprovalActivity.this, "Hủy xác nhận Admin", Toast.LENGTH_SHORT).show();

                // Send rejection email to the user
                EmailHelper.sendRejectionEmail(AdminApprovalActivity.this, adminRequest.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminApprovalActivity.this, "Failed to update admin status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
