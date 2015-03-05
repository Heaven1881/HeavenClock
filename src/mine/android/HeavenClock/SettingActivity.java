package mine.android.HeavenClock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import mine.android.api.ConfigAPI;
import mine.android.modules.Configuration;

/**
 * Created by Heaven on 2015/2/17.
 */
public class SettingActivity extends Activity {
    private EditText email;
    private EditText password;
    private EditText repeatSong;
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

        repeatSong = (EditText) findViewById(R.id.repeatSong);
        repeatSong.setText(config.getRepeatSong());

        Button save = (Button) findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = String.valueOf(email.getText());
                String passwordStr = String.valueOf(password.getText());
                int repeatSongInt = Integer.parseInt(String.valueOf(repeatSong.getText()));

                Configuration c = new Configuration();

                c.setDoubanEmail(emailStr);
                c.setDoubanPassword(passwordStr);
                c.setRepeatSong(repeatSongInt);

                ConfigAPI.saveConfig(c);
                Toast.makeText(getContext(), getString(R.string.save_setting), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public static Context getContext() {
        return context;
    }
}