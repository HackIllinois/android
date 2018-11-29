package org.hackillinois.android.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.hackillinois.android.R;
import org.hackillinois.android.model.Attendee;
import org.hackillinois.android.model.QR;
import org.hackillinois.android.viewmodel.ProfileViewModel;

import java.util.EnumMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private ImageView qrImageView;
    private TextView nameTextView;
    private TextView dietaryRestrictionsTextView;
    private TextView universityTextView;
    private TextView majorTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProfileViewModel viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        viewModel.getQR().observe(this, new Observer<QR>() {
            public void onChanged(QR qr) {
                String text = qr.getQrInfo();
                Bitmap bitmap = generateQR(text);
                qrImageView.setImageBitmap(bitmap);
            }
        });

        viewModel.fetchQR();

        viewModel.getAttendee().observe(this, new Observer<Attendee>() {
            public void onChanged(Attendee attendee) {
                nameTextView.setText(attendee.getName());
                dietaryRestrictionsTextView.setText(attendee.getDiet());
                universityTextView.setText(attendee.getSchool());
                majorTextView.setText(attendee.getMajor());
            }
        });

        viewModel.fetchAttendee();
    }

    private Bitmap generateQR(String text) {
        int width = qrImageView.getWidth(), height = qrImageView.getHeight();
        int[] pixels = new int[width * height];

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 0);

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            final int CLEAR = Color.WHITE;
            final int SOLID = getResources().getColor(R.color.colorPrimary);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    pixels[y * width + x] = bitMatrix.get(x, y) ? SOLID : CLEAR;
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        qrImageView = view.findViewById(R.id.qr);
        nameTextView = view.findViewById(R.id.name);
        dietaryRestrictionsTextView = view.findViewById(R.id.dietaryRestrictions);
        universityTextView = view.findViewById(R.id.university);
        majorTextView = view.findViewById(R.id.major);

        return view;
    }
}
