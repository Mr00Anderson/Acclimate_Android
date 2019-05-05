package com.acclimate.payne.simpletestapp.photo;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;

import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PhotoUtils {

    public static final String BASE_PATH_TO_PHOTO = "img/user-alert/%s/";

    public static Bitmap rotateIfRequired(Bitmap img, @Nullable String path) throws IOException {

        ExifInterface exif;

        if (path != null) {
            exif = new ExifInterface(path);
        } else {
            exif = new ExifInterface(toByteStream(img));
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        // Determine Rotation
        int rotation = 0;
        if (orientation == 6) rotation = 90;
        else if (orientation == 3) rotation = 180;
        else if (orientation == 8) rotation = 270;

        // Rotate Image if Necessary
        if (rotation != 0) {
            // Create Matrix
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            // Rotate Bitmap
            return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        } else {
            return img;
        }

    }

    static ByteArrayInputStream toByteStream(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();
        return new ByteArrayInputStream(bitmapData);
    }

    public static String initPhotoPath(UserAlert alert) {
        // todo : remove method in user
        return alert.initPhotoPath();
    }


    public static class SendImageToCloudStorage {

        Bitmap bitmap;
        UserAlert alert;
        String pathToImg;

        public SendImageToCloudStorage(@NonNull Bitmap bitmap, @NonNull UserAlert alert) {
            this.bitmap = bitmap;
            this.alert = alert;
        }

        public void upload() {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

//            Log.i(AppTag.PHOTO_UPLOAD, "upload alert path = " + alert.getPhotoPath());
            pathToImg = alert.getPhotoPath();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(pathToImg);

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(this::taskFailure).addOnSuccessListener(this::taskSuccess);

        }

        private void taskSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            Log.i(AppTag.PHOTO_UPLOAD, "upload success ! dl url = " + taskSnapshot.getUploadSessionUri());
//            Log.i(AppTag.PHOTO_UPLOAD, "upload success ! class url= " + pathToImg);
        }


        private void taskFailure(Exception exception) {
 //           Log.e(AppTag.PHOTO_UPLOAD, "upload taskFailure = " + exception.getMessage());
        }

    }

}
