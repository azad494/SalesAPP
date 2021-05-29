package peash.esales.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import peash.esales.Controller.SessionUserController;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.SessionUser;
import Adi.esales.R;

public class Header extends Activity {

    TextView username,email;

    private SessionManager session;
    private SessionUserController sessionUserController;

    @Override
    public void onBackPressed(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header);

        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        username.setText(sessionList.get(0).getName());
        email.setText(sessionList.get(0).getEmail());
    }
}
