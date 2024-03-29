package geolab.dags.fileUpload;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import geolab.dags.MainActivity;
import geolab.dags.R;

public class UploadActivity extends ActionBarActivity{

    // LogCat tag
    private static final String TAG = UploadActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload, btnDone;
    long totalSize = 0;

    TextView longitude, latitude;

    //get current location coordinates
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
                    longitude.setText(location.getLongitude() + "");
                    latitude.setText(location.getLatitude() + "");
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    String userID;
    private EditText titleEditText,descriptionEditText;
    private TextInputLayout titleInputLayout,descriptionInputLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //loads application settings, choosed by user
        LoadSettings();
        final Context context = this;

        // loads pref data (user_id)
        userID = getUserIDFromPref();

        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        titleInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutTitle);
        descriptionInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutTitle);

        titleInputLayout.setErrorEnabled(true);
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    titleInputLayout.setError(null);
                } else {
                    titleInputLayout.setError("სავალდებულოა ;)");
                }
            }
        });

        descriptionInputLayout.setErrorEnabled(true);
        descriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    descriptionInputLayout.setError(null);
                } else {
                    descriptionInputLayout.setError("სავალდებულოა ;)");
                }
            }
        });



        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);


        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("GPS სერვისი არაა გააქტიურებული");
            dialog.setPositiveButton("გააქტიურება", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);

                }
            });
            dialog.setNegativeButton("გაუქმება", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    //
                }
            });
            dialog.show();
        }




        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnDone = (Button) findViewById(R.id.doneBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);

        // Changing action bar background color

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");
        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                new UploadFileToServer().execute();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * loads application settings, choosed by user
     */
    private void LoadSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int toolbarResColor = sharedPreferences.getInt("toolbarColor", 1) ;
        int tabLayoutResColor = sharedPreferences.getInt("tabLayoutColor", 2) ;
        int statusBarResColor = sharedPreferences.getInt("statusBarColor", 3) ;

//        Toast.makeText(this,toolbarResColor +" "+ tabLayoutResColor, Toast.LENGTH_LONG).show();

        MainActivity.SaveUserSettings(getApplicationContext(),toolbarResColor,tabLayoutResColor,statusBarResColor);
    }

    /**
     * loads saved
     * @return
     */
    private String getUserIDFromPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  data = sharedPreferences.getString("user_id", "0") ;
//        Toast.makeText(this,data, Toast.LENGTH_LONG).show();
        return data;
    }



    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
    }


    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            progressBar.setProgress(progress[0]);
            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            // showing the server response in an alert dialog
            showAlert();
            super.onPostExecute(result);
        }



        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpParams httpParameters = new BasicHttpParams();
            HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
            HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {

                EditText title = (EditText) findViewById(R.id.titleEditText);
                String postTitle = title.getText().toString();
                EditText description = (EditText) findViewById(R.id.descriptionEditText);
                String postDescription = description.getText().toString();

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                try {
                    entity.addPart("title",
                            new StringBody(postTitle));
                    entity.addPart("description",
                            new StringBody(postDescription));
                    entity.addPart("longitude",
                            new StringBody(longitude.getText().toString()));
                    entity.addPart("latitude",
                            new StringBody(latitude.getText().toString()));
                    entity.addPart("uploadDateTime",
                            new StringBody(new Date().toString()));
                    entity.addPart("user_id",
                            new StringBody(userID));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }


    }



    /**
     * Method to show alert dialog
     * */
    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ფოტოს დამატება ").setTitle("Success")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent toMainActivity = new Intent(UploadActivity.this,MainActivity.class);
                        startActivity(toMainActivity);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent toUploadActivity = new Intent(UploadActivity.this,UploadFileActivity.class);
                        startActivity(toUploadActivity);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
