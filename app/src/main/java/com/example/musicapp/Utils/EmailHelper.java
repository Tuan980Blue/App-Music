package com.example.musicapp.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHelper {

    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String EMAIL_USERNAME = "thehandsometeam4you@gmail.com";
    private static final String EMAIL_PASSWORD = "hiif ivfu gbhw vquv";
    private static final String TAG = "EmailHelper";

    public static void sendApprovalEmail(Context context, String recipientEmail) {
        String subject = "Admin Approval Request Accepted";
        String body = "Xin chào bạn,\n\nThật là một điều tuyệt vời! Yêu cầu để làm Admin của bạn đã trở thành hiện thực.\n\nBây giờ bạn có thể vào trang Admin của chúng tôi.\n\nTrân trọng!,\nThe Handsome Team (4you)";
        sendEmail(context, recipientEmail, subject, body);
        Log.d(TAG, "sendApprovalEmail: running");
    }

    public static void sendRejectionEmail(Context context, String recipientEmail) {
        String subject = "Admin Approval Request Rejected";
        String body = "Xin chào bạn,\n\nĐây là một điều bình thường! Khi yêu cầu của bạn còn một xúi may mắn nữa để trở thành Admin của chúng tôi.\n\nRất tiếc bạn đã bị chúng tôi từ chối. Xin chúc bạn may mắn lần sau!\n\nTrân trọng!,\nThe Handsome Team (4you)";
        sendEmail(context, recipientEmail, subject, body);
        Log.d(TAG, "sendRejectionEmail: running");
    }

    private static void sendEmail(final Context context, final String recipientEmail, final String subject, final String body) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", EMAIL_HOST);
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                    }
                });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(EMAIL_USERNAME));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                    message.setSubject(subject);
                    message.setText(body);
                    Transport.send(message);
                    // Gửi kết quả thành công trở lại UI
                    publishProgress();
                } catch (MessagingException e) {
                    e.printStackTrace();
                    // Gửi kết quả thất bại trở lại UI
                    publishProgress();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                // Cập nhật giao diện người dùng dựa trên kết quả
                Toast.makeText(context, "Email sent successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

}
