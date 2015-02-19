package josiassena.simpleemailapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import josiassena.simpleemailapp.R;

public class SendEmail extends AsyncTask<Void, Void, Void> {
    private ProgressDialog pDialog;
    private final Context context;
    private final Message mMessage;

    public SendEmail(Context context, Message message) {
        this.context = context;
        mMessage = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = ProgressDialog.show(context, context.getString(R.string.wait),
                context.getString(R.string.sending_email), true, false);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Transport.send(mMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }

        Toast.makeText(context, context.getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
    }
}