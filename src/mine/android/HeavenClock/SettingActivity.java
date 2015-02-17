package mine.android.HeavenClock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import mine.android.api.ConfigAPI;
import mine.android.modules.Configuration;

/**
 * Created by Heaven on 2015/2/17.
 */
public class SettingActivity extends Activity {
    private EditText email;
    private EditText password;
    private static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        context = this;
        Configuration config = ConfigAPI.getConfig();

        email = (EditText) findViewById(R.id.email);
        email.setText(config.getDoubanEmail());

        password = (EditText) findViewById(R.id.password);
        password.setText(config.getDoubanPassword());

        Button save = (Button) findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = String.valueOf(email.getText());
                String passwordStr = String.valueOf(password.getText());
                Configuration c = new Configuration();
                c.setDoubanEmail(emailStr);
                c.setDoubanPassword(passwordStr);
                ConfigAPI.saveConfig(c);
                finish();
            }
        });
    }

    public static Context getContext() {
        return context;
    }
}