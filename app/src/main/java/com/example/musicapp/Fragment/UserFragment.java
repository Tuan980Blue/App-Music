package com.example.musicapp.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.Activity.InfoAccountActivity;
import com.example.musicapp.Activity.LoginActivity;
import com.example.musicapp.Models.SharedViewModel;
import com.example.musicapp.Models.User;
import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class UserFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private SharedViewModel sharedViewModel;
    TextView tvNameOfUser;
    ImageView avatar;
    CardView cvPremiumOfUser;
    TextView info_account;
    TextView logout;
    TextView TvStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Khi dữ liệu name thay đổi trong SharedViewModel, cập nhật tvNameOfUser
        sharedViewModel.getName().observe(getViewLifecycleOwner(), userName -> {
            tvNameOfUser.setText(userName);
        });

        avatar = view.findViewById(R.id.avatar);
        info_account = view.findViewById(R.id.infoAccount);
        tvNameOfUser = view.findViewById(R.id.tvNameOfUser);
        cvPremiumOfUser = view.findViewById(R.id.cvPremiumOfUser);
        logout = view.findViewById(R.id.logout);
        TvStatus = view.findViewById(R.id.TvStatus);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.isAdmin()) {
                            // Nếu người dùng là Admin, hiển thị button
                        }
                        if(user!= null){
                            String avatarUrl = user.getAvatarUser();
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                // Sử dụng thư viện Picasso để tải và hiển thị ảnh
                                Picasso.get().load(avatarUrl).into(avatar);
                            }
                            String userName = user.getNameUser();
                            tvNameOfUser.setText(userName);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu có
                }
            });
        }


        info_account.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InfoAccountActivity.class);
            startActivity(intent);
        });

        cvPremiumOfUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một instance của PremiumFragment
                PremiumFragment premiumFragment = new PremiumFragment();

                // Sử dụng FragmentManager để thay thế Fragment hiện tại bằng PremiumFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, premiumFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một hộp thoại xác nhận
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Xác nhận đăng xuất");
                builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
                builder.setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện đăng xuất
                        logoutUser();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng hộp thoại nếu người dùng chọn hủy
                        dialog.dismiss();
                    }
                });

                // Hiển thị hộp thoại
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        updateAccountStatus();

        return view;
    }

    private void updateAccountStatus() {
        TvStatus.setText("BASIC"); // Thiết lập trạng thái mặc định
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("isPremium").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isPremium = dataSnapshot.getValue(Boolean.class);
                    if (isPremium != null && isPremium) {
                        // Nếu là Premium, cập nhật trạng thái trên giao diện thành "PREMIUM"
                        TvStatus.setText("PREMIUM");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra
                }
            });
        }
    }

    private void logoutUser() {
        firebaseAuth.signOut();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        // Đặt các flags để xóa tất cả các Activity khỏi stack và đảm bảo rằng người dùng sẽ quay về LoginActivity khi đăng xuất
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Đóng Activity hiện tại sau khi chuyển đến LoginActivity
    }
}