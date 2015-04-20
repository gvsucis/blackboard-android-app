package edu.gvsu.bbmobile.MyBB;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import edu.gvsu.bbmobile.MyBB.helpers.bbemail.BbEmail;
import edu.gvsu.bbmobile.MyBB.helpers.bbemail.BbEmails;

/**
 * Created by romeroj on 10/17/13.
 */
public class ClickedOnSendEmailCourse implements View.OnClickListener, OnFetchCompleted {

    private Context ctx;
    private String username;
    private String crsId;
    private String crsName;
    private BbEmails bbEmails;

    public ClickedOnSendEmailCourse(Context c, String un, String crsId, String crsName){
        this.ctx = c;
        this.username =un;
        this.crsId = crsId;
        this.crsName = crsName;
        bbEmails = new BbEmails(this);
    }


    @Override
    public void onClick(View view) {
        bbEmails.fetchInstEmails(username, crsId);

    }

    @Override
    public void onFetchCompleted(Boolean isDone) {
        if(isDone && bbEmails.isLastFetchWorked()){

            if(bbEmails.getBbEmails() != null){
                String[] sendTo = new String[bbEmails.getBbEmails().size()];
                int c = 0;
                for(BbEmail em : bbEmails.getBbEmails()){
                    sendTo[c] = em.email;
                    c++;
                }

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, sendTo);
                email.putExtra(Intent.EXTRA_SUBJECT, crsName);
                email.setType("message/rfc822");
                Intent i = Intent.createChooser(email, "Choose an Email client :");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            }
            else{
                Toast.makeText(ctx, ctx.getString(R.string.tst_no_email_list), Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(ctx, ctx.getString(R.string.tst_no_email_list), Toast.LENGTH_LONG).show();
        }
    }
}
