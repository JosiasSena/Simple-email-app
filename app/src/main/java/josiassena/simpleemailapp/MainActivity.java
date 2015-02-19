package josiassena.simpleemailapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import josiassena.simpleemailapp.utils.Email;

import static josiassena.simpleemailapp.utils.Constants.IMG1;
import static josiassena.simpleemailapp.utils.Constants.IMG2;
import static josiassena.simpleemailapp.utils.Constants.IMG3;

public class MainActivity extends ActionBarActivity {

    private static final String LOG = MainActivity.class.getSimpleName();

    private EditText emailMessage, emailSubject, fromEmail, toEmail, emailPassword;
    private ImageButton attachment1, attachment2, attachment3;
    private String mExtraString;
    private static final int RESULT_LOAD_IMAGE = 1;
    private List<String> attachment_PathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // if build is Lollipop or higher then change the phone status bar color
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.holo_blue_dark));
        }

        init();
    }

    void init() {
        attachment_PathList = new ArrayList<>();

        toEmail = (EditText) findViewById(R.id.toEmail);
        emailSubject = (EditText) findViewById(R.id.emailSubject);
        emailMessage = (EditText) findViewById(R.id.emailMessage);
        fromEmail = (EditText) findViewById(R.id.fromEmail);
        emailPassword = (EditText) findViewById(R.id.emailPassword);

        attachment1 = (ImageButton) findViewById(R.id.img1);
        attachment2 = (ImageButton) findViewById(R.id.img2);
        attachment3 = (ImageButton) findViewById(R.id.img3);

        attachment1.setOnClickListener(new OnAttBtnClickListener(IMG1));
        attachment2.setOnClickListener(new OnAttBtnClickListener(IMG2));
        attachment3.setOnClickListener(new OnAttBtnClickListener(IMG3));
    }

    @Override
    protected void onActivityResult(int RequestCode, int ResultCode, Intent data) {
        super.onActivityResult(RequestCode, ResultCode, data);

        if (RequestCode == RESULT_LOAD_IMAGE && ResultCode == RESULT_OK && data != null) {
            Uri SelectedImage = data.getData();
            String[] FilePathColumn = {MediaStore.Images.Media.DATA};

            Cursor selectedCursor = getContentResolver().query(SelectedImage, FilePathColumn, null, null, null);
            selectedCursor.moveToFirst();

            int columnIndex = selectedCursor.getColumnIndex(FilePathColumn[0]);
            String picturePath = selectedCursor.getString(columnIndex);
            attachment_PathList.add(picturePath);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeFile(picturePath), // source
                    320, // width
                    320,  // height
                    true); // filter

            if (mExtraString != null) {
                if (getExtra().equals(IMG1)) {
                    attachment1.setImageBitmap(resizedBitmap);
                } else if (getExtra().equals(IMG2)) {
                    attachment2.setImageBitmap(resizedBitmap);
                } else if (getExtra().equals(IMG3)) {
                    attachment3.setImageBitmap(resizedBitmap);
                }
            } else {
                Log.e(LOG, "Intent extra is null");
            }

            selectedCursor.close();
        }
    }

    void saveExtra(String name) {
        mExtraString = name;
    }

    String getExtra() {
        return mExtraString;
    }

    /**
     * When one of the attachment buttons is clicked
     * - Add the passed in value to mExtraString
     * - open gallery to choose image attachment
     */
    private class OnAttBtnClickListener implements View.OnClickListener {
        private final String img;

        public OnAttBtnClickListener(String img) {
            this.img = img;
        }

        @Override
        public void onClick(View v) {
            saveExtra(img);
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_send_email) {
            Email email = new Email(
                    MainActivity.this, // context
                    fromEmail.getText().toString(), // From this email
                    emailPassword.getText().toString(), // using this password
                    toEmail.getText().toString(), // to this email
                    emailSubject.getText().toString(), // with this subject
                    emailMessage.getText().toString());

            if (!attachment_PathList.isEmpty()) {
                email.setAttachment_PathList(attachment_PathList);
            }
            email.send();
        } else if (id == R.id.action_new_email) {
            clearAll();
        } else if (id == R.id.action_clear_attachments) {
            clearAttachments();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Clears all text fields and attachments
     */
    private void clearAll() {
        clearEditTV(toEmail);
        clearEditTV(emailSubject);
        clearEditTV(emailMessage);
        clearEditTV(fromEmail);
        clearEditTV(emailPassword);
        clearAttachments();
    }

    private void clearEditTV(EditText editText) {
        editText.setText("");
    }

    /**
     * Clear all email attachments
     */
    private void clearAttachments() {
        attachment1.setImageDrawable(null);
        attachment2.setImageDrawable(null);
        attachment3.setImageDrawable(null);
        attachment_PathList.clear();
    }
}
