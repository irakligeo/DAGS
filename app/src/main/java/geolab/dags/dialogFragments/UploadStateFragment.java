package geolab.dags.dialogFragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import geolab.dags.R;
import geolab.dags.fileUpload.Config;
import geolab.dags.fileUpload.UploadActivity;

/**
 * Created by geolabedu on 10/3/15.
 */
public class UploadStateFragment extends DialogFragment implements View.OnClickListener {

    private ImageView fromCamera, fromFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View uploadStateView = inflater.inflate(R.layout.upload_state_dialog_frag,null);

        fromCamera = (ImageView) uploadStateView.findViewById(R.id.fromCameraID);
        fromFile = (ImageView) uploadStateView.findViewById(R.id.fromFileID);

        fromFile.setOnClickListener(this);
        fromCamera.setOnClickListener(this);

        return uploadStateView;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fromCameraID:
                Toast.makeText(getActivity(),"from camera",Toast.LENGTH_SHORT).show();
                captureImage();
                break;
            case R.id.fromFileID:
                Toast.makeText(getActivity(),"from file",Toast.LENGTH_SHORT).show();
                break;
        }
    }


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;

    /**
     * returning image
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create toolbarColorResId media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     *
     * @param type
     * @return
     */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static Uri fileUri; // file url to store image/video

    public void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * method starts UploadActivity for taking picture
     * @param isImage
     */
    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(getActivity(), UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
    }


    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);

            } else if (resultCode == getActivity().RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}