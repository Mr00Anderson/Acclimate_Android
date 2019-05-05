package com.acclimate.payne.simpletestapp.activities.alertForm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.photo.PhotoUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AlertFormPhotoHelper {

    static final int REQUEST_IMAGE_CAPTURE = 2;

    private NewAlertFormActivity activity;
    private ImageView photoView;
    public String currentPhotoPath;

    AlertFormPhotoHelper(NewAlertFormActivity activity, ImageView photoView){
        this.activity = activity; this.photoView = photoView;
    }


    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {

            Log.i("_photo", "dispatchTakePictureIntent");


            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

                Toast.makeText(activity, "Impossible de sauvegarder l'image sur l'appareil", Toast.LENGTH_SHORT).show();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {

        Log.i("_photo", "createImageFile");

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public void setNewPhoto(String photoPath){

        try {

            // Load Image
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, opts);

            activity.setImage(bitmap);

            bitmap = PhotoUtils.rotateIfRequired(bitmap, photoPath);
            ImageView photoView = activity.findViewById(R.id.add_photo_icon);
            photoView.setImageBitmap(bitmap);

        } catch(IOException e){
            Log.e("_photo","\n\n\n" + "impossible de trouver l'image");
            ImageView photoView = activity.findViewById(R.id.add_photo_icon);
            photoView.setImageDrawable(activity.getResources().getDrawable(R.drawable.cam_icon));

        }

        try {

            Bitmap bm = BitmapFactory.decodeFile(photoPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 20, baos); //bm is the bitmap object

        } catch (Exception e) {
            Log.e("_photo","\n\n\n" + "impossible d'encoder l'image");

        }

    }


}
