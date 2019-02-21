package org.hackillinois.androidapp2019.view;

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

import org.hackillinois.androidapp2019.R;
import org.hackillinois.androidapp2019.database.entity.Attendee;
import org.hackillinois.androidapp2019.database.entity.QR;
import org.hackillinois.androidapp2019.database.entity.User;
import org.hackillinois.androidapp2019.viewmodel.ProfileViewModel;

import java.util.EnumMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private ImageView qrImageView;
    private TextView nameTextView;
    private TextView dietTextView;

    private TextView universityLabel;
    private TextView universityTextView;
    private TextView majorLabel;
    private TextView majorTextView;

    private ProfileViewModel viewModel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        viewModel.init();

        viewModel.getQr().observe(this, new Observer<QR>() {
            public void onChanged(QR qr) {
                if (qr != null) {
                    String text = qr.getQrInfo();
                    Bitmap bitmap = generateQR(text);
                    qrImageView.setImageBitmap(bitmap);
                }
            }
        });

        viewModel.getUser().observe(this, new Observer<User>() {
            public void onChanged(User user) {
                if (user != null) {
                    nameTextView.setText(user.getFullName());
                }
            }
        });

        viewModel.getAttendee().observe(this, new Observer<Attendee>() {
            public void onChanged(Attendee attendee) {
                if (attendee != null) {
                    String diet = attendee.getDietAsString();
                    if (diet != null) {
                        dietTextView.setText(diet);
                        dietTextView.setVisibility(View.VISIBLE);
                    } else {
                        dietTextView.setVisibility(View.INVISIBLE);
                    }
                    universityTextView.setText(attendee.getSchool());
                    majorTextView.setText(attendee.getMajor());
                }

                int visibility = (attendee != null) ? View.VISIBLE : View.INVISIBLE;
                dietTextView.setVisibility(visibility);
                universityLabel.setVisibility(visibility);
                majorLabel.setVisibility(visibility);
            }
        });
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
        dietTextView = view.findViewById(R.id.dietaryRestrictions);

        universityLabel = view.findViewById(R.id.universityTitle);
        universityTextView = view.findViewById(R.id.university);
        majorLabel = view.findViewById(R.id.majorTitle);
        majorTextView = view.findViewById(R.id.major);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }
}
