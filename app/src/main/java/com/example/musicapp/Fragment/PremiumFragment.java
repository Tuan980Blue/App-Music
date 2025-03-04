package com.example.musicapp.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapp.Activity.ThanhToanActivity;
import com.example.musicapp.Models.User;
import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PremiumFragment extends Fragment {
    Button btnPremium;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_premium, container, false);

        btnPremium = view.findViewById(R.id.btnPerimium);

        btnPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra trạng thái tài khoản Premium
                checkPremiumStatus();
            }
        });
        checkPremium();
        return view;
    }
    private void checkPremium() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("isPremium").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isPremium = dataSnapshot.getValue(Boolean.class);
                    if (isPremium != null && isPremium) {
                        // Nếu tài khoản đã là Premium, không chuyển đến màn hình thanh toán
                        // Thực hiện cập nhật giao diện
                        if (btnPremium != null) {
                            btnPremium.setText("Đã mua Premium");
                            ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY); // Change Color.RED to the desired color
                            btnPremium.setBackgroundTintList(colorStateList);
                            btnPremium.setEnabled(false); // Không cho phép người dùng nhấn vào nút
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý khi có lỗi xảy ra
                }
            });
        }
    }

    private void checkPremiumStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("isPremium").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isPremium = dataSnapshot.getValue(Boolean.class);
                    if (isPremium != null && isPremium) {
                        // Nếu tài khoản đã là Premium, không chuyển đến màn hình thanh toán
                        // Thực hiện cập nhật giao diện
                        if (btnPremium != null) {
                            btnPremium.setText("Đã mua Premium");
                            ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY); // Change Color.RED to the desired color
                            btnPremium.setBackgroundTintList(colorStateList);
                            btnPremium.setEnabled(false); // Không cho phép người dùng nhấn vào nút
                        }
                    } else {
                        // Nếu chưa là Premium, chuyển qua màn hình thanh toán
                        Intent intent = new Intent(getActivity(), ThanhToanActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra
                }
            });
        }
    }
}
